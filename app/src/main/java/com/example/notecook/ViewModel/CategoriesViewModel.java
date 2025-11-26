package com.example.notecook.ViewModel;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.notecook.Model.Category_Recipe;
import com.example.notecook.Repo.CategorieRepository;

import java.util.List;

public class CategoriesViewModel extends ViewModel implements ViewModelProvider.Factory {
    private CategorieRepository repository;
    private Context context;
    private Activity appCompatActivity;
    private SharedRecipeViewModel viewModel;


    public CategoriesViewModel(Context context, Activity appCompatActivity,SharedRecipeViewModel viewModel) {
        this.context = context;
        this.appCompatActivity = appCompatActivity;
        this.viewModel = viewModel;
        repository = new CategorieRepository(context, appCompatActivity,viewModel);

    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(CategoriesViewModel.class)) {
            return (T) new CategoriesViewModel(context, appCompatActivity,viewModel);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

    public LiveData<List<Category_Recipe>> getCategories() {
        return repository.getAllCatRecipe();
    }
}
