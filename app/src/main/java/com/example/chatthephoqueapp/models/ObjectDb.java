package com.example.chatthephoqueapp.models;

import com.google.firebase.database.DatabaseReference;

public interface ObjectDb {
    String PREF_USER_PHONE = "pref_user_phone";

    /**
     * Adds itself to the Firebase Database
     * @param database  a {@link DatabaseReference} at the node of the current UserKey
     */
    void addToFirebase(DatabaseReference database);

    /**
     * Deletes the ObjectDb from Firebase
     * @param databaseReference  a {@link DatabaseReference} at the node of the current UserKey
     */
    void deleteFromFirebase(DatabaseReference databaseReference);
}
