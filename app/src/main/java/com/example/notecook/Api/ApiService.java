package com.example.notecook.Api;

import com.example.notecook.Fragement.Favorite_User_Recipe;
import com.example.notecook.Model.Detail_Recipe;
import com.example.notecook.Model.Ingredients;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.Review;
import com.example.notecook.Model.Step;
import com.example.notecook.Model.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;


public interface ApiService {

    //  API Endpoints verify token
    @Headers("Content-Type: application/json")
    @GET("protected")
    Call<TokenResponse> getVerifyToken(@Header("Authorization") String token);

    //    @FormUrlEncoded
    @Headers("Content-Type: application/json")
    @POST("auth/login")
    Call<LoginResponse> authontification(@Body LoginResponse loginResponse);

    @GET("users")
    Call<List<User>> getData(@Header("Authorization") String token);

    // Recipes API Endpoints
    @GET("recipes")
    Call<List<Recipe>> getAllRecipes(@Header("Authorization") String token);

    @GET("recipes/{id}")
    Call<RecipeResponse> getRecipeById(@Header("Authorization") String token, @Path("id") int recipeId);

    @GET("recipes/user/{id}")
    Call<List<Recipe>> getRecipeByIdUser(@Header("Authorization") String token, @Path("id") int recipeId);

    @GET("recipes/user/{username}")
    Call<List<Recipe>> getRecipeByUsernameUser(@Header("Authorization") String token, @Path("username") String username);

    @GET("recipes/{id}/user")
    Call<User> getUserByIdRecipe(@Header("Authorization") String token, @Path("id") int recipeId);

    @POST("recipes")
        //@Headers("Content-Type: application/json")
    Call<Recipe> createRecipe(@Header("Authorization") String token, @Body Recipe recipe);

    @PUT("recipes/{id}")
    @Headers("Content-Type: application/json")
    Call<Recipe> updateRecipe(@Path("id") int recipeId, @Body Recipe recipe);

    @DELETE("recipes/{id}")
    Call<Void> deleteRecipe(@Path("id") int recipeId);

    // Users API Endpoints
    @GET("users")
    Call<List<User>> getAllUsers(@Header("Authorization") String token);

    @GET("users/{id}")
    Call<User> getUserById(@Path("id") int userId);

    @GET("users/filtre/{username}")
    Call<User> getUserByUsername(@Path("username") String username);

    @GET("users/image/{username}")
    @Headers("Content-Type: application/octet-stream")
    Call<ResponseBody> getImageUSerBytes(@Path("username") String username);

    @Multipart
    @POST("users/upload/{username}")
    Call<ResponseBody> uploadFile(@Path("username") String username,
                                  @Part MultipartBody.Part image
    );

    @Multipart
    @POST("recipes/upload/{id}")
    Call<ResponseBody> uploadRecipeFile(
            @Header("Authorization") String token,
            @Path("id") int username,
            @Part MultipartBody.Part image
    );

    @DELETE("users/delete/{path}")
    Call<ResponseBody> deleteimage(@Path("path") String fileUrl);

    @GET
    Call<ResponseBody> downloadImage(@Url String fileUrl);

    @POST("users")
        //@Headers("Content-Type: application/json")
    Call<User> createUser(@Body User user);

    @PUT("users/{id}")
    @Headers("Content-Type: application/json")
    Call<User> updateUser(@Path("id") int id, @Body User user);

    @PUT("users/filtre/{username}")
    @Headers("Content-Type: application/json")
    Call<User> updateUserByUsername(@Path("username") String username, @Body User user);

    @PUT("users/image/{username}")
    @Headers("Content-Type: application/json")
    Call<String> updateUserGoogleImageUrl(@Path("username") String username, @Body RequestBody path);
//    @Multipart
//    @PUT("users/filtre/{username}")
//    Call<User> updateUserByUsername(
//            @Path("username") String username,
//            @Part("user") User user,
//            @Part("newProfileImage") RequestBody imageRequestBody
//    );


