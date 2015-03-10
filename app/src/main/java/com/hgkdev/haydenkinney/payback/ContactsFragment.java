package com.hgkdev.haydenkinney.payback;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by H on 2/15/2015.
 */
public class ContactsFragment extends Fragment {
    private static final String ARG_SECTION_NUMBER = "5";
    private static ParseObject group;
    ListView list;
    ArrayList<Contact> contactsList;
    public static ContactsFragment newInstance(int sectionNumber, ParseObject gr) {
        ContactsFragment fragment = new ContactsFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        group = gr;
        return fragment;
    }

    public ContactsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_contacts, container, false);

        list = (ListView)rootView.findViewById(R.id.listView_contacts);

        LoadContactsAsync lca = new LoadContactsAsync(getActivity(), list, this);
        lca.execute();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final ArrayList<String> contactMethods = contactsList.get(i).getNumber();
                contactMethods.addAll(contactsList.get(i).getEmail());
                CharSequence[] cM = new CharSequence[contactMethods.size()];
                cM = contactMethods.toArray(cM);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Select Contact Method");
                builder.setItems(cM, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ParseUser invitee;
                        String contactInfo = contactMethods.get(i);

                        if(isEmail(contactInfo)) {
                            if( ( invitee = isAccount(contactInfo ) ) != null) {
//                                createGroupInvite(invitee);  //TO:DO - ADD IN THE GROUP, SHOULD BE SENT WHEN GROUP PICKED FROM LIST
                                addToGroup(invitee);
                            }
                            else {
                                sendAppInviteEmail(contactInfo);
                            }
                        }
                        else {
                            sendAppInviteText(contactInfo);
                        }
                    }
                });
                builder.show();
            }
        });


        return rootView;
    }

    public void addToGroup(ParseUser invitee) {
//        ParseObject post = ...;

//        ParseUser user = ParseUser.getCurrentUser();
//        ParseRelation relation = user.getRelation("posts");
//        relation.add(post);
//        user.saveInBackground();
        ParseRelation relation = group.getRelation("users");
        relation.add(invitee);
        group.saveInBackground();

    }

    public void createGroupInvite(ParseUser invitee) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseObject invitation = new ParseObject("Invitation");

        invitation.put("OriginatingUser", currentUser);
        invitation.put("Invitee", invitee);
//        invitation.put("Group", null);

        invitation.saveInBackground(new SaveCallback()  {
            public void done(ParseException e) {
                if( e == null ) {
                    Log.d("PAYBACK:CONTACTSFRAG: ", "Created a new invitation");
                    Toast.makeText(getActivity(), "Invite sent!", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("PAYBACK:CONTACTSFRAG: ", "Failed to create invitation w/ error " + e.toString());
                }
            }
        });
    }

    public void sendAppInviteEmail(String email) {
        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);

        emailIntent.setType("plain/text");
        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{email});
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Your friend wants you to come join Payback!");
        emailIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml("<h1><small>Hello!</small></h1>\n" +
                "\n" +
                "<p>You&#39;ve been requested to come join a group in Payback and simplify your group finances! It doesn't look like you're a user, so go ahead and download the app in the Play Store here:&nbsp;</p>"));

        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseObject invitation = new ParseObject("Invitation");

        invitation.put("OriginatingUser", currentUser);
        invitation.put("InviteeEmail", email);
