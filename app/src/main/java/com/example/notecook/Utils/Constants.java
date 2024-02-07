package com.example.notecook.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.MutableLiveData;

import com.example.notecook.Api.TokenResponse;
import com.example.notecook.Model.Detail_Recipe;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.Review;
import com.example.notecook.Model.Step;
import com.example.notecook.Model.User;
import com.example.notecook.R;

import java.util.ArrayList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Constants {

    public static final String API_KEY = "";
    public static final int NETWORK_TIMEOUT = 3000;
    public static String Token = "";

    public static final String TAG_ERREUR_SYSTEM = "erreur_Systeme";
    public static final String TAG_CHARGEMENT_VALIDE = "chargement_Valide";
    public static final String TAG_PAS_RESULTAT = "pasDeResultat";
    public static final String TAG_TOKEN_EXPIRE = "tokenExpire";
    public static final String TAG_ONLINE = "online";
    public static final String TAG_AUTHENTIFICATION_ECHOUE = "authentification_Echoue";
    public static final String TAG_OFFLINE = "Offline";
    public static final String TAG_REMOTE = "Remote";
    public static final String TAG_LOCAL = "Local";
    public static int TAG_CONNEXION = -1;
    public static String TAG_CONNEXION_MESSAGE = "";
    public static Bitmap imageprofill;


    public static ArrayList<Recipe> list_recipe = new ArrayList<>();
    public static List<Detail_Recipe> list_Detailrecipe ;
    public static Detail_Recipe Detail_CurrentRecipe;
    public static List<Step> Steps_CurrentRecipe = new ArrayList<>();
    public static List<Review> Review_CurrentRecipe = new ArrayList<>();
    public static List<Recipe> Search_list = new ArrayList<>();
    public static User User_CurrentRecipe = new User();
    public static MutableLiveData<List<Recipe>> Remotelist_recipe = new MutableLiveData<>();
    public static MutableLiveData<List<Recipe>> RemotelistByIdUser_recipe = new MutableLiveData<>();
    public static String TAG_CONNEXION_LOCAL = "";
    public static TokenResponse user_login = new TokenResponse();
    public static TokenResponse user_login_local = new TokenResponse();
    public static String pathimageuser ="";
    public static final String lOGIN_KEY = "Connection_complete";


    public static final String TAG_MODE_INVITE = "Mode Invite";
    public static final String TAG_MODE_UTILISATEUR = "Mode Utilisateur";
    public static  boolean MODE_ONLINE = false;
    public static SweetAlertDialog alertDialog;
    
    



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
    public static List<User> listUser = new ArrayList<>();
    public static Recipe CURRENT_RECIPE = null;
    public static Detail_Recipe DETAIL_RECIPE = null;
    public static Drawable DEFAUL_IMAGE = null;

    public static boolean fingerprint_id = false;

    public static void DisplayErrorMessage(final AppCompatActivity _context, String message) {
        SweetAlertDialog sd;
        sd = new SweetAlertDialog(_context, SweetAlertDialog.WARNING_TYPE);
        sd.setTitleText("")
                .setContentText(message);
        sd.setOnShowListener(dialog -> {
            SweetAlertDialog alertDialog = (SweetAlertDialog) dialog;
            TextView text = (TextView) alertDialog.findViewById(cn.pedant.SweetAlert.R.id.content_text);
            text.setTextAppearance(_context, android.R.style.TextAppearance_Large);
            text.setGravity(Gravity.CENTER);
            text.setSingleLine(false);
            text.setLines(5);
        });
        sd.show();
    }

    public static void Loading(SweetAlertDialog pDialog)
    {

        pDialog.getProgressHelper().setBarColor(Color.parseColor("#E41818"));
        pDialog.setTitleText("Chargement ...");
        pDialog.setCancelable(true);
        pDialog.show();

        //pDialog.cancel();
    }

    public static void AffichageMessage(String _tag, final AppCompatActivity _context) {
        SweetAlertDialog sd;
        switch (_tag) {
            case TAG_CHARGEMENT_VALIDE:
                sd = new SweetAlertDialog(_context, SweetAlertDialog.SUCCESS_TYPE);
                sd.setTitleText("")
                        .setContentText(_context.getResources().getString(R.string.message_chargement_valide));
                break;
            case TAG_ERREUR_SYSTEM:
                sd = new SweetAlertDialog(_context, SweetAlertDialog.ERROR_TYPE);
                sd.setTitleText("Alerte")
                        .setContentText(_context.getResources().getString(R.string.message_erreur_system));
                break;
            case TAG_PAS_RESULTAT:
                sd = new SweetAlertDialog(_context, SweetAlertDialog.NORMAL_TYPE);
                sd.setTitleText("")
                        .setContentText(_context.getResources().getString(R.string.message_erreur_pas_resultat));
                break;
            case TAG_TOKEN_EXPIRE:
                sd = new SweetAlertDialog(_context, SweetAlertDialog.NORMAL_TYPE);
                sd.setTitleText("Alerte")
                        .setContentText(_context.getResources().getString(R.string.message_erreur_token_expire));
                _context.finish();
                break;
            case TAG_AUTHENTIFICATION_ECHOUE:
                sd = new SweetAlertDialog(_context, SweetAlertDialog.WARNING_TYPE);
                sd.setTitleText("Alerte")
                        .setContentText(_context.getResources().getString(R.string.message_erreur_auth_echoue));
                break;
            case TAG_OFFLINE:
                sd = new SweetAlertDialog(_context, SweetAlertDialog.WARNING_TYPE);
                sd.setTitleText("")
                        .setContentText(_context.getResources().getString(R.string.message_erreur_offline));
                break;
            default:
                sd = new SweetAlertDialog(_context, SweetAlertDialog.NORMAL_TYPE);
                sd.setTitleText("")
                        .setContentText(_tag);
                break;
        }
        sd.setOnShowListener(dialog -> {
            alertDialog = (SweetAlertDialog) dialog;
            alertDialog.setCanceledOnTouchOutside(false);
            TextView text = (TextView) alertDialog.findViewById(cn.pedant.SweetAlert.R.id.content_text);
            text.setTextAppearance(_context, android.R.style.TextAppearance_Large);
            text.setGravity(Gravity.CENTER);
            text.setSingleLine(false);
            text.setLines(7);
        });
        sd.show();
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
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        return (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);
    }




}
