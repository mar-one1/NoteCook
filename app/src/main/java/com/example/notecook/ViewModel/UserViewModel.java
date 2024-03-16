package com.example.notecook.ViewModel;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.notecook.Model.User;
import com.example.notecook.Repo.UserRepository;


public class UserViewModel extends ViewModel {

    private UserRepository repository;
    private Context context;

    public UserViewModel(Context context) {
        this.context = context;
        repository = new UserRepository(context);
    }

    public LiveData<User> getUser(String username) {
        return repository.getUserApi(username);
    }

    public LiveData<User> UpdateUser(User user,Bitmap bitmap) {
        return repository.UpdateUserApi(user,bitmap);
    }

    public void getUserLocal(String username,String tag) {
        repository.getLocalUserLogin(username, tag);
    }
}
