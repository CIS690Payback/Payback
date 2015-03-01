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

import com.parse.ParseObject;


public class IndividualTransactionsFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "6";
    public static ParseObject transaction;

    private LinearLayout netLinearLayout;
    private TextView nameTxtView, costTxtView, dateTxtView, netTxtView;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static IndividualTransactionsFragment newInstance(int sectionNumber, ParseObject g) {
        IndividualTransactionsFragment fragment = new IndividualTransactionsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        transaction = g;
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

        nameTxtView.setText(transaction.getString("Name") );
        costTxtView.setText("$" + transaction.getNumber("Cost").toString() );
        dateTxtView.setText("Created on: " + transaction.getCreatedAt().toString().substring(0, 19) );
        netTxtView.setText("You are owed/owe $x");
        netLinearLayout.setBackgroundColor(Color.parseColor("#ffdddddd"));

        return rootView;
    }
}
