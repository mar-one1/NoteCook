package com.example.notecook;

import static com.example.notecook.Api.ApiClient.BASE_URL;
import static com.example.notecook.Utils.Constants.All_Ingredients_Recipe;
import static com.example.notecook.Utils.Constants.MODE_ONLINE;
import static com.example.notecook.Utils.Constants.RemotelistByIdUser_recipe;
import static com.example.notecook.Utils.Constants.Remotelist_recipe;
import static com.example.notecook.Utils.Constants.Review_CurrentRecipe;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.TAG_MODE_INVITE;
import static com.example.notecook.Utils.Constants.TAG_OFFLINE;
import static com.example.notecook.Utils.Constants.Token;
import static com.example.notecook.Utils.Constants.User_CurrentRecipe;
import static com.example.notecook.Utils.Constants.lOGIN_KEY;
import static com.example.notecook.Utils.Constants.listUser;
import static com.example.notecook.Utils.Constants.list_recipe;
import static com.example.notecook.Utils.Constants.pathimageuser;
import static com.example.notecook.Utils.Constants.user_login;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Api.ConnexionRetrofit;
import com.example.notecook.Api.ValidationError;
import com.example.notecook.Data.DetailRecipeDataSource;
import com.example.notecook.Data.RecipeDatasource;
import com.example.notecook.Fragement.Favorite_User_Recipe;
import com.example.notecook.Fragement.MainFragment;
import com.example.notecook.Model.Detail_Recipe;
import com.example.notecook.Model.Ingredients;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.Review;
import com.example.notecook.Model.User;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.SimpleService;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.ViewModel.UserViewModel;
import com.google.gson.Gson;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.annotation.Nullable;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CODE = 1000;
    public static String Type_User = "";
    public static byte[] iconUser = null;
    private static ArrayList<String> array_image = new ArrayList<>();
    private static List<Detail_Recipe> list_detail_recipe = new ArrayList<>();

    public SharedPreferences sharedPreferences;
    SimpleService service = new SimpleService();
    IntentFilter filtreConectivite = new IntentFilter();
    NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
    FragmentTransaction fragmentTransaction;

    private boolean doubleBackToExitPressedOnce = false;
    private SwipeRefreshLayout swipeRefreshLayout;

    public static Bitmap decod(byte[] image) {
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        } catch (Exception e) {
            Log.e("tag", "" + e);
        }
        return bitmap;
    }

    public static byte[] encod(Bitmap b) {
        //Bitmap bb = Bitmap.createBitmap(b);
        byte[] imageBytes;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        imageBytes = baos.toByteArray();
        String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        array_image.add(imageString);
        Log.i("test encode", imageString);
        //decode base64 string to image
        //imageBytes = Base64.decode(array_image.get(0), Base64.DEFAULT);
        //Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        return imageBytes;
    }



    public static void UpdateUserApi(User user, Context context) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        // Example: Fetch users from the API

        //RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), user.getIcon());

        Call<User> call = apiService.updateUserByUsername(user.getUsername().toString(), user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                if (response.isSuccessful()) {
                    User UserResponse = response.body();

                    if (UserResponse != null) {
                        UserResponse.setId_User(user_login.getUser().getId_User());
                        user_login.setUser(UserResponse);
                        Log.d("TAG", String.valueOf(UserResponse.getId_User()));
                        // Store the token securely (e.g., in SharedPreferences) for later use
                        TAG_CONNEXION = response.code();
                        user_login.setUser(response.body());
                        TAG_CONNEXION_MESSAGE = response.message();
                        //Constants.AffichageMessage("Vous avez Modifier Utilisateur avec  succes with server", context);
                        Toast.makeText(context, TAG_CONNEXION_MESSAGE + " " + "user updated To Api", Toast.LENGTH_LONG).show();
                    }
                } else {

                    // Handle error response here
                    // The HTTP request was not successful (status code is not 2xx).
                    // You can handle errors here based on the response status code.
                    Toast.makeText(context, TAG_CONNEXION_MESSAGE + " " + "User not updated To Api", Toast.LENGTH_LONG).show();
                    handleErrorResponse(context,"User",response);

                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                TAG_CONNEXION_MESSAGE = call.toString();
                //Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, context);
                Toast.makeText(context, TAG_CONNEXION_MESSAGE, Toast.LENGTH_SHORT).show();
                handleNetworkFailure(context,call);
            }
        });

    }

    private static void handleErrorResponse(Context context, String model, Response<?> response) {
        int statusCode = response.code();
        String message = response.message();
        if (response.errorBody() != null) {
            if (response.code() == 400) {
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
                    Toast.makeText(context, errorMessages, Toast.LENGTH_LONG).show();
                } catch (IOException e) {
                    // Handle error parsing error body
                }
            }
        } else if (statusCode == 409) {
            //Constants.AffichageMessage("User already exists", context);
            //Toast.makeText(context, "User already exists", Toast.LENGTH_SHORT).show();
            // Unauthorized, handle accordingly (e.g., reauthentication).
        } else if (statusCode == 404) {
            // Not found, handle accordingly (e.g., show a 404 error message).
            //Constants.AffichageMessage(TAG_OFFLINE, context);
            Toast.makeText(context, TAG_OFFLINE, Toast.LENGTH_SHORT).show();
        } else if (statusCode >= 500) {
            // Handle other status codes or generic error handling.
            //Constants.AffichageMessage("Internal Server Error", context);
            Toast.makeText(context, "Internal Server Error", Toast.LENGTH_SHORT).show();
        } else if (statusCode == 406) {
            // Handle other status codes or generic error handling.
            // Constants.AffichageMessage("User not found", context);
        } else Toast.makeText(context, model + " not found", Toast.LENGTH_SHORT).show();
        //Constants.AffichageMessage(response.message(), context);

    }

    private static void handleNetworkFailure(Context context, Call<User> call) {
        // Handle network failure
        TAG_CONNEXION_MESSAGE = call.toString();
        //Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, context);
        Toast.makeText(context, TAG_CONNEXION_MESSAGE, Toast.LENGTH_SHORT).show();
    }


    public static void synchronizeDataDetailRecipe(Detail_Recipe RemotedetailRecipe, Context context) {
        // Step 1: Compare local and remote data to identify differences
        list_detail_recipe = getAllocalDR(context);
        boolean found = false;
        for (Detail_Recipe localDetailRecipe : list_detail_recipe) {
            Log.d("found", "fooor" + localDetailRecipe.getDt_recipe());


            Log.d("found", "RemotedetailRecipe" + RemotedetailRecipe.getFrk_recipe());
            Log.d("found", "localDetailRecipe" + localDetailRecipe.getFrk_recipe());
            if (RemotedetailRecipe.getFrk_recipe() == localDetailRecipe.getFrk_recipe()) {
                // Recipe exists locally; update it if necessary
                //if (!RemotedetailRecipe.equals(localDetailRecipe)) {
                // Update local detail recipe with remote data
                updateDetaliRecipeLocally(RemotedetailRecipe, RemotedetailRecipe.getFrk_recipe(), context);
                //}
                found = true;
                Log.d("found", "" + found);
                break;
            }
        }
        if (!found) {
            // Recipe doesn't exist locally; insert it
            Log.d("found", "" + found);
            insertDetailRecipeLocally(RemotedetailRecipe, context);
        }

       /* boolean found1 = false;
        // Handle deleted recipes
        for (Detail_Recipe localDeatilRecipe : list_detail_recipe) {

            if (localDeatilRecipe.getFrk_recipe() == RemotedetailRecipe.getFrk_recipe()) {
                found1 = true;
                break;
            }
        }
        if (!found1) {
            // Recipe exists locally but not remotely; mark it as deleted
            markDetailRecipeAsDeletedLocally(RemotedetailRecipe, context);
        }

        */
    }

    private static void insertDetailRecipeLocally(Detail_Recipe remotedetailRecipe, Context context) {
        DetailRecipeDataSource detailRecipeDataSource = new DetailRecipeDataSource(context);
        detailRecipeDataSource.open();
        detailRecipeDataSource.insertDetail_recipe(remotedetailRecipe);
        detailRecipeDataSource.close();
    }

    private static void updateDetaliRecipeLocally(Detail_Recipe remotedetailRecipe, int frk_recipe, Context context) {
        DetailRecipeDataSource detailRecipeDataSource = new DetailRecipeDataSource(context);
        detailRecipeDataSource.open();
        detailRecipeDataSource.Update_Detail_RecipeByIdRecipe(remotedetailRecipe, frk_recipe);
        detailRecipeDataSource.close();
    }

    private static void markRecipeAsDeletedLocally(Recipe localRecipe, Context context) {
        RecipeDatasource recipeDatasource = new RecipeDatasource(context);
        recipeDatasource.open();
        recipeDatasource.deleteRecipe(localRecipe);
        recipeDatasource.close();
    }

    private static void markDetailRecipeAsDeletedLocally(Detail_Recipe localDetailRecipe, Context context) {
        DetailRecipeDataSource detailRecipeDataSource = new DetailRecipeDataSource(context);
        detailRecipeDataSource.open();
        detailRecipeDataSource.deleteDR(localDetailRecipe);
        detailRecipeDataSource.close();
    }








    public static void uploadImage(String username, Bitmap bitmp, String type, Context context) {
        //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.add_photo_profil); // Replace 'your_image' with the image resource name
        // Create a file to save the bitmap
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
        Call<ResponseBody> call = apiService.uploadFile(username, filePart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    String path = null;
                    try {
                        path = response.body().string();
                        //String str = new String(bytes, StandardCharsets.UTF_8);
                        path = path.replaceAll("\"", "");// For UTF-8 encoding
                        if (!type.equals("register"))
                            user_login.getUser().setPathimageuser(path);
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
                // Handle failure
                Toast.makeText(context, "OnFailure upload image : " + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void fetchImage(String s, String tag, int position, Context context) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        // URL of the image you want to download
        //String imageUrl = "https://da97-196-75-207-18.ngrok.io/uploads/1701348093930-989771596-image.jpg"; // Replace with your image URL
        String imageUrl = BASE_URL + "uploads/" + s; // Replace with your image URL

        // Enqueue the download request
        Call<ResponseBody> call = apiService.downloadImage(imageUrl);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    //ResponseBody responseBody = response.body();
                    byte[] bytes = new byte[0];
                    try {
                        bytes = response.body().bytes();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    //Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length)
                    // ;
                    if (Objects.equals(tag, "user_login"))
                        user_login.getUser().setIcon(bytes);
                    pathimageuser = s;
                    if (Objects.equals(tag, "recipe_user")) {
                        User_CurrentRecipe.setIcon(bytes);
                        MainFragment.viewPager2.setCurrentItem(1, false);
                    }
//                    if(Objects.equals(tag, "image_recipe"))
//                    {
//                        Remotelist_recipe.get(position).setIcon_recipe(bytes);
//
//                    }
                    Toast.makeText(context, "succes  image down : ", Toast.LENGTH_SHORT).show();
                } else {
                    // Handle unsuccessful download
                    Toast.makeText(context, "unsuccessful download" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // Handle failure
                Toast.makeText(context, "Handle failure", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private static List<Detail_Recipe> getAllocalDR(Context context) {
        List<Detail_Recipe> localDetaliRecipes;
        DetailRecipeDataSource detailRecipeDataSource = new DetailRecipeDataSource(context);
        detailRecipeDataSource.open();
        localDetaliRecipes = detailRecipeDataSource.getAllDR();
        detailRecipeDataSource.close();
        return localDetaliRecipes;
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            // If you want to exit the app when back button is pressed twice
            System.exit(0);
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        // Reset flag after a certain time (e.g., 2 seconds)
        new android.os.Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list_recipe.setValue(new ArrayList<>());
        listUser = new ArrayList<>();
        Remotelist_recipe.setValue(new ArrayList<>());

        String[] permissions = {"android.permission.READ_PHONE_STATE", "android.permission.CAMERA", "android.permission.INTERNET"};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);

        String tag = "";
        if (getIntent().getExtras() != null) {
            tag = getIntent().getStringExtra("TAG");
            if (Objects.equals(tag, Constants.TAG_MODE_INVITE))
                Type_User = tag;
        }
        Token = getToken();
        SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#E41818"));
        pDialog.setTitleText("Chargement ...");
        pDialog.setCancelable(true);
        pDialog.show();
        // get Recipe From Api

        list_detail_recipe = getAllocalDR(getBaseContext());

        sharedPreferences = getSharedPreferences(lOGIN_KEY, Context.MODE_PRIVATE);
        String s1 = sharedPreferences.getString("username", "");
        String s2 = sharedPreferences.getString("password", "");

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_main, new MainFragment());
        fragmentTransaction.commit();

        RecipeViewModel model = new RecipeViewModel(this);
        UserViewModel userVM = new UserViewModel(this);
        if (!Type_User.equals(TAG_MODE_INVITE)) {

           userVM.getUser(s1).observe(this, new Observer<User>() {
               @Override
               public void onChanged(User user) {
                   Toast.makeText(getBaseContext(), "user get by observe", Toast.LENGTH_SHORT).show();
               }
           });

            //---------------------
            //Toast.makeText(MainActivity.this,"Offline mode",Toast.LENGTH_LONG );

            /*model.getRecipes().observe(this, new Observer<List<Recipe>>() {
                @Override
                public void onChanged(@Nullable List<Recipe> recipeList) {
                    //Remotelist_recipe.clear();
                    Remotelist_recipe.setValue(recipeList);
                    Toast.makeText(getBaseContext(), "changed main " + Remotelist_recipe.getValue().size(), Toast.LENGTH_SHORT).show();
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fl_main, new MainFragment());
                    fragmentTransaction.commit();
                }
            });*/
        }
        pDialog.cancel();
        model.getRecipesByUsername(s1).observe(this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(@Nullable List<Recipe> recipeList) {
                //Remotelist_recipe.clear();
                RemotelistByIdUser_recipe.setValue(recipeList);
                Toast.makeText(getBaseContext(), "changed main " + RemotelistByIdUser_recipe.getValue().size(), Toast.LENGTH_SHORT).show();
            }
        });

        getIngredientsRecipeApi();



        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Perform your data refreshing operations here
                onResume();
                // Simulate refresh delay (remove this in your actual code)
                new android.os.Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // Finish refreshing
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 2000); // 2 seconds simulated refresh time (adjust as needed)
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_CODE) {
//            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//                Constants.DisplayErrorMessage(MainActivity.this, "Required Camera Permission For take Photo For your App");
//            } else
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, requestCode);
//
            if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                if (!ConnexionRetrofit.isOnline(MainActivity.this)) {
                    Constants.DisplayErrorMessage(MainActivity.this, "Mode Offline");
                } else Constants.DisplayErrorMessage(MainActivity.this, "Mode Online");
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.INTERNET}, requestCode);
                }
            }
        }
    }




    public void getReviewRecipeApi(int idRecipe) {

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<List<Review>> call = apiService.getReviewByIdRecipe(Token, idRecipe);

        call.enqueue(new Callback<List<Review>>() {
            @Override
            public void onResponse(Call<List<Review>> call, Response<List<Review>> response) {
                if (response.isSuccessful()) {
                    Review_CurrentRecipe = response.body();
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
            public void onFailure(Call<List<Review>> call, Throwable t) {
                TAG_CONNEXION = call.hashCode();
            }
        });
    }

    public void getIngredientsRecipeApi() {

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<List<Ingredients>> call = apiService.getAllIngredients(Token);

        call.enqueue(new Callback<List<Ingredients>>() {
            @Override
            public void onResponse(Call<List<Ingredients>> call, Response<List<Ingredients>> response) {
                if (response.isSuccessful()) {
                    All_Ingredients_Recipe = response.body();
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
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Constants.alertDialog != null && Constants.alertDialog.isShowing()) {
            Constants.alertDialog.dismiss();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(networkChangeReceiver, filtreConectivite);
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(networkChangeReceiver);
    }

    public String getToken() {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return preferences.getString("token", null);
    }

    public void searchRecipes(String key) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<List<Recipe>> call = apiService.searchRecipes(Token, key);
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    Constants.Search_list = response.body();
                    Log.d("TAG", Constants.Search_list.toString());
                    // Handle the list of products obtained from the server
                } else {
                    // Handle unsuccessful response
                }
            }

            @Override
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                // Handle failure to make the API call
            }
        });
    }

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


    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(final Context context, final Intent intent) {
            if (checkInternet(context)) {
                Toast.makeText(context, "Network Available Do operations", Toast.LENGTH_LONG).show();
                MODE_ONLINE = true;
            } else MODE_ONLINE = false;


        }

        boolean checkInternet(Context context) {
            return service.isNetworkAvailable();
        }
    }


}