    @DELETE("users/{id}")
    Call<Void> deleteUser(@Path("id") int userId);

    // Ingredients API Endpoints
    @GET("ingredientrecipes")
    @Headers("Content-Type: application/json")
    Call<List<Ingredients>> getAllIngredients(@Header("Authorization") String token);

    @GET("ingredients/{id}")
    Call<Ingredients> getIngredientById(@Path("id") int ingredientId);

    @POST("ingredients")
    @Headers("Content-Type: application/json")
    Call<Ingredients> createIngredient(@Body Ingredients ingredient);

    @PUT("ingredients/{id}")
    @Headers("Content-Type: application/json")
    Call<Ingredients> updateIngredient(@Path("id") int ingredientId, @Body Ingredients ingredient);

    @DELETE("ingredients/{id}")
    Call<Void> deleteIngredient(@Path("id") int ingredientId);


    // Step API Endpoints
    @GET("steprecipes")
    Call<List<Step>> getAllSteps();

    @GET("steprecipes/{id}")
    Call<Step> getStepById(@Path("id") int stepId);

    @GET("steprecipes/recipe/{id}")
    @Headers("Content-Type: application/json")
    Call<List<Step>> getStepsByIdRecipe(@Header("Authorization") String token, @Path("id") int stepId);

    @POST("steps")
    @Headers("Content-Type: application/json")
    Call<Step> createStep(@Body Step step);

    @PUT("steps/{id}")
    @Headers("Content-Type: application/json")
    Call<Step> updateStep(@Path("id") int stepId, @Body Step step);

    @DELETE("steps/{id}")
    Call<Void> deleteStep(@Path("id") int stepId);

    // Review API Endpoints
    @GET("reviewrecipes")
    Call<List<Review>> getAllReviews();

    @GET("reviewrecipes/{id}")
    Call<Review> getReviewById(@Path("id") int reviewId);

    @GET("reviewrecipes/recipe/{id}")
    Call<List<Review>> getReviewByIdRecipe(@Header("Authorization") String token, @Path("id") int RecipeId);

    @POST("reviewrecipes")
    @Headers("Content-Type: application/json")
    Call<Review> createReview(@Body Review review);

    @PUT("reviewrecipes/{id}")
    @Headers("Content-Type: application/json")
    Call<Review> updateReview(@Path("id") int reviewId, @Body Review review);

    @DELETE("reviewrecipes/{id}")
    Call<Void> deleteReview(@Path("id") int reviewId);

    // DetailRecipe API Endpoints
    @GET("DetailRecipes")
    Call<List<Detail_Recipe>> getAllDetailRecipes(@Header("Authorization") String token);

    @GET("DetailRecipes/{id}")
    Call<Detail_Recipe> getDetailRecipeById(@Header("Authorization") String token, @Path("id") int DetailRecipeId);

    @GET("DetailRecipes/{id}/detailrecipe")
    Call<Detail_Recipe> getDetailRecipeByIdRecipeFRK(@Header("Authorization") String token, @Path("id") int DetailRecipeId);

    @POST("DetailRecipes")
    @Headers("Content-Type: application/json")
    Call<Detail_Recipe> createDetailRecipe(@Body Detail_Recipe DetailRecipe);

    @PUT("DetailRecipes/{id}")
    @Headers("Content-Type: application/json")
    Call<Detail_Recipe> updateDetailRecipe(@Path("id") int DetailRecipeId, @Body Detail_Recipe DetailRecipe);

    @DELETE("DetailRecipes/{id}")
    Call<Void> deleteDetailRecipe(@Path("id") int DetailRecipeId);

    // ...
    // Add more endpoints for other models as needed

    // Example of a custom query parameter
    @GET("recipes/search/nom")
    Call<List<Recipe>> searchRecipes(@Header("Authorization") String token, @Query("key") String query);

    @POST("favorites")
    @Headers("Content-Type: application/json")
    Call<Favorite_User_Recipe> createFavorite(@Header("Authorization") String token, @Body Favorite_User_Recipe favorite);

}
