package com.example.chatthephoqueapp;

import java.util.ArrayList;

public class Conversation {
    private final int id;
    private final ArrayList<Message> messages;

    private final String contact, lastHour, lastMessage;

    // TODO: get from BD
    Conversation(String contact, String hour, String lastMessage) {
        this.contact = contact;
        messages = new ArrayList<>();
        id = 1;
        this.lastMessage = lastMessage;
        lastHour = hour;
    }

    public String getContact() {
        return contact;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public String getLastHour() {

        return lastHour;
    }
}
