package com.example.notecook.Utils;

import static android.content.Context.MODE_PRIVATE;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;
import static org.chromium.base.ThreadUtils.runOnUiThread;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.notecook.Activity.MainActivity;
import com.example.notecook.Adapter.AdapterFragment;
import com.example.notecook.Adapter.Adapter_Rc_Ingredents;
import com.example.notecook.Adapter.Adapter_Rc_Steps;
import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Dto.RecipeRequest;
import com.example.notecook.Dto.RecipeResponse;
import com.example.notecook.Dto.TokenResponse;
import com.example.notecook.Fragement.Acceuill_Frg;
import com.example.notecook.Fragement.Frg_Basket;
import com.example.notecook.Fragement.Frg_EditProfil;
import com.example.notecook.Fragement.Frg_Search;
import com.example.notecook.Fragement.Frg_detail_recipe;
import com.example.notecook.Fragement.MainFragment;
import com.example.notecook.Fragement.frg_Profil;
import com.example.notecook.Model.Category_Recipe;
import com.example.notecook.Model.Detail_Recipe;
import com.example.notecook.Model.Favorite_Recipe;
import com.example.notecook.Model.Ingredients;
import com.example.notecook.Model.Nutrition;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.Review;
import com.example.notecook.Model.Step;
import com.example.notecook.Model.User;
import com.example.notecook.R;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class    Constants {

    public static final int NETWORK_TIMEOUT = 3000;
    public static final String TAG_ERREUR_SYSTEM = "erreur_Systeme";
    public static final String TAG_CHARGEMENT_VALIDE = "chargement_Valide";
    public static final String TAG_PAS_RESULTAT = "palertDialogeResultat";
    public static final String TAG_SERVEUR_HORS_SERVICE = "502 Serveur en maintenance ,hors service ,Mode Offline On";
    public static final String TAG_TOKEN_EXPIRE = "tokenExpire";
    public static final String TAG_ONLINE = "online";
    public static final String TAG_AUTHENTIFICATION_ECHOUE = "authentification_Echoue";
    public static final String TAG_OFFLINE = "Offline";
    public static final String TAG_NOT_FOUND = "404 Not Found";
    public static final String TAG_INFO_ERONEE = "Username or password Invalide!!  ";
    public static final String TAG_REMOTE = "Remote";
    public static final String TAG_LOCAL = "Local";
    public static Boolean TAG_EDIT_RECIPE = false;
    public static Boolean TAG_MY = false;
    public static final String lOGIN_KEY = "Connection_complete";
    public static final String SYNCH_KEY = "Synch_complete";
    public static final String TAG_MODE_INVITE = "Mode Invite";
    public static final String TAG_MODE_UTILISATEUR = "Mode Utilisateur";
    public static final String[] DEFAULT_SEARCH_CATEGORIES =
            {"Barbecue", "Breakfast", "Chicken", "Beef", "Brunch", "Dinner", "Wine", "Italian"};
    public static final String[] DEFAULT_SEARCH_CATEGORY_IMAGES =
            {
                    "barbecue",
                    "breakfast",
                    "chicken",
                    "beef",
                    "brunch",
                    "dinner",
                    "wine",
                    "italian"
            };
    public static String Token = "";
    public static int TAG_CONNEXION = -1;
    public static String TAG_CONNEXION_MESSAGE = "";
    public static MutableLiveData<List<Recipe>> list_recipe = new MutableLiveData<>();
    public static List<Detail_Recipe> list_Detailrecipe;
    public static Detail_Recipe Detail_CurrentRecipe;
    public static List<Step> Steps_CurrentRecipe = new ArrayList<>();
    public static List<Review> Review_CurrentRecipe = new ArrayList<>();
    public static MutableLiveData<List<Ingredients>> Ingredients_CurrentRecipe = null;
    public static List<Favorite_Recipe> Favorite_CurrentRecipe = new ArrayList<>();
    public static List<Ingredients> All_Ingredients_Recipe = new ArrayList<>();
    public static List<Category_Recipe> All_Categories_Recipe = new ArrayList<>();
    public static List<Recipe> Search_list = new ArrayList<>();
    public static List<Recipe> Basket_list = new ArrayList<>();
    public static User User_CurrentRecipe = new User();
    public static List<Recipe> Recipes_Fav_User = new ArrayList<>();
    public static MutableLiveData<List<Recipe>> Remotelist_recipe = new MutableLiveData<>();
    public static MutableLiveData<List<Recipe>> RemotelistByIdUser_recipe = new MutableLiveData<>();
    public static MutableLiveData<List<RecipeResponse>> RemotelistFullRecipe = new MutableLiveData<>();
    public static MutableLiveData<Nutrition> Remote_nutritions = new MutableLiveData<>();
    public static String TAG_CONNEXION_LOCAL = "";
    public static TokenResponse user_login = new TokenResponse();
    public static TokenResponse user_login_local = new TokenResponse();
    public static String pathimageuser = "";
    public static boolean MODE_ONLINE = false;
    public static SweetAlertDialog alertDialog;
    public static List<User> listUser = new ArrayList<>();
    public static Recipe CURRENT_RECIPE = null;
    public static RecipeResponse CURRENT_FULL_RECIPE = null;
    public static SweetAlertDialog loadingDialog;

    public static boolean fingerprint_id = false;

    public static void DisplayErrorMessage(final AppCompatActivity _context, String message) {
        alertDialog= new SweetAlertDialog(_context, SweetAlertDialog.WARNING_TYPE);alertDialog.setTitleText("")
                .setContentText(message);alertDialog.setOnShowListener(dialog -> {
            SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
            TextView text = (TextView) alertDialog.findViewById(cn.pedant.SweetAlert.R.id.content_text);
            text.setTextAppearance(_context, android.R.style.TextAppearance_Large);
            text.setGravity(Gravity.CENTER);
            text.setSingleLine(false);
            text.setLines(5);
        });alertDialog.show();
    }

    public static void loading_ui(final Context _context, final Activity activity, String message) {
        // Initialize loadingDialog
        loadingDialog = new SweetAlertDialog(_context, SweetAlertDialog.PROGRESS_TYPE);
        loadingDialog.getProgressHelper().setBarColor(Color.parseColor("#86BFDC"));
        loadingDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
                TextView text = (TextView) alertDialog.findViewById(cn.pedant.SweetAlert.R.id.title_text);
                text.setSingleLine(false);
                text.setGravity(Gravity.CENTER);
                text.setTextAppearance(_context, android.R.style.TextAppearance_Large);
                text.setMaxLines(6);
            }
        });
        loadingDialog.setTitleText(message);
        loadingDialog.setCancelable(false);
        loadingDialog.show();

        // Initialize Handler
        Handler handler = new Handler(Looper.getMainLooper());

        // Dismiss the loading dialog and show another message after 10 seconds
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Dismiss the loading dialog if it is showing
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                    // Show your pop-up dialog here
                    AffichageMessage(TAG_NOT_FOUND, "404", activity);
                }
            }
        }, 10000); // 10 seconds delay
    }

    // Method to dismiss the loading dialog from another place in your code
    public static void dismissLoadingDialog() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    public static String DateTimeNow(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        String timestamp = dateFormat.format(date);
        return timestamp;
    }


    public static void progression(String val) {
        loadingDialog.setTitleText(val);
    }


    public static void init() {
        //Token = "";
        TAG_CONNEXION = -1;
        TAG_CONNEXION_MESSAGE = "";
        list_recipe = new MutableLiveData<>();
        Steps_CurrentRecipe = new ArrayList<>();
        Review_CurrentRecipe = new ArrayList<>();
        Ingredients_CurrentRecipe = new MutableLiveData<>();
        All_Ingredients_Recipe = new ArrayList<>();
        Search_list = new ArrayList<>();
        Basket_list = new ArrayList<>();
        User_CurrentRecipe = new User();
        Recipes_Fav_User = new ArrayList<>();
        Remotelist_recipe = new MutableLiveData<>();
        RemotelistByIdUser_recipe = new MutableLiveData<>();
    }

    public static void Loading(SweetAlertDialog pDialog) {

        pDialog.getProgressHelper().setBarColor(Color.parseColor("#E41818"));
        pDialog.setTitleText("Chargement ...");
        pDialog.setCancelable(true);
        pDialog.show();

        //pDialog.cancel();
    }

    public static void AffichageMessage(String _tag, String title, final Activity _context) {
        switch (_tag) {
            case TAG_CHARGEMENT_VALIDE:
                alertDialog = new SweetAlertDialog(_context, SweetAlertDialog.SUCCESS_TYPE);alertDialog.setTitleText(title)
                        .setContentText(_context.getResources().getString(R.string.message_chargement_valide));
                break;
            case TAG_ERREUR_SYSTEM:
                alertDialog= new SweetAlertDialog(_context, SweetAlertDialog.ERROR_TYPE);alertDialog.setTitleText("Alerte")
                        .setContentText(_context.getResources().getString(R.string.message_erreur_system));
                break;
            case TAG_PAS_RESULTAT:
                alertDialog= new SweetAlertDialog(_context, SweetAlertDialog.NORMAL_TYPE);alertDialog.setTitleText(title)
                        .setContentText(_context.getResources().getString(R.string.message_erreur_pas_resultat));
                break;
            case TAG_TOKEN_EXPIRE:
                alertDialog= new SweetAlertDialog(_context, SweetAlertDialog.NORMAL_TYPE);alertDialog.setTitleText("Alerte")
                        .setContentText(_context.getResources().getString(R.string.message_erreur_token_expire));
                _context.finish();
                break;
            case TAG_AUTHENTIFICATION_ECHOUE:
                alertDialog= new SweetAlertDialog(_context, SweetAlertDialog.WARNING_TYPE);alertDialog.setTitleText("Alerte")
                        .setContentText(_context.getResources().getString(R.string.message_erreur_auth_echoue));
                break;
            case TAG_OFFLINE:
                alertDialog= new SweetAlertDialog(_context, SweetAlertDialog.WARNING_TYPE);alertDialog.setTitleText(title)
                        .setContentText(_context.getResources().getString(R.string.message_erreur_offline));
                break;
            default:
                alertDialog= new SweetAlertDialog(_context, SweetAlertDialog.NORMAL_TYPE);alertDialog.setTitleText(title)
                        .setContentText(_tag);
                break;
        }alertDialog.setOnShowListener(dialog -> {
            alertDialog = (SweetAlertDialog) dialog;
            alertDialog.setCanceledOnTouchOutside(false);
            TextView text = (TextView) alertDialog.findViewById(cn.pedant.SweetAlert.R.id.content_text);
            text.setTextAppearance(_context, android.R.style.TextAppearance_Large);
            text.setGravity(Gravity.CENTER);
            text.setSingleLine(false);
            text.setLines(7);
        });alertDialog.show();
    }

    public static void DesableTimeOut(final View view)
    // Avoid double click
    {
        view.setEnabled(false);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                view.setEnabled(true);
            }
        }, 1000);
    }

    public static boolean NetworkIsConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


    public static void saveToken(String token, Context context) {
        SharedPreferences preferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public static void saveUserInput(String username, String password, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(lOGIN_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();

    }

    public static void saveUserSynch(String username, Boolean b, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(SYNCH_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(username, b);
        editor.apply();
    }

    public static Boolean getUserSynch(String username, Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SYNCH_KEY, MODE_PRIVATE);
        return preferences.getBoolean(username, false);
    }

    public static String getUserInput(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(lOGIN_KEY, MODE_PRIVATE);
        return sharedPreferences.getString("username", "");
    }

    public static boolean isConnected(Context context) {
        final AtomicBoolean connected = new AtomicBoolean(false);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                connected.set(NetworkIsConnected(context));
                Log.d("tag internet", String.valueOf(connected));
            }
        });
        thread.start();

        // Wait for the thread to finish before returning the result
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return connected.get();
    }

    private static boolean isOnline() {
        try {
            // Create a Socket and connect to a known reliable host (google.com)
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress("google.com", 80), 1000); // Port 80 is commonly used for HTTP
            socket.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static String getToken(Context context) {
        SharedPreferences preferences = context.getSharedPreferences("MyPrefs", MODE_PRIVATE);
        return preferences.getString("token", "");
    }

    public static void showToast(final Context context, final String message) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public static void bindingRcV_Ingredients(RecyclerView recyclerView, List<Ingredients> list, Context context) {
        // Create and set adapter for RecyclerView
        Adapter_Rc_Ingredents adapter = new Adapter_Rc_Ingredents(list, context);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerView.setHorizontalScrollBarEnabled(true);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }


    public static void bindingRcV_Steps(RecyclerView recyclerView, List<Step> list, Context context) {
        Adapter_Rc_Steps adapter = new Adapter_Rc_Steps(list, context);
        GridLayoutManager manager = new GridLayoutManager(context, 1);
        recyclerView.setLayoutManager(manager);
        manager.setOrientation(HORIZONTAL);
        recyclerView.setHorizontalScrollBarEnabled(true);
        recyclerView.setAdapter(adapter);
    }

    public static void level(Spinner sp, Context context) {
        levelRecipe[] values = levelRecipe.values();
        // Create an array of display names
        String[] displayNames = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            displayNames[i] = values[i].name();
        }
        // Create an ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, displayNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
    }

    public static Bitmap drawableToBitmap(Context context, int drawableId) {
        // Get a reference to the drawable resource
        Drawable drawable = context.getResources().getDrawable(drawableId);

        // Convert the drawable to a Bitmap
        Bitmap bitmap = null;
        if (drawable != null) {
            bitmap = BitmapFactory.decodeResource(context.getResources(), drawableId);
        }

        return bitmap;
    }

    public static void navAction(AppCompatActivity activity ,Fragment fragment,ViewPager2 Vp2) {
        int bnvId = R.id.bottom_nav;
        BottomNavigationView btnV = activity.findViewById(bnvId);

        btnV.setOnNavigationItemSelectedListener(
                item ->

                {
                    FragmentTransaction fragmentTransaction = activity.getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.detach(fragment);
                    fragmentTransaction.commitNow();
                    int i = 0;
                    switch (item.getItemId()) {
                        case R.id.tips:
                            i = 0;
                            break;
                        case R.id.fav:
                            i = 1;
                            break;
                        case R.id.search:
                            i = 2;
                            break;
                        case R.id.cart:
                            i = 3;
                            break;
                        case R.id.parson:
                            i = 4;
                            break;
                    }
                    Vp2.setCurrentItem(i, false);
                    return false;
                });
    }

    public static void clickMoins(TextView textView, Button buttonMoins) {
        int t = Integer.parseInt(textView.getText().toString());
        if (t <= 0) {
            buttonMoins.setEnabled(false);
        } else
            t--;
        textView.setText("" + t);
    }

    public static int clickPlus(TextView textView, Button buttonMoins) {
        int t = Integer.parseInt(textView.getText().toString());
        buttonMoins.setEnabled(true);
        t++;
        textView.setText("" + t);
        return t;
    }



}

