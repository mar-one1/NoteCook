package com.example.notecook.ViewModel;

import static com.example.notecook.Data.RecipeDatasource.createRecipe;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.Token;
import static com.example.notecook.Utils.Constants.list_recipe;
import static com.example.notecook.Utils.Constants.user_login;
import static com.example.notecook.Utils.Constants.user_login_local;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Data.RecipeDatasource;
import com.example.notecook.Model.Recipe;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RecipeViewModel extends ViewModel {
    private MutableLiveData<List<Recipe>> remoteRecipeList;
    private MutableLiveData<List<Recipe>> remoteRecipeListByUserId;

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
    public LiveData<List<Recipe>> getRecipebyiduser(Context context,int id) {
        //if the list is null
        if (remoteRecipeListByUserId == null) {
            remoteRecipeListByUserId = new MutableLiveData<List<Recipe>>();
            //we will load it asynchronously from server in this method
            getRecipeByUserIdApi(context,id);

        }
        //finally we will return the list
        return remoteRecipeListByUserId;
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

    public void getRecipeByUserIdApi(Context context,int id_user) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        Call<List<Recipe>> call = apiService.getRecipeByIdUser(Token,id_user);
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    List<Recipe> recipes = response.body();
                    Log.d("recipes", recipes.toString());

                    remoteRecipeListByUserId.setValue(recipes);

                    Log.d("listrecipes by user", remoteRecipeListByUserId.toString());
                    TAG_CONNEXION_MESSAGE = response.message();
                    TAG_CONNEXION = response.code();
                    //TokenApi(Token);
                    if (remoteRecipeListByUserId.getValue().size() != 0) {
                        synchronizeData(context,list_recipe, remoteRecipeListByUserId.getValue());
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
                if (remoteRecipe.getFrk_user() == user_login.getUser().getId_User()) {
                    // Recipe exists locally; update it if necessary
                    localRecipe.setFrk_user(user_login.getUser().getId_User());
                    if (!remoteRecipe.equals(localRecipe)) {
                        // Update local recipe with remote data
                        remoteRecipe.setFrk_user(user_login_local.getUser().getId_User());
                        updateRecipeLocally(context,remoteRecipe, localRecipe.getId_recipe());
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                // Recipe doesn't exist locally; insert it
                insertRecipeLocally(context,remoteRecipe,user_login_local.getUser().getId_User());
            }
        }

//        // Handle deleted recipes
//        for (Recipe localRecipe : localRecipes) {
//            boolean found = false;
//            for (Recipe remoteRecipe : remoteRecipes) {
//                if (localRecipe.getNom_recipe().equals(remoteRecipe.getNom_recipe())) {
//                    found = true;
//                    break;
//                }
//            }
//            if (!found) {
//                // Recipe exists locally but not remotely; mark it as deleted
//                markRecipeAsDeletedLocally(localRecipe, context);
//            }
//        }
    }

    public int insertRecipeLocally(Context context,Recipe remoteRecipe,int id_user) {
        RecipeDatasource recipeDatasource = new RecipeDatasource(context);
        recipeDatasource.open();
        Recipe recipe =createRecipe(remoteRecipe.getIcon_recipe(), remoteRecipe.getNom_recipe(), remoteRecipe.getFav(), id_user);
        recipeDatasource.close();
        return recipe.getId_recipe();
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