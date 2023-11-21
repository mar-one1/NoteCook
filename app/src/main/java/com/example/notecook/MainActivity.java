package com.example.notecook;

import static com.example.notecook.Data.RecipeDatasource.createRecipe;
import static com.example.notecook.Utils.Constants.CURRENT_RECIPE;
import static com.example.notecook.Utils.Constants.Detail_CurrentRecipe;
import static com.example.notecook.Utils.Constants.MODE_ONLINE;
import static com.example.notecook.Utils.Constants.Remotelist_recipe;
import static com.example.notecook.Utils.Constants.Review_CurrentRecipe;
import static com.example.notecook.Utils.Constants.Steps_CurrentRecipe;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.TAG_LOCAL;
import static com.example.notecook.Utils.Constants.TAG_OFFLINE;
import static com.example.notecook.Utils.Constants.Token;
import static com.example.notecook.Utils.Constants.User_CurrentRecipe;
import static com.example.notecook.Utils.Constants.lOGIN_KEY;
import static com.example.notecook.Utils.Constants.list_recipe;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import com.example.notecook.Adapter.Adapter_RC_RecipeDt;
import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Api.ConnexionRetrofit;
import com.example.notecook.Api.TokenResponse;
import com.example.notecook.Data.DetailRecipeDataSource;
import com.example.notecook.Data.RecipeDatasource;
import com.example.notecook.Data.UserDatasource;
import com.example.notecook.Fragement.Acceuill_Frg;
import com.example.notecook.Fragement.MainFragment;
import com.example.notecook.Model.Categorie_Food;
import com.example.notecook.Model.Detail_Recipe;
import com.example.notecook.Model.Ingredient_recipe;
import com.example.notecook.Model.Ingredients;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.Review;
import com.example.notecook.Model.Step;
import com.example.notecook.Model.User;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.SimpleService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 1888;
    private final static String EXIST = "exist";
    private static final int REQUEST_CODE = 1000;
    public static String Type_User = "";
    public static byte[] iconUser = null;
    private static ArrayList<String> array_image = new ArrayList<>();
    private static List<Detail_Recipe> list_detail_recipe = new ArrayList<>();
    private final int STORAGE_PERMISSION_CODE = 23;
    private final int GALLERY_REQUEST_CODE = 24;
    public SharedPreferences sharedPreferences;
    SimpleService service = new SimpleService();
    IntentFilter filtreConectivite = new IntentFilter();
    NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
    FragmentTransaction fragmentTransaction;
    Adapter_RC_RecipeDt adapter_rc_menuCat;
    Acceuill_Frg acceuill_frg;
    Categorie_Food categorie_food;
    Recipe mRecipe;
    SweetAlertDialog pDialog;
    private ArrayList<Ingredients> listIngredient;
    private ArrayList<Ingredient_recipe> listIngredientRecipe;

    public static Bitmap decod(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
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
        imageBytes = Base64.decode(array_image.get(0), Base64.DEFAULT);

        //Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

        return imageBytes;
    }

    public static void UpdateUserApi(User user, Context context) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        // Example: Fetch users from the API

        //RequestBody imageRequestBody = RequestBody.create(MediaType.parse("image/*"), user.getIcon());

        Call<User> call = apiService.updateUserByUsername(user.getUsername(), user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User UserResponse = response.body();
                    if (UserResponse != null) {
                        // Store the token securely (e.g., in SharedPreferences) for later use
                        TAG_CONNEXION = response.code();
                        user_login.setUser(response.body());
                        TAG_CONNEXION_MESSAGE = response.message();
                        //Constants.AffichageMessage("Vous avez Modifier Utilisateur avec  succes with server", context);
                        Toast.makeText(context, TAG_CONNEXION_MESSAGE + " " + "Add User To Api", Toast.LENGTH_LONG).show();
                    }
                } else {

                    // Handle error response here
                    // The HTTP request was not successful (status code is not 2xx).
                    // You can handle errors here based on the response status code.
                    int statusCode = response.code();
                    Constants.TAG_CONNEXION = statusCode;
                    TAG_CONNEXION_MESSAGE = response.message();
                    Toast.makeText(context, TAG_CONNEXION_MESSAGE + " " + "Add User To Api", Toast.LENGTH_LONG).show();

                    // Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, Main);
                    // Handle different status codes as per your API's conventions.
                    if (statusCode == 409) {
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
                    } else Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
                    //Constants.AffichageMessage(response.message(), context);
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                TAG_CONNEXION_MESSAGE = call.toString();
                //Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, context);
                Toast.makeText(context, TAG_CONNEXION_MESSAGE, Toast.LENGTH_SHORT).show();
            }
        });

    }

    public static void synchronizeDataDetailRecipe(Detail_Recipe RemotedetailRecipe, Context context) {
        // Step 1: Compare local and remote data to identify differences

        boolean found = false;
        for (Detail_Recipe localDetailRecipe : list_detail_recipe) {
            if (RemotedetailRecipe.getFrk_recipe() == localDetailRecipe.getFrk_recipe()) {
                // Recipe exists locally; update it if necessary
                if (!RemotedetailRecipe.equals(localDetailRecipe)) {
                    // Update local recipe with remote data
                    updateDetaliRecipeLocally(RemotedetailRecipe, localDetailRecipe.getFrk_recipe(), context);
                }
                found = true;
                break;
            }
        }
        if (!found) {
            // Recipe doesn't exist locally; insert it
            insertDetailRecipeLocally(RemotedetailRecipe, context);
        }

        boolean found1 = false;
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
        detailRecipeDataSource.Update_Detail_Recipe(remotedetailRecipe, frk_recipe);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        getLocalRecipes();
        list_detail_recipe = getAllocalDR();
        getRecipeApi();
        if (user_login.getUser() == null)
            getUserApi("", getBaseContext());
        else getUserApi(user_login.getUser().getUsername(), getBaseContext());
        //---------------------
        pDialog.cancel();
        //Toast.makeText(MainActivity.this,"Offline mode",Toast.LENGTH_LONG );

//        fragmentTransaction = getSupportFragmentManager().beginTransaction();
//        fragmentTransaction.replace(R.id.fl_main, new MainFragment());
//        fragmentTransaction.commit();


    }

    private ArrayList<Recipe> getLocalRecipes() {

        RecipeDatasource recipeDatasource = new RecipeDatasource(this);
        recipeDatasource.open();
        Constants.list_recipe = recipeDatasource.getAllRecipes();
        recipeDatasource.close();
        getLocalDetailsRecipes();
        return Constants.list_recipe;
    }

    public void getLocalDetailsRecipes() {

        DetailRecipeDataSource detailRecipeDataSource = new DetailRecipeDataSource(this);
        detailRecipeDataSource.open();
        Constants.list_Detailrecipe = detailRecipeDataSource.getAllDR();
        detailRecipeDataSource.close();
    }

    public void getDetailRecipeByIdRecipeApi(int Recipeid) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<Detail_Recipe> call = apiService.getDetailRecipeByIdRecipeFRK(Token, Recipeid);


        call.enqueue(new Callback<Detail_Recipe>() {
            @Override
            public void onResponse(Call<Detail_Recipe> call, Response<Detail_Recipe> response) {
                if (response.isSuccessful()) {
                    Detail_Recipe detail_recipe = response.body();
                    Detail_CurrentRecipe = detail_recipe;
                    Log.d("TAG", detail_recipe.getLevel().toString());
                    TAG_CONNEXION_MESSAGE = response.message();
                    TAG_CONNEXION = response.code();

                    if (CURRENT_RECIPE.getFrk_user() != user_login.getUser().getId_User())
                        getUserByIdRecipeApi(CURRENT_RECIPE.getId_recipe());
                    else {
                        User_CurrentRecipe = user_login.getUser();
                        MainFragment.viewPager2.setCurrentItem(1);
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
            public void onFailure(Call<Detail_Recipe> call, Throwable t) {
                TAG_CONNEXION = call.hashCode();
            }
        });

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

    public void getUserByIdRecipeApi(int Recipeid) {

        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<User> call = apiService.getUserByIdRecipe(Token, Recipeid);


        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User user = response.body();
                    User_CurrentRecipe = user;
                    Log.d("TAG", user.getUsername().toString());
                    TAG_CONNEXION_MESSAGE = response.message();
                    TAG_CONNEXION = response.code();
                    MainFragment.viewPager2.setCurrentItem(1);
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

    private void getLocalUser(String username) {
        UserDatasource userDatasource = new UserDatasource(this);
        userDatasource.open();
        if (user_login == null) {
            user_login = new TokenResponse();
        }
        User user = userDatasource.select_User_BYUsername(username);
        user_login.setUser(user);
        user_login.setMessage(TAG_LOCAL);
        userDatasource.close();
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

    public void getUserApi(String username, Context context) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        // Example: Fetch users from the API
        sharedPreferences = getSharedPreferences(lOGIN_KEY, Context.MODE_PRIVATE);
        String s1 = sharedPreferences.getString("username", "");
        String s2 = sharedPreferences.getString("password", "");

        Call<User> call = apiService.getUserByUsername(s1);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User UserResponse = response.body();
                    if (UserResponse != null) {
                        // Store the token securely (e.g., in SharedPreferences) for later use
                        TAG_CONNEXION = response.code();
                        user_login.setUser(UserResponse);
                        TAG_CONNEXION_MESSAGE = response.message();
                        //Constants.AffichageMessage("Vous avez Modifier Utilisateur avec  succes with server", context);
                        Toast.makeText(context, TAG_CONNEXION_MESSAGE + " " + "get User from Api", Toast.LENGTH_LONG).show();
                    }
                } else {

                    // Handle error response here
                    // The HTTP request was not successful (status code is not 2xx).
                    // You can handle errors here based on the response status code.
                    int statusCode = response.code();
                    Constants.TAG_CONNEXION = statusCode;
                    TAG_CONNEXION_MESSAGE = response.message();
                    Toast.makeText(context, TAG_CONNEXION_MESSAGE + " " + "get User from Api", Toast.LENGTH_LONG).show();

                    // Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, Main);
                    // Handle different status codes as per your API's conventions.
                    if (statusCode == 409) {
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
                    } else Toast.makeText(context, "User not found", Toast.LENGTH_SHORT).show();
                    //Constants.AffichageMessage(response.message(), context);
                }

                if (TAG_CONNEXION != 200) {
                    //Constants.AffichageMessage("Online mode", MainActivity.this);
                    //Toast.makeText(MainActivity.this,"Online mode",Toast.LENGTH_LONG );
                    getLocalRecipes();
                    getLocalUser(s1);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fl_main, new MainFragment());
                    fragmentTransaction.commit();
                }

//                if (response.errorBody() != null) {
//                    try {
//                        String errorResponse = response.errorBody().string();
//                        // Print or log the errorResponse for debugging
//                        TAG_CONNEXION_MESSAGE = errorResponse;
//                        Constants.AffichageMessage(TAG_ERREUR_SYSTEM,Main);
//                        //Constants.DisplayErrorMessage(Main,TAG_CONNEXION_MESSAGE);
//                        TAG_CONNEXION = response.code();
//                        Log.e("token", "Error Response: " + errorResponse);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {

                TAG_CONNEXION_MESSAGE = call.toString();
                //Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, context);
                Toast.makeText(context, TAG_CONNEXION_MESSAGE, Toast.LENGTH_SHORT).show();

                if (TAG_CONNEXION != 200) {
                    //Constants.AffichageMessage("Online mode", MainActivity.this);
                    //Toast.makeText(MainActivity.this,"Online mode",Toast.LENGTH_LONG );
                    getLocalRecipes();
                    getLocalUser(s1);
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fl_main, new MainFragment());
                    fragmentTransaction.commit();
                }
            }
        });
    }

    public void synchronizeData(List<Recipe> localRecipes, List<Recipe> remoteRecipes) {
        // Step 1: Compare local and remote data to identify differences
        for (Recipe remoteRecipe : remoteRecipes) {
            boolean found = false;
            for (Recipe localRecipe : localRecipes) {
                if (remoteRecipe.getId_recipe() == localRecipe.getId_recipe()) {
                    // Recipe exists locally; update it if necessary
                    if (!remoteRecipe.equals(localRecipe)) {
                        // Update local recipe with remote data
                        updateRecipeLocally(remoteRecipe, localRecipe.getId_recipe());
                    }
                    found = true;
                    break;
                }
            }
            if (!found) {
                // Recipe doesn't exist locally; insert it
                insertRecipeLocally(remoteRecipe);
            }
        }

        // Handle deleted recipes
        for (Recipe localRecipe : localRecipes) {
            boolean found = false;
            for (Recipe remoteRecipe : remoteRecipes) {
                if (localRecipe.getId_recipe() == remoteRecipe.getId_recipe()) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                // Recipe exists locally but not remotely; mark it as deleted
                //markRecipeAsDeletedLocally(localRecipe);
            }
        }
    }

    private List<Detail_Recipe> getAllocalDR() {
        List<Detail_Recipe> localDetaliRecipes;
        DetailRecipeDataSource detailRecipeDataSource = new DetailRecipeDataSource(MainActivity.this);
        detailRecipeDataSource.open();
        localDetaliRecipes = detailRecipeDataSource.getAllDR();
        detailRecipeDataSource.close();
        return localDetaliRecipes;
    }

    private void insertRecipeLocally(Recipe remoteRecipe) {
        RecipeDatasource recipeDatasource = new RecipeDatasource(this);
        recipeDatasource.open();
        createRecipe(remoteRecipe.getIcon_recipe(), remoteRecipe.getNom_recipe(), remoteRecipe.getFav(), 1);
        recipeDatasource.close();
    }

    private void updateRecipeLocally(Recipe remoteRecipe, int id) {
        RecipeDatasource recipeDatasource = new RecipeDatasource(this);
        recipeDatasource.open();
        recipeDatasource.UpdateRecipe(remoteRecipe, id);
        recipeDatasource.close();
    }


    private void getRecipeApi() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        sharedPreferences = getSharedPreferences(lOGIN_KEY, Context.MODE_PRIVATE);
        String s1 = sharedPreferences.getString("username", "");
        String s2 = sharedPreferences.getString("password", "");

        Call<List<Recipe>> call = apiService.getAllRecipes(getToken());

        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    List<Recipe> recipes = response.body();
                    Log.d("recipes", recipes.toString());
                    Remotelist_recipe.addAll(recipes);
                    Log.d("listrecipes", Remotelist_recipe.toString());
                    TAG_CONNEXION_MESSAGE = response.message();
                    TAG_CONNEXION = response.code();
                    //TokenApi(Token);
                    if (Remotelist_recipe.size() != 0) {
                        synchronizeData(list_recipe, Remotelist_recipe);
                    }
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fl_main, new MainFragment());
                    fragmentTransaction.commit();
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
            public void onFailure(Call<List<Recipe>> call, Throwable t) {
                TAG_CONNEXION = call.hashCode();
            }
        });


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

    public  void searchRecipes(String key)
    {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);

        Call<List<Recipe>> call = apiService.searchRecipes(Token,key);
        call.enqueue(new Callback<List<Recipe>>() {
            @Override
            public void onResponse(Call<List<Recipe>> call, Response<List<Recipe>> response) {
                if (response.isSuccessful()) {
                    Constants.Search_list = response.body();
                    Log.d("TAG",Constants.Search_list.toString());
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

}