package com.example.notecook.Activity;

import static com.example.notecook.Api.env.BASE_URL;
import static com.example.notecook.Utils.Constants.TAG_MODE_INVITE;
import static com.example.notecook.Utils.Constants.getToken;
import static com.example.notecook.Utils.Constants.getUserInput;
import static com.example.notecook.Utils.Constants.handleDbChange;
import static com.example.notecook.Utils.ImageHelper.decodeBase64ToBitmap;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Activity.OnBoarding.OnBoarding_screen;
import com.example.notecook.Adapter.Adapter_RC_RecipeDt;
import com.example.notecook.Fragement.MainFragment;
import com.example.notecook.Model.Category_Recipe;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.User;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.ImageHelper;
import com.example.notecook.Utils.NetworkChangeReceiver;
import com.example.notecook.ViewModel.SharedRecipeViewModel;
import com.example.notecook.ViewModel.CategoriesViewModel;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.ViewModel.UserViewModel;
import com.example.notecook.databinding.ActivityMainBinding;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {


    private static final int REQUEST_CODE = 1000;
    public static String Type_User = "";
    private IntentFilter filtreConectivite = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
    private NetworkChangeReceiver networkChangeReceiver = new NetworkChangeReceiver();
    private FragmentTransaction fragmentTransaction;
    private RecipeViewModel recipeVM;
    private UserViewModel userVM;
    private CategoriesViewModel categoryVM;
    private ActivityMainBinding binding;
    private View view;
    private boolean doubleBackToExitPressedOnce = false;
    private SharedRecipeViewModel viewModel;
    public static final String TAG_REMOTE = "Remote";
    public static final String TAG_LOCAL = "Local";
    private Adapter_RC_RecipeDt adapter_rc_recipeDt;


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
        viewModel = new ViewModelProvider(this).get(SharedRecipeViewModel.class);
        //check OnBoarding
        SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
        boolean isFirstRun = prefs.getBoolean("isFirstRun", true);

        if (isFirstRun) {
            Intent intent = new Intent(this, OnBoarding_screen.class);
            startActivity(intent);
            finish();
            return;
        }
        viewModel.init();
        viewModel.setToken(getToken(this));

        String[] permissions = {"android.permission.READ_PHONE_STATE", "android.permission.CAMERA", "android.permission.INTERNET"};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);

        recipeVM = new RecipeViewModel(this, this,viewModel);
        userVM = new UserViewModel(this, this,viewModel);
        categoryVM = new CategoriesViewModel(this, this,viewModel);
        recipeVM = new ViewModelProvider(this, recipeVM).get(RecipeViewModel.class);
        userVM = new ViewModelProvider(this, userVM).get(UserViewModel.class);
        categoryVM = new ViewModelProvider(this, categoryVM).get(CategoriesViewModel.class);

        String tag = "";
        if (getIntent().getExtras() != null) {
            tag = getIntent().getStringExtra("TAG");
                Type_User = tag;
        }
        // get Recipe From Api
        if (Type_User!=null && !Type_User.equals(TAG_MODE_INVITE)) {
            getUserInfo();
            categoryVM.getCategories().observe(this, new Observer<List<Category_Recipe>>() {
                @Override
                public void onChanged(List<Category_Recipe> category_recipes) {
                    if (category_recipes != null) {
                        viewModel.setAllCategoriesRecipe(category_recipes);
                        Log.d("tag cat", String.valueOf(viewModel.getAllCategoriesRecipe().size()));
                    }
                }
            });
            fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fl_main, new MainFragment());
            fragmentTransaction.commit();

        }else if(getToken(this).isEmpty()) {
            Intent intent = new Intent(this,Login.class);
            startActivity(intent);
            return;
        }



        setContentView(view);
    }

    private void getUserInfo() {
//            fetchData();
        //Constants.loading_ui(this,this,"Loading...");
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


    public static void showImageUsers(User user, ImageView imageView) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            if (user.getPathimageuser() != null && !user.getPathimageuser().isEmpty()) {
                String path = user.getPathimageuser();

                if (path.startsWith("data:")) {
                    // Decode base64 in background
                    String base64 = path.replaceFirst("^data:image/[^;]+;base64,", "");
                    Bitmap bitmap = decodeBase64ToBitmap(base64);
                    handler.post(() -> imageView.setImageBitmap(bitmap));

                } else if (path.startsWith("/data")) {
                    // Load local file in background
                    Bitmap bitmap = ImageHelper.loadImageFromPath(path);
                    handler.post(() -> imageView.setImageBitmap(bitmap));

                } else if (path.startsWith("http")) {
                    // Remote URL: post to UI thread (Picasso handles background loading)
                    handler.post(() -> Picasso.get().load(path).into(imageView));

                } else {
                    // Backend relative path: build full URL and load
                    String fullUrl = BASE_URL + "uploads/" + path;
                    handler.post(() -> Picasso.get().load(fullUrl).into(imageView));
                }
            } else {
                // No image path: show default
                handler.post(() -> imageView.setImageDrawable(
                        ContextCompat.getDrawable(imageView.getContext(),R.drawable.aec4b1a59b7165562698470ce91494be)));
            }
        });
    }

    public static void bindingRcV_recipes(
            RecyclerView recyclerView,
            List<Recipe> newList,
            boolean isRemote
    ) {

        if (adapter_rc_recipeDt == null) {

            List<Recipe> baseList = new ArrayList<>();

            if (isRemote && newList != null) {
                baseList.addAll(newList);
            } else if (!isRemote
                    && viewModel.getListRecipe() != null
                    && viewModel.getListRecipe().getValue() != null) {

                baseList.addAll(viewModel.getListRecipe().getValue());
            }

            adapter_rc_recipeDt = new Adapter_RC_RecipeDt(
                    this,
                    this,
                    viewModel,
                    baseList,
                    isRemote ? TAG_REMOTE : TAG_LOCAL
            );

            LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(manager);
            recyclerView.setAdapter(adapter_rc_recipeDt);
            recyclerView.setHasFixedSize(true);

        } else {
            // 👉 إضافة فقط
            adapter_rc_recipeDt.addRecipes(newList);
        }
    }

}