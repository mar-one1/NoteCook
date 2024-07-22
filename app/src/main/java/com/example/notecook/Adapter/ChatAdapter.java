package com.example.notecook.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Model.ChatMessage;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder> {

    private List<ChatMessage> messages;
    private Context context;
    private int currentUserID;

    public ChatAdapter(Context context, List<ChatMessage> messages, int currentUserID) {
        this.context = context;
        this.messages = messages;
        this.currentUserID = currentUserID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);

        // Set message content
        holder.messageText.setText(message.getMessage());
        // Set message timestamp (you may format it as desired)
        String timestamp = Constants.DateTimeNow(message.getTimestamp());
        holder.timestampText.setText(timestamp);
        // Align message based on sender/receiver (example: right for current user, left for others)
        if (message.getSenderId() == currentUserID) {
            // Align right
            holder.messageText.setBackgroundResource(R.drawable.ic_tips_and_updates_fill0_wght400_grad0_opsz48);
            holder.messageText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);
        } else {
            // Align left
            holder.messageText.setBackgroundResource(R.drawable.ic_tips_and_updates_fill0_wght400_grad0_opsz48_red);
            holder.messageText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged(); // Notify adapter of dataset change
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageText, timestampText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            timestampText = itemView.findViewById(R.id.timestampText);
        }
    }
}
