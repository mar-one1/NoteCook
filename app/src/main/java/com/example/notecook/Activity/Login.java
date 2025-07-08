package com.example.notecook.Activity;

import static com.example.notecook.Data.MySQLiteHelperTable.COLUMN_EMAIL_USER;
import static com.example.notecook.Data.MySQLiteHelperTable.COLUMN_USERNAME;
import static com.example.notecook.Data.MySQLiteHelperTable.TABLE_USER;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_LOCAL;
import static com.example.notecook.Utils.Constants.captureImage;
import static com.example.notecook.Utils.Constants.lOGIN_KEY;
import static com.example.notecook.Utils.Constants.saveUserInput;
import static com.example.notecook.Utils.Constants.user_login;
import static com.example.notecook.Utils.Constants.user_login_local;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.biometrics.BiometricPrompt;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;

import com.example.notecook.Data.UserDatasource;
import com.example.notecook.Model.User;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.ImageHelper;
import com.example.notecook.Utils.InputValidator;
import com.example.notecook.Utils.PasswordHasher;
import com.example.notecook.ViewModel.AccessViewModel;
import com.example.notecook.ViewModel.UserViewModel;
import com.example.notecook.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Login extends AppCompatActivity implements View.OnClickListener {

    // Nom Complexe  3 groupe
    public static final String NOM_REGEX_3 = "^[^ ]([A-Z]*) ?[^ ]([A-Z]*)? ?[^ ]([A-Z]*)?$|^[^ ]([A-Z]*)$";
    public static final String NOM_REGEX_2 = "^[^ ]([A-Z]*) ?[^ ]([A-Z]*)?$|^[^ ]([A-Z]*)$";
    public static final String PRENOM_REGEX = "^[^ ]([A-Z]{1,}) ?([A-Z]{1,})?$";
    private final static int RC_SIGN_IN = 9001;
    private static final int CAMERA_REQUEST = 1888;
    private static final int REQUEST_CODE = 1000;


    private final int STORAGE_PERMISSION_CODE = 23;
    private final int GALLERY_REQUEST_CODE = 24;
    private final int INTERNET_REQUEST_CODE = 25;
    // Variable used for storing the key in the Android Keystore container
    private ActivityLoginBinding binding;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    // create an authenticationCallback
    private BiometricPrompt.AuthenticationCallback authenticationCallback;
    private SharedPreferences sharedPreferences;
    private View view;
    private PasswordHasher passwordHasher = new PasswordHasher();
    private InputValidator inputValidator = new InputValidator();
    private UserDatasource userDatasource;
    private UserViewModel userVM;
    private AccessViewModel accessVM;
    private Boolean isPosted = false;

    //@TargetApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        userVM = new UserViewModel(this, this);
        accessVM = new AccessViewModel(this, this);

        // Check FingerPrint In Device
        try {
            if(checkBiometricSupport()) empreinte();
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

        try {
            sharedPreferences = getSharedPreferences(lOGIN_KEY, Context.MODE_PRIVATE);
            if (sharedPreferences.getBoolean(lOGIN_KEY, true)) {
                String s1 = sharedPreferences.getString("username", "");
                String s2 = sharedPreferences.getString("password", "");
                userVM.getUserLocal(s1, "success");
                binding.etUsername.setText(s1);
                binding.etPassword.setText("");
                //binding.etUsername.setEnabled(false);
                if(checkBiometricSupport()) secoundLogin();
            }
        } catch (Exception e) {
            Log.e("tag", e.getMessage());
        }

        if (user_login_local.getUser() != null && user_login_local.getUser().getPathimageuser() != null) {
            binding.ivUserlogo1.setImageBitmap(ImageHelper.loadImageFromPath(user_login_local.getUser().getPathimageuser()));
        }


        binding.SignUp.setOnClickListener(view -> {
            addNotification();
            binding.layoutLoginCheck.setVisibility(View.GONE);
            binding.layoutRegistre.setVisibility(View.VISIBLE);
        });

        binding.RegistreBtn.setOnClickListener(v -> {
            try {
                Save_Preference_Data("registre");
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        //binding.btnLogin.setOnClickListener(view -> loginclk());
        binding.btnLogin.setOnClickListener(this);


        binding.ModeInvite.setOnClickListener(v -> {
            Intent i = new Intent(getBaseContext(), MainActivity.class);
            i.putExtra("TAG", Constants.TAG_MODE_INVITE);
            startActivity(i);
        });

        binding.btnSignInBack.setOnClickListener(v -> {
            binding.layoutLoginCheck.setVisibility(View.VISIBLE);
            binding.layoutRegistre.setVisibility(View.GONE);
        });

        binding.editIconProfil.setOnClickListener(v -> captureImage(view,this ));


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        updateUI(account);

        // Set the dimensions of the sign-in button.
        SignInButton signInButton = binding.btnSignInGoogle;
        signInButton.setSize(SignInButton.SIZE_STANDARD);

        signInButton.setOnClickListener(this);
        String[] permissions = {"android.permission.READ_PHONE_STATE", "android.permission.CAMERA", "android.permission.INTERNET"};
        ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE);

        setContentView(view);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE)
            if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                if (!Constants.NetworkIsConnected(this)) {
                    Constants.DisplayErrorMessage(Login.this, "Veuillez vérifier votre connectivité réseau SVP");
                    return;
                }
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
            } else {
                Toast.makeText(getApplicationContext(), "PERMISSION_DENIED_PHONE_STATE", Toast.LENGTH_SHORT).show();
                finish();
            }
    }

    @Override
    public void onClick(View v) {
        Constants.DesableTimeOut(v);
        if (v.getId() == R.id.btn_sign_in_google || v.getId() == R.id.btn_sign_up_google) {
            signIn();
            // ...000000
        }
        if (v.getId() == R.id.btn_login) {
            loginclk();
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                putPicture(photo);
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    data.getData().getPath();
                    binding.editIconProfil.setImageURI(selectedImageUri);
                }
            }
        }
        // Pass the activity result back to the Facebook SDK
        //mCallbackManager.onActivityResult(requestCode, resultCode, data);


    }//L'objet GoogleSignInAccount contient des informations sur l'utilisateur connecté, telles que son nom.


    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account);
            Toast.makeText(this, "Success Authentication with google", Toast.LENGTH_LONG).show();
            Save_Preference_Data("google");
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());
            updateUI(null);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    //@TargetApi(api = Build.VERSION_CODES.P)
    @TargetApi(Build.VERSION_CODES.P)
    public void empreinte() {
        try {

            authenticationCallback = new BiometricPrompt.AuthenticationCallback() {
                // here we need to implement two methods
                // onAuthenticationError and
                // onAuthenticationSucceeded If the
                // fingerprint is not recognized by the
                // app it will call onAuthenticationError
                // and show a toast
                @Override
                public void onAuthenticationError(
                        int errorCode, CharSequence errString) {
                    super.onAuthenticationError(errorCode, errString);
                    notifyUser("Authentication Error : " + errString);
                }

                // If the fingerprint is recognized by the
                // app then it will call
                // onAuthenticationSucceeded and show a
                // toast that Authentication has Succeed
                // Here you can also start a new activity
                // after that
                @Override
                public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                    super.onAuthenticationSucceeded(result);
                    notifyUser("Authentication Succeeded");
                    loginclk();
                    // or start a new Activity
                }
            };

            checkBiometricSupport();
            // create a biometric dialog on Click of button
            binding.IMVFinger.setOnClickListener(view -> {
                // This creates a dialog of biometric
                // auth and it requires title , subtitle
                // , and description In our case there
                // is a cancel button by clicking it, it
                // will cancel the process of
                // fingerprint authentication
                BiometricPrompt biometricPrompt = new BiometricPrompt
                        .Builder(getApplicationContext())
                        .setTitle("Authentication")
                        .setSubtitle("Fingerprint")
                        .setDescription("please apply your fingerprint to access the application")
                        .setNegativeButton("Cancel", getMainExecutor(), (dialogInterface, i) -> notifyUser("Authentication Cancelled")).build();

                // start the authenticationCallback in
                // mainExecutor
                biometricPrompt.authenticate(
                        getCancellationSignal(),
                        getMainExecutor(),
                        authenticationCallback);
            });

        } catch (Exception e) {
            Log.e("tag", "Empreint exception " + e);
        }
    }

    @TargetApi(Build.VERSION_CODES.P)
    private void secoundLogin() {
        BiometricPrompt biometricPrompt = new BiometricPrompt
                .Builder(getApplicationContext())
                .setTitle("Authentication")
                .setSubtitle("Fingerprint")
                .setDescription("please apply your fingerprint to access the application")
                .setNegativeButton("Cancel", getMainExecutor(), (dialogInterface, i) -> notifyUser("Authentication Cancelled")).build();

        // start the authenticationCallback in
        // mainExecutor
        biometricPrompt.authenticate(
                getCancellationSignal(),
                getMainExecutor(),
                authenticationCallback);
    }

    // it will be called when
    // authentication is cancelled by
    // the user
    private CancellationSignal getCancellationSignal() {
        CancellationSignal cancellationSignal = new CancellationSignal();
        cancellationSignal.setOnCancelListener(
                () -> {
                    notifyUser("Authentication was Cancelled by the user");
                });
        return cancellationSignal;
    }

    // it checks whether the
    // app the app has fingerprint
    // permission
    @TargetApi(Build.VERSION_CODES.M)
    private Boolean checkBiometricSupport() {
        try {
            KeyguardManager keyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
            if (!keyguardManager.isDeviceSecure()) {
                notifyUser("Fingerprint authentication has not been enabled in settings");
                return false;
            }
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.USE_BIOMETRIC) != PackageManager.PERMISSION_GRANTED) {
                notifyUser("Fingerprint Authentication Permission is not enabled");
                return false;
            }
            if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)) {
                return true;
            } else
                return true;
        } catch (Exception e) {
            Log.e("tag", "" + e);
            return false;
        }
    }

    // this is a toast method which is responsible for
    // showing toast it takes a string as parameter
    private void notifyUser(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        TAG_CONNEXION = -1;

    }

    private void addNotification() {
        int NOTIFICATION_ID = 234;
        NotificationManager notificationManager = (NotificationManager) getSystemService(getBaseContext().NOTIFICATION_SERVICE);
        String CHANNEL_ID = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CHANNEL_ID = "my_channel_01";
            CharSequence name = "my_channel";
            String Description = "This is my channel";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            mChannel.setDescription(Description);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.RED);
            mChannel.enableVibration(true);
            mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            mChannel.setShowBadge(false);
            notificationManager.createNotificationChannel(mChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_icon_app)
                .setContentTitle(getTitle())
                .setContentText("toast notif");

        Intent resultIntent = new Intent(getBaseContext(), Login.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(getBaseContext());
        stackBuilder.addParentStack(Login.class);
        stackBuilder.addNextIntent(resultIntent);
        // PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        //builder.setContentIntent(resultPendingIntent);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }


    public void loginclk() {
        try {
            if (inputValidator.isValidLogin(binding.etUsername, binding.etPassword)) {
                SweetAlertDialog pDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.PROGRESS_TYPE);
                pDialog.getProgressHelper().setBarColor(Color.parseColor("#E41818"));
                pDialog.setTitleText("Chargement ...");
                pDialog.setCancelable(true);
                pDialog.show();

                if (Constants.NetworkIsConnected(Login.this)) {
                    String username = binding.etUsername.getText().toString();
                    String password = binding.etPassword.getText().toString();
                    accessVM.connectApi(username, password).observe(this, new Observer<String>() {
                        @Override
                        public void onChanged(String s) {
                            //if(!s.equals("")) saveToken(s);
                        }
                    });
                    //connectionApi();
                } else Constants.DisplayErrorMessage(Login.this, "Verifier la Connectivitee!!!");
                pDialog.cancel();
            }
        } catch (Exception e) {
            Log.e("tag", "" + e);
        }
    }

    private void updateUI(Object account) {

        //if (account instanceof GoogleSignInAccount) {
        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(Login.this);

        if (acct != null) {
            String personName = acct.getDisplayName();
            String personGivenName = acct.getGivenName();
            String personFamilyName = acct.getFamilyName();
            String personEmail = acct.getEmail();
            String personId = acct.getId();
            Uri personPhoto = acct.getPhotoUrl();
            if (personName.contains(" "))
                personName = personName.replace(" ", "_");
            binding.etUsername.setText(personName);
            binding.etPassword.setText(acct.getId());

        }
        //}
    }


    private boolean Save_Preference_Data(String check) throws IOException {

        UserDatasource dataSourceUser = new UserDatasource(this);
        dataSourceUser.open();
//        User user = dataSourceUser.select_User_BYUsername(binding.txtUsername.getText().toString());
//        Constants.listUser = dataSourceUser.getAllUser();

        if (check.equals("google")) {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(Login.this);
            String username = acct.getDisplayName();
            if (username.contains(" "))
                username = username.replace(" ", "_");
            boolean b = dataSourceUser.isRecordExist(TABLE_USER, COLUMN_EMAIL_USER, acct.getEmail());

            if (!b) {
                String jsonInputString = "";
                if (acct.getPhotoUrl() != null)
                    jsonInputString = "{\"url\": \"" + acct.getPhotoUrl().toString() + "\"}";
                else jsonInputString = "";
                passwordHasher = new PasswordHasher();
                String password = passwordHasher.hashPassword(acct.getId().toString());
                User Newuser = new User(username, acct.getFamilyName(), acct.getGivenName(), "00/00/0000", acct.getEmail(),
                        null, "000000000000", password, "active", "good");

                userVM.postUser(Newuser, jsonInputString, null, "google").observe(this, new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        Constants.AffichageMessage("Registre Success", "", Login.this);
                    }
                });
                if (!dataSourceUser.isRecordExist(TABLE_USER, COLUMN_USERNAME, username) && !dataSourceUser.isRecordExist(TABLE_USER, COLUMN_EMAIL_USER, acct.getEmail())) {
                    User userInsered = dataSourceUser.createUserlogin(null, username, acct.getGivenName(),
                            acct.getFamilyName(), "00/00/0000", acct.getEmail(),
                            "0", password, "Chef ", "active");
                    if (userInsered.equals(Newuser)) isPosted = true;

                    Constants.AffichageMessage("Vous avez Register avec succes Localy", "", Login.this);
                }


                //MainActivity.uploadImage(Newuser.getUsername(),bitmap,getBaseContext());
            } else
                Toast.makeText(this, "Welcome Back " + acct.getDisplayName(), Toast.LENGTH_LONG).show();


        }
        if (check.equals("registre")) {
            if (inputValidator.isValidRegistration(binding.txtUsername, binding.txtFirstnameLast, binding.txtTel,
                    binding.txtEmail, binding.txtPassword, binding.txtConfirmationPassword)) {
                boolean b = dataSourceUser.isRecordExist(TABLE_USER, COLUMN_EMAIL_USER, String.valueOf(binding.txtEmail.getText()));

                if (!b) {
                    String username = (binding.txtUsername.getText().toString()) + "_" + binding.txtFirstnameLast.getText().toString();
                    Drawable d = binding.editIconProfil.getDrawable();
                    Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                    passwordHasher = new PasswordHasher();
                    String password = passwordHasher.hashPassword(binding.txtPassword.getText().toString());
                    User newUser = new User(username, binding.txtUsername.getText().toString(), binding.txtFirstnameLast.getText().toString(), "00/00/0000", binding.txtEmail.getText().toString(), null, binding.txtTel.getText().toString(), password, "active", "Chef");
                    if (Constants.NetworkIsConnected(this)) {
                        userVM.postUser(newUser, "", bitmap, "registre").observe(this, new Observer<User>() {
                            @Override
                            public void onChanged(User user) {
                                if (user != null) {
                                    isPosted = true;
                                    String Path = ImageHelper.saveImageToInternalStorage(Login.this,bitmap,"UserImages");
                                    newUser.setPathimageuser(Path);
                                    User userPost = dataSourceUser.insertUser(newUser);
                                    if (!Objects.equals(userPost.getUsername(), ""))
                                        Constants.AffichageMessage("Vous avez Register avec succes in local", "", Login.this);
                                    binding.layoutLoginCheck.setVisibility(View.VISIBLE);
                                    binding.layoutRegistre.setVisibility(View.GONE);
                                    String s = binding.txtUsername.getText().toString() + "_" + binding.txtFirstnameLast.getText().toString();
                                    binding.etUsername.setText(s);
                                    binding.etPassword.setText("");
                                }
                            }
                        });
                    }

                } else
                    Constants.AffichageMessage("Votre Email est existe dans la base Changer Email or Sign_in", "", Login.this);
            }
            dataSourceUser.close();
        }
        return isPosted;
    }

    void putPicture(Bitmap bitmap) {
        binding.editIconProfil.setImageBitmap(bitmap);
    }



    @Override
    protected void onPause() {
        super.onPause();
        if (Constants.alertDialog != null && Constants.alertDialog.isShowing()) {
            Constants.alertDialog.dismiss();
        }
    }


}




