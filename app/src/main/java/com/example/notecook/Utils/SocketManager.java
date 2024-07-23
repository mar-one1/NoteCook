package com.example.notecook.Utils;

import static com.example.notecook.Api.ApiClient.BASE_URL;
import static com.example.notecook.Utils.Constants.user_login;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketManager {

    private static final String TAG = "SocketManager";
    private Socket mSocket;

    public SocketManager() {
        try {
            // Initialize the Socket.IO client with your server URL
            mSocket = IO.socket("http://192.168.56.1:3000"); // Replace with your server URL

            // Set up event listeners
            mSocket.on(Socket.EVENT_CONNECT, onConnect);
            mSocket.on(Socket.EVENT_DISCONNECT, onDisconnect);
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onConnectError);
            mSocket.on(Socket.EVENT_CONNECT_TIMEOUT, onConnectTimeout);
            mSocket.on("chat message", onNewMessage);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    // Connect to the Socket.IO server
    public void connect() {
        mSocket.connect();
    }

    // Disconnect from the Socket.IO server
    public void disconnect() {
        if (mSocket != null) {
            mSocket.disconnect();
        }
    }

    // Send a message to the server
    public void sendMessage(String recipeId, String receiverId, String message) {
        if (mSocket != null && mSocket.connected()) {
            JSONObject data = new JSONObject();
            try {
                data.put("recipeId", recipeId);
                data.put("senderId", Constants.user_login.getUser().getId_User());
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

    // Event listeners
    private Emitter.Listener onConnect = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.d(TAG, "Connected to Socket.IO server");
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

    private Emitter.Listener onConnectTimeout = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "Socket connection timeout");
        }
    };

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONObject data = (JSONObject) args[0];
            // Handle incoming message
            Log.d(TAG, "New message received: " + data.toString());
        }
    };
}
