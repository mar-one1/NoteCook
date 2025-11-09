package com.example.notecook.Repo;

import android.util.Log;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Model.Review;
import com.example.notecook.Utils.SharedRecipeViewModel;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReviewRecipeRepository {
    public void getReviewRecipeApi(int idRecipe,SharedRecipeViewModel viewModel) {

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<List<Review>> call = apiService.getReviewByIdRecipe(viewModel.getToken().getValue(), idRecipe);

        call.enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful()) {
                    viewModel.setReviewCurrentRecipe(response.body());
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
            public void onFailure(Call<List<Review>> call, Throwable t) {
                viewModel.setTagConnexion(call.hashCode());
            }
        });
    }
}
