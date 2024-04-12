package com.example.notecook.ViewModel;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.notecook.Dto.RecipeResponse;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Repo.RecipeRepository;

import java.util.List;
import java.util.Map;


public class RecipeViewModel extends ViewModel implements ViewModelProvider.Factory {
    private RecipeRepository repository;
    private Context context;
    private Activity appCompatActivity;

    public RecipeViewModel(Context context) {
        repository = new RecipeRepository(context);
        this.context = context;
    }

    public RecipeViewModel(Context context, Activity appCompatActivity) {
        this.context = context;
        this.appCompatActivity = appCompatActivity;
        repository = new RecipeRepository(context, appCompatActivity);

    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(RecipeViewModel.class)) {
            return (T) new RecipeViewModel(context, appCompatActivity);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

    public LiveData<List<Recipe>> getRecipes() {
        return repository.getRecipes();
    }

    public LiveData<List<Recipe>> getRecipesByFilter(Map<String, String> conditions) {
        return repository.getRecipesByConditionApi(conditions);
    }

    public LiveData<List<Recipe>> getRecipesByUsername(String username) {
        return repository.getRecipesByUsername(username);
    }

    public LiveData<List<RecipeResponse>> getFullRecipesByUsername(String username) {
        return repository.getFullRecipesByUsername(username);
    }

    public LiveData<Recipe> getRecipe(int id_recipe) {
        return repository.getLocalRecipe(id_recipe);
    }

    public LiveData<RecipeResponse> getFullRecipeApi(int id_recipe) {
        return repository.getFullRecipeApi(id_recipe);
    }

    public LiveData<RecipeResponse> getFullRecipeLocal(Recipe recipe) {
        return repository.getFullLocalRecipe(recipe);
    }


    public LiveData<List<Recipe>> getRecipesLocal(int id_user) {
        return repository.getLocalRecipes(id_user);
    }

    public LiveData<List<Recipe>> getRecipesByUsernameLocal(int id_user) {
        return repository.getLocalRecipes(id_user);
    }

    public void uploadRecipeImage(int idRecipe, Bitmap bitmap) {
        repository.uploadImageRecipe(idRecipe, bitmap);
    }


    public LiveData<Recipe> postRecipe(Recipe recipe, Bitmap bitmap) {
        return repository.InsertRecipeApi(recipe, bitmap);
    }

    public int postRecipeLocal(Recipe recipe, int id_user) {
        return repository.insertRecipeLocally(recipe, id_user);
    }

    public LiveData<List<Recipe>> SearchRecipe(String s) {
        return repository.searchRecipes(s);
    }


}