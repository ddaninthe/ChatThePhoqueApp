package com.example.chatthephoqueapp;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chatthephoqueapp.models.Message;

import java.util.List;

public class MessageRecyclerViewAdapter extends RecyclerView.Adapter<MessageRecyclerViewAdapter.ViewHolder> {

    private final List<Message> mValues;
    private final int sentColor, receivedColor;

    MessageRecyclerViewAdapter(List<Message> items, int colorSent, int colorReceived) {
        mValues = items;
        sentColor = colorSent;
        receivedColor = colorReceived;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_message_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        Message message = mValues.get(position);
        holder.mContent.setText(message.getContent());
        holder.mHour.setText(message.getHour());


        if (message.isReceived()) {
            holder.mViewRight.setVisibility(View.INVISIBLE);
            holder.mContent.setBackgroundColor(receivedColor);
        } else {
            holder.mViewLeft.setVisibility(View.INVISIBLE);
            holder.mContent.setBackgroundColor(sentColor);
        }
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final View mView;
        private final View mViewLeft, mViewRight;
        private final TextView mContent;
        private final TextView mHour;

        private ViewHolder(View view) {
            super(view);
            mView = view;
            mContent = view.findViewById(R.id.textMessageContent);
            mHour = view.findViewById(R.id.textMessageHour);
            mViewLeft = view.findViewById(R.id.viewMessageLeft);
            mViewRight = view.findViewById(R.id.viewMessageRight);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mContent.getText() + "'";
        }
    }
}
