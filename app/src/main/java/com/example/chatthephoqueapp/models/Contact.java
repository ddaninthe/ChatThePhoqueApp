package com.example.chatthephoqueapp.models;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;

import com.google.firebase.database.Exclude;

public class Contact {
    @Exclude
    private static final String[] PROJECTION = {
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };
    @Exclude
    private final static String SELECTION = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";

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

    public static Contact fromIdAndName(Context context, int id, String name) {
        Contact contact = new Contact(id, name);

        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                PROJECTION, SELECTION, new String[] { Integer.toString(id) }, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String phoneNumber = cursor.getString(0);
                contact.setPhoneNumber(phoneNumber);
            }

            cursor.close();
        } else {
            throw new IllegalArgumentException("No contact matching for this id");
        }

        return contact;
    }
}
