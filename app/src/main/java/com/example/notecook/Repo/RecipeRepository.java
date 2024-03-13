package com.example.notecook.Repo;

import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.Token;
import static com.example.notecook.Utils.Constants.list_recipe;
import static com.example.notecook.Utils.Constants.user_login;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Data.RecipeDatasource;
import com.example.notecook.Data.UserDatasource;
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

    public RecipeRepository(Context context) {
        apiService = ApiClient.getClient().create(ApiService.class);
        recipeDatasource = new RecipeDatasource(context);
    }

    public static int insertRecipeLocally(Context context, Recipe recipe) {
        // Insert the recipe locally using your createRecipe method
        User user = getUserRecipe(context, user_login.getUser().getUsername());
        recipe.setFrk_user(user.getId_User());
        RecipeDatasource recipeDatasource = new RecipeDatasource(context);
        recipeDatasource.open();
        int insertedId = (int) RecipeDatasource.InsertRecipe(recipe);
        recipeDatasource.close();
        return insertedId;
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

    public LiveData<List<Recipe>> getRecipes(Context context) {
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

    public LiveData<List<Recipe>> getRecipesByUsername(Context context, String username) {
        MutableLiveData<List<Recipe>> remoteRecipeListByUser = new MutableLiveData<>();
        apiService.getRecipeByUsernameUser(Token, username).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    remoteRecipeListByUser.setValue(response.body());
                    if (remoteRecipeListByUser.getValue() != null && remoteRecipeListByUser.getValue().size() != 0) {
                        synchronizeDataFromRemoteTOLocal(context, list_recipe, remoteRecipeListByUser.getValue(), user_login.getUser().getId_User());
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

    public void uploadImageRecipe(int idRecipe, Bitmap bitmp, Context context) {

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

    public void synchronizeDataFromRemoteTOLocal(Context context, List<Recipe> localRecipes, List<Recipe> remoteRecipes, int userId) {
        // Step 1: Update local recipes with data from remote recipes
        for (Recipe remoteRecipe : remoteRecipes) {
            // Check if the remote recipe belongs to the specified user
            if (remoteRecipe.getFrk_user() == userId) {
                boolean foundLocally = false;
                for (Recipe localRecipe : localRecipes) {
                    if (remoteRecipe.getNom_recipe().equals(localRecipe.getNom_recipe())) {
                        // Recipe exists locally; update it with remote data
                        updateRecipeLocally(context, remoteRecipe);
                        foundLocally = true;
                        break;
                    }
                }
                if (!foundLocally) {
                    // Recipe doesn't exist locally; add it to the local list
                    insertRecipeLocally(context, remoteRecipe);
                }
            }
        }

        // Step 2: Update remote recipes with data from local recipes (if needed)
        for (Recipe localRecipe : localRecipes) {
            if (localRecipe.getFrk_user() == userId) {
                boolean foundRemotely = false;
                for (Recipe remoteRecipe : remoteRecipes) {
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

    public void synchronizeDataFromLocalToRemote(Context context, List<Recipe> localRecipes, List<Recipe> remoteRecipes, int userId) {
        // Step 1: Update local recipes with data from remote recipes
        for (Recipe remoteRecipe : remoteRecipes) {
            // Check if the remote recipe belongs to the specified user
            if (remoteRecipe.getFrk_user() == userId) {
                boolean foundLocally = false;
                for (Recipe localRecipe : localRecipes) {
                    if (remoteRecipe.getNom_recipe().equals(localRecipe.getNom_recipe())) {
                        // Recipe exists locally; update it with remote data
                        updateRecipeLocally(context, remoteRecipe);
                        foundLocally = true;
                        break;
                    }
                }
                if (!foundLocally) {
                    // Recipe doesn't exist locally; add it to the local list
                    insertRecipeLocally(context, remoteRecipe);
                }
            }
        }

        // Step 2: Update remote recipes with data from local recipes (if needed)
        for (Recipe localRecipe : localRecipes) {
            if (localRecipe.getFrk_user() == userId) {
                boolean foundRemotely = false;
                for (Recipe remoteRecipe : remoteRecipes) {
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

    private void updateRecipeLocally(Context context, Recipe recipe) {
        // Update the recipe locally using your UpdateRecipe method
        RecipeDatasource recipeDatasource = new RecipeDatasource(context);
        recipeDatasource.open();
        recipeDatasource.UpdateRecipe(recipe, recipe.getId_recipe());
        recipeDatasource.close();
    }

    private void updateRecipeRemotely(Recipe recipe) {
        // Implement logic to update recipe remotely if needed
        // This will depend on your API implementation
    }

}