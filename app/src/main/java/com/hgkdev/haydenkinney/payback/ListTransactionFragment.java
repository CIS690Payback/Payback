package com.hgkdev.haydenkinney.payback;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

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
        RetrieveTransactions();
    }


    private void RetrieveTransactions() {
        ParseQueryAdapter<ParseObject> transactionAdapter = new ParseQueryAdapter<ParseObject>(this.getActivity(), "Transaction");
        transactionAdapter.setTextKey("Name");
        transactionAdapter.setTextKey("Cost");
        transactions.setAdapter(transactionAdapter);
        transactionAdapter.loadObjects();
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