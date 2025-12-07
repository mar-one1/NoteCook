package com.example.notecook.Utils;

import static android.content.Context.MODE_PRIVATE;

import static androidx.recyclerview.widget.RecyclerView.HORIZONTAL;
import static com.example.notecook.Api.env.BASE_URL;
import static com.example.notecook.Utils.ImageHelper.decodeBase64ToBitmap;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCaller;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.notecook.Adapter.Adapter_Rc_Ingredents;
import com.example.notecook.Adapter.Adapter_Rc_Steps;
import com.example.notecook.Model.Ingredients;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.Step;
import com.example.notecook.R;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.ViewModel.SharedRecipeViewModel;
import com.example.notecook.ViewModel.StepViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.squareup.picasso.Callback;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Constants {

    // Nom Complexe  3 groupe
    public  final String NOM_REGEX_3 = "^[^ ]([A-Z]*) ?[^ ]([A-Z]*)? ?[^ ]([A-Z]*)?$|^[^ ]([A-Z]*)$";
    public  final String NOM_REGEX_2 = "^[^ ]([A-Z]*) ?[^ ]([A-Z]*)?$|^[^ ]([A-Z]*)$";
    public  final String PRENOM_REGEX = "^[^ ]([A-Z]{1,}) ?([A-Z]{1,})?$";
    // String tags
    public static final String TAG_ERREUR_SYSTEM = "Erreur système";
    public static final String TAG_CHARGEMENT_VALIDE = "chargement_Valide";
    public static final String TAG_PAS_RESULTAT = "palertDialogeResultat";
    public static final String TAG_TOKEN_EXPIRE = "tokenExpire";
    public static final String TAG_ONLINE = "online";
    public static final String TAG_AUTHENTIFICATION_ECHOUE = "authentification_Echoue";
    public static final String TAG_OFFLINE = "Offline";
    public static final String TAG_NOT_FOUND = "404 Not Found";
    public static final String TAG_REMOTE = "Remote";
    public static final String TAG_LOCAL = "Local";
    public static final String TAG_MODE_INVITE = "Mode Invite";

    // Keys
    public static final String LOGIN_KEY = "Connection_complete";
    public static final String SYNCH_KEY = "Synch_complete";

    // Default categories
    public static final String[] DEFAULT_SEARCH_CATEGORIES =
            {"Barbecue", "Breakfast", "Chicken", "Beef", "Brunch", "Dinner", "Wine", "Italian"};

    public static final String[] DEFAULT_SEARCH_CATEGORY_IMAGES =
            {"barbecue", "breakfast", "chicken", "beef", "brunch", "dinner", "wine", "italian"};

    // Permissions / request codes
    public static final int STORAGE_PERMISSION_CODE = 23;
    public static final int GALLERY_REQUEST_CODE = 24;
    public static final int CAMERA_REQUEST = 1888;
    public static boolean fingerprint_id = false;

    public static SweetAlertDialog loadingDialog;
    public static SweetAlertDialog alertDialog;

    public static void DisplayErrorMessage(final AppCompatActivity _context, String message) {
        alertDialog = new SweetAlertDialog(_context, SweetAlertDialog.WARNING_TYPE);
        alertDialog.setTitleText("")
                .setContentText(message);
        alertDialog.setOnShowListener(dialog -> {
            SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
            TextView text = (TextView) alertDialog.findViewById(cn.pedant.SweetAlert.R.id.content_text);
            text.setTextAppearance(_context, android.R.style.TextAppearance_Large);
            text.setGravity(Gravity.CENTER);
            text.setSingleLine(false);
            text.setLines(5);
        });
        alertDialog.show();
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

    public static void showSnackPar(View view, String message) {
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Handle undo action
                    }
                }).show();
    }

    public static View getRootViewFromContext(Context context) {
        if (context instanceof Activity) {
            return ((Activity) context).findViewById(android.R.id.content);
        } else {
            return null; // Can't get view from non-activity context
        }
    }

    public static String DateTimeNow(Date date) {
        if (date == null) {
            Log.e("DateTimeNow", "Received null date!");
            return "Invalid Date";
        }

        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault());
        return dateFormat.format(date);
    }

    public static void progression(String val) {
        loadingDialog.setTitleText(val);
    }


    public static void Loading(SweetAlertDialog pDialog) {

        pDialog.getProgressHelper().setBarColor(Color.parseColor("#E41818"));
        pDialog.setTitleText("Chargement ...");
        pDialog.setCancelable(true);
        pDialog.show();

        //pDialog.cancel();
    }

    public static void captureImage(Context context, ActivityResultCaller caller) {

        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};

        new AlertDialog.Builder(context)
                .setTitle("Add Photo!")
                .setItems(options, (dialog, item) -> {

                    switch (item) {

                        case 0: // Take Photo
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                                    != PackageManager.PERMISSION_GRANTED) {

                                if (caller instanceof Activity) {
                                    ActivityCompat.requestPermissions(
                                            (Activity) caller,
                                            new String[]{Manifest.permission.CAMERA},
                                            CAMERA_REQUEST
                                    );
                                } else if (caller instanceof Fragment) {
                                    ((Fragment) caller).requestPermissions(
                                            new String[]{Manifest.permission.CAMERA},
                                            CAMERA_REQUEST
                                    );
                                }

                            } else {
                                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                if (caller instanceof Activity) {
                                    ((Activity) caller).startActivityForResult(cameraIntent, CAMERA_REQUEST);
                                } else {
                                    ((Fragment) caller).startActivityForResult(cameraIntent, CAMERA_REQUEST);
                                }
                            }
                            break;

                        case 1: // Choose from Gallery
                            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED) {

                                if (caller instanceof Activity) {
                                    ActivityCompat.requestPermissions(
                                            (Activity) caller,
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            STORAGE_PERMISSION_CODE
                                    );
                                } else if (caller instanceof Fragment) {
                                    ((Fragment) caller).requestPermissions(
                                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                                            STORAGE_PERMISSION_CODE
                                    );
                                }

                            } else {
                                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                                if (caller instanceof Activity) {
                                    ((Activity) caller).startActivityForResult(intent, GALLERY_REQUEST_CODE);
                                } else {
                                    ((Fragment) caller).startActivityForResult(intent, GALLERY_REQUEST_CODE);
                                }
                            }
                            break;

                        case 2: // Cancel
                            dialog.dismiss();
                            break;
                    }

                })
                .show();
    }



    public static void AffichageMessage(String _tag, String title, final Activity _context) {
        switch (_tag) {
            case TAG_CHARGEMENT_VALIDE:
                alertDialog = new SweetAlertDialog(_context, SweetAlertDialog.SUCCESS_TYPE);
                alertDialog.setTitleText(title)
                        .setContentText(_context.getResources().getString(R.string.message_chargement_valide));
                break;
            case TAG_ERREUR_SYSTEM:
                alertDialog = new SweetAlertDialog(_context, SweetAlertDialog.ERROR_TYPE);
                alertDialog.setTitleText("Alerte")
                        .setContentText(_context.getResources().getString(R.string.message_erreur_system));
                break;
            case TAG_PAS_RESULTAT:
                alertDialog = new SweetAlertDialog(_context, SweetAlertDialog.NORMAL_TYPE);
                alertDialog.setTitleText(title)
                        .setContentText(_context.getResources().getString(R.string.message_erreur_pas_resultat));
                break;
            case TAG_TOKEN_EXPIRE:
                alertDialog = new SweetAlertDialog(_context, SweetAlertDialog.NORMAL_TYPE);
                alertDialog.setTitleText("Alerte")
                        .setContentText(_context.getResources().getString(R.string.message_erreur_token_expire));
                _context.finish();
                break;
            case TAG_AUTHENTIFICATION_ECHOUE:
                alertDialog = new SweetAlertDialog(_context, SweetAlertDialog.WARNING_TYPE);
                alertDialog.setTitleText("Alerte")
                        .setContentText(_context.getResources().getString(R.string.message_erreur_auth_echoue));
                break;
            case TAG_OFFLINE:
                alertDialog = new SweetAlertDialog(_context, SweetAlertDialog.WARNING_TYPE);
                alertDialog.setTitleText(title)
                        .setContentText(_context.getResources().getString(R.string.message_erreur_offline));
                break;
            default:
                alertDialog = new SweetAlertDialog(_context, SweetAlertDialog.NORMAL_TYPE);
                alertDialog.setTitleText(title)
                        .setContentText(_tag);
                break;
        }
        alertDialog.setOnShowListener(dialog -> {
            alertDialog = (SweetAlertDialog) dialog;
            alertDialog.setCanceledOnTouchOutside(false);
            TextView text = (TextView) alertDialog.findViewById(cn.pedant.SweetAlert.R.id.content_text);
            text.setTextAppearance(_context, android.R.style.TextAppearance_Large);
            text.setGravity(Gravity.CENTER);
            text.setSingleLine(false);
            text.setLines(7);
        });
        alertDialog.show();
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
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_KEY, MODE_PRIVATE);
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
        SharedPreferences sharedPreferences = context.getSharedPreferences(LOGIN_KEY, MODE_PRIVATE);
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
    public static void bindingRcV_Ingredients(RecyclerView recyclerView, List<Ingredients> list, Context context, SharedRecipeViewModel viewModel) {
        // Create and set adapter for RecyclerView
        Adapter_Rc_Ingredents adapter = new Adapter_Rc_Ingredents(list, context,viewModel);
        recyclerView.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerView.setHorizontalScrollBarEnabled(true);
        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }


    public static void bindingRcV_Steps(RecyclerView recyclerView, List<Step> list, Context context,SharedRecipeViewModel viewModel) {
        Adapter_Rc_Steps adapter = new Adapter_Rc_Steps(list, context,viewModel);
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

    public static void navAction(AppCompatActivity activity, Fragment fragment, ViewPager2 Vp2) {
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

    public static void showImageRecipes(RecipeViewModel recipeVM, Recipe recipe, ImageView imageView) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());

        executor.execute(() -> {
            if (recipe.getPathimagerecipe() != null) {
                if (recipe.getPathimagerecipe().startsWith("data:")) {
                    // Base64 image - decode in background
                    String imageUrl = recipe.getPathimagerecipe().replaceFirst("^data:image/[^;]+;base64,", "");
                    Bitmap bitmap = decodeBase64ToBitmap(imageUrl);
                    handler.post(() -> imageView.setImageBitmap(bitmap));

                } else if (recipe.getPathimagerecipe().startsWith("/data")) {
                    // Local file - load in background
                    Bitmap bitmap = ImageHelper.loadImageFromPath(recipe.getPathimagerecipe());
                    handler.post(() -> imageView.setImageBitmap(bitmap));

                } else {
                    // Remote image - Picasso handles threading itself
                    String url = BASE_URL + "data/uploads/" + recipe.getPathimagerecipe();
                    handler.post(() -> {
                        Picasso.get()
                                .load(url)
                                .error(R.drawable.eror_image_download)
                                .memoryPolicy(MemoryPolicy.NO_STORE)
                                .into(imageView, new Callback() {
                                    @Override
                                    public void onSuccess() {
                                        recipeVM.postImageRecipeLocal(ImageHelper.drawableToBitmap(imageView.getDrawable()), recipe.getId_recipe());
                                    }

                                    @Override
                                    public void onError(Exception e) {
                                        if (recipe.getPathimagerecipe().startsWith("/data")) {
                                            Bitmap fallback = ImageHelper.loadImageFromPath(recipe.getPathimagerecipe());
                                            imageView.setImageBitmap(fallback);
                                        }
                                    }
                                });
                    });
                }
            } else {
                handler.post(() -> imageView.setImageDrawable(imageView.getResources().getDrawable(R.drawable.ic_baseline_image_not_supported_24)));
            }
        });
    }

    private static final ExecutorService executor = Executors.newFixedThreadPool(2);
    private static final Handler handler = new Handler(Looper.getMainLooper());

    public static void showImageSteps(StepViewModel stepViewModel, Step step, ImageView imageView) {
        if (step.getImage_step() == null) {
            imageView.setImageResource(R.drawable.ic_baseline_image_not_supported_24);
            return;
        }

        if (step.getImage_step().startsWith("data:")) {
            // Base64 → decode async
            executor.execute(() -> {
                String imageUrl = step.getImage_step().replaceFirst("^data:image/[^;]+;base64,", "");
                Bitmap bitmap = decodeBase64ToBitmap(imageUrl);
                handler.post(() -> imageView.setImageBitmap(bitmap));
            });

        } else if (step.getImage_step().startsWith("/data")) {
            // Local file → load async
            executor.execute(() -> {
                Bitmap bitmap = ImageHelper.loadImageFromPath(step.getImage_step());
                handler.post(() -> imageView.setImageBitmap(bitmap));
            });

        } else {
            // Remote image → let Picasso handle threading + cache
            String url = BASE_URL + "data/uploads/" + step.getImage_step();
            Picasso.get()
                    .load(url)
                    .placeholder(R.drawable.baseline_image_24)
                    .error(R.drawable.eror_image_download)
                    .into(imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            stepViewModel.postImageStepLocal(
                                    ImageHelper.drawableToBitmap(imageView.getDrawable()),
                                    step.getFRK_recipe_step()
                            );
                        }

                        @Override
                        public void onError(Exception e) {
                            Bitmap fallback = ImageHelper.loadImageFromPath(step.getImage_step());
                            if (fallback != null) imageView.setImageBitmap(fallback);
                        }
                    });
        }
    }


}

