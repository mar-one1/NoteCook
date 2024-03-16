package com.example.notecook.Repo;

import static com.example.notecook.Utils.Constants.Steps_CurrentRecipe;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.Token;

import android.content.Context;
import android.util.Log;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Data.RecipeDatasource;
import com.example.notecook.Data.StepsDataSource;
import com.example.notecook.Data.UserDatasource;
import com.example.notecook.Model.Step;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StepRecipeRepository {
    private ApiService apiService;
    private RecipeDatasource recipeDatasource;
    private StepsDataSource stepsDataSource;

    public StepRecipeRepository(Context context) {
        apiService = ApiClient.getClient().create(ApiService.class);
        recipeDatasource = new RecipeDatasource(context);
        stepsDataSource = new StepsDataSource(context);
    }

    public void getStepRecipeByIdRecipeApi(int Recipeid) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<List<Step>> call = apiService.getStepsByIdRecipe(Token, Recipeid);

        call.enqueue(new Callback<List<Step>>() {
            @Override
            public void onResponse(Call<List<Step>> call, Response<List<Step>> response) {
                if (response.isSuccessful()) {
                    Steps_CurrentRecipe = response.body();
                    //Log.d("TAG", String.valueOf(steps.get(0).getTime_step()));
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
            public void onFailure(Call<List<Step>> call, Throwable t) {
                TAG_CONNEXION = call.hashCode();
            }
        });
    }
}
