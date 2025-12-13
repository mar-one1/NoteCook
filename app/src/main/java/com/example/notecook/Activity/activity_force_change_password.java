package com.example.notecook.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.InputValidator;
import com.example.notecook.ViewModel.AccessViewModel;
import com.example.notecook.ViewModel.SharedRecipeViewModel;
import com.example.notecook.databinding.FragmentActivityForceChangePasswordBinding;

public class activity_force_change_password extends AppCompatActivity {

    private ProgressBar progressBar;
    private FragmentActivityForceChangePasswordBinding binding;
    private AccessViewModel accessViewModel;
    private SharedRecipeViewModel viewModel;
    private View view;
    private InputValidator inp = new InputValidator();
    private ViewPager2 Vp2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentActivityForceChangePasswordBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        viewModel = new ViewModelProvider(this).get(SharedRecipeViewModel.class);
        accessViewModel = new AccessViewModel(this, this, viewModel);
        progressBar = binding.progressBar;
        Vp2 = view.findViewById(R.id.vp2);
        binding.btnChange.setOnClickListener(view ->
                {

                    Log.e("password","btn clicked");
                    if (inp.isValidChangePassWord(binding.etOldPassword, binding.etNewPassword, binding.etConfirm)) {
                        Log.e("password", "valideted success");
                        changePassword();
                    }
                }
        );
        setContentView(view);
    }

    private void changePassword() {
        progressBar.setVisibility(View.VISIBLE);
        // GET DATA FROM INTENT
        Intent intent = getIntent();
        int userId = intent.getIntExtra("USER_ID", -1);
        String username = intent.getStringExtra("USERNAME");
        // DEBUG
        Log.d("password", "User ID = " + userId + ", Username = " + username);
        if (username != null && !username.isEmpty() && userId!=-1)
            accessViewModel.mustChangePassword(userId, binding.etOldPassword.getText().toString(), binding.etNewPassword.getText().toString()).observe(this, new Observer<String>() {
                @Override
                public void onChanged(String s) {
                    if(!s.isEmpty()) {
                        Intent intent = new Intent(getBaseContext(), Login.class);
                        startActivity(intent);
                        Constants.showSnackPar(view,s);
                        progressBar.setVisibility(View.GONE);
                    }else {
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        else progressBar.setVisibility(View.GONE);
    }
}
