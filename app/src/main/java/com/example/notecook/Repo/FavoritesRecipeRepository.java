package com.example.notecook.Repo;

import static com.example.notecook.Utils.Constants.Token;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Fragement.Favorite_User_Recipe;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FavoritesRecipeRepository {

    public static void Insert_Fav(int id_user, int id_recipe) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // Create a new favorite object
        Favorite_User_Recipe newFavorite = new Favorite_User_Recipe();
        newFavorite.setUserId(id_user); // Set user ID
        newFavorite.setRecipeId(id_recipe); // Set recipe ID

        // Send a POST request to create the new favorite
        Call<Favorite_User_Recipe> call = apiService.createFavorite(Token, newFavorite);
        call.enqueue(new Callback<Favorite_User_Recipe>() {
            @Override
            public void onResponse(Call<Favorite_User_Recipe> call, Response<Favorite_User_Recipe> response) {
                if (response.isSuccessful()) {
                    Favorite_User_Recipe createdFavorite = response.body();
                    // Handle the newly created favorite
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<Favorite_User_Recipe> call, Throwable t) {
                // Handle network failure
            }
        });
    }
}
