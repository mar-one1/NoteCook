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

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.notecook.Adapter.ChatAdapter;
import com.example.notecook.Model.ChatMessage;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
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

    // Example method to update RecyclerView with new messages
    private void updateMessagesInView(List<ChatMessage> messages) {
        chatAdapter.setMessages(messages);
        chatAdapter.notifyDataSetChanged();
        scrollToBottom();
    }

        // Method to send message
    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (!message.isEmpty()) {
            chatViewModel.sendMessage(String.valueOf(CURRENT_RECIPE.getId_recipe()), String.valueOf(User_CurrentRecipe.getId_User()), message);
            messageInput.setText("");
            // Observe LiveData for messages
            chatViewModel.getMessageByRecipeId(CURRENT_RECIPE.getId_recipe()).observe(getViewLifecycleOwner(), new Observer<List<ChatMessage>>() {
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

    // Scroll to the bottom of RecyclerView
    private void scrollToBottom() {
        messagesRecyclerView.scrollToPosition(chatAdapter.getItemCount() - 1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        chatViewModel = new ViewModelProvider(this, new ChatViewModel(getContext(), getActivity())).get(ChatViewModel.class);

        // Initialize messages list and adapter
        messages = new ArrayList<>();
        chatAdapter = new ChatAdapter(getContext(), messages, currentUserID);

        // Observe messages LiveData
        chatViewModel.getMessageByRecipeId(CURRENT_RECIPE.getId_recipe()).observe(this, newMessages -> {
            messages.clear();
            messages.addAll(newMessages);
            updateMessagesInView(messages);
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
        ViewPager2 Vp2 = getActivity().findViewById(R.id.vp2);
        Constants.navAction((AppCompatActivity) getActivity(),Frg_chat.this,Vp2);
        // Initialize SocketManager
        socketManager = new SocketManager(chatMessage -> {
            getActivity().runOnUiThread(() -> {
                chatViewModel.addMessage(chatMessage);
                updateMessagesInView(chatViewModel.getMessages().getValue());
                scrollToBottom();
            });
        });
        socketManager.connect();

        messageInput = rootView.findViewById(R.id.message_input);
        sendButton = rootView.findViewById(R.id.send_button);

        sendButton.setOnClickListener(v -> sendMessage());

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Disconnect the socket when the fragment's view is destroyed
        if (socketManager != null) {
            socketManager.disconnect();
        }
    }

}
