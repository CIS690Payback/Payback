package com.hgkdev.haydenkinney.payback;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * Created by HaydenKinney on 1/1/15.
 */

/**
 * A placeholder fragment containing a simple view.
 */
public class AddTransactionFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "2";

    Button   addTransactionButton;
    EditText nameEditText;
    EditText costEditText;
    Spinner  groupSpinner;
    DatabaseInteractor db;
    ParseQueryAdapter<ParseObject> adapter;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static AddTransactionFragment newInstance(int sectionNumber) {
        AddTransactionFragment fragment = new AddTransactionFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public AddTransactionFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_create_transaction, container, false);

        addTransactionButton = (Button) rootView.findViewById(R.id.btn_AddTransaction);
        nameEditText = (EditText) rootView.findViewById(R.id.editText_Name);
        costEditText = (EditText) rootView.findViewById(R.id.editText_Cost);
        groupSpinner = (Spinner)  rootView.findViewById(R.id.spinner_Group);

        costEditText.setCompoundDrawablesWithIntrinsicBounds(new TextDrawable("$"), null, null, null);
        costEditText.setCompoundDrawablePadding("$".length() * 45);

        ParseQueryAdapter.QueryFactory<ParseObject> factory =
                new ParseQueryAdapter.QueryFactory<ParseObject>() {
                    public ParseQuery create() {
                        ParseQuery query = new ParseQuery("Group");
                        query.whereEqualTo("users", ParseUser.getCurrentUser());
                        return query;
                    }
                };

        adapter = new ParseQueryAdapter<ParseObject>(this.getActivity(), factory);
        adapter.setTextKey("groupName");
        groupSpinner.setAdapter(adapter);
        adapter.loadObjects();

        groupSpinner.setSelection(2);


        db = new DatabaseInteractor();

        addTransactionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addTransaction(v);
            }
        });

        return rootView;

    }

    private void addTransaction(View v) {
        hideKeyboard();
        if(nameEditText.getText().length() > 0 && costEditText.getText().length() > 0) {
            ParseObject group = adapter.getItem(groupSpinner.getSelectedItemPosition());
            ParseObject addTransactionObject = new ParseObject("Transaction");
            addTransactionObject.put("Name", nameEditText.getText().toString());
            addTransactionObject.put("Cost", Double.parseDouble(costEditText.getText().toString()));
            addTransactionObject.put("Group", group);
            addTransactionObject.put("Payer", ParseUser.getCurrentUser());
            addTransactionObject.put("userCount", group.getNumber("userCount").intValue());
            addTransactionObject.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if( e == null ) {
                        Toast.makeText(getActivity(), "Added Transaction", Toast.LENGTH_LONG).show();
                        nameEditText.setText("");
                        costEditText.setText("");
                        nameEditText.requestFocus();
                    } else {
                        Toast.makeText(getActivity(), "Transaction Creation Failed", Toast.LENGTH_LONG).show();
                    }
                }
            });

        } else {
            sendToast("Please insert all values");
        }
    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void sendToast(String message) {

        Activity context = getActivity();
        CharSequence text = (CharSequence) message;

        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        toast.show();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

}

