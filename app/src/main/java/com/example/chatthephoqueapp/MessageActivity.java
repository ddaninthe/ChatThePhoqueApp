package com.example.chatthephoqueapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.chatthephoqueapp.models.Conversation;
import com.example.chatthephoqueapp.models.Message;
import com.example.chatthephoqueapp.models.ObjectDb;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;

public class MessageActivity extends AppCompatActivity {
    static final String EXTRA_CONVERSATION_ID = "intent.extra.CONVERSATION_ID";
    static final String EXTRA_CONTACT_ID = "intent.extra.CONTACT_ID";

    private final static String FRAGMENT_TAG = "message_fragment";

    private static final String[] PROJECTION = {
            ContactsContract.Contacts.DISPLAY_NAME
    };

    private static final String SELECTION =  ContactsContract.Contacts._ID + " = ?";

    private static  String userKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        final String conversationId = intent.getStringExtra(EXTRA_CONVERSATION_ID);
        int contactId = intent.getIntExtra(EXTRA_CONTACT_ID, -1);

        userKey = PreferenceManager.getDefaultSharedPreferences(this).getString(ObjectDb.PREF_USER_PHONE, null);
        if (userKey == null || conversationId == null || contactId == -1) {
            throw new IllegalArgumentException("User Phone cannot be null");
        }

        // Display contactName as title
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                PROJECTION, SELECTION, new String[] { Integer.toString(contactId) }, null);
        if (cur != null) {
            if (cur.moveToFirst()) {
                String name = cur.getString(0);
                setTitle(name);
            }
            cur.close();
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_holder,
                    MessageFragment.newInstance(conversationId), FRAGMENT_TAG)
                    .commit();
        }

        final ImageButton sendButton = findViewById(R.id.btnSend);
        final EditText editText = findViewById(R.id.editMessage);
        editText.requestFocus();

        final DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(userKey);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    sendButton.performClick();
                    return true;
                }
                return false;
            }
        });

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sendingMessage = editText.getText().toString();
                if (sendingMessage.length() > 0) {
                    Message message = new Message(conversationId, sendingMessage, new Date(), false);
                    FirebaseMessaging fm = FirebaseMessaging.getInstance();
                    fm.send(new RemoteMessage.Builder(userKey + "@gcm.googleapis.com")
                            .addData("message", sendingMessage)
                            .build());

                    // Save to Database
                    // Add new Message
                    DatabaseReference newRef = databaseReference.child(Message.DB_REF).push();
                    newRef.setValue(message);

                    // Update conversation's lastMessage
                    databaseReference.child(Conversation.DB_REF).child(conversationId).child("lastMessage").setValue(message);

                    // clear EditText
                    editText.getText().clear();

                    //Send Notification
                    newRef
                            .child("notifications")
                            .child("messages")
                            .push()
                            .setValue(message);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
