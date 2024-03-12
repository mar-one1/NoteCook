package com.example.notecook;

import static com.example.notecook.Data.MySQLiteHelperTable.COLUMN_EMAIL_USER;
import static com.example.notecook.Data.MySQLiteHelperTable.COLUMN_USERNAME;
import static com.example.notecook.Data.MySQLiteHelperTable.TABLE_USER;
import static com.example.notecook.Data.UserDatasource.createUserlogin;
import static com.example.notecook.Data.UserDatasource.insertUser;
import static com.example.notecook.Data.UserDatasource.isRecordExist;
import static com.example.notecook.Utils.Constants.TAG_AUTHENTIFICATION_ECHOUE;
import static com.example.notecook.Utils.Constants.TAG_CHARGEMENT_VALIDE;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_LOCAL;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_MESSAGE;
import static com.example.notecook.Utils.Constants.TAG_OFFLINE;
import static com.example.notecook.Utils.Constants.lOGIN_KEY;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.notecook.Api.ApiClient;
import com.example.notecook.Api.ApiService;
import com.example.notecook.Api.ConnexionRetrofit;
import com.example.notecook.Api.LoginResponse;
import com.example.notecook.Api.ValidationError;
import com.example.notecook.Data.UserDatasource;
import com.example.notecook.Model.User;
import com.example.notecook.Repo.RecipeRepository;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.InputValidator;
import com.example.notecook.Utils.PasswordHasher;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.databinding.ActivityLoginBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.gson.Gson;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
    ActivityLoginBinding binding;
    MainActivity m;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    TextView txt_username_login, txt_password_login;
    TextView txt_firstname, txt_lastname, txt_phoneNumber, txt_email, txt_password, txt_passwordConfirmation;
    ViewPager2 viewPager2;
    // create an authenticationCallback
    private BiometricPrompt.AuthenticationCallback authenticationCallback;
    private SharedPreferences sharedPreferences;
    private View view;
    private PasswordHasher passwordHasher = new PasswordHasher();
    private InputValidator inputValidator = new InputValidator();

    //@TargetApi(api = Build.VERSION_CODES.P)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        view = binding.getRoot();
        m = new MainActivity();

        // Check FingerPrint In Device
        try {
            empreinte();
        }catch (Exception e) {Log.e("tag",e.getMessage());}


        try {
        sharedPreferences = getSharedPreferences(lOGIN_KEY, Context.MODE_PRIVATE);
        if (sharedPreferences.getBoolean(lOGIN_KEY, true)) {
            String s1 = sharedPreferences.getString("username", "");
            String s2 = sharedPreferences.getString("password", "");
            getLocalUserLogin(s1);
            binding.etUsername.setText(s1);
            binding.etPassword.setText(s2);
            secoundLogin();
        }
        }catch (Exception e) {Log.e("tag",e.getMessage());}

        if(user_login_local.getUser()!=null && user_login_local.getUser().getIcon()!=null)
        {
            binding.ivUserlogo1.setImageBitmap(MainActivity.decod(user_login_local.getUser().getIcon()));
        }


        binding.SignUp.setOnClickListener(view -> {
            addNotification();
            binding.layoutLoginCheck.setVisibility(View.GONE);
            binding.layoutRegistre.setVisibility(View.VISIBLE);
        });

        binding.RegistreBtn.setOnClickListener(v -> {
            try {
                boolean b = Save_Preference_Data("registre");
                if (b) {
                    binding.layoutLoginCheck.setVisibility(View.VISIBLE);
                    binding.layoutRegistre.setVisibility(View.GONE);
                    String s = binding.txtUsername.getText().toString() + "_" + binding.txtFirstnameLast.getText().toString();
                    binding.etUsername.setText(s);
                    binding.etPassword.setText("");
                }
            } catch (IOException e) {
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

        binding.editIconProfil.setOnClickListener(v -> captureImage());


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

    private void getLocalUserLogin(String username)
    {
        UserDatasource userDatasource = new UserDatasource(this);
        userDatasource.open();
        user_login_local.setUser(userDatasource.select_User_BYUsername(username));
        userDatasource.close();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE)
            if (grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                if (!ConnexionRetrofit.isOnline(Login.this)) {
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
            binding.btnLogin.callOnClick();
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
                () -> notifyUser("Authentication was Cancelled by the user"));
        return cancellationSignal;
    }

    // it checks whether the
    // app the app has fingerprint
    // permission
    @TargetApi(Build.VERSION_CODES.M)
    private Boolean checkBiometricSupport() {
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
        if (inputValidator.isValidLogin(binding.etUsername, binding.etPassword)) {
            SweetAlertDialog pDialog = new SweetAlertDialog(Login.this, SweetAlertDialog.PROGRESS_TYPE);
            pDialog.getProgressHelper().setBarColor(Color.parseColor("#E41818"));
            pDialog.setTitleText("Chargement ...");
            pDialog.setCancelable(true);
            pDialog.show();

            if (Constants.NetworkIsConnected(Login.this)) {
                connectionApi();
            } else Constants.DisplayErrorMessage(Login.this, "Verifier la Connectivitee!!!");

            pDialog.cancel();
        }
    }

    private String ConnectLocal() {
        TAG_CONNEXION_LOCAL = "";
        UserDatasource dataSourceUser = new UserDatasource(this);
        dataSourceUser.open();
        Constants.listUser = dataSourceUser.getAllUser();
        dataSourceUser.close();
        String username = binding.etUsername.getText().toString();
        String Pass = binding.etPassword.getText().toString();
        passwordHasher = new PasswordHasher();
        for (User item : Constants.listUser) {
            //Toast.makeText(getBaseContext(), "user : " + item.getUser_name() + " pass : " + item.getPassWord(), Toast.LENGTH_SHORT).show();
            if (Objects.equals(item.getFirstname(), username) && passwordHasher.verifyPassword(Pass, item.getPassWord())) {
                if (sharedPreferences.getBoolean(lOGIN_KEY, true)) {
                    saveUserInput(username, Pass);
                }
                TAG_CONNEXION_LOCAL = "success";
                user_login.setUser(item);
                /*if (!Objects.equals(user_login.getUser(), null)) {
                    user_login.getUser().setUser_name(username);
                    user_login.getUser().setUser_name(item.getPassWord());
                    user_login.setMessage("Local");
                    Log.d("message",user_login.getMessage());
                }*/
                Intent i = new Intent(getBaseContext(), MainActivity.class);
                startActivity(i);
                this.finish();


                //break;
            }
        }
        return TAG_CONNEXION_LOCAL;
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

        boolean vrai = false;

        UserDatasource dataSourceUser = new UserDatasource(this);
        dataSourceUser.open();
//        User user = dataSourceUser.select_User_BYUsername(binding.txtUsername.getText().toString());
//        Constants.listUser = dataSourceUser.getAllUser();

        if (check.equals("google")) {
            GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(Login.this);
            String username = acct.getDisplayName();
            if (username.contains(" "))
                username = username.replace(" ", "_");
            boolean b = isRecordExist(TABLE_USER, COLUMN_EMAIL_USER, acct.getEmail());

            if (!b) {
                passwordHasher = new PasswordHasher();
                String password = passwordHasher.hashPassword(acct.getId().toString());
                User Newuser = new User(username, acct.getFamilyName(), acct.getGivenName(), "00/00/0000", acct.getEmail(),
                        null, "000000000000", password, "active", "good");

                InsertUserApi(Newuser);
                if (!isRecordExist(TABLE_USER, COLUMN_USERNAME, username) && !isRecordExist(TABLE_USER, COLUMN_EMAIL_USER, acct.getEmail())) {
                    User userInsered = createUserlogin(null, username, acct.getGivenName(),
                            acct.getFamilyName(), "00/00/0000", acct.getEmail(),
                            "0", password, "Chef ", "active");
                    if (userInsered.equals(Newuser)) vrai = true;

                    Constants.AffichageMessage("Vous avez Register avec succes Localy", Login.this);
                }


                if (acct.getPhotoUrl() != null)
                    updateGoogleUserImage(username, acct.getPhotoUrl().toString());
                //MainActivity.uploadImage(Newuser.getUsername(),bitmap,getBaseContext());
            } else
                Toast.makeText(this, "Welcome Back " + acct.getDisplayName(), Toast.LENGTH_LONG).show();


        }
        if (check.equals("registre")) {
            if (inputValidator.isValidRegistration(binding.txtUsername, binding.txtFirstnameLast, binding.txtTel,
                    binding.txtEmail, binding.txtPassword, binding.txtConfirmationPassword)) {
                boolean b = isRecordExist(TABLE_USER, COLUMN_EMAIL_USER, String.valueOf(binding.txtEmail.getText()));

                if (!b) {
                    String username = (binding.txtUsername.getText().toString()) + "_" + binding.txtFirstnameLast.getText().toString();
                    Drawable d = binding.editIconProfil.getDrawable();
                    Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                    passwordHasher = new PasswordHasher();
                    String password = passwordHasher.hashPassword(binding.txtPassword.getText().toString());
                    byte[] icon = MainActivity.iconUser;
                    User newUser;
                    newUser = createUserlogin(icon, username, binding.txtUsername.getText().toString(), binding.txtFirstnameLast.getText().toString(), "00/00/0000", binding.txtEmail.getText().toString(), binding.txtTel.getText().toString(), password, "Chef", "active");
                    vrai = true;
                    if (Constants.NetworkIsConnected(this) && !Objects.equals(newUser.getUsername(), "")) {
                        newUser.setIcon(null);
                        InsertUserApi(newUser);
                        MainActivity.uploadImage(newUser.getUsername(), bitmap, "register", getBaseContext());
                    }
                    if (!Objects.equals(newUser.getUsername(), ""))
                        Constants.AffichageMessage("Vous avez Register avec succes in local", Login.this);
                } else
                    Constants.AffichageMessage("Votre Email est existe dans la base Changer Email or Sign_in", Login.this);
            }
            dataSourceUser.close();
        }
        return vrai;
    }


    private void captureImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(options, (dialog, item) -> {

            if (options[item].equals("Take Photo")) {
                if (ContextCompat.checkSelfPermission(view.getContext(),
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Login.this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                }
                if (ContextCompat.checkSelfPermission(view.getContext(),
                        Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {
                    {
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        String picture = getString(R.string.Puctire);
                        String pick = getString(R.string.pick);
//                            startActivityForResult(Intent.createChooser(cameraIntent,pick),GALLERY_REQUEST_CODE);
                        startActivityForResult(cameraIntent, CAMERA_REQUEST);
                    }
                }
            } else if (options[item].equals("Choose from Gallery")) {
                if (ContextCompat.checkSelfPermission(view.getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Login.this, new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE}
                            , STORAGE_PERMISSION_CODE);
                }
                if (ContextCompat.checkSelfPermission(view.getContext(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, GALLERY_REQUEST_CODE);
                }
            } else if (options[item].equals("Cancel")) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    void putPicture(Bitmap bitmap) {
        binding.editIconProfil.setImageBitmap(bitmap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //TokenApi();
    }


    private void connectionApi() {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        // Example: Fetch users from the API
        //Call<List<User>> call = apiService.getAllUsers();
        //Call<List<User>> call = apiService.getData("Bearer " + Token);
        String username = binding.etUsername.getText().toString();
        String password = binding.etPassword.getText().toString();


        LoginResponse login = new LoginResponse();
        login.setUsername(username);
        login.setPassword(password);

        Call<LoginResponse> call = apiService.authontification(login);

        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    if (loginResponse != null) {
                        String token = loginResponse.getToken();
                        // Store the token securely (e.g., in SharedPreferences) for later use
                        TAG_CONNEXION = response.code();
                        TAG_CONNEXION_MESSAGE = response.message();

                        Log.d("TAG", TAG_CONNEXION_MESSAGE);
                        try {
                            User user = new User();
                            user.setUsername(username);
                            passwordHasher = new PasswordHasher();
                            String password = passwordHasher.hashPassword(binding.etPassword.getText().toString());
                            user.setPassWord(password);
                            user_login.setUser(user);

                            saveToken(token);
                            saveUserInput(binding.etUsername.getText().toString(), binding.etPassword.getText().toString());
                            Constants.AffichageMessage(TAG_CHARGEMENT_VALIDE, Login.this);
                        }catch (Exception e) {Log.e("tag",e.toString());}
                        Intent i = new Intent(getBaseContext(), MainActivity.class);
                        startActivity(i);
                    }
                } else {

                    // Handle error response here
                    // The HTTP request was not successful (status code is not 2xx).
                    // You can handle errors here based on the response status code.
                    int statusCode = response.code();
                    Constants.TAG_CONNEXION = statusCode;
                    TAG_CONNEXION_MESSAGE = response.message();
                    // Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, Login.this);
                    // Handle different status codes as per your API's conventions.
                    if (statusCode == 401) {
                        Constants.AffichageMessage(TAG_AUTHENTIFICATION_ECHOUE, Login.this);
                        // Unauthorized, handle accordingly (e.g., reauthentication).
                    } else if (statusCode == 404) {
                        // Not found, handle accordingly (e.g., show a 404 error message).
                        Constants.AffichageMessage(TAG_OFFLINE, Login.this);
                    } else if (statusCode >= 500) {
                        // Handle other status codes or generic error handling.
                        Constants.AffichageMessage("Internal Server Error", Login.this);
                    } else if (statusCode == 406) {
                        // Handle other status codes or generic error handling.
                        Constants.AffichageMessage("User not found", Login.this);
                    } else Constants.AffichageMessage(response.message(), Login.this);
                }

//                if (response.errorBody() != null) {
//                    try {
//                        String errorResponse = response.errorBody().string();
//                        // Print or log the errorResponse for debugging
//                        TAG_CONNEXION_MESSAGE = errorResponse;
//                        Constants.AffichageMessage(TAG_ERREUR_SYSTEM,Login.this);
//                        //Constants.DisplayErrorMessage(Login.this,TAG_CONNEXION_MESSAGE);
//                        TAG_CONNEXION = response.code();
//                        Log.e("token", "Error Response: " + errorResponse);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {

                TAG_CONNEXION_MESSAGE = call.toString();
                Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, Login.this);
            }
        });

    }

    private void UpdateUserApi(User user) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        // Example: Fetch users from the API
        // Convert the byte array to RequestBody
        //RequestBody imageRequestBody = RequestBody.create(null, user.getIcon());

        Call<User> call = apiService.updateUserByUsername(user.getUsername(), user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User UserResponse = response.body();
                    if (UserResponse != null) {
                        // Store the token securely (e.g., in SharedPreferences) for later use
                        TAG_CONNEXION = response.code();
                        TAG_CONNEXION_MESSAGE = response.message();
                        Constants.AffichageMessage("Vous avez Modifier Utilisateur avec  succes with server", Login.this);
                        Log.d("TAG", TAG_CONNEXION_MESSAGE + " " + "Add User To Api");
                    }
                } else {

                    // Handle error response here
                    // The HTTP request was not successful (status code is not 2xx).
                    // You can handle errors here based on the response status code.
                    int statusCode = response.code();
                    Constants.TAG_CONNEXION = statusCode;
                    TAG_CONNEXION_MESSAGE = response.message();

                    // Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, Login.this);
                    // Handle different status codes as per your API's conventions.
                    if (statusCode == 409) {
                        Constants.AffichageMessage("User already exists", Login.this);
                        // Unauthorized, handle accordingly (e.g., reauthentication).
                    } else if (statusCode == 404) {
                        // Not found, handle accordingly (e.g., show a 404 error message).
                        Constants.AffichageMessage(TAG_OFFLINE, Login.this);
                    } else if (statusCode >= 500) {
                        // Handle other status codes or generic error handling.
                        Constants.AffichageMessage("Internal Server Error", Login.this);
                    } else if (statusCode == 406) {
                        // Handle other status codes or generic error handling.
                        Constants.AffichageMessage("User not found", Login.this);
                    } else Constants.AffichageMessage(response.message(), Login.this);
                }

//                if (response.errorBody() != null) {
//                    try {
//                        String errorResponse = response.errorBody().string();
//                        // Print or log the errorResponse for debugging
//                        TAG_CONNEXION_MESSAGE = errorResponse;
//                        Constants.AffichageMessage(TAG_ERREUR_SYSTEM,Login.this);
//                        //Constants.DisplayErrorMessage(Login.this,TAG_CONNEXION_MESSAGE);
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
                Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, Login.this);
            }
        });

    }

    private void InsertUserApi(User user) {
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        // Example: Fetch users from the API
        Call<User> call = apiService.createUser(user);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User UserResponse = response.body();
                    if (UserResponse != null) {
                        // Store the token securely (e.g., in SharedPreferences) for later use
                        TAG_CONNEXION = response.code();
                        TAG_CONNEXION_MESSAGE = response.message();
                        Constants.AffichageMessage("Vous avez Register avec succes with server", Login.this);
                        Log.d("TAG", TAG_CONNEXION_MESSAGE + " " + "Add User To Api");
                    }
                } else {

                    // Handle error response here
                    // The HTTP request was not successful (status code is not 2xx).
                    // You can handle errors here based on the response status code.
                    int statusCode = response.code();
                    Constants.TAG_CONNEXION = statusCode;
                    TAG_CONNEXION_MESSAGE = response.message();
                    // Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, Login.this);
                    // Handle different status codes as per your API's conventions.
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
                            Constants.AffichageMessage(errorMessages.toString(), Login.this);
                        } catch (IOException e) {
                            // Handle error parsing error body
                        }
                        // Unauthorized, handle accordingly (e.g., reauthentication).
                    } else if (statusCode == 409) {
                        Constants.AffichageMessage("User already exists", Login.this);
                        // Unauthorized, handle accordingly (e.g., reauthentication).
                    } else if (statusCode == 404) {
                        // Not found, handle accordingly (e.g., show a 404 error message).
                        Constants.AffichageMessage(TAG_OFFLINE, Login.this);
                    } else if (statusCode >= 500) {
                        // Handle other status codes or generic error handling.
                        Constants.AffichageMessage("Internal Server Error", Login.this);
                    } else if (statusCode == 406) {
                        // Handle other status codes or generic error handling.
                        Constants.AffichageMessage("User not found", Login.this);
                    } else Constants.AffichageMessage(response.message(), Login.this);
                }

