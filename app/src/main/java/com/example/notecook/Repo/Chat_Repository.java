package com.example.notecook.Repo;

import static com.example.notecook.Utils.Constants.user_login;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Model.ChatMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

public class Chat_Repository {
    private Socket socket;
    private MutableLiveData<List<ChatMessage>> messages = new MutableLiveData<>();

    public Chat_Repository() {
        try {
            socket = IO.socket(ApiClient.BASE_URL); // Replace with your server URL
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        // Connect to socket
        socket.connect();

        // Handle incoming messages
        socket.on("chat message", args -> {
            JSONObject data = (JSONObject) args[0];
            try {
                String senderId = data.getString("senderId");
                String message = data.getString("message");
                String timestamp = data.getString("timestamp");
                ChatMessage chatMessage = new ChatMessage(Integer.valueOf(senderId), message, Date.valueOf(timestamp));

                // Update LiveData with new message
                List<ChatMessage> updatedMessages = new ArrayList<>(messages.getValue());
                updatedMessages.add(chatMessage);
                messages.postValue(updatedMessages);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    public LiveData<List<ChatMessage>> getMessages() {
        return messages;
    }

    public void sendMessage(String recipeId, String receiverId, String message) {
        JSONObject data = new JSONObject();
        try {
            data.put("recipeId", recipeId);
            data.put("senderId", user_login.getUser().getId_User()); // Replace with actual user ID
            data.put("receiverId", receiverId);
            data.put("message", message);
            socket.emit("chat message", data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void disconnectSocket() {
        socket.disconnect();
        socket.off("chat message");
    }
}


