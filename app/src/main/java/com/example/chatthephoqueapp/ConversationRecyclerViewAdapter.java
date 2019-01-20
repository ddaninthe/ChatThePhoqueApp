package com.example.chatthephoqueapp;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chatthephoqueapp.ConversationFragment.OnListFragmentInteractionListener;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Conversation} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ConversationRecyclerViewAdapter extends RecyclerView.Adapter<ConversationRecyclerViewAdapter.ViewHolder> {

    private final List<Conversation> mValues;
    private final OnListFragmentInteractionListener mListener;

    public ConversationRecyclerViewAdapter(List<Conversation> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Conversation conversation = mValues.get(position);
        holder.mItem = conversation;
        holder.mContact.setText(conversation.getContact());
        holder.mHour.setText(conversation.getLastHour());
        holder.mLastMessage.setText(conversation.getLastMessage());

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mContact;
        public final TextView mHour;
        public final TextView mLastMessage;
        public Conversation mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mContact = view.findViewById(R.id.textContactName);
            mHour = view.findViewById(R.id.textMessageHour);
            mLastMessage = view.findViewById(R.id.textLastMessage);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mLastMessage.getText() + "'";
        }
    }
}