//                if (response.errorBody() != null) {
//                    try {
//                        String errorResponse = response.errorBody().string();
//                        // Print or log the errorResponse for debugging
//                        TAG_CONNEXION_MESSAGE = errorResponse;
//                        Constants.AffichageMessage(TAG_ERREUR_SYSTEM,Login.this);
//                        //Constants.DisplayErrorMessage(Login.this,TAG_CONNEXION_MESSAGE);
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
                Constants.AffichageMessage(TAG_CONNEXION_MESSAGE, Login.this);
            }
        });

    }

    public void updateGoogleUserImage(String username, String path) {

        String jsonInputString = "{\"url\": \"" + path + "\"}";
// Create a RequestBody from the string
        RequestBody requestBody = RequestBody.create(MediaType.parse("text/plain"), jsonInputString);

        // Create a service using the Retrofit interface
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        // Call the method to upload the file
        Call<String> call = apiService.updateUserGoogleImageUrl(username, requestBody);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    String path = null;
                    path = response.body().toString();
                    //String str = new String(bytes, StandardCharsets.UTF_8);
                    //path = path.replaceAll("\"", "");// For UTF-8 encoding
                    Toast.makeText(getApplicationContext(), "upload image : " + path, Toast.LENGTH_SHORT).show();
                    // File upload successful
                    //fetchImage(path);
                    Toast.makeText(getApplicationContext(), "upload image : " + TAG_CONNEXION_MESSAGE, Toast.LENGTH_SHORT).show();

                } else {
                    // Handle unsuccessful upload
                    Toast.makeText(getApplicationContext(), "Not upload image : " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                // Handle failure
                Toast.makeText(getApplicationContext(), "OnFailure upload image : " + t.toString(), Toast.LENGTH_SHORT).show();
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

    private void saveToken(String token) {
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    private void saveUserInput(String username, String password) {
        SharedPreferences sharedPreferences = getSharedPreferences(lOGIN_KEY, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.apply();

    }


}




