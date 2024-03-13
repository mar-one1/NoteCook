package com.example.notecook.ViewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;

import com.example.notecook.Model.User;
import com.example.notecook.Repo.UserRepository;


public class UserViewModel {

    private UserRepository repository;

    public UserViewModel(Context context) {
        repository = new UserRepository(context);
    }

    public LiveData<User> getUser(String username,Context context) {
        return repository.getUserApi(username,context);
    }

    public LiveData<User> UpdateUser(User user,Context context) {
        return repository.UpdateUserApi(user, context);
    }
}
