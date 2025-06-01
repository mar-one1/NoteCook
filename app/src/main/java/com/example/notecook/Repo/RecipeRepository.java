package com.example.notecook.Repo;

import static com.example.notecook.Api.env.BASE_URL;
import static com.example.notecook.Data.MySQLiteHelperTable.COLUMN_NOM_RECIPE;
import static com.example.notecook.Data.MySQLiteHelperTable.COLUMN_UNIQUE_KEY;
import static com.example.notecook.Data.MySQLiteHelperTable.TABLE_RECIPE;
import static com.example.notecook.Utils.Constants.RemotelistFullRecipe;
import static com.example.notecook.Utils.Constants.Search_list;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.Token;
import static com.example.notecook.Utils.Constants.getUserSynch;
import static com.example.notecook.Utils.Constants.list_recipe;
import static com.example.notecook.Utils.Constants.saveUserSynch;
import static com.example.notecook.Utils.Constants.showToast;
import static com.example.notecook.Utils.Constants.user_login_local;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Data.DetailRecipeDataSource;
import com.example.notecook.Data.IngredientsDataSource;
import com.example.notecook.Data.RecipeDatasource;
import com.example.notecook.Data.ReviewDataSource;
import com.example.notecook.Data.StepsDataSource;
import com.example.notecook.Data.UserDatasource;
import com.example.notecook.Dto.RecipeResponse;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.User;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.ImageHelper;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

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
    private DetailRecipeDataSource detailRecipeDataSource;
    private StepsDataSource stepsDataSource;
    private IngredientsDataSource ingredientsDataSource;
    private ReviewDataSource reviewDataSource;
    private Context context;
    private DetailRecipeRepository detailRecipeRepository;
    private UserRepository userRepo;
    private Activity appCompatActivity;

    public RecipeRepository(Context context, Activity appCompatActivity) {
        this.context = context;
        this.appCompatActivity = appCompatActivity;
        apiService = ApiClient.getClient().create(ApiService.class);
        recipeDatasource = new RecipeDatasource(context);
        userDatasource = new UserDatasource(context);
        detailRecipeDataSource = new DetailRecipeDataSource(context);
        stepsDataSource = new StepsDataSource(context);
        ingredientsDataSource = new IngredientsDataSource(context);
        reviewDataSource = new ReviewDataSource(context);
        detailRecipeRepository = new DetailRecipeRepository(context);
        userRepo = new UserRepository(context, appCompatActivity);
    }

    public RecipeRepository(Context context) {
        this.context = context;
        apiService = ApiClient.getClient().create(ApiService.class);
        recipeDatasource = new RecipeDatasource(context);
        userDatasource = new UserDatasource(context);
        detailRecipeDataSource = new DetailRecipeDataSource(context);
        stepsDataSource = new StepsDataSource(context);
        ingredientsDataSource = new IngredientsDataSource(context);
        detailRecipeRepository = new DetailRecipeRepository(context);
        reviewDataSource = new ReviewDataSource(context);
        userRepo = new UserRepository(context, appCompatActivity);
    }

    private static void markRecipeAsDeletedLocally(Recipe localRecipe, Context context) {
        RecipeDatasource recipeDatasource = new RecipeDatasource(context);
        recipeDatasource.deleteRecipe(localRecipe);
    }

    private static User getUserRecipe(Context context, String username) {
        UserDatasource userDatasource = new UserDatasource(context);
        User user = userDatasource.select_User_BYUsername(username);
        return user;
    }

    public  int insertRecipeLocally(Recipe recipe, int id) {
        // Insert the recipe locally using your createRecipe method
        if (recipe.getPathimagerecipe() != null && !recipe.getPathimagerecipe().isEmpty()) {
            String url = BASE_URL + "data/uploads/" + recipe.getPathimagerecipe();

            // Load the image using Picasso
            Picasso.get().load(url)
                    .memoryPolicy(MemoryPolicy.NO_STORE)
                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            // Image loaded successfully
//                            recipe.setIcon_recipe(ImageHelper.bitmapToDrawable(context,bitmap));
                            recipe.setPathimagerecipe(ImageHelper.saveImageToInternalStorage(context, bitmap, "RecipeImages"));
                            Log.d("tag", "image loaded success");
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            // Error occurred while loading the image
                            Log.d("tag", "image loaded onBitmapFailed");
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                            // Image is being prepared to load
                        }
                    });
        }


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
                    uploadRemoteImageRecipe(recipe1.getUnique_key_recipe(), bitmap);
                    //fetchImage(str,tag,0,context);
                    Toast.makeText(context, "succes  Created Api ", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle unsuccessful download
                    Toast.makeText(context, "unsuccessful Created Api" + response.message(), Toast.LENGTH_SHORT).show();
                    ErrorHandler.handleErrorResponse(response, appCompatActivity);
                }

            }

            @Override
            public void onFailure(Call<Recipe> call, Throwable t) {
                Toast.makeText(context, "Handle failure Insert Recipe to api", Toast.LENGTH_SHORT).show();
                ErrorHandler.handleNetworkFailure(t, appCompatActivity);
            }
        });
        return null;
    }

    public LiveData<Integer> insertFullRecipeApi(RecipeResponse recipe, Bitmap bitmap) {
        MutableLiveData<Integer> fullRecipeLiveData = new MutableLiveData<>();
        // Enqueue the download request
        apiService.postFullRecipe(Token, recipe).enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int recipeId = response.body();
                    fullRecipeLiveData.postValue(recipeId);
                    //Log.d("TAG", recipe1.getId_recipe() + "et nom :  " + recipe1.getNom_recipe());
                    //ResponseBody responseBody = response.body();
                    uploadRemoteImageRecipe(recipe.getRecipe().getUnique_key_recipe(), bitmap);
                    //fetchImage(str,tag,0,context);
                    Toast.makeText(context, "succes Full Created Api ", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle unsuccessful download
                    Toast.makeText(context, "unsuccessful Full Created Api" + response.message(), Toast.LENGTH_SHORT).show();
                    ErrorHandler.handleErrorResponse(response, appCompatActivity);
                }

            }

            @Override
            public void onFailure(Call<Integer> call, Throwable t) {
                Toast.makeText(context, "Handle failure Insert Full Recipe to api", Toast.LENGTH_SHORT).show();
                ErrorHandler.handleNetworkFailure(t, appCompatActivity);
            }
        });
        return fullRecipeLiveData;
    }

    public LiveData<List<Recipe>> getLocalRecipes(int i, int pages, int itemperpage) {
        MutableLiveData<List<Recipe>> recipe = new MutableLiveData<>();
        list_recipe.setValue(recipeDatasource.getRecipeByIdUser(i, 3, 3));
        recipe.setValue(list_recipe.getValue());
        return recipe;
    }

    public LiveData<List<Recipe>> getLocalRecipes(int i) {
        MutableLiveData<List<Recipe>>  recipe = new MutableLiveData<>();
        list_recipe.setValue(recipeDatasource.getRecipeByIdUser(i));
        recipe.setValue(list_recipe.getValue());
        ImageHelper.deleteUnusedImages(context, recipeDatasource.getAllRecipesImagePath(), "RecipeImages");
        return recipe;
    }

    public LiveData<Recipe> getLocalRecipe(int i) {
        MutableLiveData<Recipe> recipe = new MutableLiveData<>();
        recipe.setValue(recipeDatasource.getRecipe(i));
        return recipe;
    }

    public LiveData<RecipeResponse> getFullLocalRecipe(Recipe RC) {
        MutableLiveData<RecipeResponse> recipe = new MutableLiveData<>();
        RecipeResponse responseRecipe = new RecipeResponse();
        responseRecipe.setRecipe(recipeDatasource.getRecipe(RC.getId_recipe()));
        responseRecipe.setDetail_recipe(detailRecipeDataSource.getDrByIdRecipe(RC.getId_recipe()));
        responseRecipe.setUser(userDatasource.getUserBYid(RC.getFrk_user()));
        responseRecipe.setIngredients(ingredientsDataSource.getListIngerdientsByidRecipe(RC.getId_recipe()));
        responseRecipe.setSteps(stepsDataSource.getStepByIdRecipe(RC.getId_recipe()));
        responseRecipe.setReviews(reviewDataSource.getReviewsByIdRecipe(RC.getId_recipe()));
        recipe.setValue(responseRecipe);
        return recipe;
    }

    public LiveData<RecipeResponse> insertFullRecipeInLocal(RecipeResponse RC) {
        MutableLiveData<RecipeResponse> fullRecipeLiveData = new MutableLiveData<>();
        if (!recipeDatasource.isRecordExist(TABLE_RECIPE, COLUMN_NOM_RECIPE, RC.getRecipe().getNom_recipe())) {
            boolean isInsertionSuccessful = true;
            long id_recipe = recipeDatasource.InsertRecipe(RC.getRecipe(), RC.getRecipe().getFrk_user());
            if (id_recipe == -1) {
                // Failed to insert recipe
                isInsertionSuccessful = false;
            } else {
                detailRecipeDataSource.insertDetail_recipe(RC.getDetail_recipe(), (int) id_recipe);
                ingredientsDataSource.insertIngredients(RC.getIngredients(), (int) id_recipe);
                stepsDataSource.insert_Steps(RC.getSteps(), (int) id_recipe);
                fullRecipeLiveData.setValue(RC);
                fullRecipeLiveData.postValue(RC);
                list_recipe.setValue(recipeDatasource.getRecipeByIdUser((int) user_login_local.getUser().getId_User()));
            }
        }
        return fullRecipeLiveData;
    }

    public LiveData<RecipeResponse> updateFullRecipeInLocal(RecipeResponse RC) {
        MutableLiveData<RecipeResponse> fullRecipeLiveData = new MutableLiveData<>();
        if(RC.getRecipe().getUnique_key_recipe()==null) {
            String randomKey = UUID.randomUUID().toString();
            RC.getRecipe().setUnique_key_recipe(randomKey);
            updateRecipeLocally(RC.getRecipe(),RC.getRecipe().getId_recipe());
        }
        if (recipeDatasource.isRecordExist(TABLE_RECIPE, COLUMN_UNIQUE_KEY, RC.getRecipe().getUnique_key_recipe())) {
            int id_recipe = recipeDatasource.UpdateRecipe(RC.getRecipe(), RC.getRecipe().getId_recipe());
            if (id_recipe == -1) {
                // Failed to insert recipe
                fullRecipeLiveData.setValue(null);
            } else {
                id_recipe = RC.getRecipe().getId_recipe();
                detailRecipeDataSource.Update_Detail_Recipe(RC.getDetail_recipe(), id_recipe);
                ingredientsDataSource.Update_Ingerdeients2(RC.getIngredients(), id_recipe);
                stepsDataSource.Update_Step2(RC.getSteps(), id_recipe);
                fullRecipeLiveData.setValue(RC);
                fullRecipeLiveData.postValue(RC);
                list_recipe.postValue(recipeDatasource.getRecipeByIdUser((int) user_login_local.getUser().getId_User()));
            }
        }
        return fullRecipeLiveData;
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
//                    if (CURRENT_RECIPE.getFrk_user() != user_login.getUser().getId_User() && User_CurrentRecipe.getId_User() != CURRENT_RECIPE.getFrk_user())
//                        userRepo.getUserByIdRecipeApi(CURRENT_RECIPE.getId_recipe());
//                    else if (User_CurrentRecipe.getId_User() != CURRENT_RECIPE.getFrk_user()) {
//                        User_CurrentRecipe = user_login.getUser();
//                        //MainFragment.viewPager2.setCurrentItem(1, false);
//                    }
                } else {
                    ErrorHandler.handleErrorResponse(response, appCompatActivity);
                }
            }

            @Override
            public void onFailure(Call<RecipeResponse> call, Throwable t) {
                TAG_CONNEXION = call.hashCode();
                ErrorHandler.handleNetworkFailure(t, appCompatActivity);
            }
        });
        return recipeResponseMutableLiveData;
    }

    public LiveData<String> updateFullRecipeApi(RecipeResponse recipe) {
        MutableLiveData<String> recipeResponseMutableLiveData = new MutableLiveData<>();
        apiService.updateRecipe(Token, recipe).enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String recipeResponse = response.body();
                    if (recipeResponse != null) {
                        recipeResponseMutableLiveData.setValue(recipeResponse);
                    }
                    TAG_CONNEXION_MESSAGE = response.message();
                    TAG_CONNEXION = response.code();
//                    if (CURRENT_RECIPE.getFrk_user() != user_login.getUser().getId_User() && User_CurrentRecipe.getId_User() != CURRENT_RECIPE.getFrk_user())
//                        userRepo.getUserByIdRecipeApi(CURRENT_RECIPE.getId_recipe());
//                    else if (User_CurrentRecipe.getId_User() != CURRENT_RECIPE.getFrk_user()) {
//                        User_CurrentRecipe = user_login.getUser();
//                        //MainFragment.viewPager2.setCurrentItem(1, false);
//                    }
                } else {
                    ErrorHandler.handleErrorResponse(response, appCompatActivity);
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                TAG_CONNEXION = call.hashCode();
                ErrorHandler.handleNetworkFailure(t, appCompatActivity);
            }
        });
        return recipeResponseMutableLiveData;
    }


    public LiveData<List<Recipe>> getRecipesByConditionApi(Map<String, String> conditions) {
        MutableLiveData<List<Recipe>> data = new MutableLiveData<>();
        /*Map<String, String> condition = new HashMap<>();
        conditions.put("recipeName", "Spaghetti");
        conditions.put("ingredientName", "Tomato");
        conditions.put("userId", "1");*/

        apiService.getRecipesByConditions(Token, conditions).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    data.postValue(response.body());
                } else {
                    // Handle error
                    //ErrorHandler.handleErrorResponse(response, appCompatActivity);
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                ErrorHandler.handleNetworkFailure(t, appCompatActivity);
            }
        });
        return data;
    }

    public LiveData<List<Recipe>> getRecipes() {
        MutableLiveData<List<Recipe>> remoteRecipeList = new MutableLiveData<>();
        apiService.getAllRecipes(Token).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    List<Recipe> recipes = response.body();
                    remoteRecipeList.setValue(recipes);
                    remoteRecipeList.postValue(recipes);
                } else {
                    ErrorHandler.handleErrorResponse(response, appCompatActivity);
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                ErrorHandler.handleNetworkFailure(t, appCompatActivity);
            }
        });
        return remoteRecipeList;
    }

    //TODO make synch with recipe with it image and with full data
    public LiveData<List<Recipe>> getRecipesByUsername(String username) {
        MutableLiveData<List<Recipe>> remoteRecipeListByUser = new MutableLiveData<>();
        apiService.getRecipeByUsernameUser(Token, username).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    remoteRecipeListByUser.postValue(response.body());
                    if (!getUserSynch(username, context) && list_recipe.getValue().size() < remoteRecipeListByUser.getValue().size()) {
                        if (remoteRecipeListByUser.getValue() != null && !remoteRecipeListByUser.getValue().isEmpty()) {
                            // Synchronize data from local to remote
                            //synchronizeDataFromLocalToRemote(list_recipe.getValue(), remoteRecipeListByUser.getValue(), username);
                        }
                    } else {
                        saveUserSynch(username, true, context);
                    }

                } else {
                    // Handle error response here
                    if (response != null)
                        ErrorHandler.handleErrorResponse(response, appCompatActivity);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Recipe>> call, Throwable t) {
                // Handle network failure
//                ErrorHandler.handleNetworkFailure(t, appCompatActivity);
                ErrorHandler.handleNetworkFailure(t, appCompatActivity, call);
            }
        });
        return remoteRecipeListByUser;
    }

    //TODO make synch with recipe with it image and with full data
    public LiveData<List<RecipeResponse>> getFullRecipesByUsername(String username) {
        MutableLiveData<List<RecipeResponse>> remoteRecipeListByUser = new MutableLiveData<>();
        apiService.getFullRecipesByIdUsername(Token, username).enqueue(new Callback<List<RecipeResponse>>() {
            @Override
            public void onResponse(Call<List<RecipeResponse>> call, Response<List<RecipeResponse>> response) {
                if (response.isSuccessful()) {
                    RemotelistFullRecipe.postValue(response.body());
                    remoteRecipeListByUser.setValue(response.body());
                } else {
                    // Handle error response here
                    if (response != null)
                        ErrorHandler.handleErrorResponse(response, appCompatActivity);
                }
            }

            @Override
            public void onFailure(Call<List<RecipeResponse>> call, Throwable t) {
                // Handle network failure
//                ErrorHandler.handleNetworkFailure(t, appCompatActivity);
                ErrorHandler.handleNetworkFailure(t, appCompatActivity, call);
            }
        });
        return remoteRecipeListByUser;
    }

    public LiveData<String> uploadRemoteImageRecipe(String unique_key, Bitmap bitmp) {
        MutableLiveData<String> pathImage = new MutableLiveData<>();
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
        apiService.UpdateRecipeImage(Token, unique_key, filePart).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String path = null;
                    try {
                        path = response.body().string();
                        //String str = new String(bytes, StandardCharsets.UTF_8);
                        path = path.replaceAll("\"", "");// For UTF-8 encoding
                        pathImage.setValue(path);
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
        return pathImage;
    }

    public LiveData<Boolean> synchronizeDataRecipe(List<Recipe> localRecipes, List<RecipeResponse> remoteRecipes, String username) {
        MutableLiveData<Boolean> synch = new MutableLiveData<>();

        // Step 1: Get the user ID from the local database using the username
        int userId = getUserId(username);
        if (userId == -1) {
            synch.setValue(false);
            return synch; // Return immediately if the user ID is invalid
        }

        // Step 2: Perform the synchronization using the user ID
        boolean toRemote = synchronizeDataBetweenLocalToRemote(localRecipes, remoteRecipes, userId);
        boolean toLocal = synchronizeDataBetweenRemoteToLocal(localRecipes, remoteRecipes, userId);

        synch.setValue(toRemote && toLocal);
        return synch;
    }

    private int getUserId(String username) {
        userDatasource.open();
        try {
            if (user_login_local.getUser() != null && user_login_local.getUser().getId_User() != 0) {
                return user_login_local.getUser().getId_User();
            }
            user_login_local.setUser(userDatasource.select_User_BYUsername(username));
            return user_login_local.getUser() != null ? user_login_local.getUser().getId_User() : -1;
        } finally {
            userDatasource.close();
        }
    }

    public Boolean synchronizeDataBetweenRemoteToLocal(List<Recipe> localRecipes, List<RecipeResponse> remoteRecipes, int userId) {
        Set<String> localRecipeNames = new HashSet<>();
        for (Recipe recipe : localRecipes) {
            localRecipeNames.add(recipe.getNom_recipe());
        }

        boolean updated = false;
        for (RecipeResponse remoteRecipe : remoteRecipes) {
            String recipeName = remoteRecipe.getRecipe().getNom_recipe();
            if (localRecipeNames.contains(recipeName)) {
                updateRecipeLocally(remoteRecipe.getRecipe(), getLocalRecipeId(localRecipes, recipeName));
            } else {
                remoteRecipe.getRecipe().setFrk_user(userId);
                insertFullRecipeInLocal(remoteRecipe);
                updated = true;
            }
        }
        return updated || localRecipes.size() == remoteRecipes.size();
    }

    private int getLocalRecipeId(List<Recipe> localRecipes, String recipeName) {
        for (Recipe recipe : localRecipes) {
            if (recipe.getNom_recipe().equals(recipeName)) {
                return recipe.getId_recipe();
            }
        }
        return -1; // Should never happen if the set check is correct
    }

    public Boolean synchronizeDataBetweenLocalToRemote(List<Recipe> localRecipes, List<RecipeResponse> remoteRecipes, int userId) {
        Set<String> remoteRecipeNames = new HashSet<>();
        for (RecipeResponse remoteRecipe : remoteRecipes) {
            remoteRecipeNames.add(remoteRecipe.getRecipe().getNom_recipe());
        }

        boolean updated = false;
        for (Recipe localRecipe : localRecipes) {
            if (localRecipe.getFrk_user() == userId && !remoteRecipeNames.contains(localRecipe.getNom_recipe())) {
                updateRecipeRemotely(localRecipe);
                updated = true;
            }
        }
        return updated;
    }

    public void updateRecipeLocally(Recipe remoteRecipe, int id) {
        recipeDatasource.UpdateRecipe(remoteRecipe, id);
    }

    public int updateRecipeImageLocally(Bitmap image, int id) {
        return recipeDatasource.UpdateRecipeImage(context, image, id);
    }

    private void updateRecipeRemotely(Recipe recipe) {
        // Implement logic to update recipe remotely
    }


    public LiveData<List<Recipe>> searchRecipes(String key) {
        MutableLiveData<List<Recipe>> SearchRecipeList = new MutableLiveData<>();
        apiService.searchRecipes(Token, key).enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    Search_list = response.body();
                    SearchRecipeList.setValue(Search_list);
                    if (Search_list.size() > 0)
                        Log.d("TAG", Constants.Search_list.toString());
                    // Handle the list of products obtained from the server
                } else {
                    // Handle unsuccessful response
                    ErrorHandler.handleErrorResponse(response, appCompatActivity);
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                // Handle failure to make the API call
                ErrorHandler.handleNetworkFailure(t, appCompatActivity);
            }
        });
        return SearchRecipeList;
    }

    public LiveData<byte[]> fetchImage(String imageUrl, Context context) {
        MutableLiveData<byte[]> byteImage = new MutableLiveData<>();

        // Enqueue the download request
        apiService.downloadImage(imageUrl).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        // Convert response body to byte array
                        byte[] bytes = response.body().bytes();
                        // Set byte array value to LiveData
                        byteImage.postValue(bytes);
                        // Show success message on UI thread
                        showToast(context, "Image download successful");
                    } catch (IOException e) {
                        e.printStackTrace();
                        // Show error message on UI thread
                        showToast(context, "Error: " + e.getMessage());
                    }
                } else {
                    // Handle unsuccessful download
                    // Show error message on UI thread
                    showToast(context, "Unsuccessful download: " + response.message());
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                // Show error message on UI thread
                showToast(context, "Failed to connect to server: " + t.getMessage());
            }
        });

        return byteImage;
    }
}
