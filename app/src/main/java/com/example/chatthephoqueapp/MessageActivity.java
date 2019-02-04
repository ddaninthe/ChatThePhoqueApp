package com.example.chatthephoqueapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

public class MessageActivity extends AppCompatActivity {
    static final String EXTRA_CONVERSATION_ID = "intent.extra.CONVERSATION_ID";
    static final String EXTRA_CONTACT_ID = "intent.extra.CONTACT_ID";

    private final static String FRAGMENT_TAG = "message_fragment";

    private static final String[] PROJECTION = {
            ContactsContract.Contacts.DISPLAY_NAME
    };

    private static final String SELECTION =  ContactsContract.Contacts._ID + " = ?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String conversationId = intent.getStringExtra(EXTRA_CONVERSATION_ID);
        int contactId = intent.getIntExtra(EXTRA_CONTACT_ID, -1);

        if (conversationId == null || contactId == -1) {
            finish();
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
