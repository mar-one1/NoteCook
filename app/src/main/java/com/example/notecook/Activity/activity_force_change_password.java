package com.example.notecook.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentActivityForceChangePasswordBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        viewModel = new SharedRecipeViewModel();
        accessViewModel = new AccessViewModel(this, this, viewModel);
        progressBar = binding.progressBar;

        binding.btnChange.setOnClickListener(view ->
                {
                    if (inp.isValidChangePassWord(binding.etOldPassword, binding.etNewPassword, binding.etConfirm))
                        changePassword();
                }
        );
        setContentView(view);
    }

    private void changePassword() {
        progressBar.setVisibility(View.VISIBLE);
        if (viewModel.getUserLogin().getValue().getUser() != null)
            accessViewModel.mustChangePassword(viewModel.getUserLogin().getValue().getUser().getId_User(), binding.etOldPassword.getText().toString(), binding.etNewPassword.getText().toString()).observe(this, new Observer<String>() {
                @Override
                public void onChanged(String s) {

                }
            });
        else progressBar.setVisibility(View.GONE);
    }
}
