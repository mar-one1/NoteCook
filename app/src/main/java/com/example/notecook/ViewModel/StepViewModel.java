package com.example.notecook.ViewModel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.notecook.Repo.StepRecipeRepository;
import com.example.notecook.Utils.ImageHelper;

public class StepViewModel extends ViewModel implements ViewModelProvider.Factory {
    private StepRecipeRepository repository;
    private Context context;
    private Activity appCompatActivity;

    public StepViewModel(StepRecipeRepository repository, Context context, Activity appCompatActivity) {
        this.repository = repository;
        this.context = context;
        this.appCompatActivity = appCompatActivity;
    }

    public StepViewModel(Context context, Activity appCompatActivity) {
        this.context = context;
        this.appCompatActivity = appCompatActivity;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RecipeViewModel.class)) {
            return (T) new StepViewModel(context, appCompatActivity);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

    public void postImageStepLocal(Bitmap image, int id)
    {
        this.repository.updateStepImageLocally(image,id);
    }
}
