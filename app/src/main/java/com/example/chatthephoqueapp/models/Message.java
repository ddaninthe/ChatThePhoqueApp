package com.example.chatthephoqueapp.models;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message implements ObjectDb {
    @Exclude
    private final static String DB_REF = "messages";

    private String content;
    private Date date;
    private boolean received; // True if user is not the sender
    private String conversationKey;

    @Exclude
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM HH:mm", Locale.FRENCH);

    // Firebase Need
    @SuppressWarnings("unused")
    public Message() { }

    public Message(String conversationKey, String content, Date date, boolean received) {
        this.content = content;
        this.date = date;
        this.received = received;
    }

    @Exclude
    public String getTime() {
        // TODO: display hours if today
        return DATE_FORMAT.format(date);
    }

    public Date getDate() {
        return date;
    }

    public boolean isReceived() {
        return received;
    }

    public String getConversationKey() {
        return conversationKey;
    }

    public String getContent() {
        return content;
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
