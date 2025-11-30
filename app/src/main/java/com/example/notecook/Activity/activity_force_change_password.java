package com.example.notecook.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import com.example.notecook.ViewModel.AccessViewModel;
import com.example.notecook.ViewModel.SharedRecipeViewModel;
import com.example.notecook.databinding.FragmentActivityForceChangePasswordBinding;

public class activity_force_change_password extends AppCompatActivity {

    private ProgressBar progressBar;
    private Long userId;
    private FragmentActivityForceChangePasswordBinding binding;
    private AccessViewModel accessViewModel;
    private SharedRecipeViewModel viewModel;
    private View view;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentActivityForceChangePasswordBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
                viewModel = new SharedRecipeViewModel();
        accessViewModel = new AccessViewModel(this,this,viewModel);
        progressBar = binding.progressBar;

        binding.btnChange.setOnClickListener(view -> changePassword());
        setContentView(view);
    }
    private void changePassword()
    {

        if (binding.etOldPassword.getText().toString().isEmpty()) {
            binding.etOldPassword.setError("Required");
            return;
        }
        if (binding.etNewPassword.getText().toString().isEmpty() || binding.etNewPassword.getText().length() < 8) {
            binding.etNewPassword.setError("Min 8 chars");
            return;
        }
        if (binding.etConfirm.getText().equals(binding.etNewPassword.getText())) {
            binding.etConfirm.setError("Doesn't match");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        accessViewModel.mustChangePassword(viewModel.getUserLogin().getValue().getUser().getId_User(),binding.etOldPassword.getText().toString(),binding.etNewPassword.getText().toString()).observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {

            }
        });
    }
}
