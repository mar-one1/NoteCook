package com.example.notecook.Fragement;


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

import com.example.notecook.Adapter.Adapter_RC_Chat;
import com.example.notecook.Model.ChatMessage;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.ViewModel.SharedRecipeViewModel;
import com.example.notecook.Utils.SocketManager;
import com.example.notecook.ViewModel.ChatViewModel;

import java.util.ArrayList;
import java.util.List;

public class Frg_chat extends Fragment {

    private RecyclerView messagesRecyclerView;
    private Adapter_RC_Chat adapterRCChat;
    private List<ChatMessage> messages;
    private int currentUserID;
    private ChatViewModel chatViewModel;
    private EditText messageInput;
    private Button sendButton;
    private SocketManager socketManager;
    private SharedRecipeViewModel viewModel;



    private void updateMessagesInView(List<ChatMessage> messages) {
        adapterRCChat.setMessages(messages);
        adapterRCChat.notifyDataSetChanged();
        scrollToBottom();

        for (ChatMessage msg : messages) {
            if (msg.getReceiverId() == currentUserID) {
                socketManager.sendMessageRead(msg.getId(), msg.getSenderId());
            }
        }
    }

    private void sendMessage() {
        String message = messageInput.getText().toString().trim();
        if (!message.isEmpty()) {
            chatViewModel.sendMessage(
                    String.valueOf(viewModel.getCurrentRecipe().getValue().getId_recipe()),
                    String.valueOf(viewModel.getUserCurrentRecipe().getValue().getId_User()),
                    message
            );
            messageInput.setText("");

            chatViewModel.getMessageByRecipeId(viewModel.getCurrentRecipe().getValue().getId_recipe(), viewModel.getCurrentRecipe().getValue().getFrk_user())
                    .observe(getViewLifecycleOwner(), new Observer<List<ChatMessage>>() {
                        @Override
                        public void onChanged(List<ChatMessage> chatMessages) {
                            updateMessagesInView(chatMessages);
                        }
                    });
        } else {
            Toast.makeText(getContext(), "Message cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    private void scrollToBottom() {
        messagesRecyclerView.scrollToPosition(adapterRCChat.getItemCount() - 1);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(SharedRecipeViewModel.class);
        currentUserID = viewModel.getUserLogin().getValue().getUser().getId_User();
        chatViewModel = new ViewModelProvider(this, new ChatViewModel(getContext(), getActivity(),viewModel)).get(ChatViewModel.class);

        messages = new ArrayList<>();
        adapterRCChat = new Adapter_RC_Chat(getContext(), messages, currentUserID);

        chatViewModel.getMessageByRecipeId(viewModel.getCurrentRecipe().getValue().getId_recipe(), viewModel.getCurrentRecipe().getValue().getFrk_user())
                .observe(this, newMessages -> {
                    messages.clear();
                    messages.addAll(newMessages); 
                    updateMessagesInView(messages);
                    scrollToBottom();
                });
        MainFragment.flBtn.hide();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_chat, container, false);

        messagesRecyclerView = rootView.findViewById(R.id.messages_view);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        messagesRecyclerView.setAdapter(adapterRCChat);

        ViewPager2 Vp2 = getActivity().findViewById(R.id.vp2);
        Constants.navAction((AppCompatActivity) getActivity(), Frg_chat.this, Vp2);

        socketManager = new SocketManager(chatMessage -> {
            getActivity().runOnUiThread(() -> {
                chatViewModel.addMessage(chatMessage);
                updateMessagesInView(chatViewModel.getMessages().getValue());
            });
        },viewModel);
        socketManager.connect();

        messageInput = rootView.findViewById(R.id.message_input);
        sendButton = rootView.findViewById(R.id.send_button);
        sendButton.setOnClickListener(v -> sendMessage());

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (socketManager != null) {
            socketManager.disconnect();
        }
    }
}
