package com.example.chatthephoqueapp.models;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;

public class Conversation implements ObjectDb {
    @Exclude
    public final static String DB_REF = "conversations";
    @Exclude
    private String key;

    private Contact contact;
    public static Message lastMessage;


    // Firebase need
    @SuppressWarnings("unused")
    public Conversation() { }

    public Conversation(String key, String message, int contactId, String contactName) {
        this.key = key;
        lastMessage = new Message(key, message, new Date(), true);
        this.contact = new Contact(contactId, contactName);
    }

    public Contact getContact() {
        return contact;
    }

    public Message getLastMessage() {
        return lastMessage;
    }

    @Exclude
    public String getLastTime() {
        return lastMessage.getTime();
    }

    @Exclude
    public String getKey() {
        return key;
    }

    public void setLastMessage(Message lastMessage) {
        this.lastMessage = lastMessage;
    }

    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Adds itself to the Firebase Database
     * @param database  a {@link DatabaseReference} at the node of the current UserKey
     */
    @Override
    public void addToFirebase(DatabaseReference database) {
        database.child(DB_REF).push();
    }

    /**
     * Deletes the ObjectDb from Firebase and Deletes all messages associated
     *
     * @param databaseReference a {@link DatabaseReference} at the node of the current UserKey
     */
    @Override
    public void deleteFromFirebase(DatabaseReference databaseReference) {
        databaseReference.child(DB_REF).child(key).removeValue();

        Query deleteMessages = databaseReference.child(Message.DB_REF).orderByChild("conversationKey").equalTo(key);
        deleteMessages.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    messageSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("Error on conversation delete: " + databaseError.getMessage());
            }
        });
    }
}
