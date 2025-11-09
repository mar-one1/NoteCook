package com.example.notecook.ViewModel;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.notecook.Model.User;
import com.example.notecook.Repo.AccessRepository;
import com.example.notecook.Repo.RecipeRepository;
import com.example.notecook.Utils.SharedRecipeViewModel;

public class AccessViewModel extends ViewModel {

    private AccessRepository repository;
    private SharedRecipeViewModel viewModel;

    public AccessViewModel(Context context, Activity activity,SharedRecipeViewModel viewModel) {
        repository = new AccessRepository(context,activity,viewModel);
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


}
