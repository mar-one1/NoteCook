package com.example.notecook.ViewModel;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.notecook.Model.ChatMessage;
import com.example.notecook.Repo.Chat_Repository;

import java.util.List;

public class ChatViewModel extends ViewModel {

    private Chat_Repository chatRepository;
    private LiveData<List<ChatMessage>> messages;

    public ChatViewModel() {
        chatRepository = new Chat_Repository();
        messages = chatRepository.getMessages();
    }

    public LiveData<List<ChatMessage>> getMessages() {
        return chatRepository.getMessages();
    }

    public void sendMessage(String recipeId, String receiverId, String message) {
        chatRepository.sendMessage(recipeId, receiverId, message);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        chatRepository.disconnectSocket();
    }
}
