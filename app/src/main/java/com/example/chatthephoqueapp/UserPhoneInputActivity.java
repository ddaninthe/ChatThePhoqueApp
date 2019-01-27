package com.example.chatthephoqueapp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.chatthephoqueapp.models.Contact;
import com.example.chatthephoqueapp.models.ObjectDb;

public class UserPhoneInputActivity extends AppCompatActivity {
    private static final String PHONE_REGEX = "^((\\+33)|0)[67]\\d{8}$";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_phone_input);

        final EditText editPhone = findViewById(R.id.editPhone);
        final Button btnSavePhone = findViewById(R.id.btnSavePhone);

        editPhone.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    btnSavePhone.performClick();

                    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(editPhone.getApplicationWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        btnSavePhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNumber = editPhone.getText().toString();

                if (phoneNumber.matches(PHONE_REGEX)) {
                    String key = Contact.fromPhoneToKey(phoneNumber);
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(UserPhoneInputActivity.this);
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putString(ObjectDb.PREF_USER_PHONE, key);
                    editor.apply();

                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(UserPhoneInputActivity.this, R.string.phone_bad_number, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
