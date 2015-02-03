package com.hgkdev.haydenkinney.payback;

/**
 * Created by H on 2/2/2015.
 */

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;

/**
 * A Group fragment containing a simple view.
 */
public class GroupFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "3";
    private ParseQueryAdapter<ParseObject> groupsAdapter;

    private LayoutInflater groupInflater;

    ListView groupsListView;
    Button   registerGroupButton;
    EditText groupNameEditText;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static GroupFragment newInstance(int sectionNumber) {
        GroupFragment fragment = new GroupFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public GroupFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_group, container, false);

        // Set up the views
        groupsListView = (ListView) rootView.findViewById(R.id.groupView_Groups);
        groupNameEditText = (EditText) rootView.findViewById(R.id.editText_GroupName);
        registerGroupButton = (Button) rootView.findViewById(R.id.button_CreateGroup);

        registerGroupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createGroup();
            }
        });

        ParseQueryAdapter.QueryFactory<ParseObject> factory = new ParseQueryAdapter.QueryFactory<ParseObject>() {
            public ParseQuery<ParseObject> create() {
                ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
                query.fromLocalDatastore();
                return query;
            }
        };

        groupsAdapter = new GroupsAdapter(this.getActivity(), factory);
        groupsListView.setAdapter(groupsAdapter);

        groupInflater = (LayoutInflater) this.getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(!ParseAnonymousUtils.isLinked(ParseUser.getCurrentUser())) {
            findUserGroups();
        }
    }

    public void createGroup() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseObject group = new ParseObject("Group");

        group.put("groupName", groupNameEditText.getText().toString());

        ParseRelation<ParseObject> relation = group.getRelation("users");
        relation.add(currentUser);

        group.saveInBackground(new SaveCallback()  {
            public void done(ParseException e) {
                if( e == null ) {
                    createdGroup();
                } else {
                    Log.d("PAYBACK:GROUPS: ", "Failed to create group w/ error " + e.toString());
                }
            }
        });
    }

    public void createdGroup() {
        Log.d("PAYBACK:GROUPS: ", "Created new group " + groupNameEditText.getText().toString());
        Toast.makeText(this.getActivity(), "Created new group.", Toast.LENGTH_LONG).show();
        findUserGroups();

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }

    private void findUserGroups() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Group");
        ParseUser currentUser = ParseUser.getCurrentUser();
        query.whereEqualTo("users", currentUser);
//        query.clearCachedResult();
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> groupsList, ParseException e) {
                if ( e == null ) {
                    Log.d("PAYBACK:GROUPS: ", "Retrieved " + groupsList.size() + " groups for user");
                    ParseObject.pinAllInBackground((List<ParseObject>) groupsList, new SaveCallback() {
                                public void done(ParseException e) {
                                    if( e == null ) {
                                        groupsAdapter.loadObjects();
                                        Log.d("PAYBACK:GROUPS: ", "Ran adapter's loadObjects()");
                                    }
                                    else {
                                        Log.d("PAYBACK:GROUPS: ", "Error pinning groups: " + e.getMessage());
                                    }
                                }
                            });
                } else {
                    Log.d("PAYBACK:GROUPS:", "Error: " + e.getMessage());
                }
            }
        });
    }


    private class GroupsAdapter extends ParseQueryAdapter<ParseObject> {


        public GroupsAdapter(Context context,
                             ParseQueryAdapter.QueryFactory<ParseObject> queryFactory) {
            super(context, queryFactory);
        }

        @Override
        public View getItemView(ParseObject group, View view, ViewGroup parent) {
            ViewHolder holder;
            if( view == null ) {
                view = groupInflater.inflate(R.layout.item_group_list, parent, false);
                holder = new ViewHolder();
                holder.groupName = (TextView) view.findViewById(R.id.group_name);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            TextView groupName = holder.groupName;
            groupName.setText(group.getString("groupName"));

            return view;
        }
    }

    private static class ViewHolder {
        TextView groupName;
    }
}
