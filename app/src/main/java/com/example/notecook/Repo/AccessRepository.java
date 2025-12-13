package com.example.notecook.Repo;

import static com.example.notecook.Data.MySQLiteHelperTable.COLUMN_USERNAME;
import static com.example.notecook.Data.MySQLiteHelperTable.TABLE_USER;
import static com.example.notecook.Utils.Constants.TAG_CHARGEMENT_VALIDE;
import static com.example.notecook.Utils.Constants.getToken;
import static com.example.notecook.Utils.Constants.getUserInput;
import static com.example.notecook.Utils.Constants.saveToken;
import static com.example.notecook.Utils.Constants.saveUserInput;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
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
import com.example.notecook.Activity.activity_force_change_password;
import com.example.notecook.Model.User;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.PasswordHasher;
import com.example.notecook.ViewModel.SharedRecipeViewModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccessRepository {
    private final Context context;
    private ApiService apiService;
    private PasswordHasher passwordHasher;
    private UserDatasource userDatasource;
    private Activity activity;
    private SharedRecipeViewModel viewModel;

    public AccessRepository(Context context, Activity activity,SharedRecipeViewModel viewModel) {
        apiService = ApiClient.getClient().create(ApiService.class);
        this.context = context;
        userDatasource = new UserDatasource(context);
        this.activity = activity;
        this.viewModel = viewModel;
    }

    // TODO make insert user local in methode
    public LiveData<String> connectionApi(String username, String password) {
        MutableLiveData<String> TokenMutableLiveData = new MutableLiveData<>();

        LoginResponse login = new LoginResponse();
        login.setUsername(username);
        login.setPassword(password);

        Call<LoginResponse> call = apiService.authentication(login);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {

                    LoginResponse loginResponse = response.body();

                    if (loginResponse == null) {
                        ErrorHandler.handleErrorResponse(response, activity);
                        return;
                    }

                    // 1️⃣ Check MUST CHANGE PASSWORD
                    if ("PASSWORD_CHANGE_REQUIRED".equals(loginResponse.getStatus())) {
                        int userId = loginResponse.getUser_id();

                        Intent intent = new Intent(activity, activity_force_change_password.class);
                        intent.putExtra("USER_ID", userId);
                        intent.putExtra("USERNAME", username);
                        activity.startActivity(intent);
                        return; // STOP normal login
                    }

                    // 2️⃣ NORMAL LOGIN
                    String token = loginResponse.getToken();
                    TokenMutableLiveData.setValue(token);

                    viewModel.setTagConnexion(response.code());
                    viewModel.setTagConnexionMessage(response.message());

                    try {
                        User user = new User();
                        user.setUsername(username);

                        passwordHasher = new PasswordHasher();
                        String passwordHacher = passwordHasher.hashPassword(password);
                        user.setPassWord(passwordHacher);

                        viewModel.getUserLogin().getValue().setUser(user);

                        if (!userDatasource.isRecordExist(TABLE_USER, COLUMN_USERNAME, username)) {
                            userDatasource.insertUser(user);
                        }

                        saveToken(token, context);
                        viewModel.setToken(token);

                        saveUserInput(username, password, context);

                        Constants.AffichageMessage(TAG_CHARGEMENT_VALIDE, "message", activity);

                    } catch (Exception e) {
                        Log.e("tag", e.toString());
                    }

                    Intent i = new Intent(context, MainActivity.class);
                    i.putExtra("TAG","user");
                    activity.startActivity(i);

                } else {
                    ErrorHandler.handleErrorResponse(response, activity);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                viewModel.setTagConnexionMessage(call.toString());
                ErrorHandler.handleNetworkFailure(t, activity, call);
            }
        });

        return TokenMutableLiveData;
    }


    public LiveData<User> ConnectLocal(String username, String password) {
        MutableLiveData<User> s = new MutableLiveData<>();
        User user = userDatasource.select_User_BYUsername(username);
        passwordHasher = new PasswordHasher();
        if (user != null)
            if (passwordHasher.verifyPassword(password, user.getPassWord())) {
                saveUserInput(username, password, context);
                viewModel.setTagConnexionLocal("success");
                s.postValue(user);
                viewModel.getUserLogin().getValue().setUser(user);
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

        public LiveData<String> changePassword(long userId, String etOldPassword, String etNewPassword) {
            MutableLiveData<String> s = new MutableLiveData<>();
            Map<String, String> body = new HashMap<>();
            body.put("user_id", String.valueOf(userId));
            body.put("old_password", etOldPassword);
            body.put("new_password", etNewPassword);

            Call<LoginResponse> call = apiService.changePassword(body);
            call.enqueue(new Callback<LoginResponse>() {
                @Override
                public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                    if (response.isSuccessful()) {
                        LoginResponse loginResponse = response.body();
                        s.setValue(loginResponse != null ? loginResponse.getMessage() : "changed");
                    }else
                        ErrorHandler.handleErrorResponse(response, activity);
                }

                @Override
                public void onFailure(Call<LoginResponse> call, Throwable t) {
                    viewModel.setTagConnexionMessage(call.toString());
                    ErrorHandler.handleNetworkFailure(t, activity, call);
                }
            });
            return s;
        }


    private void handleSuccessfulResponse(Response<TokenResponse> response, MutableLiveData<String> mutableLiveDataToken, Intent iM) {
        TokenResponse tokenResponse = response.body();
        int statusCode = response.code();

        if (tokenResponse != null) {
            viewModel.setUserLogin(tokenResponse);
            viewModel.setTagConnexion(statusCode);

            if (statusCode == 201) {
                saveToken(tokenResponse.getToken(), context);
                mutableLiveDataToken.setValue(tokenResponse.getToken());
            }

            viewModel.setToken(tokenResponse.getToken());
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
