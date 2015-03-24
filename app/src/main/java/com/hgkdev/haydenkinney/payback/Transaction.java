package com.hgkdev.haydenkinney.payback;

import com.parse.ParseObject;
import java.util.Date;
/**
 * Created by HaydenKinney on 3/10/15.
 */
public class Transaction {
    private String description;
    private double cost;
    private boolean owed;
    private String comment;
    private ParseObject group;
    private Date date;
    private int userCount;

    public Transaction( String de, double cos, boolean o, String com, ParseObject g, Date da, int uC ) {
        description = de;
        cost = cos;
        owed = o;
        comment = com;
        group = g;
        date = da;
        userCount = uC;
    }

    public void setDescription(String d) {
        description = d;
    }

    public void setCost(double c) {
        cost = c;
    }

    public void setOwed(boolean o) {
        owed = o;
    }

    public void setComment(String c) {
        comment = c;
    }

    public void setGroup(ParseObject g) {
        group = g;
    }

    public void setDate(Date d) {
        date = d;
    }

    public void setUserCount(int i) { userCount = i; }
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

    public ParseObject getGroup() {
        return group;
    }

    public Date getDate() { return date; }

    public int getUserCount() { return userCount; }
}


