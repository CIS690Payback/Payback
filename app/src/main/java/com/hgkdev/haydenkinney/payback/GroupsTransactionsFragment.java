package com.hgkdev.haydenkinney.payback;

import android.app.Fragment;
import android.app.FragmentManager;
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
    private ListView transactionList, membersList;
    private TextView groupNameTxt;
    private Button   addMemberBtn;
    private ParseQueryAdapter<ParseObject> adapter;
    private ArrayList<Contact> groupMems;

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
        transactionList = (ListView) rootView.findViewById(R.id.listView_GroupTransactions);

        ParseQueryAdapter.QueryFactory<ParseObject> factory =
        new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("Transaction");
                query.whereEqualTo("Group", group);
                return query;
            }
        };

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

        adapter = new ParseQueryAdapter<ParseObject>(this.getActivity(), factory);
        adapter.setTextKey("Name");
        transactionList.setAdapter(adapter);

        adapter.loadObjects();

        transactionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragmentManager = getFragmentManager();

                fragmentManager.beginTransaction()
                        .replace(R.id.container, IndividualTransactionsFragment.newInstance(6, adapter.getItem( position )))
                        .addToBackStack("IndividualTransactionsFromGroupsTransactions")
                        .commit();

            }
        });


        return rootView;
    }



}

//class LoadGroupMembersAsync extends AsyncTask<Void, Void, ArrayList<Contact>> {
//    ProgressDialog progressDialog;
//    Activity mActivity;
//    ListView contactsList;
//    GroupsTransactionsFragment gtf;
//
//    public LoadGroupMembersAsync(Activity activity, ListView contactsList, GroupsTransactionsFragment gtf ) {
//        mActivity = activity;
//        this.contactsList = contactsList;
//        progressDialog = new ProgressDialog( mActivity );
//        this.gtf = gtf;
//    }
//
//    protected void onPreExecute() {
//        super.onPreExecute();
//        progressDialog.setCancelable(true);
//        progressDialog.setMessage("Loading Group Members");
//        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
//        progressDialog.show();
//    }
//
//    protected ArrayList<Contact> doInBackground(Void... params) {
//        ArrayList<Contact> contacts = new ArrayList<Contact>();
//
//        ParseQuery<ParseObject> currentMembersQuery = new ParseQuery("Group");
//        currentMembersQuery.get("")
////        Cursor c = mActivity.getApplicationContext().getContentResolver().query(
////                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
////                null, null, null);
////
////        while(c.moveToNext()) {
////            Uri thumbnailURI;
////            boolean contactExists = false;
////
////            String contactName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
////            String phoneNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
////
////            for( Contact con : contacts ) {
////                if (contactName.equals(con.getName())) {
////                    con.setNumber(phoneNumber);
////                    contactExists = true;
////                }
////            }
////            if(!contactExists) {
////                try {
////                    thumbnailURI = Uri.parse(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)));
////                } catch(Exception exception) {
////                    thumbnailURI = null;
////                }
////                contacts.add(new Contact(contactName, phoneNumber, thumbnailURI));
////            }
////        }
////        c.close();
////
////        Cursor e = mActivity.getApplicationContext().getContentResolver().query(
////                ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
////                null, null, null);
////
////        while(e.moveToNext()) {
////            boolean contactExists = false;
////
////            String contactName = e.getString(e.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
////            String emailAddress = e.getString(e.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
////
////            for( Contact con : contacts ) {
////                if (contactName.equals(con.getName())) {
////                    con.setNumber(emailAddress);
////                    contactExists = true;
////                }
////            }
////            if(!contactExists) {
////                contacts.add(new Contact(contactName, emailAddress));
////            }
////        }
////        c.close();
////
////        Collections.sort(contacts, new Comparator<Contact>() {
////            public int compare(Contact c1, Contact c2) {
////                return c1.getName().compareTo(c2.getName());
////            }
////        });
////
////        return contacts;
//    }
//
//    protected void onPostExecute(ArrayList<Contact> contacts) {
//        super.onPostExecute(contacts);
//        cf.asyncResult(contacts);
//        Contact[] contactArray = new Contact[contacts.size()];
//        contactArray = contacts.toArray(contactArray);
//        ContactsAdapter customAdapter = new ContactsAdapter( mActivity, R.layout.item_contact, contactArray);
//        contactsList.setAdapter(customAdapter);
//        progressDialog.cancel();
//
//    }
//}
//
