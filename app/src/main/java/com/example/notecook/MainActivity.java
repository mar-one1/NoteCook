package com.example.notecook;

import static com.example.notecook.Api.ApiClient.BASE_URL;
import static com.example.notecook.Utils.Constants.TAG_MODE_INVITE;
import static com.example.notecook.Utils.Constants.Token;
import static com.example.notecook.Utils.Constants.User_CurrentRecipe;
import static com.example.notecook.Utils.Constants.getToken;
import static com.example.notecook.Utils.Constants.pathimageuser;
import static com.example.notecook.Utils.Constants.user_login;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Fragement.MainFragment;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.NetworkChangeReceiver;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.ViewModel.UserViewModel;
import com.example.notecook.databinding.ActivityMainBinding;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

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
        binding  = ActivityMainBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        Constants.init();
        Token = getToken(this);

        String[] permissions = {"android.permission.READ_PHONE_STATE", "android.permission.CAMERA", "android.permission.INTERNET"};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);

//        recipeVM = new RecipeViewModel(this, this);
//        userVM = new UserViewModel(this, this);
//        recipeVM = new ViewModelProvider(this,recipeVM).get(RecipeViewModel.class);
//        userVM = new ViewModelProvider(this,userVM).get(UserViewModel.class);

        String tag = "";
        if (getIntent().getExtras() != null) {
            tag = getIntent().getStringExtra("TAG");
            if (Objects.equals(tag, TAG_MODE_INVITE))
                Type_User = tag;
        }
        // get Recipe From Api
        if (!Type_User.equals(TAG_MODE_INVITE)) {
            //getUserInfo();
        }

        fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.fl_main, new MainFragment());
        fragmentTransaction.commit();

        setContentView(view);
    }

//    private void getUserInfo()
//    {
////            fetchData();
//            //Constants.loading_ui(this,"Loading...");
//            String s1 = getUserInput(this);
//            userVM.getUser(s1).observe(this, new Observer<User>() {
//                @Override
//                public void onChanged(User user) {
//                    Toast.makeText(getBaseContext(), "user get by observe", Toast.LENGTH_SHORT).show();
//                    //extracted();
//                   // Constants.stop_loading();
//                }
//            });
//
//    }


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