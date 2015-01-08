package com.hgkdev.haydenkinney.payback;

import com.parse.ParseObject;


/**
 * Created by HaydenKinney on 1/3/15.
 */
public class DatabaseInteractor {

    double sum;
    boolean downloadTransactionsComplete;

    public DatabaseInteractor() {

    }

    public void AddTransactionObject(String name, Double cost ) {
        ParseObject addTransactionObject = new ParseObject("Transaction");
        addTransactionObject.put("Name", name);
        addTransactionObject.put("Cost", cost);
        addTransactionObject.saveInBackground();
    }


}
