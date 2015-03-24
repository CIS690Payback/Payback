package com.hgkdev.haydenkinney.payback;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by H on 2/10/2015.
 */
public class GroupsTransactionsFragment extends Fragment {

    private static ParseObject group;
    public ListView transactionList, membersList;
    private TextView groupNameTxt;
    private Button   addMemberBtn;
    private ParseQueryAdapter<ParseObject> adapter;
    private ArrayList<Contact> groupMems;
    private ArrayList<Transaction> transactionsArray;

    public static GroupsTransactionsFragment newInstance(int sectionNumber, ParseObject g) {
        GroupsTransactionsFragment fragment = new GroupsTransactionsFragment();
        Bundle args = new Bundle();
        args.putInt("4", sectionNumber);
        group = g;
        fragment.setArguments(args);

        return fragment;
    }

    public GroupsTransactionsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group_transactions, container, false);

        getActivity().getActionBar().setTitle(group.getString("groupName"));



        // Set up the view
        groupNameTxt = (TextView) rootView.findViewById(R.id.txtView_GroupTransactions_groupName);
        groupNameTxt.setText(group.getString("groupName"));
        addMemberBtn = (Button) rootView.findViewById(R.id.button_GroupTransactions_addMembers);

        addMemberBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();

                fragmentManager.beginTransaction()
                        .replace(R.id.container, ContactsFragment.newInstance(5, group))
                        .addToBackStack("GroupsFromGroupsTransactionsFragment")
                        .commit();
            }
        });

        membersList = (ListView) rootView.findViewById(R.id.listView_GroupTransactions_members);

        ParseRelation usersGroupRelation = group.getRelation( "users" );
        groupMems = new ArrayList<Contact>();

        usersGroupRelation.getQuery().findInBackground(new FindCallback() {
            @Override
            public void done(List list, ParseException e) {
                if(e == null) {
                    Log.d("PB:GTF:", "Found " + list.size() + " group members");
                    for(int i = 0; i < list.size(); i++) {
                        ParseUser u = ((ParseUser)list.get(i));
                        Contact c = new Contact(u.getUsername());
                        groupMems.add(c);
                    }
                    Contact[] cA = new Contact[groupMems.size()];
                    cA = groupMems.toArray(cA);
                    GroupMemberAdapter gMA = new GroupMemberAdapter(getActivity(), R.layout.item_group_member, cA);
                    membersList.setAdapter(gMA);
                } else {
                    Log.d("PB:GTF:", e.toString());
                }
            }
        });


        transactionList = (ListView) rootView.findViewById(R.id.listView_GroupTransactions);
        transactionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragmentManager = getFragmentManager();

                fragmentManager.beginTransaction()
                        .replace(R.id.container, IndividualTransactionsFragment.newInstance(6, (Transaction)transactionList.getAdapter().getItem( position )))
                        .addToBackStack("IndividualTransactionsFromListTransactions")
                        .commit();

            }
        });

        LoadGroupTransactionsAsync lGTA = new LoadGroupTransactionsAsync(getActivity(), transactionList, this, group);
        lGTA.execute();

        return rootView;
    }

    public void asyncResult(ArrayList<Transaction> trans) {
        transactionsArray = trans;
    }
}

class LoadGroupTransactionsAsync extends AsyncTask<Void, Void, ArrayList<Transaction>> {
    ProgressDialog progressDialog;
    Activity mActivity;
    ListView transactionsList;
    GroupsTransactionsFragment gtf;
    ParseObject currentGroup;

    public LoadGroupTransactionsAsync(Activity activity, ListView transactionsList, GroupsTransactionsFragment gtf, ParseObject cG ) {
        mActivity = activity;
        this.transactionsList = transactionsList;
        progressDialog = new ProgressDialog( mActivity );
        this.gtf = gtf;
        currentGroup = cG;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Loading Transactions");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    protected ArrayList<Transaction> doInBackground(Void... params) {
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();

        ParseQuery transactionQuery = new ParseQuery("Transaction");
        transactionQuery.whereEqualTo("Group", currentGroup);
        try{
            List<ParseObject> ts = transactionQuery.find();
            for( int i = 0; i < ts.size(); i++ ) {
                ParseObject pO = ts.get( i );
                boolean owed = false;
                if(pO.getParseUser("Payer") == ParseUser.getCurrentUser()) {
                    owed = true;
                }
                Transaction t = new Transaction(pO.getString("Name"),
                        pO.getDouble("Cost"),
                        owed,
                        pO.getString("Comment"),
                        pO.getParseObject("Group"),
                        pO.getCreatedAt(),
                        pO.getNumber("userCount").intValue());
                transactions.add(t);
            }
        } catch( Exception ex ) {
            Log.d("ListTransFrag: ", "Something screwed up in background " + ex.toString());
        }

        return transactions;
    }

    protected void onPostExecute(ArrayList<Transaction> transactions) {
        super.onPostExecute(transactions);
        gtf.asyncResult(transactions);
        Transaction[] transactionsArray = new Transaction[transactions.size()];
        transactionsArray = transactions.toArray(transactionsArray);
        GroupsTransactionsAdapter customAdapter = new GroupsTransactionsAdapter( mActivity, R.layout.item_group_transaction, transactionsArray);
        transactionsList.setAdapter(customAdapter);
        progressDialog.cancel();

    }
}
