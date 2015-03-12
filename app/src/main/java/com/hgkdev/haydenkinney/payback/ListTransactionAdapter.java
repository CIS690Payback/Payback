package com.hgkdev.haydenkinney.payback;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.parse.ParseObject;

/**
 * Created by HaydenKinney on 3/10/15.
 */
public class ListTransactionAdapter extends ArrayAdapter<Transaction> {
    Context context;
    int layoutResourceId;
    Transaction data[] = null;

    public ListTransactionAdapter(Context context, int layoutResourceId, Transaction[] data) {

        super(context, layoutResourceId, data);
        Log.d("PAYBACK:TRANSADAPTER: ", "Entered constructor of Adapter");

        this.context = context;
        this.layoutResourceId = layoutResourceId;
        this.data = data;

        Log.d("PAYBACK:TRANSADAPTER: ", "Exited constructor of Adapter");

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        TransactionHolder holder = null;

        Log.d("PAYBACK:TRANSADAPTER: ", "Entered if block of getView");

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new TransactionHolder();
            holder.txtDescription = (TextView)row.findViewById(R.id.txtView_TransactionListItem_description);
            holder.txtGroupName = (TextView)row.findViewById(R.id.txtView_TransactionListItem_group);
            holder.txtCost = (TextView)row.findViewById(R.id.txtView_TransactionListItem_owedAmount);

            row.setTag(holder);
        } else {
            holder = (TransactionHolder)row.getTag();
        }

        Log.d("PAYBACK:TRANSADAPTER: ", "Exited if block of getView");

        Transaction trans = data[position];
        String description = trans.getDescription();
        ParseObject group = trans.getGroup();
        String groupName = group.getString("groupName");
        String cost = String.valueOf(trans.getCost());
        Boolean owed = trans.getOwed();

        holder.txtDescription.setText(description);
        holder.txtGroupName.setText(groupName);
        holder.txtCost.setText(cost);
        if(owed) {
            holder.txtCost.setTextColor(R.string.owed_color);
        } else {
            holder.txtCost.setTextColor(R.string.owe_color);
        }

        return row;
    }

    static class TransactionHolder {
        TextView txtCost;
        TextView txtDescription;
        TextView txtGroupName;
    }

}
