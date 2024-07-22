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
import com.example.notecook.Dto.RecipeResponse;
import com.example.notecook.Model.ChatMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Chat_Repository {
    private Socket socket;
    private MutableLiveData<List<ChatMessage>> messages = new MutableLiveData<>();
    private Context context;
    private Activity appCompatActivity;
    private ApiService apiService;


    public Chat_Repository(Context context, Activity appCompatActivity) {
        this.apiService = ApiClient.getClient().create(ApiService.class);
        this.context = context;
        this.appCompatActivity = appCompatActivity;
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

        apiService.getAllMessage(Token).enqueue(new Callback<List<ChatMessage>>() {
            @Override
            public void onResponse(Call<List<ChatMessage>> call, Response<List<ChatMessage>> response) {
                if (response.isSuccessful()) {
                    List<ChatMessage> chatMessage = response.body();
                    if (chatMessage != null) {
                        messages.setValue(chatMessage);
                    }
                    TAG_CONNEXION_MESSAGE = response.message();
                    TAG_CONNEXION = response.code();
//                    if (CURRENT_RECIPE.getFrk_user() != user_login.getUser().getId_User() && User_CurrentRecipe.getId_User() != CURRENT_RECIPE.getFrk_user())
//                        userRepo.getUserByIdRecipeApi(CURRENT_RECIPE.getId_recipe());
//                    else if (User_CurrentRecipe.getId_User() != CURRENT_RECIPE.getFrk_user()) {
//                        User_CurrentRecipe = user_login.getUser();
//                        //MainFragment.viewPager2.setCurrentItem(1, false);
//                    }
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


