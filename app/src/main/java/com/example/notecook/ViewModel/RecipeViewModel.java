package com.example.notecook.ViewModel;

import static com.example.notecook.Data.RecipeDatasource.createRecipe;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.Token;
import static com.example.notecook.Utils.Constants.list_recipe;
import static com.example.notecook.Utils.Constants.user_login;
import static com.example.notecook.Utils.Constants.user_login_local;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Data.RecipeDatasource;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Repo.RecipeRepository;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RecipeViewModel extends ViewModel {
    private RecipeRepository repository;

    public RecipeViewModel(Context context) {
        repository = new RecipeRepository(context);
    }

    public LiveData<List<Recipe>> getRecipes(Context context) {
        return repository.getRecipes(context);
    }

    public LiveData<List<Recipe>> getRecipesByUsername(Context context, String username) {
        return repository.getRecipesByUsername(context, username);
    }

    public void uploadRecipeImage(int idRecipe, Bitmap bitmap, Context context) {
        repository.uploadImageRecipe(idRecipe, bitmap, context);
    }



}