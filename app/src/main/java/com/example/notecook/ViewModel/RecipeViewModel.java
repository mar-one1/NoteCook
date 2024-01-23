package com.example.notecook.ViewModel;
import static com.example.notecook.Data.RecipeDatasource.createRecipe;
import static com.example.notecook.Utils.Constants.Remotelist_recipe;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.Token;
import static com.example.notecook.Utils.Constants.list_recipe;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Data.RecipeDatasource;

import com.example.notecook.Model.Recipe;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RecipeViewModel extends ViewModel {
    private MutableLiveData<List<Recipe>> remoteRecipeList;

    //we will call this method to get the data
    public LiveData<List<Recipe>> getRecipe(Context context) {
        //if the list is null
        if (remoteRecipeList == null) {
            remoteRecipeList = new MutableLiveData<List<Recipe>>();
            //we will load it asynchronously from server in this method
            getRecipeApi(context);
        }
        //finally we will return the list
        return remoteRecipeList;
    }


    public void getRecipeApi(Context context) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Recipe>> call = apiService.getAllRecipes(Token);
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    List<Recipe> recipes = response.body();
                    Log.d("recipes", recipes.toString());

                    remoteRecipeList.setValue(recipes);

                    Log.d("listrecipes", remoteRecipeList.toString());
                    TAG_CONNEXION_MESSAGE = response.message();
                    TAG_CONNEXION = response.code();
                    //TokenApi(Token);
                    if (remoteRecipeList.getValue().size() != 0) {
                        synchronizeData(context,list_recipe, remoteRecipeList.getValue());
                    }
//                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(R.id.fl_main, new MainFragment());
//                    fragmentTransaction.commit();
                } else {
                    // Handle error response here
                    int statusCode = response.code();
                    TAG_CONNEXION = statusCode;
                    TAG_CONNEXION_MESSAGE = response.message();
                    if (response.errorBody() != null) {
                        try {
                            String errorResponse = response.errorBody().string();
                            // Print or log the errorResponse for debugging
                            Log.e("token", "Error Response: " + errorResponse);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                TAG_CONNEXION = call.hashCode();
            }
        });


    }

    public void synchronizeData(Context context,List<Recipe> localRecipes, List<Recipe> remoteRecipes) {
        // Step 1: Compare local and remote data to identify differences
        for (Recipe remoteRecipe : remoteRecipes) {
            boolean found = false;
            for (Recipe localRecipe : localRecipes) {
                if (remoteRecipe.getId_recipe() == localRecipe.getId_recipe()) {
                    // Recipe exists locally; update it if necessary
                    if (!remoteRecipe.equals(localRecipe)) {
                        // Update local recipe with remote data
                        updateRecipeLocally(context,remoteRecipe, localRecipe.getId_recipe());
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                // Recipe doesn't exist locally; insert it
                insertRecipeLocally(context,remoteRecipe);
            }
        }

        // Handle deleted recipes
        for (Recipe localRecipe : localRecipes) {
            boolean found = false;
            for (Recipe remoteRecipe : remoteRecipes) {
                if (localRecipe.getId_recipe() == remoteRecipe.getId_recipe()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                // Recipe exists locally but not remotely; mark it as deleted
                markRecipeAsDeletedLocally(localRecipe, context);
            }
        }
    }

    private void insertRecipeLocally(Context context,Recipe remoteRecipe) {
        RecipeDatasource recipeDatasource = new RecipeDatasource(context);
        recipeDatasource.open();
        createRecipe(remoteRecipe.getIcon_recipe(), remoteRecipe.getNom_recipe(), remoteRecipe.getFav(), remoteRecipe.getFrk_user());
        recipeDatasource.close();
    }

    private void updateRecipeLocally(Context context,Recipe remoteRecipe, int id) {
        RecipeDatasource recipeDatasource = new RecipeDatasource(context);
        recipeDatasource.open();
        recipeDatasource.UpdateRecipe(remoteRecipe, id);
        recipeDatasource.close();
    }

    private static void markRecipeAsDeletedLocally(Recipe localRecipe, Context context) {
        RecipeDatasource recipeDatasource = new RecipeDatasource(context);
        recipeDatasource.open();
        recipeDatasource.deleteRecipe(localRecipe);
        recipeDatasource.close();
    }

}