//        invitation.put("Group", null);

        invitation.saveInBackground(new SaveCallback()  {
            public void done(ParseException e) {
                if( e == null ) {
                    Log.d("PAYBACK:CONTACTSFRAG: ", "Created a new invitation");
//                    Toast.makeText(getActivity(), "Invite sent!", Toast.LENGTH_LONG).show();
                } else {
                    Log.d("PAYBACK:CONTACTSFRAG: ", "Failed to create invitation w/ error " + e.toString());
                }
            }
        });

        startActivity(Intent.createChooser(emailIntent, "Send mail..."));
    }

    public boolean isEmail(String val) {
        if(val.contains("@") && val.contains(".")) {
            return true;
        }
        return false;
    }

    public ParseUser isAccount(String email) {
        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username", email);
        List<ParseUser> foundUser;
        try {
            foundUser = query.find();
        } catch(Exception e) {
            foundUser = new ArrayList<ParseUser>();
            Log.d("PAYBACK:CONTACTSFRAG: ", "Failed in getting users with matching email");
        }
        if(!foundUser.isEmpty() ) {
            return foundUser.get(0);
        } else {
            return null;
        }

    }

    public void sendAppInviteText(String num) {
        ParseUser currentUser = ParseUser.getCurrentUser();

        Intent sendIntent = new Intent(Intent.ACTION_VIEW);
        sendIntent.setData(Uri.parse("sms:" + num));
        sendIntent.putExtra("sms_body", "Hello! The fine citizen behind " + currentUser.getUsername() + " has requested to add you to a group in Payback! It's a handy app to help simplify group finances! Come check it out and accept your invite at the Play Store here: ");
        startActivity(sendIntent);

    }

    public void asyncResult(ArrayList<Contact> aL) {
        contactsList = aL;
    }
}



class LoadContactsAsync extends AsyncTask<Void, Void, ArrayList<Contact>> {
    ProgressDialog progressDialog;
    Activity mActivity;
    ListView contactsList;
    ContactsFragment cf;

    public LoadContactsAsync(Activity activity, ListView contactsList, ContactsFragment cf ) {
        mActivity = activity;
        this.contactsList = contactsList;
        progressDialog = new ProgressDialog( mActivity );
        this.cf = cf;
    }

    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Loading Contacts");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();
    }

    protected ArrayList<Contact> doInBackground(Void... params) {
        ArrayList<Contact> contacts = new ArrayList<Contact>();

        Cursor c = mActivity.getApplicationContext().getContentResolver().query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                null, null, null);

        while(c.moveToNext()) {
            Uri thumbnailURI;
            boolean contactExists = false;

            String contactName = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            for( Contact con : contacts ) {
                if (contactName.equals(con.getName())) {
                    con.setNumber(phoneNumber);
                    contactExists = true;
                }
            }
            if(!contactExists) {
                try {
                    thumbnailURI = Uri.parse(c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI)));
                } catch(Exception exception) {
                    thumbnailURI = null;
                }
                contacts.add(new Contact(contactName, phoneNumber, thumbnailURI));
            }
        }
        c.close();

        Cursor e = mActivity.getApplicationContext().getContentResolver().query(
                ContactsContract.CommonDataKinds.Email.CONTENT_URI, null,
                null, null, null);

        while(e.moveToNext()) {
            boolean contactExists = false;

            String contactName = e.getString(e.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String emailAddress = e.getString(e.getColumnIndex(ContactsContract.CommonDataKinds.Email.ADDRESS));

            for( Contact con : contacts ) {
                if (contactName.equals(con.getName())) {
                    con.setNumber(emailAddress);
                    contactExists = true;
                }
            }
            if(!contactExists) {
                contacts.add(new Contact(contactName, emailAddress));
            }
        }
        c.close();

        Collections.sort(contacts, new Comparator<Contact>() {
            public int compare(Contact c1, Contact c2) {
                return c1.getName().compareTo(c2.getName());
            }
        });

        return contacts;
    }

    protected void onPostExecute(ArrayList<Contact> contacts) {
        super.onPostExecute(contacts);
        cf.asyncResult(contacts);
        Contact[] contactArray = new Contact[contacts.size()];
        contactArray = contacts.toArray(contactArray);
        ContactsAdapter customAdapter = new ContactsAdapter( mActivity, R.layout.item_contact, contactArray);
        contactsList.setAdapter(customAdapter);
        progressDialog.cancel();

    }
}
