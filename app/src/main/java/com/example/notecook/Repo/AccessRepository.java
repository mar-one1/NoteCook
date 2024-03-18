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
import static com.example.notecook.Utils.Constants.lOGIN_KEY;
import static com.example.notecook.Utils.Constants.saveToken;
import static com.example.notecook.Utils.Constants.saveUserInput;
import static com.example.notecook.Utils.Constants.user_login;

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
import com.example.notecook.Api.LoginResponse;
import com.example.notecook.Api.TokenResponse;
import com.example.notecook.Data.DetailRecipeDataSource;
import com.example.notecook.Data.UserDatasource;
import com.example.notecook.Loading_Srcreen;
import com.example.notecook.Login;
import com.example.notecook.MainActivity;
import com.example.notecook.Model.User;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.PasswordHasher;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccessRepository {
    private  Context context;
    private ApiService apiService;
    private PasswordHasher passwordHasher;
    private UserDatasource userDatasource;
    private SharedPreferences sharedPreferences;

    public AccessRepository(Context context) {
        apiService = ApiClient.getClient().create(ApiService.class);
        this.context = context;
        userDatasource = new UserDatasource(context);
    }
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
                            if(!isRecordExist(TABLE_USER,COLUMN_USERNAME,username)) {
                                insertUser(user);
                                Log.e("tag",user.getUsername());
                            }
                            userDatasource.close();
                            saveToken(token,context);
                            saveUserInput(username, password,context);
                            Constants.AffichageMessage(TAG_CHARGEMENT_VALIDE, (AppCompatActivity) context);
                        } catch (Exception e) {
                            Log.e("tag", e.toString());
                        }
                        Intent i = new Intent(context, MainActivity.class);
                        ((AppCompatActivity) context).startActivity(i);
                    }
                } else {

                    // Handle error response here
                    // The HTTP request was not successful (status code is not 2xx).
                    // You can handle errors here based on the response status code.
                    int statusCode = response.code();
                    Constants.TAG_CONNEXION = statusCode;
                    TAG_CONNEXION_MESSAGE = response.message();
                    // Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, (AppCompatActivity) context);
                    // Handle different status codes as per your API's conventions.
                    if (statusCode == 401) {
                        Constants.AffichageMessage(TAG_AUTHENTIFICATION_ECHOUE, (AppCompatActivity) context);
                        // Unauthorized, handle accordingly (e.g., reauthentication).
                    } else if (statusCode == 404) {
                        // Not found, handle accordingly (e.g., show a 404 error message).
                        Constants.AffichageMessage(TAG_OFFLINE, (AppCompatActivity) context);
                    } else if (statusCode >= 500) {
                        // Handle other status codes or generic error handling.
                        Constants.AffichageMessage("Internal Server Error", (AppCompatActivity) context);
                    } else if (statusCode == 406) {
                        // Handle other status codes or generic error handling.
                        Constants.AffichageMessage("User not found", (AppCompatActivity) context);
                    } else Constants.AffichageMessage(response.message(), (AppCompatActivity) context);
                }

//                if (response.errorBody() != null) {
//                    try {
//                        String errorResponse = response.errorBody().string();
//                        // Print or log the errorResponse for debugging
//                        TAG_CONNEXION_MESSAGE = errorResponse;
//                        Constants.AffichageMessage(TAG_ERREUR_SYSTEM,(AppCompatActivity) context);
//                        //Constants.DisplayErrorMessage((AppCompatActivity) context,TAG_CONNEXION_MESSAGE);
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
                Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, (AppCompatActivity) context);
            }
        });
        return  TokenMutableLiveData;
    }

    private String ConnectLocal(String username,String password) {
        TAG_CONNEXION_LOCAL = "";
        userDatasource.open();
        Constants.listUser = userDatasource.getAllUser();
        userDatasource.close();
        passwordHasher = new PasswordHasher();
        for (User item : Constants.listUser) {
            //Toast.makeText(getBaseContext(), "user : " + item.getUser_name() + " pass : " + item.getPassWord(), Toast.LENGTH_SHORT).show();
            if (Objects.equals(item.getFirstname(), username) && passwordHasher.verifyPassword(password, item.getPassWord())) {
                if (sharedPreferences.getBoolean(lOGIN_KEY, true)) {
                    saveUserInput(username, password,context);
                }
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
                ((AppCompatActivity) context).finish();


                //break;
            }
        }
        return TAG_CONNEXION_LOCAL;
    }

    public void TokenApi() {

        Intent iM = new Intent(context, MainActivity.class);
        Intent iLg = new Intent(context, Login.class);
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<TokenResponse> call = apiService.getVerifyToken(getToken());

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
                        if (statusCode == 201) saveToken(tokenResponse.getToken(),context);
                        Constants.Token = getToken();
                        //Log.d("TAG", "" + user_login.getUser().getUsername() + " " + user_login.getMessage());
                        Toast.makeText(context, "Validation : " + statusCode, Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(context, MainActivity.class);
//                        i.putExtra("TAG","online");
                        Constants.AffichageMessage(TAG_CHARGEMENT_VALIDE, ((AppCompatActivity) context));
                        context.startActivity(i);
                        ((AppCompatActivity) context).finish();
                    }
                } else {
                    // The HTTP request was not successful (status code is not 2xx).
                    // You can handle errors here based on the response status code.

                    Constants.TAG_CONNEXION = statusCode;

                    // Handle different status codes as per your API's conventions.
                    if (statusCode == 401) {
                        // Unauthorized, handle accordingly (e.g., reauthentication).
                        saveToken("",context);
                        Constants.AffichageMessage("Erreur Unauthorized by server ", ((AppCompatActivity) context));
                        context.startActivity(iLg);
                    } else {
                        //(statusCode == 404) {
                        Constants.AffichageMessage("Erreur 404", ((AppCompatActivity) context));
//                        i.putExtra("TAG","online");
                        context.startActivity(iM);
                        ((AppCompatActivity) context).finish();
                    }
                    // Not found, handle accordingly (e.g., show a 404 error message).
                    // Resource not found
                    //}
                }


            }

            @Override
            public void onFailure(Call<TokenResponse> call, Throwable t) {
//                Intent i = new Intent(Loading_Srcreen.this,
//                        Login.class);
//                startActivity(i);
                Constants.AffichageMessage(TAG_ERREUR_SYSTEM, ((AppCompatActivity) context));
                Constants.TAG_CONNEXION = call.hashCode();
                sharedPreferences = context.getSharedPreferences(Constants.lOGIN_KEY, MODE_PRIVATE);
                if (sharedPreferences.getBoolean(Constants.lOGIN_KEY, true)) {
                    String s1 = sharedPreferences.getString("username", "");
                    if (s1.equals("")) {
                        context.startActivity(iLg);
                    } else context.startActivity(iM);
                }
                ((AppCompatActivity) context).finish();
            }


        });

        //startActivity(iM);

    }

    private String getToken() {
        SharedPreferences preferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return preferences.getString("token", "");
    }


}
