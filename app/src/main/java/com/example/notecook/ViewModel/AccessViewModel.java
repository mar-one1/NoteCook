package com.example.notecook.ViewModel;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.notecook.Model.User;
import com.example.notecook.Repo.AccessRepository;
import com.example.notecook.Repo.CategorieRepository;
import com.example.notecook.Repo.UserRepository;

public class AccessViewModel extends ViewModel implements ViewModelProvider.Factory {

    private Context context;
    private Activity appCompatActivity;
    private AccessRepository repository;
    private SharedRecipeViewModel viewModel;

    public AccessViewModel(Context context, Activity activity,SharedRecipeViewModel viewModel) {
        this.context =context;
        this.appCompatActivity = activity;
        this.viewModel = viewModel;
        repository = new AccessRepository(context,activity,viewModel);
    }
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new AccessViewModel(context,appCompatActivity,viewModel);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

    public LiveData<String> connectApi(String username,String password) {
        return repository.connectionApi(username,password);
    }

    public LiveData<String> verifyToken() {
        return repository.TokenApi();
    }

    public LiveData<User> connectLocal(String username,String password) {
        return repository.ConnectLocal(username,password);
    }

    public LiveData<String> mustChangePassword(int userId,String OldPassword,String NewPassword) {
        return repository.changePassword(userId,OldPassword,NewPassword);
    }


}
