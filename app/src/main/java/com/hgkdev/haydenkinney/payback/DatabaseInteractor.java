package com.hgkdev.haydenkinney.payback;

import com.parse.ParseObject;
import com.parse.ParseUser;


/**
 * Created by HaydenKinney on 1/3/15.
 */
public class DatabaseInteractor {

    double sum;
    boolean downloadTransactionsComplete;

    public DatabaseInteractor() {

    }

    public void AddTransactionObject(String name, Double cost, ParseObject group, ParseUser user ) {
        ParseObject addTransactionObject = new ParseObject("Transaction");
        addTransactionObject.put("Name", name);
        addTransactionObject.put("Cost", cost);
        addTransactionObject.put("Group", group);
        addTransactionObject.put("Payer", user);
        addTransactionObject.put("userCount", group.getNumber("userCount").intValue());
        addTransactionObject.saveInBackground();
    }


}
