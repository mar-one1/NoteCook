package com.example.notecook.ViewModel;


import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.notecook.Model.ChatMessage;
import com.example.notecook.Repo.Chat_Repository;

import java.util.List;

public class ChatViewModel extends ViewModel  implements ViewModelProvider.Factory{

    private Chat_Repository chatRepository;
    private LiveData<List<ChatMessage>> messages;
    private Context context;
    private Activity appCompatActivity;

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ChatViewModel.class)) {
            return (T) new ChatViewModel(context, appCompatActivity);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

    public ChatViewModel(Context context, Activity appCompatActivity) {
        this.context = context;
        this.appCompatActivity = appCompatActivity;
        chatRepository = new Chat_Repository(context,appCompatActivity);
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
