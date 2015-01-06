package com.hgkdev.haydenkinney.payback;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


/**
 * Created by HaydenKinney on 1/3/15.
 */
public class DatabaseInteractor {

    double sum;

    public DatabaseInteractor() {

    }

    public void AddTransactionObject(String name, Double cost ) {
        ParseObject addTransactionObject = new ParseObject("Transaction");
        addTransactionObject.put("Name", name);
        addTransactionObject.put("Cost", cost);
        addTransactionObject.saveInBackground();
    }

    public void RetrieveAndSumAllTransactions() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Transaction");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> transactionList, ParseException e) {
                if ( e == null ) {
                    sum = 0;
                    Log.d( "transaction", "Retrieved " + transactionList.size() + " transactions" );
                    for( int i = 0; i < transactionList.size(); i++ ) {
                        sum += transactionList.get( i ).getDouble( "Cost" );
                    }
                } else {
                    Log.d("transaction", "Error: " + e.getMessage());
                }
            }
        });
    }
}
