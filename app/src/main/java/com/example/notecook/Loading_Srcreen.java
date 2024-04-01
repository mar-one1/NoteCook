package com.example.notecook;

import static com.example.notecook.Utils.Constants.MODE_ONLINE;

import static com.example.notecook.Utils.Constants.Token;
import static com.example.notecook.Utils.Constants.getToken;



import android.content.Intent;
import android.content.IntentFilter;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.NetworkChangeReceiver;

import com.example.notecook.ViewModel.AccessViewModel;
import com.example.notecook.databinding.ActivityLoadingSrcreenBinding;

import java.util.Objects;



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
        Token = getToken(this);
        Log.e("tag",Token);
        Intent i = new Intent(getBaseContext(), Login.class);
        Intent iM = new Intent(getBaseContext(), MainActivity.class);
        if (!MODE_ONLINE && Objects.equals(Token, "")) {
            Constants.AffichageMessage("Welcome to Notebook APP!!!","ok", Loading_Srcreen.this);
            startActivity(i);
        } else if (!Objects.equals(Token, "") && MODE_ONLINE) {
            accessVM.verifyToken().observe(this, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    if(s!=null && !s.equals(""))
                        Toast.makeText(Loading_Srcreen.this, "welcome back", Toast.LENGTH_SHORT).show();
                }
            });
            //TokenApi();
        }else if (!Objects.equals(Token, "") && !MODE_ONLINE) {
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
