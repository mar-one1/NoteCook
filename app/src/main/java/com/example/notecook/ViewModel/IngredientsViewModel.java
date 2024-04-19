package com.example.notecook.ViewModel;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.notecook.Model.Ingredients;
import com.example.notecook.Repo.IngredientsRecipeRepository;

import java.io.Closeable;
import java.util.List;

public class IngredientsViewModel extends ViewModel implements ViewModelProvider.Factory {

    private Context context;
    private Activity appCompatActivity;
    private IngredientsRecipeRepository repository;

    public IngredientsViewModel(Context context, Activity appCompatActivity) {
        this.context = context;
        this.appCompatActivity = appCompatActivity;
        repository = new IngredientsRecipeRepository(appCompatActivity);
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(IngredientsViewModel.class)) {
            return (T) new IngredientsViewModel(context,appCompatActivity);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

    public LiveData<List<Ingredients>> getAllIngredientsApi() {
        return repository.getIngredientsRecipeApi();
    }

}
