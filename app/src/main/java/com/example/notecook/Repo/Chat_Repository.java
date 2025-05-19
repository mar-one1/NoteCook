package com.example.notecook.Repo;

import static com.example.notecook.Utils.Constants.Token;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Model.ChatMessage;
import com.example.notecook.Utils.SocketManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Chat_Repository {

    private SocketManager socketManager;
    private MutableLiveData<List<ChatMessage>> messages = new MutableLiveData<>(new ArrayList<>());
    private ApiService apiService;
    private Context context;
    private Activity appCompatActivity;

    public Chat_Repository(Context context, Activity appCompatActivity) {
        this.context = context;
        this.appCompatActivity = appCompatActivity;
        this.apiService = ApiClient.getClient().create(ApiService.class);

        socketManager = new SocketManager(new SocketManager.SocketCallback() {
            @Override
            public void onNewMessage(ChatMessage chatMessage) {
                addMessage(chatMessage);
            }
        });
        socketManager.connect();
    }

    public LiveData<List<ChatMessage>> getMessages() {
        apiService.getAllMessage(Token).enqueue(new Callback<List<ChatMessage>>() {
            @Override
            public void onResponse(Call<List<ChatMessage>> call, Response<List<ChatMessage>> response) {
                if (response.isSuccessful()) {
                    List<ChatMessage> chatMessages = response.body();
                    if (chatMessages != null) {
                        messages.setValue(chatMessages);
                    }
                } else {
                    ErrorHandler.handleErrorResponse(response, appCompatActivity);
                }
            }

            @Override
            public void onFailure(Call<List<ChatMessage>> call, Throwable t) {
                ErrorHandler.handleNetworkFailure(t, appCompatActivity);
            }
        });
        return messages;
    }

    public LiveData<List<ChatMessage>> getMessageByRecipeId(int id_recipe,int userId) {
        apiService.getMessageByRecipe(Token, id_recipe,userId).enqueue(new Callback<List<ChatMessage>>() {
            @Override
            public void onResponse(Call<List<ChatMessage>> call, Response<List<ChatMessage>> response) {
                if (response.isSuccessful()) {
                    List<ChatMessage> chatMessages = response.body();
                    if (chatMessages != null) {
                        messages.setValue(chatMessages);
                    }
                } else {
                    ErrorHandler.handleErrorResponse(response, appCompatActivity);
                }
            }

            @Override
            public void onFailure(Call<List<ChatMessage>> call, Throwable t) {
                ErrorHandler.handleNetworkFailure(t, appCompatActivity);
            }
        });
        return messages;
    }

    public void sendMessage(String recipeId, String receiverId, String message) {
        socketManager.sendMessage(recipeId, receiverId, message);
    }

    public void disconnectSocket() {
        socketManager.disconnect();
    }

    public void addMessage(ChatMessage chatMessage) {
        List<ChatMessage> currentMessages = messages.getValue();
        if (currentMessages == null) {
            currentMessages = new ArrayList<>();
        }
        currentMessages.add(chatMessage);
        messages.postValue(currentMessages);
    }
}
