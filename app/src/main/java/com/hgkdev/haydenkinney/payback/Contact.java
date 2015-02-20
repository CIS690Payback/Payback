package com.hgkdev.haydenkinney.payback;

import android.net.Uri;

import java.util.ArrayList;

/**
 * Created by H on 2/15/2015.
 */
public class Contact {
    private String name;
    private ArrayList<String> number;
    private ArrayList<String> email;
    private Uri thumbnailURI;

    public Contact(String name, String num, Uri tURI) {
        number = new ArrayList<String>();
        email = new ArrayList<String>();
        this.name = name;
        number.add(num);
        this.thumbnailURI = tURI;
    }

    public Contact(String name, String email) {
        number = new ArrayList<String>();
        this.email = new ArrayList<String>();
        this.name = name;
        this.email.add(email);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getNumber() {
        return number;
    }

    public void setNumber(String num) {
        number.add(num);
    }

    public ArrayList<String> getEmail() {
        return this.email;
    }

    public void setEmail(String email) {
        this.email.add(email);
    }

    public Uri getIcon() {
        return thumbnailURI;
    }
}
