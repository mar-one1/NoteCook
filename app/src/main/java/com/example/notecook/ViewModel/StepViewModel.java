package com.example.notecook.ViewModel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.notecook.Repo.StepRecipeRepository;

public class StepViewModel extends ViewModel implements ViewModelProvider.Factory {
    private StepRecipeRepository repository;
    private Context context;
    private Activity appCompatActivity;
    private SharedRecipeViewModel viewModel;

    public StepViewModel(Context context, Activity appCompatActivity, SharedRecipeViewModel viewModel) {
        this.context = context;
        this.appCompatActivity = appCompatActivity;
        this.viewModel = viewModel;
        repository = new StepRecipeRepository(context,viewModel);
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(StepViewModel.class)) {
            return (T) new StepViewModel(context, appCompatActivity,viewModel);
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
