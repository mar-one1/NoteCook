package com.example.notecook.Repo;

import static com.example.notecook.Utils.Constants.CURRENT_RECIPE;
import static com.example.notecook.Utils.Constants.Detail_CurrentRecipe;
import static com.example.notecook.Utils.Constants.Ingredients_CurrentRecipe;
import static com.example.notecook.Utils.Constants.Review_CurrentRecipe;
import static com.example.notecook.Utils.Constants.Steps_CurrentRecipe;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.Token;
import static com.example.notecook.Utils.Constants.User_CurrentRecipe;
import static com.example.notecook.Utils.Constants.list_recipe;
import static com.example.notecook.Utils.Constants.user_login;
import static com.example.notecook.Utils.Constants.user_login_local;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Api.RecipeResponse;
import com.example.notecook.Data.RecipeDatasource;
import com.example.notecook.Data.UserDatasource;
import com.example.notecook.Fragement.MainFragment;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.User;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeRepository {
    private ApiService apiService;
    private RecipeDatasource recipeDatasource;
    private UserDatasource userDatasource;
    private Context context;
    private DetailRecipeRepository detailRecipeRepository;
    private UserRepository userRepo;

    public RecipeRepository(Context context) {
        this.context = context;
        apiService = ApiClient.getClient().create(ApiService.class);
        recipeDatasource = new RecipeDatasource(context);
        userDatasource = new UserDatasource(context);
        detailRecipeRepository = new DetailRecipeRepository(context);
        userRepo = new UserRepository(context);
    }

    private static void markRecipeAsDeletedLocally(Recipe localRecipe, Context context) {
        RecipeDatasource recipeDatasource = new RecipeDatasource(context);
        recipeDatasource.open();
        recipeDatasource.deleteRecipe(localRecipe);
        recipeDatasource.close();
    }

    private static User getUserRecipe(Context context, String username) {
        UserDatasource userDatasource = new UserDatasource(context);
        userDatasource.open();
        User user = userDatasource.select_User_BYUsername(username);
        userDatasource.close();
        return user;
    }

    public int insertRecipeLocally(Recipe recipe, int id) {
        // Insert the recipe locally using your createRecipe method
        recipeDatasource.open();
        int insertedId = (int) RecipeDatasource.InsertRecipe(recipe, id);
        recipeDatasource.close();
        return insertedId;
    }

    public void getUserByIdRecipeApi(int Recipeid, Context context) {

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<User> call = apiService.getUserByIdRecipe(Token, Recipeid);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    User_CurrentRecipe = user;
                    userRepo.getImageUserUrl(User_CurrentRecipe.getUsername(), "recipe_user", context);
                    Log.d("TAG", user.getUsername().toString());
                    TAG_CONNEXION_MESSAGE = response.message();
                    TAG_CONNEXION = response.code();
                    //MainFragment.viewPager2.setCurrentItem(1);
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
            public void onFailure(Call<User> call, Throwable t) {
                TAG_CONNEXION = call.hashCode();
            }
        });
    }


    public LiveData<Recipe> InsertRecipeApi(Recipe recipe, Bitmap bitmap) {

        // Enqueue the download request
        apiService.createRecipe(Token, recipe).enqueue(new Callback<Recipe>() {
            @Override
            public void onResponse(Call<Recipe> call, Response<Recipe> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Recipe recipe1 = response.body();
                    Log.d("TAG", recipe1.getId_recipe() + "et nom :  " + recipe1.getNom_recipe());
                    //ResponseBody responseBody = response.body();
                    uploadImageRecipe(recipe1.getId_recipe(), bitmap);
                    //fetchImage(str,tag,0,context);
                    Toast.makeText(context, "succes  Created Api ", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle unsuccessful download
                    Toast.makeText(context, "unsuccessful Created Api" + response.message(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<Recipe> call, Throwable t) {
                Toast.makeText(context, "Handle failure Insert Recipe to api", Toast.LENGTH_SHORT).show();
            }
        });
        return null;
    }

    public LiveData<List<Recipe>> getLocalRecipes(int i) {
        recipeDatasource.open();
        //ModelsDataSource<Recipe> model = new ModelsDataSource<>(context,Recipe.class);
        //list_recipe.addAll(Objects.requireNonNull(model.getAllRecordsByIdUser(TABLE_RECIPE, COLUMN_ID_FRK_USER_RECIPE, i).getValue()));
        list_recipe.setValue(recipeDatasource.getRecipeById(i).getValue());
        recipeDatasource.close();
        detailRecipeRepository.getLocalDetailsRecipes();
        return list_recipe;
    }

    public void getFullRecipeApi(int Recipeid, Context context) {

        Call<RecipeResponse> call = apiService.getRecipeById(Token, Recipeid);


        call.enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.isSuccessful()) {
                    RecipeResponse recipeResponse = response.body();
                    if (recipeResponse != null) {
                        CURRENT_RECIPE =recipeResponse.getRecipe();
                        Detail_CurrentRecipe = recipeResponse.getDetail_recipe();
                        Steps_CurrentRecipe = recipeResponse.getSteps();
                        Review_CurrentRecipe = recipeResponse.getReviews();
                        Ingredients_CurrentRecipe = recipeResponse.getIngredients();
                    }
                    TAG_CONNEXION_MESSAGE = response.message();
                    TAG_CONNEXION = response.code();
                    if (CURRENT_RECIPE.getFrk_user() != user_login.getUser().getId_User() && User_CurrentRecipe.getId_User() != CURRENT_RECIPE.getFrk_user())
                        getUserByIdRecipeApi(CURRENT_RECIPE.getId_recipe(), context);
                    else if (User_CurrentRecipe.getId_User() == CURRENT_RECIPE.getFrk_user()) {
                        MainFragment.viewPager2.setCurrentItem(1);
                    } else {
                        User_CurrentRecipe = user_login.getUser();
                        MainFragment.viewPager2.setCurrentItem(1, false);
                    }
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
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                TAG_CONNEXION = call.hashCode();
            }
        });

    }

    public LiveData<List<Recipe>> getRecipes() {
        MutableLiveData<List<Recipe>> remoteRecipeList = new MutableLiveData<>();
        apiService.getAllRecipes(Token).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    List<Recipe> recipes = response.body();
                    remoteRecipeList.setValue(recipes);
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                handleNetworkFailure();
            }
        });
        return remoteRecipeList;
    }

    private void handleNetworkFailure() {
        // Handle network failure
    }

    public LiveData<List<Recipe>> getRecipesByUsername( String username) {
        MutableLiveData<List<Recipe>> remoteRecipeListByUser = new MutableLiveData<>();
        apiService.getRecipeByUsernameUser(Token, username).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    remoteRecipeListByUser.setValue(response.body());
                    if (remoteRecipeListByUser.getValue() != null && remoteRecipeListByUser.getValue().size() != 0) {
                        synchronizeDataFromLocalToRemote(list_recipe.getValue(),remoteRecipeListByUser.getValue(),username);
                    }

                } else {
                    // Handle error response here
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                // Handle network failure
                handleNetworkFailure();
            }
        });
        return remoteRecipeListByUser;
    }

    public void uploadImageRecipe(int idRecipe, Bitmap bitmp) {

        File filesDir = context.getFilesDir();
        File imageFile = new File(filesDir, "image.jpg"); // Change 'image.jpg' to the desired file name and format

        // Convert bitmap to file
        try {
            OutputStream os = new FileOutputStream(imageFile);
            bitmp.compress(Bitmap.CompressFormat.JPEG, 100, os); // Compress bitmap into JPEG with quality 100%
            os.flush();
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Create a File instance with the path to the file to upload
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);

// Create MultipartBody.Part instance from the RequestBody
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);
// Create a service using the Retrofit interface
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
// Call the method to upload the file
        apiService.uploadRecipeFile(Token, idRecipe, filePart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String path = null;
                    try {
                        path = response.body().string();
                        //String str = new String(bytes, StandardCharsets.UTF_8);
                        path = path.replaceAll("\"", "");// For UTF-8 encoding
//                            user_login.getUser().setPathimageuser(path);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Toast.makeText(context, "upload image : " + path, Toast.LENGTH_SHORT).show();
                    // File upload successful
                    //fetchImage(path);
                    Toast.makeText(context, "upload image : " + TAG_CONNEXION_MESSAGE, Toast.LENGTH_SHORT).show();

                } else {
                    // Handle unsuccessful upload
                    Toast.makeText(context, "Not upload image : " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(context, "OnFailure upload image : " + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void handleErrorResponse(Response<?> response) {
        int statusCode = response.code();
        String message = response.message();
        if (response.errorBody() != null) {
            try {
                String errorResponse = response.errorBody().string();
                Log.e("ErrorResponse", "Error Response: " + errorResponse);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void synchronizeDataFromLocalToRemote(List<Recipe> localRecipes, List<Recipe> remoteRecipes, String username) {
        // Step 1: Get the user ID from the local database using the username
        userDatasource.open();
        int userId;
        if (user_login_local.getUser() != null && user_login_local.getUser().getId_User() != 0) {
            userId = user_login_local.getUser().getId_User();
        } else {
            user_login_local.setUser(userDatasource.select_User_BYUsername(username));
            userId = user_login_local.getUser().getId_User();
        }
        if (userId == -1) {
            // Handle the case where the user ID is not found
            return;
        }
        userDatasource.close();

        // Step 2: Perform the synchronization using the user ID
        synchronizeDataFromLocalToRemote(localRecipes, remoteRecipes,userId);
    }

    public void synchronizeDataFromLocalToRemote(List<Recipe> localRecipes, List<Recipe> remoteRecipes,int id) {
        // Step 1: Update local recipes with data from remote recipes
        for (Recipe remoteRecipe : remoteRecipes) {
            // Check if the remote recipe belongs to the specified user
            if (remoteRecipe.getFrk_user() == user_login.getUser().getId_User()) {
                boolean foundLocally = false;
                for (Recipe localRecipe : localRecipes) {
                    // Match recipes using a unique identifier, e.g., recipe ID
                    if (remoteRecipe.getNom_recipe().equals(localRecipe.getNom_recipe())) {
                        // Recipe exists locally; update it with remote data
                        updateRecipeLocally(remoteRecipe,localRecipe.getId_recipe());
                        foundLocally = true;
                        break;
                    }
                }
                if (!foundLocally) {
                    // Recipe doesn't exist locally; add it to the local list
                    insertRecipeLocally(remoteRecipe, user_login_local.getUser().getId_User());
                }
            }
        }

        // Step 2: Update remote recipes with data from local recipes (if needed)
        for (Recipe localRecipe : localRecipes) {
            // Check if the local recipe belongs to the specified user
            if (localRecipe.getFrk_user() == id) {
                boolean foundRemotely = false;
                for (Recipe remoteRecipe : remoteRecipes) {
                    // Match recipes using a unique identifier, e.g., recipe ID
                    if (remoteRecipe.getNom_recipe().equals(localRecipe.getNom_recipe())) {
                        // Recipe exists remotely; no need to update
                        foundRemotely = true;
                        break;
                    }
                }
                if (!foundRemotely) {
                    // Recipe exists locally but not remotely; update it remotely if needed
                    updateRecipeRemotely(localRecipe);
                }
            }
        }
    }

    private void updateRecipeLocally(Recipe remoteRecipe,int id) {
        // Implement logic to update the local recipe with data from the remote recipe
        recipeDatasource.open();
        recipeDatasource.UpdateRecipe(remoteRecipe,id);
        recipeDatasource.close();
    }

    private void updateRecipeRemotely(Recipe recipe) {
        // Implement logic to update recipe remotely if needed
        // This will depend on your API implementation
    }

}