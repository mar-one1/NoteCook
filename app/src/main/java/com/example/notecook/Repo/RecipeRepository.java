package com.example.notecook.Repo;

import static com.example.notecook.Utils.Constants.CURRENT_RECIPE;
import static com.example.notecook.Utils.Constants.Detail_CurrentRecipe;
import static com.example.notecook.Utils.Constants.Ingredients_CurrentRecipe;
import static com.example.notecook.Utils.Constants.Review_CurrentRecipe;
import static com.example.notecook.Utils.Constants.Steps_CurrentRecipe;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.TAG_OFFLINE;
import static com.example.notecook.Utils.Constants.Token;
import static com.example.notecook.Utils.Constants.User_CurrentRecipe;
import static com.example.notecook.Utils.Constants.getUserSynch;
import static com.example.notecook.Utils.Constants.list_recipe;
import static com.example.notecook.Utils.Constants.saveUserSynch;
import static com.example.notecook.Utils.Constants.user_login;
import static com.example.notecook.Utils.Constants.user_login_local;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Api.RecipeResponse;
import com.example.notecook.Api.ValidationError;
import com.example.notecook.Data.RecipeDatasource;
import com.example.notecook.Data.UserDatasource;
import com.example.notecook.Fragement.MainFragment;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.User;
import com.example.notecook.Utils.Constants;
import com.google.gson.Gson;

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
    private Activity appCompatActivity;

    public RecipeRepository(Context context, Activity appCompatActivity) {
        this.context = context;
        apiService = ApiClient.getClient().create(ApiService.class);
        recipeDatasource = new RecipeDatasource(context);
        userDatasource = new UserDatasource(context);
        detailRecipeRepository = new DetailRecipeRepository(context);
        userRepo = new UserRepository(context);
        this.appCompatActivity = appCompatActivity;
    }

    public RecipeRepository(Context context) {
        this.context = context;
        apiService = ApiClient.getClient().create(ApiService.class);
        recipeDatasource = new RecipeDatasource(context);
        userDatasource = new UserDatasource(context);
        detailRecipeRepository = new DetailRecipeRepository(context);
        userRepo = new UserRepository(context,appCompatActivity);
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
        return (int) recipeDatasource.InsertRecipe(recipe, id);
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
                    handleErrorResponse(response);
                }

            }

            @Override
            public void onFailure(Call<Recipe> call, Throwable t) {
                Toast.makeText(context, "Handle failure Insert Recipe to api", Toast.LENGTH_SHORT).show();
                handleNetworkFailure(call);
            }
        });
        return null;
    }

    public LiveData<List<Recipe>> getLocalRecipes(int i) {
        recipeDatasource.open();
        list_recipe.setValue(recipeDatasource.getRecipeByIdUser(i).getValue());
        //detailRecipeRepository.getLocalDetailsRecipes();
        recipeDatasource.close();
        return list_recipe;
    }

    public LiveData<RecipeResponse> getFullRecipeApi(int Recipeid) {
        MutableLiveData<RecipeResponse> recipeResponseMutableLiveData = new MutableLiveData<>();
        apiService.getRecipeById(Token, Recipeid).enqueue(new Callback<RecipeResponse>() {
            @Override
            public void onResponse(Call<RecipeResponse> call, Response<RecipeResponse> response) {
                if (response.isSuccessful()) {
                    RecipeResponse recipeResponse = response.body();
                    if (recipeResponse != null) {
                        recipeResponseMutableLiveData.setValue(recipeResponse);
                    }
                    TAG_CONNEXION_MESSAGE = response.message();
                    TAG_CONNEXION = response.code();
                    if (CURRENT_RECIPE.getFrk_user() != user_login.getUser().getId_User() && User_CurrentRecipe.getId_User() != CURRENT_RECIPE.getFrk_user())
                        userRepo.getUserByIdRecipeApi(CURRENT_RECIPE.getId_recipe());
                    else if (User_CurrentRecipe.getId_User() != CURRENT_RECIPE.getFrk_user()){
                        User_CurrentRecipe = user_login.getUser();
                        //MainFragment.viewPager2.setCurrentItem(1, false);
                    }
                } else {
                    handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                TAG_CONNEXION = call.hashCode();
                handleNetworkFailure(call);
            }
        });
        return recipeResponseMutableLiveData;
    }
    private void handleErrorResponse(Response<?> response) {
        int statusCode = response.code();
        String message = response.message();
        if (response.errorBody() != null) {
            if (statusCode == 400) {
                try {
                    String errorBody = response.errorBody().string();
                    Gson gson = new Gson();
                    ValidationError validationError = gson.fromJson(errorBody, ValidationError.class);
                    // Now you have the validation errors in the validationError object
                    // Handle them accordingly
                    StringBuilder errorMessages = new StringBuilder();
                    for (ValidationError.ValidationErrorItem error : validationError.getErrors()) {
                        errorMessages.append(", ").append(error.getMessage());
                    }
                    Constants.AffichageMessage(errorMessages.toString(), appCompatActivity);
                } catch (IOException e) {
                    // Handle error parsing error body
                }
                // Unauthorized, handle accordingly (e.g., reauthentication).
            } else if (statusCode == 409) {
                Constants.AffichageMessage("Recipe already exists", appCompatActivity);
                // Unauthorized, handle accordingly (e.g., reauthentication).
            } else if (statusCode == 404) {
                // Not found, handle accordingly (e.g., show a 404 error message).
                Constants.AffichageMessage(TAG_OFFLINE, appCompatActivity);
            } else if (statusCode >= 500) {
                // Handle other status codes or generic error handling.
                Constants.AffichageMessage("Internal Server Error", appCompatActivity);
            } else if (statusCode == 406) {
                // Handle other status codes or generic error handling.
                Constants.AffichageMessage("User not found", appCompatActivity);
            } else Constants.AffichageMessage(message, appCompatActivity);
        }
    }

    private void handleNetworkFailure(Call<?> call) {
        // Handle network failure
        TAG_CONNEXION_MESSAGE = call.toString();
        //Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, context);
        Toast.makeText(context, TAG_CONNEXION_MESSAGE, Toast.LENGTH_SHORT).show();
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
                handleNetworkFailure(call);
            }
        });
        return remoteRecipeList;
    }

    public LiveData<List<Recipe>> getRecipesByUsername(String username) {
        MutableLiveData<List<Recipe>> remoteRecipeListByUser = new MutableLiveData<>();
        apiService.getRecipeByUsernameUser(Token, username).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    remoteRecipeListByUser.setValue(response.body());
                    if (!getUserSynch(username, context) && list_recipe.getValue().size() < remoteRecipeListByUser.getValue().size()) {
                        if (remoteRecipeListByUser.getValue() != null && remoteRecipeListByUser.getValue().size() != 0) {
                            synchronizeDataFromLocalToRemote(list_recipe.getValue(), remoteRecipeListByUser.getValue(), username);
                        }
                    } else {
                        saveUserSynch(username, true, context);
                    }

                } else {
                    // Handle error response here
                    if(response!=null) handleErrorResponse(response);
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                // Handle network failure
                handleNetworkFailure(call);
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
        synchronizeDataFromLocalToRemote(localRecipes, remoteRecipes, userId);
    }

    public void synchronizeDataFromLocalToRemote(List<Recipe> localRecipes, List<Recipe> remoteRecipes, int id) {
        // Step 1: Update local recipes with data from remote recipes
        for (Recipe remoteRecipe : remoteRecipes) {
            // Check if the remote recipe belongs to the specified user
            if (remoteRecipe.getFrk_user() == user_login.getUser().getId_User()) {
                boolean foundLocally = false;
                for (Recipe localRecipe : localRecipes) {
                    // Match recipes using a unique identifier, e.g., recipe ID
                    if (remoteRecipe.getNom_recipe().equals(localRecipe.getNom_recipe())) {
                        // Recipe exists locally; update it with remote data
                        updateRecipeLocally(remoteRecipe, localRecipe.getId_recipe());
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

    private void updateRecipeLocally(Recipe remoteRecipe, int id) {
        // Implement logic to update the local recipe with data from the remote recipe
        recipeDatasource.open();
        recipeDatasource.UpdateRecipe(remoteRecipe, id);
        recipeDatasource.close();
    }

    private void updateRecipeRemotely(Recipe recipe) {
        // Implement logic to update recipe remotely if needed
        // This will depend on your API implementation
    }

}