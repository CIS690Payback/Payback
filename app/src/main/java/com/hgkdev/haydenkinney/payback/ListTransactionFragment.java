package com.hgkdev.haydenkinney.payback;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

/**
 * Created by HaydenKinney on 1/3/15.
 */
public class ListTransactionFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "3";
    DatabaseInteractor di;
    TextView amountOwed;

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
        di = new DatabaseInteractor();
        amountOwed = (TextView) rootView.findViewById(R.id.txtView_AmountOwed);
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
        di.RetrieveAndSumAllTransactions();
        amountOwed.setText(String.format( "Amount Owed: $%.2f", di.sum ) );
    }

}