package com.hgkdev.haydenkinney.payback;

import com.parse.ParseObject;

/**
 * Created by HaydenKinney on 3/10/15.
 */
public class Transaction {
    private String description;
    private double cost;
    private boolean owed;
    private double value;
    private String comment;
    private ParseObject group;

    public void setDescription(String d) {
        description = d;
    }

    public void setCost(double c) {
        cost = c;
    }

    public void setOwed(boolean o) {
        owed = o;
    }

    public void setValue(double v) {
        value = v;
    }

    public void setComment(String c) {
        comment = c;
    }

    public void setGroup(ParseObject g) {
        group = g;
    }

    public String getDescription() {
        return description;
    }

    public String getComment() {
        return comment;
    }

    public boolean getOwed() {
        return owed;
    }

    public double getCost() {
        return cost;
    }

    public double getValue() {
        return value;
    }

    public ParseObject getGroup() {
        return group;
    }
}


