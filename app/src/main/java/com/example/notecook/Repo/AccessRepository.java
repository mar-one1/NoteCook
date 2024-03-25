package com.example.notecook.Repo;

import static android.content.Context.MODE_PRIVATE;
import static com.example.notecook.Data.MySQLiteHelperTable.COLUMN_USERNAME;
import static com.example.notecook.Data.MySQLiteHelperTable.TABLE_USER;
import static com.example.notecook.Data.UserDatasource.insertUser;
import static com.example.notecook.Data.UserDatasource.isRecordExist;
import static com.example.notecook.Utils.Constants.TAG_AUTHENTIFICATION_ECHOUE;
import static com.example.notecook.Utils.Constants.TAG_CHARGEMENT_VALIDE;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_LOCAL;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.TAG_ERREUR_SYSTEM;
import static com.example.notecook.Utils.Constants.TAG_OFFLINE;
import static com.example.notecook.Utils.Constants.getToken;
import static com.example.notecook.Utils.Constants.getUserInput;
import static com.example.notecook.Utils.Constants.lOGIN_KEY;
import static com.example.notecook.Utils.Constants.saveToken;
import static com.example.notecook.Utils.Constants.saveUserInput;
import static com.example.notecook.Utils.Constants.user_login;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Api.ValidationError;
import com.example.notecook.Dto.LoginResponse;
import com.example.notecook.Dto.TokenResponse;
import com.example.notecook.Data.UserDatasource;
import com.example.notecook.Login;
import com.example.notecook.MainActivity;
import com.example.notecook.Model.User;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.PasswordHasher;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Objects;

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
                            userDatasource.open();
                            if (!isRecordExist(TABLE_USER, COLUMN_USERNAME, username)) {
                                insertUser(user);
                                Log.e("tag", user.getUsername());
                            }
                            userDatasource.close();
                            saveToken(token, context);
                            saveUserInput(username, password, context);
                            Constants.AffichageMessage(TAG_CHARGEMENT_VALIDE, activity);
                        } catch (Exception e) {
                            Log.e("tag", e.toString());
                        }
                        Intent i = new Intent(context, MainActivity.class);
                        activity.startActivity(i);
                    }
                } else {
                // TODO make handle response error and failure
                    // Handle error response here
                    // The HTTP request was not successful (status code is not 2xx).
                    // You can handle errors here based on the response status code.
                    int statusCode = response.code();
                    Constants.TAG_CONNEXION = statusCode;
                    TAG_CONNEXION_MESSAGE = response.message();
                    // Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, activity);
                    // Handle different status codes as per your API's conventions.
                    if (statusCode == 401) {
                        Constants.AffichageMessage(TAG_AUTHENTIFICATION_ECHOUE, activity);
                        // Unauthorized, handle accordingly (e.g., reauthentication).
                    } else if (statusCode == 404) {
                        // Not found, handle accordingly (e.g., show a 404 error message).
                        Constants.AffichageMessage(TAG_OFFLINE, activity);
                    } else if (statusCode >= 500) {
                        // Handle other status codes or generic error handling.
                        Constants.AffichageMessage("Internal Server Error", activity);
                    } else if (statusCode == 406) {
                        // Handle other status codes or generic error handling.
                        Constants.AffichageMessage("User not found", activity);
                    } else
                        Constants.AffichageMessage(response.message(), activity);
                }

