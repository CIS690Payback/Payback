package com.hgkdev.haydenkinney.payback;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseUser;


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
        groupSpinner.setSelection(1);

        adapter.loadObjects();

        db = new DatabaseInteractor();

        addTransactionButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addTransaction(v);
            }
        });

        return rootView;

    }

    private void addTransaction(View v) {
        if(nameEditText.getText().length() > 0 && costEditText.getText().length() > 0) {
            db.AddTransactionObject(nameEditText.getText().toString(),
                 Double.parseDouble(costEditText.getText().toString()),
                 adapter.getItem(groupSpinner.getSelectedItemPosition()));
        } else {
            sendToast("Please insert all values");
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

