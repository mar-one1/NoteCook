package com.example.notecook.Api;

import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.user_login;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notecook.Utils.Constants;

import org.json.JSONObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConnexionRetrofit {

    public static void CreateConnexionRetrofit(final AppCompatActivity _activity, JSONObject obj, final String _soap_action, final String tag_action, String message_loading) {

    }


    public static void TokenApi(String token) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);


        Call<TokenResponse> call = apiService.getVerifyToken(token);

        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful()) {
                    TokenResponse tokenResponse = response.body();
                    if (tokenResponse != null) {
                        user_login = tokenResponse;
                        //Toast.makeText(Main, "token : " + user_login.getUser().getId_User(), Toast.LENGTH_SHORT).show();
                        // Store the token securely (e.g., in SharedPreferences) for later use
                        TAG_CONNEXION = response.code();
                    }
                } else {
                    // The HTTP request was not successful (status code is not 2xx).
                    // You can handle errors here based on the response status code.
                    int statusCode = response.code();
                    Constants.TAG_CONNEXION = statusCode;
                    // Handle different status codes as per your API's conventions.
                    if (statusCode == 401) {
                        // Unauthorized, handle accordingly (e.g., Token authentication failed).
                    } else if (statusCode == 403) {
                        // Not found, handle accordingly (e.g., show a 404 error message).
                        // Token not provided
                    } else {
                        // Handle other status codes or generic error handling.
                    }

                    if (response.errorBody() != null) {
                        try {
                            String errorResponse = response.errorBody().string();
                            // Print or log the errorResponse for debugging
                            Log.e("token", "Error Response: " + errorResponse);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {

            }


        });
    }
}
