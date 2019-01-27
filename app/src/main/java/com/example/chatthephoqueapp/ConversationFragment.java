package com.example.chatthephoqueapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.chatthephoqueapp.models.Conversation;
import com.example.chatthephoqueapp.models.ObjectDb;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ConversationFragment extends Fragment {
    static final int EVENT_ON_LONG_CLICK = 18;
    static final int EVENT_ON_CLICK = 12;

    private static final String TAG = ConversationFragment.class.getName();

    private OnListFragmentInteractionListener mListener;
    private Query mGetConversationsQuery;
    private ValueEventListener mValueEventListener;
    private List<Conversation> mConversations;
    private ConversationRecyclerViewAdapter mAdapter;
    private ProgressBar mProgressBar;
    private TextView mTextView;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ConversationFragment() {
    }

    public static ConversationFragment newInstance() {
        return new ConversationFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String userKey = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(ObjectDb.PREF_USER_PHONE, null);
        if (userKey == null) {
            throw new IllegalArgumentException("User phone has not been set");
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference(userKey);
        mConversations = new ArrayList<>();

        // Query
        mGetConversationsQuery = databaseReference.child(Conversation.DB_REF);

        // Add listener
        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot conversationSnapshot : dataSnapshot.getChildren()) {
                    String key = conversationSnapshot.getKey();
                    Conversation conversation = conversationSnapshot.getValue(Conversation.class);
                    conversation.setKey(key);

                    Conversation old = findConversationByKey(conversation.getKey());
                    if (old == null) {
                        mConversations.add(conversation);
                    } else {
                        old.setLastMessage(conversation.getLastMessage());
                    }
                }
                // Sort the list by most recent conversation
                orderConversationByLastMessage(mConversations);

                if (mProgressBar != null) {
                    mProgressBar.setVisibility(View.GONE);
                }

                mTextView.setVisibility(mConversations.size() > 0 ? View.GONE : View.VISIBLE);

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d(TAG, "loadConversations cancelled", databaseError.toException());
            }
        };

        mGetConversationsQuery.addValueEventListener(mValueEventListener);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_conversation_list, container, false);

        mProgressBar = view.findViewById(R.id.progressConversation);
        mTextView = view.findViewById(R.id.textNoConversation);

        // Set the adapter
        RecyclerView recyclerView = view.findViewById(R.id.conversationList);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mAdapter = new ConversationRecyclerViewAdapter(mConversations, mListener);
        recyclerView.setAdapter(mAdapter);
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
                public void onListFragmentInteraction(final Conversation conversation, int eventType) {
                    if (eventType == EVENT_ON_CLICK) {
                        Intent intent = new Intent(getContext(), MessageActivity.class);
                        intent.putExtra(MessageActivity.EXTRA_CONVERSATION_ID, conversation.getKey());
                        startActivity(intent);
                    } else if (eventType == EVENT_ON_LONG_CLICK) {
                        AlertDialog dialog = new AlertDialog.Builder(context)
                                .setTitle(R.string.alert_delete_conversation_title)
                                .setMessage(R.string.alert_delete_conversation_message)
                                .setNegativeButton(android.R.string.no, null)
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String userKey = PreferenceManager.getDefaultSharedPreferences(context).getString(ObjectDb.PREF_USER_PHONE, null);
                                        if (userKey != null) {
                                            DatabaseReference ref = FirebaseDatabase.getInstance().getReference(userKey);
                                            conversation.deleteFromFirebase(ref);
                                        }
                                        mConversations.remove(conversation);
                                    }
                                })
                                .create();
                        dialog.show();
                    }
                }
            };
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mGetConversationsQuery.removeEventListener(mValueEventListener);
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
        void onListFragmentInteraction(Conversation conversation, int eventType);
    }

    @Nullable
    private Conversation findConversationByKey(String id) throws IllegalArgumentException {
        for (int i = 0; i < mConversations.size(); i++) {
            if (id.equals(mConversations.get(i).getKey())) {
                return mConversations.get(i);
            }
        }

        return null;
    }

    /**
     * Orders a list of conversation by the most recent message sent or received
     * @param conversations  a List<{@link Conversation}> to sort
     */
    static void orderConversationByLastMessage(List<Conversation> conversations) {
        Arrays.sort(conversations.toArray(), new Comparator<Object>() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((Conversation) o1).getLastMessage().getDate().compareTo(((Conversation)o2).getLastMessage().getDate());
            }
        });
    }
}
