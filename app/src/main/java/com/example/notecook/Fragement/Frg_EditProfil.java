package com.example.notecook.Fragement;

import static com.example.notecook.Activity.MainActivity.decod;
import static com.example.notecook.Activity.MainActivity.encod;
import static com.example.notecook.Utils.Constants.TAG_CHARGEMENT_VALIDE;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_LOCAL;
import static com.example.notecook.Utils.Constants.saveToken;
import static com.example.notecook.Utils.Constants.user_login;
import static com.example.notecook.Utils.env.BASE_URL;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
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
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.notecook.Data.UserDatasource;
import com.example.notecook.Activity.Login;
import com.example.notecook.Activity.MainActivity;
import com.example.notecook.Model.User;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.ImageHelper;
import com.example.notecook.ViewModel.UserViewModel;
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

import javax.annotation.Nullable;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Frg_EditProfil extends Fragment {

    static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_REQUEST = 1888;
    private final int STORAGE_PERMISSION_CODE = 23;
    private final int GALLERY_REQUEST_CODE = 24;
    private FragmentFrgEditProfilBinding binding;
    private TextView txt_save;
    private UserDatasource mUserDatasource;
    private User getUser;
    private MainActivity m;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInOptions gso;
    private SharedPreferences sharedPreferences;
    private UserViewModel userVM;
    private FragmentActivity fragmentActivity;


    public Frg_EditProfil() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        if (Constants.alertDialog != null && Constants.alertDialog.isShowing()) {
            Constants.alertDialog.dismiss();
        }
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
        userVM = new UserViewModel(getContext(), getActivity());
        fragmentActivity = (FragmentActivity) getContext();
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

        if (user.getPathimageuser() != null && !user.getPathimageuser().equals("")) {
            String imageUrl = "";
            if(user.getPathimageuser().startsWith("/data")) {
                binding.iconEditprofil.setImageBitmap(ImageHelper.loadImageFromPath(user.getPathimageuser()));
            }else
            if (user.getPathimageuser().startsWith("http"))
                Picasso.get().load(imageUrl).into(binding.iconEditprofil);
            else {
               imageUrl = BASE_URL + "uploads/" + user.getPathimageuser();
                Picasso.get().load(imageUrl).into(binding.iconEditprofil);
            }
            //binding.iconProfil.setImageDrawable(Constants.DEFAUL_IMAGE);
        } else if (user.getIcon() != null) {
            binding.iconEditprofil.setImageBitmap(decod(user.getIcon()));
        }

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
                updateUser();
                pDialog.dismissWithAnimation();
            });
            pDialog.show();
        });

        Constants.navAction((AppCompatActivity) getActivity(),Frg_EditProfil.this,Vp2);

        binding.backBtn.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.detach(Frg_EditProfil.this);
            fragmentTransaction.commitNow();
            Vp2.setCurrentItem(4, false);
        });

        binding.editIconProfil.setOnClickListener(view -> captureImage(getContext()));

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
                saveToken("", getContext());
                TAG_CONNEXION = 0;
                TAG_CONNEXION_LOCAL = "";
                //Constants.alertDialog.cancel();
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


    public void captureImage(Context context) {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Add Photo!");
        builder.setItems(options, (dialog, item) -> {

            if (options[item].equals("Take Photo")) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                }
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.CAMERA)
                        == PackageManager.PERMISSION_GRANTED) {

                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    String picture = getString(R.string.Puctire);
                    String pick = getString(R.string.pick);
//                            startActivityForResult(Intent.createChooser(cameraIntent,pick),GALLERY_REQUEST_CODE);
                    startActivityForResult(cameraIntent, CAMERA_REQUEST);
                }


            } else if (options[item].equals("Choose from Gallery")) {
                if (ContextCompat.checkSelfPermission(context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{
                                    Manifest.permission.READ_EXTERNAL_STORAGE}
                            , STORAGE_PERMISSION_CODE);
                }
                if (ContextCompat.checkSelfPermission(context,
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

    private void updateUser() {
        try {
            User currentuser = user_login.getUser();
            String oldPathImage = user_login.getUser().getPathimageuser();
            mUserDatasource = new UserDatasource(getContext());
            String nom = binding.Nome.getText().toString();
            String prenom = binding.myEditText.getText().toString();
            String naissance = binding.txtBirth.getText().toString();
            String mail = binding.txtEmail.getText().toString();
            String tel = binding.txtPhone.getText().toString();
            Drawable d = binding.iconEditprofil.getDrawable();
            Bitmap bitmap = ((BitmapDrawable) d).getBitmap();
            byte[] icon1 = null;
            String pass = user_login.getUser().getPassWord();
            String username = user_login.getUser().getUsername();
            String Status = "active";
            String grade = user_login.getUser().getGrade();
            String Path = ImageHelper.saveImageToInternalStorage(requireContext(),bitmap,"UserImages");
            getUser = new User(user_login.getUser().getId_User(), username, nom, prenom, naissance, mail, icon1, tel, pass, Status, grade, Path);
            int value = mUserDatasource.UpdateUserByUsername(getUser, user_login.getUser().getUsername());
            Toast.makeText(getContext(), String.valueOf(value), Toast.LENGTH_SHORT).show();
            if (value == 1) {
                user_login.setUser(getUser);
                Constants.AffichageMessage(TAG_CHARGEMENT_VALIDE, "", (AppCompatActivity) getContext());
            } else if (value == 0) {
                mUserDatasource.insertUser(getUser);
                Constants.DisplayErrorMessage((AppCompatActivity) getContext(), "User insert success");
            } else {
                Constants.DisplayErrorMessage((AppCompatActivity) getContext(), "the Change Not saved");
            }
            //user_login.setUser(mUserDatasource.select_User_BYid(user_login.getUser().getId_User()));

//                if (!Objects.equals(user_login.getMessage(), TAG_LOCAL)) {
            currentuser.setIcon(null);
            getUser.setIcon(null);
            if (!currentuser.equals(getUser))
                userVM.UpdateUser(getUser, bitmap).observe(getViewLifecycleOwner(), new Observer<User>() {
                    @Override
                    public void onChanged(User user) {
                        if (user != null) {
                            userVM.updateuserImageRemote(user.getUsername(),bitmap,oldPathImage,"").observe(getViewLifecycleOwner(), new Observer<String>() {
                                @Override
                                public void onChanged(String s) {
                                    user_login.setUser(user);
                                    user_login.getUser().setPathimageuser(s);
                                    detach();
                                    frg_Profil.bindingProfil.iconProfil.setImageBitmap(bitmap);
                                }
                            });

                        }
                    }
                });
            else
                Constants.DisplayErrorMessage((AppCompatActivity) getContext(), "Non Modified no change yet!!");
        } catch (Exception e) {
            Log.e("tag", "" + e);
        }
    }

    private void detach() {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(Frg_EditProfil.this);
        fragmentTransaction.commitNow();


    }

}
