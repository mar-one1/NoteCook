package com.example.notecook;

import static com.example.notecook.Api.ApiClient.BASE_URL;
import static com.example.notecook.Utils.Constants.RemotelistByIdUser_recipe;
import static com.example.notecook.Utils.Constants.TAG_MODE_INVITE;
import static com.example.notecook.Utils.Constants.Token;
import static com.example.notecook.Utils.Constants.User_CurrentRecipe;
import static com.example.notecook.Utils.Constants.getUserInput;
import static com.example.notecook.Utils.Constants.isConnected;
import static com.example.notecook.Utils.Constants.pathimageuser;
import static com.example.notecook.Utils.Constants.user_login;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Fragement.MainFragment;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.User;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.NetworkChangeReceiver;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.ViewModel.UserViewModel;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CODE = 1000;
    public static String Type_User = "";
    public static byte[] iconUser = null;
    private static ArrayList<String> array_image = new ArrayList<>();

    private IntentFilter filtreConectivite = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    private NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
    private FragmentTransaction fragmentTransaction;
    private RecipeViewModel recipeVM;
    private UserViewModel userVM;

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
        byte[] imageBytes = new byte[0];
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            imageBytes = baos.toByteArray();
            String imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            array_image.add(imageString);
            Log.i("test encode", imageString);
            //decode base64 string to image
            //imageBytes = Base64.decode(array_image.get(0), Base64.DEFAULT);
            //Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        } catch (Exception e) {
            Log.e("tag", "" + e);
        }
        return imageBytes;
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

        Constants.init();

        String[] permissions = {"android.permission.READ_PHONE_STATE", "android.permission.CAMERA", "android.permission.INTERNET"};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);

        String tag = "";
        if (getIntent().getExtras() != null) {
            tag = getIntent().getStringExtra("TAG");
            if (Objects.equals(tag, Constants.TAG_MODE_INVITE))
                Type_User = tag;
        }
        Token = Constants.getToken(this);
        SweetAlertDialog pDialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.PROGRESS_TYPE);
        pDialog.getProgressHelper().setBarColor(Color.parseColor("#E41818"));
        pDialog.setTitleText("Chargement ...");
        pDialog.setCancelable(true);
        pDialog.show();
        // get Recipe From Api


        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_main, new MainFragment());
        fragmentTransaction.commit();

        recipeVM = new RecipeViewModel(this, MainActivity.this);
        userVM = new UserViewModel(this, MainActivity.this);
        recipeVM = new ViewModelProvider(this, recipeVM).get(RecipeViewModel.class);
        userVM = new ViewModelProvider(this, userVM).get(UserViewModel.class);
        if (!Type_User.equals(TAG_MODE_INVITE)) {
//            fetchData();
            String s1 = getUserInput(this);
            userVM.getUser(s1).observe(this, new Observer<User>() {
                @Override
                public void onChanged(User user) {
                    Toast.makeText(getBaseContext(), "user get by observe", Toast.LENGTH_SHORT).show();
                    recipeVM.getRecipesLocal(user.getId_User()).observe(MainActivity.this, new Observer<List<Recipe>>() {
                        @Override
                        public void onChanged(List<Recipe> recipes) {
                            recipeVM.getRecipesByUsername(s1).observe(MainActivity.this, recipeList -> {
                                RemotelistByIdUser_recipe.setValue(recipeList);
                                Toast.makeText(getBaseContext(), "changed main " + RemotelistByIdUser_recipe.getValue().size(), Toast.LENGTH_SHORT).show();
                            });
                        }
                    });
                }

            });
        }
        pDialog.cancel();


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

    private void fetchData() {
        String userInput = getUserInput(getApplicationContext()); // Assuming getUserInput is a method defined elsewhere
        if (userInput == null || userInput.isEmpty()) {
            // Handle empty or null user input
            return;
        }

        userVM.getUser(userInput).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user != null) {
                    // User data retrieved successfully
                    observeLocalRecipes(user.getId_User(), userInput);
                } else {
                    // Handle user not found
                    Toast.makeText(getApplicationContext(), "User not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void observeLocalRecipes(int userId, String username) {
        recipeVM.getRecipesLocal(userId).observe(MainActivity.this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                if (recipes != null) {
                    // Local recipes retrieved successfully
                    observeRemoteRecipes(username);
                } else {
                    // Handle local recipes not found
                    Toast.makeText(getApplicationContext(), "Local recipes not found", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void observeRemoteRecipes(String username) {
        recipeVM.getRecipesByUsername(username).observe(MainActivity.this, new Observer<List<Recipe>>() {
            @Override
            public void onChanged(List<Recipe> recipes) {
                if (recipes != null) {
                    // Remote recipes retrieved successfully
                    RemotelistByIdUser_recipe.setValue(recipes);
                    Toast.makeText(getApplicationContext(), "Changed main " + recipes.size(), Toast.LENGTH_SHORT).show();
                } else {
                    // Handle remote recipes not found
                    Toast.makeText(getApplicationContext(), "Remote recipes not found", Toast.LENGTH_SHORT).show();
                }
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
                if (!isConnected()) {
                    Constants.DisplayErrorMessage(MainActivity.this, "Mode Offline");
                } else Constants.DisplayErrorMessage(MainActivity.this, "Mode Online");
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,
                            new String[]{Manifest.permission.INTERNET}, requestCode);
                }
            }
        }
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


}