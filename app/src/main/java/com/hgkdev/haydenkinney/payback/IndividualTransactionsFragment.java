package com.hgkdev.haydenkinney.payback;

/**
 * Created by HaydenKinney on 2/25/15.
 */

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class IndividualTransactionsFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "6";
    public static Transaction transaction;

    private LinearLayout netLinearLayout;
    private TextView nameTxtView, costTxtView, dateTxtView, netTxtView;
    private Button update, delete;
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
        delete = (Button)rootView.findViewById(R.id.button_singleTransaction_delete);

        delete.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                deleteTransaction();
            }
        });

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

    private void deleteTransaction() {
        ParseQuery pQ = new ParseQuery("Transaction");
        pQ.whereEqualTo("objectId", transaction.getObjectID());
        pQ.findInBackground(new FindCallback() {
            @Override
            public void done(List list, ParseException e) {
                if( e == null && list.size() > 0 ) {
                    ParseObject o = (ParseObject)list.get(0);
                    o.deleteInBackground(new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if( e == null ) {
                                getFragmentManager().popBackStackImmediate();
                            } else {
                                Log.d("PB:ITF", " threw in delete callback " + e.toString());
                                Toast.makeText(getActivity(), "Found transaction but could not remove it", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } else if( e != null ) {
                    Log.d("PB:ITF", " threw in find callback " + e.toString() );
                    Toast.makeText(getActivity(), "Error removing transaction", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Could not find transaction", Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
