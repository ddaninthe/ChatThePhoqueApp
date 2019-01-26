package com.example.chatthephoqueapp.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.util.Date;

public class Conversation implements ObjectDb {
    @Exclude
    public final static String DB_REF = "conversations";
    @Exclude
    private String key;

    private Contact contact;
    private Message lastMessage;


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
}
