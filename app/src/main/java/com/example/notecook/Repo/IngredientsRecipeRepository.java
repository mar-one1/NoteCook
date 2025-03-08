package com.example.notecook.Repo;

import static com.example.notecook.Utils.Constants.All_Ingredients_Recipe;
import static com.example.notecook.Utils.Constants.Review_CurrentRecipe;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.Token;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Data.IngredientsDataSource;
import com.example.notecook.Model.Ingredients;

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

    public IngredientsRecipeRepository(Activity activity) {
        this.context = context;
        this.activity = activity;
        apiService = ApiClient.getClient().create(ApiService.class);
        ingredientsDataSource = new IngredientsDataSource(context);
    }

    public LiveData<List<Ingredients>> getIngredientsRecipeApi() {
        MutableLiveData<List<Ingredients>> ingredients = new MutableLiveData<>();

        apiService.getAllIngredients(Token).enqueue(new Callback<List<Ingredients>>() {
            @Override
            public void onResponse(Call<List<Ingredients>> call, Response<List<Ingredients>> response) {
                if (response.isSuccessful()) {
                    All_Ingredients_Recipe = response.body();
                    ingredients.setValue(response.body());
                    TAG_CONNEXION_MESSAGE = response.message();
                    TAG_CONNEXION = response.code();
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
            public void onFailure(Call<List<Ingredients>> call, Throwable t) {
                TAG_CONNEXION = call.hashCode();
            }
        });
        return ingredients;
    }
}
