package com.example.notecook.Utils;

import static com.example.notecook.Api.ApiClient.BASE_URL;
import static com.example.notecook.Utils.Constants.user_login;

import android.util.Log;

import com.example.notecook.Model.ChatMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager {

    private static final String TAG = "SocketManager";
    private Socket mSocket;
    private SocketCallback socketCallback;

    public interface SocketCallback {
        void onNewMessage(ChatMessage chatMessage);
    }

    public SocketManager(SocketCallback callback) {
        try {
            mSocket = IO.socket(BASE_URL); // Replace with your server URL
            this.socketCallback = callback;
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on("chat message", onNewMessage);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void connect() {
        mSocket.connect();
    }

    public void disconnect() {
        if (mSocket != null) {
            mSocket.disconnect();
        }
    }

    public void registerUser(String userId) {
        if (mSocket != null && mSocket.connected()) {
            mSocket.emit("register", userId);
        } else {
            Log.e(TAG, "Socket is not connected");
        }
    }

    public void sendMessage(String recipeId, String receiverId, String message) {
        if (mSocket != null && mSocket.connected()) {
            JSONObject data = new JSONObject();
            try {
                data.put("recipeId", recipeId);
                data.put("senderId", user_login.getUser().getId_User());
                data.put("receiverId", receiverId);
                data.put("message", message);
                mSocket.emit("chat message", data);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.e(TAG, "Socket is not connected");
        }
    }

    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "Connected to Socket.IO server");
            // Register the user after connecting
            registerUser(String.valueOf(user_login.getUser().getId_User()));
        }
    };

    private Emitter.Listener onDisconnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "Disconnected from Socket.IO server");
        }
    };

    private Emitter.Listener onConnectError = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "Socket connection error: " + args[0]);
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            // Handle the incoming message here
            Log.d(TAG, "New message received: " + data.toString());

            // Convert JSON data to ChatMessage
            try {
                String recipeId = data.getString("recipeId");
                String senderId = data.getString("senderId");
                String receiverId = data.getString("receiverId");
                String message = data.getString("message");

                // Create a ChatMessage object
                ChatMessage chatMessage = new ChatMessage(
                        Integer.parseInt(recipeId),
                        Integer.parseInt(receiverId),
                        Integer.parseInt(senderId),
                        message
                );

                // Notify the callback about the new message
                if (socketCallback != null) {
                    socketCallback.onNewMessage(chatMessage);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    };
}
