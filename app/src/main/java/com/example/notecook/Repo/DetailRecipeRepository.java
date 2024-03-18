package com.example.notecook.Repo;

import static com.example.notecook.Utils.Constants.CURRENT_RECIPE;
import static com.example.notecook.Utils.Constants.Detail_CurrentRecipe;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.Token;
import static com.example.notecook.Utils.Constants.User_CurrentRecipe;
import static com.example.notecook.Utils.Constants.user_login;

import android.content.Context;
import android.util.Log;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Data.DetailRecipeDataSource;
import com.example.notecook.Data.RecipeDatasource;
import com.example.notecook.Fragement.MainFragment;
import com.example.notecook.Model.Detail_Recipe;
import com.example.notecook.Utils.Constants;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailRecipeRepository {
    private ApiService apiService;
    private DetailRecipeDataSource detailRecipeDataSource;

    public DetailRecipeRepository(Context context) {
        apiService = ApiClient.getClient().create(ApiService.class);
        detailRecipeDataSource = new DetailRecipeDataSource(context);
    }
    public void getLocalDetailsRecipes() {
        detailRecipeDataSource.open();
        Constants.list_Detailrecipe = detailRecipeDataSource.getAllDR();
        detailRecipeDataSource.close();
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
}
