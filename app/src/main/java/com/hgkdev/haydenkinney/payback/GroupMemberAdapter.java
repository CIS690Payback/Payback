package com.hgkdev.haydenkinney.payback;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by H on 2/15/2015.
 */
public class GroupMemberAdapter extends ArrayAdapter<Contact> {
    Context context;
    int layoutResourceId;
    Contact data[] = null;

    public GroupMemberAdapter(Context context, int layoutResourceId, Contact[] data) {

        super(context, layoutResourceId, data);
        Log.d("PAYBACK:GMEMADAPTER: ", "Entered constructor of ContactsAdapter");

        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;

        Log.d("PAYBACK:GMEMADAPTER: ", "Exited constructor of ContactsAdapter");

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ContactHolder holder = null;

        Log.d("PAYBACK:GMEMADAPTER: ", "Entered if block of getView of ContactsAdapter");

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new ContactHolder();
            holder.icon = (ImageView)row.findViewById(R.id.imgView_groupMember_icon);
            holder.txtContactInfo = (TextView)row.findViewById(R.id.txtView_groupMember_contact);

            row.setTag(holder);
        } else {
            holder = (ContactHolder)row.getTag();
        }

        Log.d("PAYBACK:GMEMADAPTER: ", "Exited if block of getView of ContactsAdapter");

        Contact contact = data[position];
        Uri thumbnailURI = contact.getIcon();
        String name = contact.getName();
        ArrayList<String> number = contact.getNumber();
        ArrayList<String> email = contact.getEmail();

        try{
            holder.txtContactInfo.setText(number.get(0));
        } catch( Exception e ) {
            holder.txtContactInfo.setText(email.get(0));
        }

        if(thumbnailURI != null) {
            holder.icon.setImageURI(thumbnailURI);
        } else {
            holder.icon.setImageResource(R.drawable.ic_action_person);
        }

        return row;
    }

    static class ContactHolder {
        ImageView icon;
        TextView txtContactInfo;
    }

}
