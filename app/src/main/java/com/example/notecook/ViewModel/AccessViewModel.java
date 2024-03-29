package com.example.notecook.ViewModel;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.notecook.Model.User;
import com.example.notecook.Repo.AccessRepository;
import com.example.notecook.Repo.RecipeRepository;

public class AccessViewModel extends ViewModel {

    private AccessRepository repository;

    public AccessViewModel(Context context, Activity activity) {
        repository = new AccessRepository(context,activity);
    }

    public LiveData<String> connectApi(String username,String password) {
        return repository.connectionApi(username,password);
    }

    public LiveData<String> verifyToken() {
        return repository.TokenApi();
    }


}
