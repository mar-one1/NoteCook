package com.example.notecook;

import static com.example.notecook.Utils.Constants.TAG_CHARGEMENT_VALIDE;
import static com.example.notecook.Utils.Constants.TAG_ERREUR_SYSTEM;
import static com.example.notecook.Utils.Constants.getUserInput;
import static com.example.notecook.Utils.Constants.isConnected;
import static com.example.notecook.Utils.Constants.user_login;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Dto.TokenResponse;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.NetworkChangeReceiver;
import com.example.notecook.Utils.SimpleService;
import com.example.notecook.ViewModel.AccessViewModel;
import com.example.notecook.databinding.ActivityLoadingSrcreenBinding;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Loading_Srcreen extends AppCompatActivity {

    private ActivityLoadingSrcreenBinding binding;
    private IntentFilter filtreConectivite = new IntentFilter();
    private NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
    private AccessViewModel accessVM;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_srcreen);
        registerReceiver(networkChangeReceiver, filtreConectivite);
        binding = ActivityLoadingSrcreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        accessVM = new AccessViewModel(this,this);

        Intent i = new Intent(getBaseContext(), Login.class);
        Intent iM = new Intent(getBaseContext(), MainActivity.class);
        if (!isConnected() && Objects.equals(getToken(), "")) {
            Constants.AffichageMessage("Welcome to Notebook APP!!!", Loading_Srcreen.this);
            startActivity(i);
        } else if (!Objects.equals(getToken(), "") && isConnected()) {
            accessVM.verifyToken().observe(this, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    if(s!=null && !s.equals(""))
                        Toast.makeText(Loading_Srcreen.this, "welcome back", Toast.LENGTH_SHORT).show();
                }
            });
            //TokenApi();
        }else if (!Objects.equals(getToken(), "") && !isConnected()) {
            startActivity(iM);
        } else
            startActivity(i);
        setContentView(view);
    }


    public void TokenApi() {

        Intent iM = new Intent(getBaseContext(), MainActivity.class);
        Intent iLg = new Intent(getBaseContext(), Login.class);
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
                        if (statusCode == 201) saveToken(tokenResponse.getToken());
                        Constants.Token = getToken();
                        //Log.d("TAG", "" + user_login.getUser().getUsername() + " " + user_login.getMessage());
                        Toast.makeText(getApplicationContext(), "Validation : " + statusCode, Toast.LENGTH_SHORT).show();

                        Intent i = new Intent(getBaseContext(), MainActivity.class);
//                        i.putExtra("TAG","online");
                        Constants.AffichageMessage(TAG_CHARGEMENT_VALIDE, Loading_Srcreen.this);
                        startActivity(i);
                        finish();
                    }
                } else {
                    // The HTTP request was not successful (status code is not 2xx).
                    // You can handle errors here based on the response status code.

                    Constants.TAG_CONNEXION = statusCode;

                    // Handle different status codes as per your API's conventions.
                    if (statusCode == 401) {
                        // Unauthorized, handle accordingly (e.g., reauthentication).
                        saveToken("");
                        Constants.AffichageMessage("Erreur Unauthorized by server ", Loading_Srcreen.this);
                        startActivity(iLg);
                    } else {
                        //(statusCode == 404) {
                        Constants.AffichageMessage("Erreur 404", Loading_Srcreen.this);
//                        i.putExtra("TAG","online");
                        startActivity(iM);
                        finish();
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
                Constants.AffichageMessage(TAG_ERREUR_SYSTEM, Loading_Srcreen.this);
                Constants.TAG_CONNEXION = call.hashCode();

                String s1 = getUserInput(getBaseContext());
                if (s1.equals("")) {
                    startActivity(iLg);
                    finish();
                } else {
                    startActivity(iM);
                    finish();
                }

            }


        });

        //startActivity(iM);

    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkChangeReceiver, filtreConectivite);
        if (Constants.alertDialog != null && Constants.alertDialog.isShowing()) {
            Constants.alertDialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkChangeReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Constants.alertDialog != null && Constants.alertDialog.isShowing()) {
            Constants.alertDialog.dismiss();
        }
    }

    private String getToken() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return preferences.getString("token", "");
    }

    public boolean isOnline(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Network network = connectivityManager.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
                    return networkCapabilities != null &&
                            (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                    networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR));
                }
            } else {
                // For devices running older versions than Android M
                // Check if any network is available
                NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                return activeNetworkInfo != null && activeNetworkInfo.isConnected();
            }
        }
        return false;
    }

    private void saveToken(String token) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.apply();
    }


}
