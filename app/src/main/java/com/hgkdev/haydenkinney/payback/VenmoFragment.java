package com.hgkdev.haydenkinney.payback;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.hgkdev.haydenkinney.payback.VenmoLibrary.VenmoResponse;

/**
 * Created by HaydenKinney on 4/2/15.
 */
public class VenmoFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final int REQUEST_CODE_VENMO_APP_SWITCH = 123;
    private static final String ARG_SECTION_NUMBER = "section_number";
    private static String contactInfo;
    private Intent myIntent;
    private RadioGroup radiog;
    private RadioButton pay;
    private RadioButton charge;
    private EditText amountText;
    private EditText noteText;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static VenmoFragment newInstance(int sectionNumber, String cI) {
        VenmoFragment fragment = new VenmoFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        contactInfo = cI;
        return fragment;
    }

    public VenmoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.venmo_transaction, container, false);

        Button proceed = (Button) rootView.findViewById(R.id.btn_venmo_accept);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeTxn();
            }
        });


        Button back = (Button) rootView.findViewById(R.id.btn_venmo_back);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        radiog = (RadioGroup) rootView.findViewById(R.id.radioGroup);
        pay = (RadioButton) rootView.findViewById(R.id.payRadio);
        charge = (RadioButton) rootView.findViewById(R.id.chargeRadio);
        amountText = (EditText) rootView.findViewById(R.id.venmo_amount);
        noteText = (EditText) rootView.findViewById(R.id.venmo_note);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    private void makeTxn() {
        String app_id="2439";
        String app_name="Payback";
        String amount;
        String note;
        String txn;

        int selected = radiog.getCheckedRadioButtonId();
        if(selected == 0) {
            txn="pay";
        } else {
            txn="charge";
        }
        note = "";
        amount = "0.00";
        note = noteText.getText().toString();
        amount = amountText.getText().toString();

        if( VenmoLibrary.isVenmoInstalled(getActivity()) ) {
            Intent sendIntent = VenmoLibrary.openVenmoPayment(app_id, app_name, contactInfo, amount, note, txn);
            startActivityForResult(sendIntent, REQUEST_CODE_VENMO_APP_SWITCH);
        }

    }

    private void goBack() {
        FragmentManager fragmentManager = getActivity().getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, GroupFragment.newInstance(3))
                .addToBackStack(null)
                .commit();
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch(requestCode) {
            case REQUEST_CODE_VENMO_APP_SWITCH: {
                if(resultCode == getActivity().RESULT_OK) {
                    String signedrequest = data.getStringExtra("signedrequest");
                    if(signedrequest != null) {
                        VenmoResponse response = (new VenmoLibrary()).validateVenmoPaymentResponse(signedrequest, "5mCxNShUfFA97AGuBQaPDjc6tgpJb54s");
                        if(response.getSuccess().equals("1")) {
                            //Payment successful.  Use data from response object to display a success message
                            String note = response.getNote();
                            String amount = response.getAmount();
                            Toast.makeText(getActivity(), "Payment for $" + amount + " complete!", Toast.LENGTH_LONG).show();
                        }
                    }
                    else {
                        String error_message = data.getStringExtra("error_message");
                        Toast.makeText(getActivity(), "Error: " + error_message, Toast.LENGTH_LONG).show();
                    }
                }
                else if(resultCode == getActivity().RESULT_CANCELED) {
                    Toast.makeText(getActivity(), "Payment canceled!", Toast.LENGTH_LONG).show();
                }
                break;
            }
        }
    }
}