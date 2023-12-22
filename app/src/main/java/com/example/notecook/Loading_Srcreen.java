package com.example.notecook;

import static com.example.notecook.Utils.Constants.TAG_CHARGEMENT_VALIDE;
import static com.example.notecook.Utils.Constants.TAG_ERREUR_SYSTEM;
import static com.example.notecook.Utils.Constants.user_login;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Api.TokenResponse;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.SimpleService;
import com.example.notecook.databinding.ActivityLoadingSrcreenBinding;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Loading_Srcreen extends AppCompatActivity {

    ActivityLoadingSrcreenBinding binding;
    SimpleService service = new SimpleService();
    IntentFilter filtreConectivite = new IntentFilter();
    NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
    private SharedPreferences sharedPreferences;

       /* Thread welcomeThread = new Thread() {
            @Override
            public void run() {


                try {
                    super.run();
                    sleep(3000);
                    //Delay of 5 seconds
                } catch (Exception e) {

                } finally {
//                    Intent i;


//                    if(Objects.equals(getToken(), "")) {
//                        i = new Intent(Loading_Srcreen.this, Login.class);
//                        startActivity(i);
//                    }
//                    else if(!Objects.equals(getToken(), "")) {
//
//                        if (Constants.TAG_CONNEXION != 200) {
//                            //saveToken(getToken()+"Local");
//                        }
//                        i = new Intent(Loading_Srcreen.this, MainActivity.class);
//                        startActivity(i);
//                    }

                    finish();
                }
            }
        };
        welcomeThread.start();*/

        /*ProgressDialog progress = new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Wait while loading...");
        progress.setCancelable(false); // disable dismiss by tapping outside of the dialog
        progress.show();
    // To dismiss the dialog
        progress.dismiss();*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading_srcreen);
        registerReceiver(networkChangeReceiver, filtreConectivite);
        binding = ActivityLoadingSrcreenBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
//        MyAsyncTask myAsyncTask = new MyAsyncTask();
//        myAsyncTask.execute();
        Intent i = new Intent(getBaseContext(), Login.class);
        if (!isOnline(getBaseContext()) && Objects.equals(getToken(), "")) {
            Constants.AffichageMessage("Welcome to Notebook APP!!!", Loading_Srcreen.this);
            startActivity(i);
        } else if (!Objects.equals(getToken(), ""))
            TokenApi();
        else {
            startActivity(i);
        }
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
                if (response.isSuccessful()) {
                    //TokenResponse tokenResponse = response.body();
                    if (tokenResponse != null) {
                        user_login = tokenResponse;
                        Constants.Token = getToken();
                        //Log.d("TAG", "" + user_login.getUser().getUsername() + " " + user_login.getMessage());
                        Constants.TAG_CONNEXION = response.code();

                        //Toast.makeText(getApplicationContext(), "Validation : " + code, Toast.LENGTH_SHORT).show();
                        // Store the token securely (e.g., in SharedPreferences) for later use
                        Intent i = new Intent(getBaseContext(), MainActivity.class);
//                        i.putExtra("TAG","online");
                        Constants.AffichageMessage(TAG_CHARGEMENT_VALIDE, Loading_Srcreen.this);
                        startActivity(i);
                        finish();
                    }
                } else {
                    // The HTTP request was not successful (status code is not 2xx).
                    // You can handle errors here based on the response status code.
                    int statusCode = response.code();
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
                sharedPreferences = getSharedPreferences(Constants.lOGIN_KEY, Context.MODE_PRIVATE);
                if (sharedPreferences.getBoolean(Constants.lOGIN_KEY, true)) {
                    String s1 = sharedPreferences.getString("username", "");
                    if (s1.equals("")) {
                        startActivity(iLg);
                    } else startActivity(iM);
                }
                finish();
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

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        //should check null because in airplane mode it will be a null
        return (netInfo != null && netInfo.isConnected());
    }

    private void saveToken(String token) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (checkInternet(context)) {
                Toast.makeText(context, "Network Available Do operations", Toast.LENGTH_LONG).show();
            }
        }

        boolean checkInternet(Context context) {

            return service.isNetworkAvailable();
        }
    }


}
