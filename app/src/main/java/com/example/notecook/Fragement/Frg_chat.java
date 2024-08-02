package com.example.notecook.Fragement;

import static com.example.notecook.Utils.Constants.CURRENT_RECIPE;
import static com.example.notecook.Utils.Constants.User_CurrentRecipe;
import static com.example.notecook.Utils.Constants.user_login;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Adapter.ChatAdapter;
import com.example.notecook.Model.ChatMessage;
import com.example.notecook.R;
import com.example.notecook.Utils.SocketManager;
import com.example.notecook.ViewModel.ChatViewModel;

import java.util.ArrayList;
import java.util.List;


public class Frg_chat extends Fragment {

    private RecyclerView messagesRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages;
    private int currentUserID = user_login.getUser().getId_User(); // Replace with actual user ID
    private ChatViewModel chatViewModel;
    private EditText messageInput;
    private Button sendButton;
    private SocketManager socketManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewModel
        ChatViewModel chatVM = new ChatViewModel(getContext(), getActivity());
        chatViewModel = new ViewModelProvider(this, chatVM).get(ChatViewModel.class);
        // Initialize messages list and adapter
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(getContext(), messages, currentUserID);

        // Observe messages LiveData
        chatViewModel.getMessageByRecipeId(CURRENT_RECIPE.getId_recipe()).observe(this, newMessages -> {
            // Update UI with new messages
            messages.clear();
            messages.addAll(newMessages);
            chatAdapter.notifyDataSetChanged();
            scrollToBottom();
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Disconnect the socket when the fragment's view is destroyed
        if (socketManager != null) {
            socketManager.disconnect();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        messagesRecyclerView = rootView.findViewById(R.id.messages_view);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messagesRecyclerView.setAdapter(chatAdapter);
        // Initialize SocketManager
        // Initialize SocketManager with a callback to update ViewModel
        socketManager = new SocketManager(new SocketManager.SocketCallback() {
            @Override
            public void onNewMessage(ChatMessage chatMessage) {
                chatViewModel.addMessage(chatMessage);
            }
        });
        socketManager.connect();
        messageInput = rootView.findViewById(R.id.message_input);
        sendButton = rootView.findViewById(R.id.send_button);
        String message = messageInput.getText().toString().trim();
        sendButton.setOnClickListener(v -> sendMessage());

        return rootView;
    }

    // Method to send message
    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (!message.isEmpty()) {
            // Replace with actual recipeId and receiverId
            chatViewModel.sendMessage(String.valueOf(CURRENT_RECIPE.getId_recipe()), String.valueOf(User_CurrentRecipe.getId_User()), message);
            messageInput.setText("");
            // Observe LiveData for messages
            chatViewModel.getMessages().observe(getViewLifecycleOwner(), new Observer<List<ChatMessage>>() {
                @Override
                public void onChanged(List<ChatMessage> chatMessages) {
                    // Update UI with new messages
                    updateMessagesInView(chatMessages);
                }
            });
        } else {
            Toast.makeText(getContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    // Example method to update RecyclerView with new messages
    private void updateMessagesInView(List<ChatMessage> messages) {
        chatAdapter.setMessages(messages);
        chatAdapter.notifyDataSetChanged();
        scrollToBottom();
    }


    // Scroll to the bottom of RecyclerView
    private void scrollToBottom() {
        messagesRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
    }
}

