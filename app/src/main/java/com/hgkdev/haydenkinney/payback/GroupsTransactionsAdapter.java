package com.hgkdev.haydenkinney.payback;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by HaydenKinney on 3/10/15.
 */
public class GroupsTransactionsAdapter extends ArrayAdapter<Transaction> {
    Context context;
    int layoutResourceId;
    Transaction data[] = null;

    public GroupsTransactionsAdapter(Context context, int layoutResourceId, Transaction[] data) {

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

        Log.d("PB:GRPTRANSADAPTER: ", "Entered if block of getView of GroupsTransactionsAdapter");

        if(row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new TransactionHolder();
            holder.txtDescription = (TextView)row.findViewById(R.id.txtView_GroupTransactionListItem_description);
            holder.txtCost = (TextView)row.findViewById(R.id.txtView_GroupTransactionListItem_owedAmount);

            row.setTag(holder);
        } else {
            holder = (TransactionHolder)row.getTag();
        }

        Log.d("PB:GRPTRANSADAPTER: ", "Exited if block of getView of GroupsTransactionsAdapter");

        Transaction trans = data[position];
        String description = trans.getDescription();
//        String cost = String.valueOf(trans.getCost());
        Boolean owed = trans.getOwed();

        holder.txtDescription.setText(description);
        holder.txtCost.setText(String.format("$%,.2f", trans.getCost()));
        if(owed) {
            holder.txtCost.setTextColor(Color.parseColor("#4CAF50"));
        } else {
            holder.txtCost.setTextColor(Color.parseColor("#D32F2F"));
        }

        return row;
    }

    static class TransactionHolder {
        TextView txtCost;
        TextView txtDescription;
    }

}
