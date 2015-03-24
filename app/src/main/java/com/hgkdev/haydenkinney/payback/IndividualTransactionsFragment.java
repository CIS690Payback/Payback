package com.hgkdev.haydenkinney.payback;

/**
 * Created by HaydenKinney on 2/25/15.
 */

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;


public class IndividualTransactionsFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "6";
    public static Transaction transaction;

    private LinearLayout netLinearLayout;
    private TextView nameTxtView, costTxtView, dateTxtView, netTxtView;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static IndividualTransactionsFragment newInstance(int sectionNumber, Transaction t) {
        IndividualTransactionsFragment fragment = new IndividualTransactionsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        transaction = t;
        return fragment;
    }

    public IndividualTransactionsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_single_transaction, container, false);

        nameTxtView = (TextView)rootView.findViewById(R.id.txtView_singleTransaction_name);
        costTxtView = (TextView)rootView.findViewById(R.id.txtView_singleTransaction_amount);
        dateTxtView = (TextView)rootView.findViewById(R.id.txtView_singleTransaction_date);
        netTxtView = (TextView)rootView.findViewById(R.id.txtView_singleTransaction_netAmount);
        netLinearLayout = (LinearLayout)rootView.findViewById(R.id.linearLayout_singleTransaction_netAmount);

        nameTxtView.setText(transaction.getDescription() );
        costTxtView.setText(String.format("$%,.2f", transaction.getCost() ) );
        dateTxtView.setText("Created on: " + transaction.getDate().toString().substring(0, 19) );
        if(transaction.getOwed()) {
            if( transaction.getUserCount() == 1 ) {
                netTxtView.setText("You owe yourself $" + transaction.getCost() );
            } else {
                netTxtView.setText("You are owed " + String.format("$%,.2f", Math.abs( transaction.getCost() / (transaction.getUserCount() ) ) * (transaction.getUserCount() - 1 ) ) );
            }
            netTxtView.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            netTxtView.setText("You owe " + String.format("$%,.2f", Math.abs( transaction.getCost() / (transaction.getUserCount() ) ) * (transaction.getUserCount() - 1 ) ) );
            netTxtView.setTextColor(Color.parseColor("#D32F2F"));
        }
        netLinearLayout.setBackgroundColor(Color.parseColor("#ffdddddd"));
        return rootView;
    }
}
