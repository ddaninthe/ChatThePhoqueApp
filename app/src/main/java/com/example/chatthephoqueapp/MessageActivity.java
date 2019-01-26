package com.example.chatthephoqueapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MessageActivity extends AppCompatActivity {
    static final String EXTRA_CONVERSATION_ID = "intent.extra.CONVERSATION_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        String conversationId = getIntent().getStringExtra(EXTRA_CONVERSATION_ID);




    }
}
