package com.hgkdev.haydenkinney.payback;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

/**
 * Created by H on 2/10/2015.
 */
public class GroupsTransactionsFragment extends Fragment {

    private static ParseObject group;
    private ListView transactionList;
    private ParseQueryAdapter<ParseObject> adapter;

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
        transactionList = (ListView) rootView.findViewById(R.id.listView_GroupTransactions);

        ParseQueryAdapter.QueryFactory<ParseObject> factory =
        new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery create() {
                ParseQuery query = new ParseQuery("Transaction");
                query.whereEqualTo("Group", group);
                return query;
            }
        };

        adapter = new ParseQueryAdapter<ParseObject>(this.getActivity(), factory);
        adapter.setTextKey("Name");
        transactionList.setAdapter(adapter);

        adapter.loadObjects();

        return rootView;
    }



}
