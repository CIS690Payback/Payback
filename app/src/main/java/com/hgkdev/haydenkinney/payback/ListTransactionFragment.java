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
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by HaydenKinney on 1/3/15.
 */
public class ListTransactionFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "3";
    TextView amountOwed;
    ListView transactions;
    double sum;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static ListTransactionFragment newInstance(int sectionNumber) {
        ListTransactionFragment fragment = new ListTransactionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public ListTransactionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list_transactions, container, false);
        amountOwed = (TextView) rootView.findViewById(R.id.txtView_AmountOwed);
        transactions = (ListView) rootView.findViewById(R.id.listView_Transactions);


        transactions.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FragmentManager fragmentManager = getFragmentManager();

                fragmentManager.beginTransaction()
                        .replace(R.id.container, IndividualTransactionsFragment.newInstance(6, (ParseObject)transactions.getAdapter().getItem( position )))
                        .addToBackStack("IndividualTransactionsFromListTransactions")
                        .commit();

            }
        });

        LoadTransactionsAsync lTA = new LoadTransactionsAsync(getActivity(), transactions, this);
        lTA.execute();

        updateAmountOwed();
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    private void updateAmountOwed() {
        RetrieveAndSumAllTransactions();
    }


    public void RetrieveAndSumAllTransactions() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Transaction");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> transactionList, ParseException e) {
                if ( e == null ) {
                    sum = 0;
                    Log.d("transaction", "Retrieved " + transactionList.size() + " transactions");
                    for( int i = 0; i < transactionList.size(); i++ ) {
                        sum += transactionList.get( i ).getDouble( "Cost" );
                    }
                    amountOwed.setText(String.format( "Amount Owed: $%.2f", sum ) );
                } else {
                    Log.d("transaction", "Error: " + e.getMessage());
                }
            }
        });
    }
}

class LoadTransactionsAsync extends AsyncTask<Void, Void, ArrayList<Transaction>> {
    ProgressDialog progressDialog;
    Activity mActivity;
    ListView transactionsList;
    ListTransactionFragment ltf;

    public LoadTransactionsAsync(Activity activity, ListView transactionsList, ListTransactionFragment ltf ) {
        mActivity = activity;
        this.transactionsList = transactionsList;
        progressDialog = new ProgressDialog( mActivity );
        this.ltf = ltf;
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
        // ParseQuery not getting pointer for Group
        ParseQuery innerQuery = new ParseQuery("Group");
        innerQuery.whereEqualTo("users", ParseUser.getCurrentUser());
        innerQuery.selectKeys(Arrays.asList("objectId"));
        ParseQuery transactionQuery = new ParseQuery("Transaction");
        transactionQuery.whereEqualTo("Group", innerQuery);
        // Need to fix.
        try{
            List<ParseObject> ts = transactionQuery.find();
            for( int i = 0; i < ts.size(); i++ ) {
                ParseObject pO = ts.get( i );
                boolean owed = false;
                if(pO.getParseUser("Payer") == ParseUser.getCurrentUser()) {
                    owed = true;
                }
                Transaction t = new Transaction(pO.getString("description"),
                                                pO.getDouble("cost"),
                                                owed,
                                                pO.getString("comment"),
                                                pO.getParseObject("Group"),
                                                pO.getCreatedAt());
                transactions.add(t);
            }
        } catch( Exception ex ) {
            Log.d("ListTransFrag: ", "Something screwed up in background " + ex.toString());
        }


//        ArrayList<Contact> contacts = new ArrayList<Contact>();
//
//        Cursor c = mActivity.getApplicationContext().getContentResolver().query(
//                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
//                null, null, null);
//
//        while(c.moveToNext()) {
//            Uri thumbnailURI;
//            boolean contactExists = false;
//
//            String contactName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//            String phoneNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//
//            for( Contact con : contacts ) {
//                if (contactName.equals(con.getName())) {
//                    con.setNumber(phoneNumber);
//                    contactExists = true;
//                }
//            }
//            if(!contactExists) {
//                try {
//                    thumbnailURI = Uri.parse(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)));
//                } catch(Exception exception) {
//                    thumbnailURI = null;
//                }
//                contacts.add(new Contact(contactName, phoneNumber, thumbnailURI));
//            }
//        }
//        c.close();
//
//        Cursor e = mActivity.getApplicationContext().getContentResolver().query(
//                ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
//                null, null, null);
//
//        while(e.moveToNext()) {
//            boolean contactExists = false;
//
//            String contactName = e.getString(e.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
//            String emailAddress = e.getString(e.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));
//
//            for( Contact con : contacts ) {
//                if (contactName.equals(con.getName())) {
//                    con.setNumber(emailAddress);
//                    contactExists = true;
//                }
//            }
//            if(!contactExists) {
//                contacts.add(new Contact(contactName, emailAddress));
//            }
//        }
//        c.close();
//
//        Collections.sort(contacts, new Comparator<Contact>() {
//            public int compare(Contact c1, Contact c2) {
//                return c1.getName().compareTo(c2.getName());
//            }
//        });

        return transactions;
    }

    protected void onPostExecute(ArrayList<Transaction> transactions) {
        super.onPostExecute(transactions);
//        cf.asyncResult(transactions);
        Transaction[] transactionsArray = new Transaction[transactions.size()];
        transactionsArray = transactions.toArray(transactionsArray);
        ListTransactionAdapter customAdapter = new ListTransactionAdapter( mActivity, R.layout.item_contact, transactionsArray);
        transactionsList.setAdapter(customAdapter);
        progressDialog.cancel();

    }
}
