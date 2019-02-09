package com.example.chatthephoqueapp;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.example.chatthephoqueapp.models.Message;
import com.example.chatthephoqueapp.models.ObjectDb;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MessageFragment extends Fragment {
    private static final String TAG = MessageFragment.class.getName();
    private static final String ARG_CONVERSATION_ID = "arg_conversation_id_key";

    private ArrayList<Message> mMessages;
    private ProgressBar mProgressBar;
    private RecyclerView mRecyclerView;

    private Query mGetMessagesQuery;
    private MessageRecyclerViewAdapter mAdapter;
    private ValueEventListener mValueEventListener;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public MessageFragment() {
    }

    public static MessageFragment newInstance(String conversationId) {
        MessageFragment fragment = new MessageFragment();
        Bundle args = new Bundle();
        args.putString(ARG_CONVERSATION_ID, conversationId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String userKey = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(ObjectDb.PREF_USER_PHONE, null);
        Bundle args = getArguments();
        if (userKey == null || args == null) {
            throw new IllegalArgumentException("Missing data");
        }

        String conversationId = args.getString(ARG_CONVERSATION_ID);

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(userKey);
        mMessages = new ArrayList<>();

        // Query
        mGetMessagesQuery = databaseReference.child(Message.DB_REF).orderByChild("conversationKey").equalTo(conversationId);

        // Add listener
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    String key = messageSnapshot.getKey();
                    assert key != null;
                    if (!contains(key, mMessages)) { // Avoid duplicates
                        Message message = messageSnapshot.getValue(Message.class);
                        message.setKey(key);

                        mMessages.add(message);
                    }
                }

                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                }

                mAdapter.notifyDataSetChanged();
                if (mMessages.size() > 0) {
                    mRecyclerView.smoothScrollToPosition(mMessages.size() - 1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "loadMessages cancelled", databaseError.toException());
            }
        };

        mGetMessagesQuery.addValueEventListener(mValueEventListener);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message_list, container, false);

        mProgressBar = view.findViewById(R.id.progressMessage);

        // Set the adapter
        mRecyclerView = view.findViewById(R.id.messageList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new MessageRecyclerViewAdapter(mMessages);
        mRecyclerView.setAdapter(mAdapter);
        return view;
    }

    /**
     * Returns true if a Message is already in an ArrayList
     * @param key  The Message key to check
     * @param messages  The List to iterate
     * @return  {@code true} if a Message in the messages has the key parameter, {@code false} otherwise.
     */
    private static boolean contains(@NonNull String key, ArrayList<Message> messages) {
        for (Message m : messages) {
            if (key.equals(m.getKey())) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGetMessagesQuery.removeEventListener(mValueEventListener);
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
        void onListFragmentInteraction(Message message);
    }
}
