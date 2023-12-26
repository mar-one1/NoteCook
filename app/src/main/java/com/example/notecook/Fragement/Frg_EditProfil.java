package com.example.notecook.Fragement;

import static android.content.Context.MODE_PRIVATE;
import static com.example.notecook.Api.ApiClient.BASE_URL;
import static com.example.notecook.MainActivity.UpdateUserApi;
import static com.example.notecook.MainActivity.decod;
import static com.example.notecook.MainActivity.deleteimage;
import static com.example.notecook.MainActivity.encod;
import static com.example.notecook.MainActivity.uploadImage;
import static com.example.notecook.Utils.Constants.TAG_CHARGEMENT_VALIDE;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_LOCAL;
import static com.example.notecook.Utils.Constants.TAG_LOCAL;
import static com.example.notecook.Utils.Constants.pathimageuser;
import static com.example.notecook.Utils.Constants.user_login;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.example.notecook.Data.UserDatasource;
import com.example.notecook.Login;
import com.example.notecook.MainActivity;
import com.example.notecook.Model.User;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.databinding.FragmentFrgEditProfilBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import javax.annotation.Nullable;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Frg_EditProfil extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_REQUEST = 1888;
    private final int STORAGE_PERMISSION_CODE = 23;
    private final int GALLERY_REQUEST_CODE = 24;
    FragmentFrgEditProfilBinding binding;
    TextView txt_save;
    UserDatasource mUserDatasource;
    User getUser;
    MainActivity m;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInOptions gso;
    private SharedPreferences sharedPreferences;


    public Frg_EditProfil() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentFrgEditProfilBinding.inflate(inflater, container, false);
        User user = new User();
        user = user_login.getUser();
        //Log.d("TAG",user_login.getUser().getUser_name().toString());
        binding.Nome.setText(user.getFirstname());
        binding.myEditText.setText(user.getLastname());
        binding.txtBirth.setText(user.getBirthday());
        binding.txtEmail.setText(user.getEmail());
        binding.txtPhone.setText(user.getPhonenumber());

        binding.txtBirth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        if (!Objects.equals(user_login.getUser().getPathimageuser(), "")) {
            String imageUrl="";
            if(user_login.getUser().getPathimageuser().startsWith("http"))
                imageUrl=user_login.getUser().getPathimageuser();
            else
                imageUrl = BASE_URL +"uploads/"+ user_login.getUser().getPathimageuser()+"?timestamp=" + System.currentTimeMillis();;
            Picasso.get().load(imageUrl).into(binding.iconEditprofil);

        }
        else  binding.iconEditprofil.setImageBitmap(decod(user_login.getUser().getIcon()));

        ViewPager2 Vp2 = getActivity().findViewById(R.id.vp2);

        binding.TxtBtnSave.setOnClickListener(view -> {
            SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
            pDialog.setTitleText("save");
            pDialog.setCancelable(true);
            pDialog.setConfirmButton("non", sweetAlertDialog -> {
//                        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//                        fragmentTransaction.replace(R.id.fl_main, new MainFragment());
//                        fragmentTransaction.commitNow();
                Toast.makeText(getContext(), "Non clicker", Toast.LENGTH_SHORT).show();
                pDialog.dismissWithAnimation();
            });
            pDialog.setCancelButton("oui", sweetAlertDialog -> {
                String urlold = user_login.getUser().getPathimageuser();
                mUserDatasource = new UserDatasource(getContext());
                mUserDatasource.open();
                String nom = binding.Nome.getText().toString();
                String prenom = binding.myEditText.getText().toString();
                String naissance = binding.txtBirth.getText().toString();
                String mail = binding.txtEmail.getText().toString();
                String tel = binding.txtPhone.getText().toString();
                Drawable d = binding.iconEditprofil.getDrawable();
                Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
                byte[] icon = encod(bitmap);
                byte[] icon1 = null;
                String pass = user_login.getUser().getPassWord();
                String username = user_login.getUser().getUsername();
                String Status = user_login.getUser().getStatus();
                String grade = user_login.getUser().getGrade();
                getUser = new User(username, nom, prenom, naissance, mail, icon, tel, pass, Status, grade);
                int value = mUserDatasource.UpdateUserByUsername(getUser, user_login.getUser().getUsername());
                Toast.makeText(getContext(), String.valueOf(value), Toast.LENGTH_SHORT).show();
                if (value == 1) {
                    user_login.setUser(getUser);
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.detach(Frg_EditProfil.this);
                    fragmentTransaction.commitNow();
                    pDialog.dismissWithAnimation();
                    Vp2.setCurrentItem(0, false);
                    Vp2.setCurrentItem(4, false);
                    Constants.AffichageMessage(TAG_CHARGEMENT_VALIDE, (AppCompatActivity) getContext());
                } else {
                    Constants.DisplayErrorMessage((AppCompatActivity) getContext(), "the Change Not saved");
                }
                //user_login.setUser(mUserDatasource.select_User_BYid(user_login.getUser().getId_User()));
                mUserDatasource.close();

                if (!Objects.equals(user_login.getMessage(), TAG_LOCAL)) {
                    getUser.setId_User(user_login.getUser().getId_User());
                    getUser.setIcon(null);
                    UpdateUserApi(getUser, getContext());
                    //Login.UpdateImageUserApi(icon,getUser.getUsername(),getContext());
                    deleteimage(urlold,getContext());
                    uploadImage(user_login.getUser().getUsername(),bitmap,getContext());
                }
            });
            pDialog.show();
        });

        int bnvId = R.id.bottom_nav;
        BottomNavigationView btnV = getActivity().findViewById(bnvId);

        btnV.setOnNavigationItemSelectedListener(
                item -> {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.detach(Frg_EditProfil.this);
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
        binding.backBtn.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.detach(Frg_EditProfil.this);
            fragmentTransaction.commitNow();
            Vp2.setCurrentItem(4, false);
        });

        binding.editIconProfil.setOnClickListener(view -> captureImage());

        binding.logOut.setOnClickListener(view -> {
            SweetAlertDialog ppDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);
            ppDialog.setTitleText("Déconnecter");
            ppDialog.setCancelable(true);
            ppDialog.setConfirmButton("non", sweetAlertDialog -> {

                Toast.makeText(getContext(), "Non clicker", Toast.LENGTH_SHORT).show();
                ppDialog.dismissWithAnimation();

            });
            ppDialog.setCancelButton("oui", sweetAlertDialog -> {
                signOut();
                Intent lointent = new Intent(getContext(), Login.class);
                startActivity(lointent);
                saveToken("");
                TAG_CONNEXION = 0;
                TAG_CONNEXION_LOCAL = "";
                Constants.alertDialog.cancel();
                getActivity().finish();
            });


            ppDialog.show();
        });


        return binding.getRoot();
    }


    private void signOut() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), task -> {
                    // ...
                });
    }


    private void captureImage() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Add Photo!");
        builder.setItems(options, (dialog, item) -> {

            if (options[item].equals("Take Photo")) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                }
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String picture = getString(R.string.Puctire);
                    String pick = getString(R.string.pick);
//                            startActivityForResult(Intent.createChooser(cameraIntent,pick),GALLERY_REQUEST_CODE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }


            } else if (options[item].equals("Choose from Gallery")) {
                if (ContextCompat.checkSelfPermission(getActivity(),
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE}
                            , STORAGE_PERMISSION_CODE);
                }
                if (ContextCompat.checkSelfPermission(getActivity(),
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                putPicture(photo);
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    binding.iconEditprofil.setImageURI(selectedImageUri);
                }
            }
        }
    }

    void putPicture(Bitmap bitmap) {
        binding.iconEditprofil.setImageBitmap(bitmap);
    }

    private void saveToken(String token) {
        sharedPreferences = requireContext().getSharedPreferences("MyPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public void showDatePickerDialog(View view) {
        String selectedDateStr = binding.txtBirth.getText().toString();

        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Date selectedDate;
        try {
            selectedDate = format.parse(selectedDateStr);
        } catch (ParseException e) {
            e.printStackTrace();
            selectedDate = new Date(); // Default to current date if parsing fails
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(selectedDate);

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // Set selected date to the TextView
                        String selectedDate = String.format("%02d/%02d/%d", day, month + 1, year);
                        binding.txtBirth.setText(selectedDate);
                    }
                },
                year,
                month,
                dayOfMonth
        );

        datePickerDialog.show();
    }

}
