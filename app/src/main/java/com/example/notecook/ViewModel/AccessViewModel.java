package com.example.notecook.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.notecook.Model.User;
import com.example.notecook.Repo.AccessRepository;
import com.example.notecook.Repo.RecipeRepository;

public class AccessViewModel {

    private AccessRepository repository;
    private Context context;

    public AccessViewModel(Context context) {
        repository = new AccessRepository(context);
        this.context = context;
    }

    public LiveData<String> connectApi(String username,String password) {
        return repository.connectionApi(username,password);
    }
}
