package com.example.chatthephoqueapp;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.chatthephoqueapp.models.Contact;
import com.example.chatthephoqueapp.models.Conversation;
import com.example.chatthephoqueapp.models.ObjectDb;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ContactFragment extends Fragment {

    // An adapter that binds the result Cursor to the ListView
    private ArrayList<Contact> mContacts;
    private OnListFragmentInteractionListener mListener;
    private RecyclerView mRecyclerView;
    private Contact mContactClicked;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ContactFragment() {
    }

    public static ContactFragment newInstance() {
        return new ContactFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get contacts
        mContacts = new ArrayList<>();
        assert getContext() != null;
        ContentResolver cr = getContext().getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, ContactsContract.Contacts.DISPLAY_NAME + " ASC");

        Contact contact;
        if ((cur != null ? cur.getCount() : 0) > 0) {
            Set<String> keys = new HashSet<>();
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if (cur.getInt(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)) > 0) {
                    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                            null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                            new String[]{id}, null);
                    if (pCur != null) {
                        while (pCur.moveToNext()) {
                            String phoneNo = pCur.getString(pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                            contact = new Contact(Integer.parseInt(id), name);
                            contact.setPhoneNumber(phoneNo);

                            // Avoid duplicates
                            if (!keys.contains(contact.getKey())) {
                                mContacts.add(contact);
                                keys.add(contact.getKey());
                            }
                        }
                        pCur.close();
                    }
                }
            }
        }
        if (cur != null) {
            cur.close();
        }

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getActivity().getMenuInflater();
        inflater.inflate(R.menu.contact_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_contact_send_message:
                String userKey = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(ObjectDb.PREF_USER_PHONE, null);
                assert userKey != null;
                // Add Conversation to Database
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference(userKey);
                DatabaseReference newConversation = ref.child(Conversation.DB_REF).push();
                Conversation conversation = new Conversation(newConversation.getKey(),
                        null, mContactClicked.getId(), mContactClicked.getName());
                newConversation.setValue(conversation);

                // Launch MessageActivity
                Intent messageIntent = new Intent(getContext(), MessageActivity.class);
                messageIntent.putExtra(MessageActivity.EXTRA_CONVERSATION_ID, newConversation.getKey());
                messageIntent.putExtra(MessageActivity.EXTRA_CONTACT_ID, mContactClicked.getId());
                startActivity(messageIntent);
                return true;
            case R.id.menu_contact_show:

                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI,
                                Integer.toString(mContactClicked.getId())));
                startActivity(intent);
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            mRecyclerView = (RecyclerView) view;
            registerForContextMenu(mRecyclerView);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            mRecyclerView.setAdapter(new ContactRecyclerViewAdapter(mContacts, mListener));
        }
        return view;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            mListener = new OnListFragmentInteractionListener() {
                @Override
                public void onListFragmentInteraction(View view, Contact contact) {
                    mRecyclerView.showContextMenuForChild(view);
                    mContactClicked = contact;
                }
            };
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(View view, Contact contact);
    }
}
