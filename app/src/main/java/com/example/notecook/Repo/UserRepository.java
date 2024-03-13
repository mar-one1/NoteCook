package com.example.notecook.Repo;

import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.User_CurrentRecipe;
import static com.example.notecook.Utils.Constants.user_login;
import static com.example.notecook.Utils.Constants.user_login_local;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Data.UserDatasource;
import com.example.notecook.Fragement.MainFragment;
import com.example.notecook.Model.User;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserRepository {
    private ApiService apiService;
    private UserDatasource userDatasource;
    private Context context;

    public UserRepository(Context context) {
        this.context = context;
        apiService = ApiClient.getClient().create(ApiService.class);
        userDatasource = new UserDatasource(context);
    }

    public LiveData<User> getUserApi(String username, Context context) {
        MutableLiveData<User> userLogin = new MutableLiveData<>();
        // Example: Fetch users from the API
        apiService.getUserByUsername(username).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User UserResponse = response.body();
                    if (UserResponse != null) {
                        UserResponse.setId_User(user_login.getUser().getId_User());
                        userLogin.setValue(UserResponse);
                        Toast.makeText(context, TAG_CONNEXION_MESSAGE + " " + "get user from Api", Toast.LENGTH_LONG).show();
                    }
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                handleNetworkFailure(call);
            }
        });
        return userLogin;
    }



    public LiveData<User> UpdateUserApi(User user, Context context) {
        MutableLiveData<User> userUpdated = new MutableLiveData<>();
        // Example: Fetch users from the API
        apiService.updateUserByUsername(user.getUsername(), user).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User UserResponse = response.body();
                    if (UserResponse != null) {
                        UserResponse.setId_User(user_login.getUser().getId_User());
                        userUpdated.setValue(UserResponse);
                        Toast.makeText(context, TAG_CONNEXION_MESSAGE + " " + "user updated To Api", Toast.LENGTH_LONG).show();
                    }
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                handleNetworkFailure(call);
            }
        });
        return userUpdated;
    }



    private void handleErrorResponse(Response<?> response) {
        int statusCode = response.code();
        String message = response.message();
        if (response.errorBody() != null) {
            try {
                String errorResponse = response.errorBody().string();
                Log.e("ErrorResponse", "Error Response: " + errorResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleNetworkFailure(Call<User> call) {
        // Handle network failure
        TAG_CONNEXION_MESSAGE = call.toString();
        //Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, context);
        Toast.makeText(context, TAG_CONNEXION_MESSAGE, Toast.LENGTH_SHORT).show();
    }


    public void getLocalUserLogin(String username) {
        UserDatasource userDatasource = new UserDatasource(context);
        userDatasource.open();
        user_login_local.setUser(userDatasource.select_User_BYUsername(username));
        userDatasource.close();
    }


}
