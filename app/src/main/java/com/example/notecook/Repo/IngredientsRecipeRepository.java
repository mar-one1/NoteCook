package com.example.notecook.Repo;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Data.IngredientsDataSource;
import com.example.notecook.Model.Ingredients;
import com.example.notecook.ViewModel.SharedRecipeViewModel;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IngredientsRecipeRepository {

    private IngredientsDataSource ingredientsDataSource;
    private  Context context;
    private Activity activity;
    private ApiService apiService;
    private SharedRecipeViewModel viewModel;

    public IngredientsRecipeRepository(Activity activity,SharedRecipeViewModel viewModel) {
        this.context = context;
        this.activity = activity;
        this.viewModel = viewModel;
        apiService = ApiClient.getClient().create(ApiService.class);
        ingredientsDataSource = new IngredientsDataSource(context);
    }

    public LiveData<List<Ingredients>> getIngredientsRecipeApi() {
        MutableLiveData<List<Ingredients>> ingredients = new MutableLiveData<>();

        apiService.getAllIngredients(viewModel.getToken().getValue()).enqueue(new Callback<List<Ingredients>>() {
            @Override
            public void onResponse(Call<List<Ingredients>> call, Response<List<Ingredients>> response) {
                if (response.isSuccessful()) {
                    viewModel.setAllIngredientsRecipe(response.body());
                    ingredients.setValue(response.body());
                    viewModel.setTagConnexionMessage(response.message());
                    viewModel.setTagConnexion(response.code());
                } else {
                    // Handle error response here
                    viewModel.setTagConnexionMessage(response.message());
                    viewModel.setTagConnexion(response.code());
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
            public void onFailure(Call<List<Ingredients>> call, Throwable t) {
                viewModel.setTagConnexion(call.hashCode());
            }
        });
        return ingredients;
    }
}
