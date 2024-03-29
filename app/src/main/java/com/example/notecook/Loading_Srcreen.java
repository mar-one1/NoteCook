package com.example.notecook;

import static com.example.notecook.Utils.Constants.TAG_CHARGEMENT_VALIDE;
import static com.example.notecook.Utils.Constants.TAG_ERREUR_SYSTEM;
import static com.example.notecook.Utils.Constants.getToken;
import static com.example.notecook.Utils.Constants.getUserInput;
import static com.example.notecook.Utils.Constants.isConnected;
import static com.example.notecook.Utils.Constants.saveToken;
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
        if (!isConnected() && Objects.equals(getToken(Loading_Srcreen.this), "")) {
            Constants.AffichageMessage("Welcome to Notebook APP!!!","ok", Loading_Srcreen.this);
            startActivity(i);
        } else if (!Objects.equals(getToken(Loading_Srcreen.this), "") && isConnected()) {
            accessVM.verifyToken().observe(this, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    if(s!=null && !s.equals(""))
                        Toast.makeText(Loading_Srcreen.this, "welcome back", Toast.LENGTH_SHORT).show();
                }
            });
            //TokenApi();
        }else if (!Objects.equals(getToken(Loading_Srcreen.this), "") && !isConnected()) {
            startActivity(iM);
        } else
            startActivity(i);
        setContentView(view);
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

}
