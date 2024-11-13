package com.example.notecook.ViewModel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.notecook.Model.User;
import com.example.notecook.Repo.UserRepository;


public class UserViewModel extends ViewModel implements ViewModelProvider.Factory{

    private UserRepository repository;
    private Context context;
    private Activity appCompatActivity;

    public UserViewModel(Context context, Activity appCompatActivity) {
        this.context = context;
        this.appCompatActivity = appCompatActivity;
        repository = new UserRepository(context, appCompatActivity);
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserViewModel.class)) {
            return (T) new UserViewModel(context,appCompatActivity);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
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

    public LiveData<String> updateUserImageRemote(String username,Bitmap bitmap,String oldPath,String type) {
        return repository.uploadImage(username,bitmap,oldPath,type);
    }
    public LiveData<User> UpdateUserLocal(User user, Bitmap bitmap) {
        return repository.UpdateUserApi(user, bitmap);
    }

    public LiveData<User> getUserLocal(String username, String tag) {
        return repository.getLocalUserLogin(username, tag);
    }

    public LiveData<User> postUserLocal(User user,Bitmap bitmap) {
        return repository.InsertUserLocal(user,bitmap);
    }
}
