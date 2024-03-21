package com.example.notecook.ViewModel;

import android.content.Context;
import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.notecook.Model.User;
import com.example.notecook.Repo.UserRepository;


public class UserViewModel extends ViewModel{

    private UserRepository repository;
    private Context context;
    private AppCompatActivity appCompatActivity;

    public UserViewModel(Context context) {
        this.context = context;
        repository = new UserRepository(context);
    }

    public UserViewModel(Context context, AppCompatActivity appCompatActivity) {
        this.context = context;
        this.appCompatActivity = appCompatActivity;
        repository = new UserRepository(context, appCompatActivity);
    }


    public LiveData<User> getUser(String username) {
        return repository.getUserApi(username);
    }

    public LiveData<User> getUserRecipe(int id_recipe) {
        return repository.getUserByIdRecipeApi(id_recipe);
    }

    public LiveData<User> postUser(User user, String imageUrl, Bitmap bitmap, String type) {
        return repository.InsertUserApi(user, imageUrl, bitmap, type);
    }


    public LiveData<User> UpdateUser(User user, Bitmap bitmap) {
        return repository.UpdateUserApi(user, bitmap);
    }

    public LiveData<User> getUserLocal(String username, String tag) {
        return repository.getLocalUserLogin(username, tag);
    }
}
