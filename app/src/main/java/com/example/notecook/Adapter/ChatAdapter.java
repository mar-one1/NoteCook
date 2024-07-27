package com.example.notecook.Adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        // Align message based on sender/receiver
        if (message.getSenderId() == currentUserID) {
            // Align right
            holder.ll_message.setGravity(Gravity.END);
            holder.image_message.setBackgroundResource(R.drawable.ic_tips_and_updates_fill0_wght400_grad0_opsz48);
        } else {
            // Align left
            holder.ll_message.setGravity(Gravity.START);
            holder.image_message.setBackgroundResource(R.drawable.ic_tips_and_updates_fill0_wght400_grad0_opsz48_red);
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
        LinearLayout ll_message;
        ImageView image_message;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.messageText);
            image_message = itemView.findViewById(R.id.image_message);
            timestampText = itemView.findViewById(R.id.timestampText);
            ll_message = itemView.findViewById(R.id.ll_message);
        }
    }
}
