package com.example.notecook.Repo;

import static com.example.notecook.Utils.Constants.Detail_CurrentRecipe;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.Token;

import android.content.Context;
import android.util.Log;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Data.DetailRecipeDataSource;
import com.example.notecook.Data.RecipeDatasource;
import com.example.notecook.Model.Detail_Recipe;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Utils.Constants;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailRecipeRepository {
    private static List<Detail_Recipe> list_detail_recipe = new ArrayList<>();
    private ApiService apiService;
    private DetailRecipeDataSource detailRecipeDataSource;

    public DetailRecipeRepository(Context context) {
        apiService = ApiClient.getClient().create(ApiService.class);
        detailRecipeDataSource = new DetailRecipeDataSource(context);
    }

    public static void getDetailRecipeByIdRecipeApi(int Recipeid, Context context) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<Detail_Recipe> call = apiService.getDetailRecipeByIdRecipeFRK(Token, Recipeid);


        call.enqueue(new Callback<Detail_Recipe>() {
            @Override
            public void onResponse(Call<Detail_Recipe> call, Response<Detail_Recipe> response) {
                if (response.isSuccessful()) {
                    Detail_Recipe detail_recipe = response.body();
                    Detail_CurrentRecipe = detail_recipe;
                    Log.d("TAG", detail_recipe.getLevel().toString());
                    TAG_CONNEXION_MESSAGE = response.message();
                    TAG_CONNEXION = response.code();
                    /*if (CURRENT_RECIPE.getFrk_user() != user_login.getUser().getId_User() && User_CurrentRecipe.getId_User() != CURRENT_RECIPE.getFrk_user())
                        //getUserByIdRecipeApi(CURRENT_RECIPE.getId_recipe(), context);
                    else if (User_CurrentRecipe.getId_User() == CURRENT_RECIPE.getFrk_user()) {
                        MainFragment.viewPager2.setCurrentItem(1);
                    } else {
                        User_CurrentRecipe = user_login.getUser();
                        MainFragment.viewPager2.setCurrentItem(1, false);
                    }*/
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
            public void onFailure(Call<Detail_Recipe> call, Throwable t) {
                TAG_CONNEXION = call.hashCode();
            }
        });

    }

    private static List<Detail_Recipe> getAllocalDR(Context context) {
        List<Detail_Recipe> localDetaliRecipes;
        DetailRecipeDataSource detailRecipeDataSource = new DetailRecipeDataSource(context);
        detailRecipeDataSource.open();
        localDetaliRecipes = detailRecipeDataSource.getAllDR();
        detailRecipeDataSource.close();
        return localDetaliRecipes;
    }

    public static void synchronizeDataDetailRecipe(Detail_Recipe RemotedetailRecipe, Context context) {
        // Step 1: Compare local and remote data to identify differences
        list_detail_recipe = getAllocalDR(context);
        boolean found = false;
        for (Detail_Recipe localDetailRecipe : list_detail_recipe) {
            Log.d("found", "fooor" + localDetailRecipe.getDt_recipe());


            Log.d("found", "RemotedetailRecipe" + RemotedetailRecipe.getFrk_recipe());
            Log.d("found", "localDetailRecipe" + localDetailRecipe.getFrk_recipe());
            if (RemotedetailRecipe.getFrk_recipe() == localDetailRecipe.getFrk_recipe()) {
                // Recipe exists locally; update it if necessary
                //if (!RemotedetailRecipe.equals(localDetailRecipe)) {
                // Update local detail recipe with remote data
                updateDetaliRecipeLocally(RemotedetailRecipe, RemotedetailRecipe.getFrk_recipe(), context);
                //}
                found = true;
                Log.d("found", "" + found);
                break;
            }
        }
        if (!found) {
            // Recipe doesn't exist locally; insert it
            Log.d("found", "" + found);
            insertDetailRecipeLocally(RemotedetailRecipe, context);
        }

       /* boolean found1 = false;
        // Handle deleted recipes
        for (Detail_Recipe localDeatilRecipe : list_detail_recipe) {

            if (localDeatilRecipe.getFrk_recipe() == RemotedetailRecipe.getFrk_recipe()) {
                found1 = true;
                break;
            }
        }
        if (!found1) {
            // Recipe exists locally but not remotely; mark it as deleted
            markDetailRecipeAsDeletedLocally(RemotedetailRecipe, context);
        }

        */
    }

    private static void insertDetailRecipeLocally(Detail_Recipe remotedetailRecipe, Context context) {
        DetailRecipeDataSource detailRecipeDataSource = new DetailRecipeDataSource(context);
        detailRecipeDataSource.open();
        detailRecipeDataSource.insertDetail_recipe(remotedetailRecipe);
        detailRecipeDataSource.close();
    }

    private static void updateDetaliRecipeLocally(Detail_Recipe remotedetailRecipe, int frk_recipe, Context context) {
        DetailRecipeDataSource detailRecipeDataSource = new DetailRecipeDataSource(context);
        detailRecipeDataSource.open();
        detailRecipeDataSource.Update_Detail_RecipeByIdRecipe(remotedetailRecipe, frk_recipe);
        detailRecipeDataSource.close();
    }

    private static void markRecipeAsDeletedLocally(Recipe localRecipe, Context context) {
        RecipeDatasource recipeDatasource = new RecipeDatasource(context);
        recipeDatasource.open();
        recipeDatasource.deleteRecipe(localRecipe);
        recipeDatasource.close();
    }

    private static void markDetailRecipeAsDeletedLocally(Detail_Recipe localDetailRecipe, Context context) {
        DetailRecipeDataSource detailRecipeDataSource = new DetailRecipeDataSource(context);
        detailRecipeDataSource.open();
        detailRecipeDataSource.deleteDR(localDetailRecipe);
        detailRecipeDataSource.close();
    }

    public void getLocalDetailsRecipes() {
        detailRecipeDataSource.open();
        Constants.list_Detailrecipe = detailRecipeDataSource.getAllDR();
        detailRecipeDataSource.close();
    }

}
