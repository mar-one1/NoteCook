package com.example.notecook.Repo;

import android.content.Context;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Data.DetailRecipeDataSource;
import com.example.notecook.Data.RecipeDatasource;
import com.example.notecook.Utils.Constants;

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
}
