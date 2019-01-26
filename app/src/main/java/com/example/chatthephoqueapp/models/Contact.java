package com.example.chatthephoqueapp.models;

import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;

public class Contact {
    private String key;
    private int id;

    @Exclude
    private String name;
    @Exclude
    private String phoneNumber;

    // Firebase Must
    public Contact() { }

    public Contact(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Exclude
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getId() {
        return id;
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setPhoneNumber(String phoneNumber) {
        // Remove spaces
        this.phoneNumber = phoneNumber.replaceAll("\\s", "");
        setKey();
    }

    public void setName(String name) {
        this.name = name;
    }

    private void setKey() {
        key = phoneNumber.substring(phoneNumber.length() > 9 ? phoneNumber.length() - 9 : 0);
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Contact)) {
            return false;
        } else {
            return key.equals(((Contact) obj).key);
        }
    }

    public static String fromPhoneToKey(String phoneNumber)  {
        String trim = phoneNumber.replaceAll("\\s", "");
        return trim.substring(trim.length() - 9);
    }
}
