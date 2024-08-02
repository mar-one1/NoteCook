package com.example.notecook.Repo;

import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.Token;
import static com.example.notecook.Utils.Constants.user_login;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Model.ChatMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Chat_Repository {
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private Socket socket;
    private MutableLiveData<List<ChatMessage>> messages = new MutableLiveData<>(new ArrayList<>());
    private Context context;
    private Activity appCompatActivity;
    private ApiService apiService;
    private Emitter.Listener onNewMessage = args -> {
        JSONObject data = (JSONObject) args[0];
        try {
            String senderId = data.getString("senderId");
            String receiverId = data.getString("receiverId");
            String message = data.getString("message");
            String timestamp = data.getString("timestamp");

            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            java.util.Date date = sdf.parse(timestamp);
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());

            ChatMessage chatMessage = new ChatMessage(
                    Integer.parseInt(senderId),
                    Integer.parseInt(receiverId),
                    message,
                    sqlDate
            );

            List<ChatMessage> updatedMessages = new ArrayList<>(messages.getValue());
            updatedMessages.add(chatMessage);
            messages.postValue(updatedMessages);
        } catch (JSONException | ParseException e) {
            e.printStackTrace();
        }
    };

    public Chat_Repository(Context context, Activity appCompatActivity) {
        this.apiService = ApiClient.getClient().create(ApiService.class);
        this.context = context;
        this.appCompatActivity = appCompatActivity;

        try {
            socket = IO.socket(ApiClient.BASE_URL); // Ensure correct WebSocket URL
            socket.on(Socket.EVENT_CONNECT, args -> {
                System.out.println("Socket connected");
            });
            socket.on(Socket.EVENT_DISCONNECT, args -> {
                System.out.println("Socket disconnected");
            });
            socket.on("chat message", onNewMessage);
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
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
                    TAG_CONNEXION_MESSAGE = response.message();
                    TAG_CONNEXION = response.code();
                } else {
                    ErrorHandler.handleErrorResponse(response, appCompatActivity);
                }
            }

            @Override
            public void onFailure(Call<List<ChatMessage>> call, Throwable t) {
                TAG_CONNEXION = call.hashCode();
                ErrorHandler.handleNetworkFailure(t, appCompatActivity);
            }
        });
        return messages;
    }

    public LiveData<List<ChatMessage>> getMessageByRecipeId(int id_recipe) {
        apiService.getMessageByRecipe(Token,id_recipe).enqueue(new Callback<List<ChatMessage>>() {
            @Override
            public void onResponse(Call<List<ChatMessage>> call, Response<List<ChatMessage>> response) {
                if (response.isSuccessful()) {
                    List<ChatMessage> chatMessages = response.body();
                    if (chatMessages != null) {
                        messages.setValue(chatMessages);
                    }
                    TAG_CONNEXION_MESSAGE = response.message();
                    TAG_CONNEXION = response.code();
                } else {
                    ErrorHandler.handleErrorResponse(response, appCompatActivity);
                }
            }

            @Override
            public void onFailure(Call<List<ChatMessage>> call, Throwable t) {
                TAG_CONNEXION = call.hashCode();
                ErrorHandler.handleNetworkFailure(t, appCompatActivity);
            }
        });
        return messages;
    }

    public void sendMessage(String recipeId, String receiverId, String message) {
        if (socket != null && socket.connected()) {
            JSONObject data = new JSONObject();
            try {
                data.put("recipeId", recipeId);
                data.put("senderId", user_login.getUser().getId_User());
                data.put("receiverId", receiverId);
                data.put("message", message);
                socket.emit("chat message", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            System.err.println("Socket is not connected");
        }
    }

    public void disconnectSocket() {
        if (socket != null) {
            socket.disconnect();
            socket.off("chat message");
        }
    }
    public void addMessage(ChatMessage chatMessage) {
        List<ChatMessage> currentMessages = messages.getValue();
        if (currentMessages == null) {
            currentMessages = new ArrayList<>();
        }
        currentMessages.add(chatMessage);
        messages.setValue(currentMessages);
    }
}