//                if (response.errorBody() != null) {
//                    try {
//                        String errorResponse = response.errorBody().string();
//                        // Print or log the errorResponse for debugging
//                        TAG_CONNEXION_MESSAGE = errorResponse;
//                        Constants.AffichageMessage(TAG_ERREUR_SYSTEM,activity);
//                        //Constants.DisplayErrorMessage(activity,TAG_CONNEXION_MESSAGE);
//                        TAG_CONNEXION = response.code();
//                        Log.e("token", "Error Response: " + errorResponse);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                TAG_CONNEXION_MESSAGE = call.toString();
                Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, activity);
            }
        });
        return TokenMutableLiveData;
    }

    private String ConnectLocal(String username, String password) {
        TAG_CONNEXION_LOCAL = "";
        userDatasource.open();
        Constants.listUser = userDatasource.getAllUser();
        userDatasource.close();
        passwordHasher = new PasswordHasher();
        for (User item : Constants.listUser) {
            //Toast.makeText(getBaseContext(), "user : " + item.getUser_name() + " pass : " + item.getPassWord(), Toast.LENGTH_SHORT).show();
            if (Objects.equals(item.getFirstname(), username) && passwordHasher.verifyPassword(password, item.getPassWord())) {
                saveUserInput(username, password, context);
                TAG_CONNEXION_LOCAL = "success";
                user_login.setUser(item);
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
        }
        return TAG_CONNEXION_LOCAL;
    }
    // TODO make response error and failure handle
    public LiveData<String> TokenApi() {
        MutableLiveData<String> mutableLiveDataToken = new MutableLiveData<>();
        Intent iM = new Intent(context, MainActivity.class);
        Intent iLg = new Intent(context, Login.class);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<TokenResponse> call = apiService.getVerifyToken(getToken(context));

        call.enqueue(new Callback<TokenResponse>() {
            @Override
            public void onResponse(Call<TokenResponse> call, Response<TokenResponse> response) {
                Log.d("JSON Response", response.toString());
                //Gson gson = new GsonBuilder().create();
                //TokenResponse tokenResponse = gson.fromJson(response.toString(), TokenResponse.class);
                TokenResponse tokenResponse = response.body();
                int statusCode = response.code();
                if (response.isSuccessful()) {

                    //TokenResponse tokenResponse = response.body();
                    if (tokenResponse != null) {
                        user_login = tokenResponse;
                        Constants.TAG_CONNEXION = response.code();
                        // Store the token securely (e.g., in SharedPreferences) for later use
                        if (statusCode == 201) {
                            saveToken(tokenResponse.getToken(), context);
                            mutableLiveDataToken.setValue(tokenResponse.getToken());
                        }
                        Constants.Token = tokenResponse.getToken();
                        //Log.d("TAG", "" + user_login.getUser().getUsername() + " " + user_login.getMessage());
                        Toast.makeText(context, "Validation : " + statusCode, Toast.LENGTH_SHORT).show();

//                        i.putExtra("TAG","online");
                        Constants.AffichageMessage(TAG_CHARGEMENT_VALIDE, activity);
                        activity.startActivity(iM);
                        activity.finish();
                    }
                } else {
                    // TODO check this start activity logic
                    // The HTTP request was not successful (status code is not 2xx).
                    // You can handle errors here based on the response status code.

                    Constants.TAG_CONNEXION = statusCode;

                    // Handle different status codes as per your API's conventions.
                    if (statusCode == 401) {
                        // Unauthorized, handle accordingly (e.g., reauthentication).
                        saveToken("", context);
                        Constants.AffichageMessage("Erreur Unauthorized by server ", activity);
                        context.startActivity(iLg);
                    } else {
                        //(statusCode == 404) {
                        Constants.AffichageMessage("Erreur 404", activity);
//                        i.putExtra("TAG","online");
                        activity.startActivity(iM);
                        activity.finish();
                    }
                }
            }
            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
                Constants.AffichageMessage(TAG_ERREUR_SYSTEM, activity);
                Constants.TAG_CONNEXION = call.hashCode();
                String s1 = getUserInput(context);
                if (s1.equals("")) {
                    context.startActivity(iLg);
                } else {
                    activity.startActivity(iM);
                }
                activity.finish();
            }
        });
        return mutableLiveDataToken;
    }

    private static void handleErrorResponse(Context context, String model, Response<?> response) {
        int statusCode = response.code();
        String message = response.message();
        if (response.errorBody() != null) {
            if (response.code() == 400) {
                try {
                    String errorBody = response.errorBody().string();
                    Gson gson = new Gson();
                    ValidationError validationError = gson.fromJson(errorBody, ValidationError.class);
                    // Now you have the validation errors in the validationError object
                    // Handle them accordingly
                    StringBuilder errorMessages = new StringBuilder();
                    for (ValidationError.ValidationErrorItem error : validationError.getErrors()) {
                        errorMessages.append(", ").append(error.getMessage());
                    }
                    Toast.makeText(context, errorMessages, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    // Handle error parsing error body
                }
            }
        } else if (statusCode == 409) {
            //Constants.AffichageMessage("User already exists", context);
            //Toast.makeText(context, "User already exists", Toast.LENGTH_SHORT).show();
            // Unauthorized, handle accordingly (e.g., reauthentication).
        } else if (statusCode == 404) {
            // Not found, handle accordingly (e.g., show a 404 error message).
            //Constants.AffichageMessage(TAG_OFFLINE, context);
            Toast.makeText(context, TAG_OFFLINE, Toast.LENGTH_SHORT).show();
        } else if (statusCode >= 500) {
            // Handle other status codes or generic error handling.
            //Constants.AffichageMessage("Internal Server Error", context);
            Toast.makeText(context, "Internal Server Error", Toast.LENGTH_SHORT).show();
        } else if (statusCode == 406) {
            // Handle other status codes or generic error handling.
            // Constants.AffichageMessage("User not found", context);
        } else Toast.makeText(context, model + " not found", Toast.LENGTH_SHORT).show();
        //Constants.AffichageMessage(response.message(), context);

    }

    private static void handleNetworkFailure(Context context, Call<User> call) {
        // Handle network failure
        TAG_CONNEXION_MESSAGE = call.toString();
        //Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, context);
        Toast.makeText(context, TAG_CONNEXION_MESSAGE, Toast.LENGTH_SHORT).show();
    }


}
