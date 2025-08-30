package com.example.notecook.ViewModel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.notecook.Repo.StepRecipeRepository;
import com.example.notecook.Repo.UserRepository;
import com.example.notecook.Utils.ImageHelper;

public class StepViewModel extends ViewModel implements ViewModelProvider.Factory {
    private StepRecipeRepository repository;
    private Context context;
    private Activity appCompatActivity;

    public StepViewModel(Context context, Activity appCompatActivity) {
        this.context = context;
        this.appCompatActivity = appCompatActivity;
        repository = new StepRecipeRepository(context);
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(StepViewModel.class)) {
            return (T) new StepViewModel(context, appCompatActivity);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

    public void postImageStepLocal(Bitmap image, int id)
    {
        this.repository.updateStepImageLocally(image,id);
    }

    public LiveData<String> postImageStepRemote(String unique, Bitmap image)
    {
        return this.repository.uploadRemoteImageStep(unique,image);
    }
}
