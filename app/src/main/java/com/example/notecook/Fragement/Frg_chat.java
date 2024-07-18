package com.example.notecook.Fragement;

import static com.example.notecook.Api.ApiClient.BASE_URL;
import static com.example.notecook.Utils.Constants.CURRENT_RECIPE;
import static com.example.notecook.Utils.Constants.User_CurrentRecipe;
import static com.example.notecook.Utils.Constants.user_login;
import static org.chromium.base.ThreadUtils.runOnUiThread;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Adapter.ChatAdapter;
import com.example.notecook.Model.ChatMessage;
import com.example.notecook.R;
import com.example.notecook.ViewModel.ChatViewModel;
import com.example.notecook.databinding.FragmentChatBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;


public class Frg_chat extends Fragment {

    private RecyclerView messagesRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messages;
    private int currentUserID = user_login.getUser().getId_User(); // Replace with actual user ID
    private ChatViewModel chatViewModel;
    private EditText messageInput;
    private Button sendButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize ViewModel
        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        // Initialize messages list and adapter
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(getContext(), messages, currentUserID);

        // Observe messages LiveData
        chatViewModel.getMessages().observe(this, newMessages -> {
            // Update UI with new messages
            messages.clear();
            messages.addAll(newMessages);
            chatAdapter.notifyDataSetChanged();
            scrollToBottom();
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        messagesRecyclerView = rootView.findViewById(R.id.messages_view);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messagesRecyclerView.setAdapter(chatAdapter);

        messageInput = rootView.findViewById(R.id.message_input);
        sendButton = rootView.findViewById(R.id.send_button);

        sendButton.setOnClickListener(v -> sendMessage());

        return rootView;
    }
    // Method to send message
    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (!message.isEmpty()) {
            // Replace with actual recipeId and receiverId
            chatViewModel.sendMessage(String.valueOf(CURRENT_RECIPE.getId_recipe()),String.valueOf(User_CurrentRecipe.getId_User()), message);
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

