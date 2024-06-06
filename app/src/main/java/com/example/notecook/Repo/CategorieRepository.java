package com.example.notecook.Repo;

import static com.example.notecook.Utils.Constants.Token;

import android.app.Activity;
import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Data.CategoryRecipeDataSource;
import com.example.notecook.Model.Category_Recipe;
import com.example.notecook.Utils.Constants;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategorieRepository {
    private ApiService apiService;
    private CategoryRecipeDataSource categoryRecipeDataSource;
    private Context context;
    private Activity appCompatActivity;

    public CategorieRepository(ApiService apiService, CategoryRecipeDataSource categoryRecipeDataSource, Context context, Activity appCompatActivity) {
        this.apiService = apiService;
        this.categoryRecipeDataSource = categoryRecipeDataSource;
        this.context = context;
        this.appCompatActivity = appCompatActivity;
    }

    public CategorieRepository(Context context, Activity appCompatActivity) {
        this.context = context;
        this.appCompatActivity = appCompatActivity;
        apiService = ApiClient.getClient().create(ApiService.class);
    }

    public LiveData<List<Category_Recipe>> getAllCatRecipe() {
        MutableLiveData<List<Category_Recipe>> CategoriesLiveData = new MutableLiveData<>();
        // Enqueue the download request
        apiService.getAllCategories(Token).enqueue(new Callback<List<Category_Recipe>>() {
            @Override
            public void onResponse(Call<List<Category_Recipe>> call, Response<List<Category_Recipe>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CategoriesLiveData.setValue(response.body());
                }else ErrorHandler.handleErrorResponse(response, appCompatActivity);
            }

            @Override
            public void onFailure(Call<List<Category_Recipe>> call, Throwable t) {
                ErrorHandler.handleNetworkFailure(t, appCompatActivity);
            }
        });
        return CategoriesLiveData;
    }

    public LiveData<Category_Recipe> getCatRecipe(int id) {
        MutableLiveData<Category_Recipe> CategoriesLiveData = new MutableLiveData<>();
        // Enqueue the download request
        apiService.getCategory(id,Token).enqueue(new Callback<Category_Recipe>() {
            @Override
            public void onResponse(Call<Category_Recipe> call, Response<Category_Recipe> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CategoriesLiveData.setValue(response.body());
                }else ErrorHandler.handleErrorResponse(response, appCompatActivity);
            }

            @Override
            public void onFailure(Call<Category_Recipe> call, Throwable t) {
                ErrorHandler.handleNetworkFailure(t, appCompatActivity);
            }
        });
        return CategoriesLiveData;
    }


}
