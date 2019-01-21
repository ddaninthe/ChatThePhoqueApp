package com.example.chatthephoqueapp;

import android.support.annotation.Nullable;

public class Contact {
    private final String key;
    private final String id, name, phoneNumber;

    Contact(String id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        // Remove spaces
        this.phoneNumber = phoneNumber.replaceAll("\\s", "");
        key = this.phoneNumber.substring(this.phoneNumber.length() > 9 ? this.phoneNumber.length() - 9 : 0);
    }

    public String getName() {
        return name;
    }

    String getPhoneNumber() {
        return phoneNumber;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof Contact)) {
            return false;
        } else {
            return key.equals(((Contact) obj).key);
        }
    }


}
