package com.example.notecook.Repo;

import static com.example.notecook.Data.MySQLiteHelperTable.COLUMN_USERNAME;
import static com.example.notecook.Data.MySQLiteHelperTable.TABLE_USER;
import static com.example.notecook.Utils.Constants.TAG_CHARGEMENT_VALIDE;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_LOCAL;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.Token;
import static com.example.notecook.Utils.Constants.getToken;
import static com.example.notecook.Utils.Constants.getUserInput;
import static com.example.notecook.Utils.Constants.saveToken;
import static com.example.notecook.Utils.Constants.saveUserInput;
import static com.example.notecook.Utils.Constants.user_login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.notecook.Activity.Login;
import com.example.notecook.Activity.MainActivity;
import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Data.UserDatasource;
import com.example.notecook.Dto.LoginResponse;
import com.example.notecook.Dto.TokenResponse;
import com.example.notecook.Model.User;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.PasswordHasher;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccessRepository {
    private Context context;
    private ApiService apiService;
    private PasswordHasher passwordHasher;
    private UserDatasource userDatasource;
    private Activity activity;

    public AccessRepository(Context context, Activity activity) {
        apiService = ApiClient.getClient().create(ApiService.class);
        this.context = context;
        userDatasource = new UserDatasource(context);
        this.activity = activity;
    }

    // TODO make insert user local in methode
    public LiveData<String> connectionApi(String username, String password) {
        MutableLiveData<String> TokenMutableLiveData = new MutableLiveData<>();
        // Example: Fetch users from the API

        LoginResponse login = new LoginResponse();
        login.setUsername(username);
        login.setPassword(password);

        Call<LoginResponse> call = apiService.authontification(login);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null) {
                        String token = loginResponse.getToken();
                        TokenMutableLiveData.setValue(token);
                        // Store the token securely (e.g., in SharedPreferences) for later use
                        TAG_CONNEXION = response.code();
                        TAG_CONNEXION_MESSAGE = response.message();

                        Log.d("TAG", TAG_CONNEXION_MESSAGE);
                        try {
                            User user = new User();
                            user.setUsername(username);
                            passwordHasher = new PasswordHasher();
                            String passwordHacher = passwordHasher.hashPassword(password);
                            user.setPassWord(passwordHacher);
                            user_login.setUser(user);
                            if (!userDatasource.isRecordExist(TABLE_USER, COLUMN_USERNAME, username)) {
                                userDatasource.insertUser(user);
                                Log.e("tag", user.getUsername());
                            }
                            saveToken(token, context);
                            Token = token;
                            saveUserInput(username, password, context);
                            Constants.AffichageMessage(TAG_CHARGEMENT_VALIDE, "message", activity);
                        } catch (Exception e) {
                            Log.e("tag", e.toString());
                        }
                        Intent i = new Intent(context, MainActivity.class);
                        activity.startActivity(i);
                    }
                } else {
                    // TODO make handle response error and failure
                    ErrorHandler.handleErrorResponse(response, activity);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                TAG_CONNEXION_MESSAGE = call.toString();
                ErrorHandler.handleNetworkFailure(t, activity,call);
            }
        });
        return TokenMutableLiveData;
    }

    public LiveData<User> ConnectLocal(String username, String password) {
        MutableLiveData<User> s = new MutableLiveData<>();
        User user = userDatasource.select_User_BYUsername(username);
        passwordHasher = new PasswordHasher();
        if (user != null)
            //Toast.makeText(getBaseContext(), "user : " + item.getUser_name() + " pass : " + item.getPassWord(), Toast.LENGTH_SHORT).show();
            if (passwordHasher.verifyPassword(password, user.getPassWord())) {
                saveUserInput(username, password, context);
                TAG_CONNEXION_LOCAL = "success";
                s.postValue(user);
                user_login.setUser(user);
                /*if (!Objects.equals(user_login.getUser(), null)) {
                    user_login.getUser().setUser_name(username);
                    user_login.getUser().setUser_name(item.getPassWord());
                    user_login.setMessage("Local");
                    Log.d("message",user_login.getMessage());
                }*/
                Intent i = new Intent(context, MainActivity.class);
                context.startActivity(i);
                activity.finish();


                //break;
            }
        return s;
    }


    public LiveData<String> TokenApi() {
        MutableLiveData<String> mutableLiveDataToken = new MutableLiveData<>();
        Intent iM = new Intent(context, MainActivity.class);
        Intent iLg = new Intent(context, Login.class);

        Call<TokenResponse> call = apiService.getVerifyToken(getToken(context));

        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                if (response.isSuccessful()) {
                    handleSuccessfulResponse(response, mutableLiveDataToken, iM);
                } else {
                    handleErrorResponse(response, iM, iLg);
                }
            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                handleFailure(t, iM, iLg);
            }
        });

        return mutableLiveDataToken;
    }

    private void handleSuccessfulResponse(Response<TokenResponse> response, MutableLiveData<String> mutableLiveDataToken, Intent iM) {
        TokenResponse tokenResponse = response.body();
        int statusCode = response.code();

        if (tokenResponse != null) {
            user_login = tokenResponse;
            Constants.TAG_CONNEXION = statusCode;

            if (statusCode == 201) {
                saveToken(tokenResponse.getToken(), context);
                mutableLiveDataToken.setValue(tokenResponse.getToken());
            }

            Constants.Token = tokenResponse.getToken();
            Toast.makeText(context, "Validation : " + statusCode, Toast.LENGTH_SHORT).show();
            Constants.AffichageMessage(TAG_CHARGEMENT_VALIDE, "", activity);

            // Ensure `activity` is not null before running UI code
            if (activity != null) {
                activity.runOnUiThread(() -> {
                    activity.startActivity(iM);
                    activity.finish();
                });
            }
        }
    }

    private void handleErrorResponse(Response<TokenResponse> response, Intent iM, Intent iLg) {
        ErrorHandler.handleErrorResponse(response, activity);
        int statusCode = response.code();

        if (statusCode == 401) {
            // Unauthorized, handle reauthentication
            saveToken("", context);
            if (activity != null) {
                activity.runOnUiThread(() -> context.startActivity(iLg));
            }
        } else {
            // For other status codes, navigate to MainActivity
            if (activity != null) {
                activity.runOnUiThread(() -> {
                    activity.startActivity(iM);
                    activity.finish();
                });
            }
        }
    }

    private void handleFailure(Throwable t, Intent iM, Intent iLg) {
        ErrorHandler.handleNetworkFailure(t, activity);

        if (t instanceof IOException) {
            {
                String s1 = getUserInput(context);
                if (activity != null) {
                    activity.runOnUiThread(() -> {
                        if (s1.equals("")) {
                            context.startActivity(iLg);
                        } else {
                            activity.startActivity(iM);
                        }
                        activity.finish();
                    });
                }
            }


        }
    }
}
