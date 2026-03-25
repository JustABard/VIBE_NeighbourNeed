package com.example.shopassist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopassist.R;
import com.example.shopassist.models.Message;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {

    private final ArrayList<Message> messages;
    private final String currentUserEmail;

    public MessageAdapter(ArrayList<Message> messages, String currentUserEmail) {
        this.messages = messages;
        this.currentUserEmail = currentUserEmail;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        boolean isMine = message.getSenderId().equalsIgnoreCase(currentUserEmail);

        holder.tvMessageSender.setText(isMine ? "You" : message.getSenderId());
        holder.tvMessageText.setText(message.getText());
        holder.tvMessageTime.setText(message.getTime());
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessageSender;
        TextView tvMessageText;
        TextView tvMessageTime;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessageSender = itemView.findViewById(R.id.tvMessageSender);
            tvMessageText = itemView.findViewById(R.id.tvMessageText);
            tvMessageTime = itemView.findViewById(R.id.tvMessageTime);
        }
    }
}

