package com.example.notecook.Activity;

import static com.example.notecook.Utils.Constants.TAG_MODE_INVITE;
import static com.example.notecook.Utils.Constants.Token;
import static com.example.notecook.Utils.Constants.getToken;
import static com.example.notecook.Utils.Constants.getUserInput;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.notecook.Activity.OnBoarding.OnBoarding_screen;
import com.example.notecook.Fragement.MainFragment;
import com.example.notecook.Model.Category_Recipe;
import com.example.notecook.Model.Nutrition;
import com.example.notecook.Model.User;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.FetchNutritionTask;
import com.example.notecook.Utils.NetworkChangeReceiver;
import com.example.notecook.Utils.NutritionParser;
import com.example.notecook.ViewModel.CategoriesViewModel;
import com.example.notecook.ViewModel.IngredientsViewModel;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.ViewModel.UserViewModel;
import com.example.notecook.databinding.ActivityMainBinding;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class MainActivity extends AppCompatActivity  {


    private static final int REQUEST_CODE = 1000;
    public static String Type_User = "";
    public static byte[] iconUser = null;
    private static ArrayList<String> array_image = new ArrayList<>();
    private IntentFilter filtreConectivite = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    private NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
    private FragmentTransaction fragmentTransaction;
    private RecipeViewModel recipeVM;
    private UserViewModel userVM;
    private CategoriesViewModel categoryVM;
    private IngredientsViewModel ingredientsVM;
    private ActivityMainBinding binding;
    private View view;
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
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        view = binding.getRoot();

        //check OnBoarding
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);

        if (isFirstRun) {
            Intent intent = new Intent(this, OnBoarding_screen.class);
            startActivity(intent);
            finish();
            return;
        }

        Constants.init();
        Token = getToken(this);

        //Get All Ingredients Recipes
        ingredientsVM = new IngredientsViewModel(this, this);
        ingredientsVM.getAllIngredientsApi();

        String[] permissions = {"android.permission.READ_PHONE_STATE", "android.permission.CAMERA", "android.permission.INTERNET"};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);

        recipeVM = new RecipeViewModel(this, this);
        userVM = new UserViewModel(this, this);
        categoryVM = new CategoriesViewModel(this, this);
        recipeVM = new ViewModelProvider(this, recipeVM).get(RecipeViewModel.class);
        userVM = new ViewModelProvider(this, userVM).get(UserViewModel.class);
        categoryVM = new ViewModelProvider(this, categoryVM).get(CategoriesViewModel.class);

        String tag = "";
        if (getIntent().getExtras() != null) {
            tag = getIntent().getStringExtra("TAG");
            if (Objects.equals(tag, TAG_MODE_INVITE))
                Type_User = tag;
        }
        // get Recipe From Api
        if (!Type_User.equals(TAG_MODE_INVITE)) {
            getUserInfo();
            categoryVM.getCategories().observe(this, new Observer<List<Category_Recipe>>() {
                @Override
                public void onChanged(List<Category_Recipe> category_recipes) {
                    if(category_recipes!=null) {
                        Constants.All_Categories_Recipe = category_recipes;
                        Log.d("tag cat", String.valueOf(Constants.All_Categories_Recipe.size()));
                    }
                }
            });
        }

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_main, new MainFragment());
        fragmentTransaction.commit();

        setContentView(view);
    }

    private void getUserInfo() {
//            fetchData();
        //Constants.loading_ui(this,"Loading...");
        String s1 = getUserInput(this);
        userVM.getUser(s1).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                Toast.makeText(getBaseContext(), "user get by observe", Toast.LENGTH_SHORT).show();
                //extracted();
                // Constants.stop_loading();
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