package com.example.notecook.Fragement;

import static com.example.notecook.Utils.Constants.TAG_CHARGEMENT_VALIDE;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION;
import static com.example.notecook.Utils.Constants.TAG_CONNEXION_LOCAL;
import static com.example.notecook.Utils.Constants.captureImage;
import static com.example.notecook.Utils.Constants.saveToken;
import static com.example.notecook.Utils.Constants.user_login;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.notecook.Activity.Login;
import com.example.notecook.Activity.MainActivity;
import com.example.notecook.Data.UserDatasource;
import com.example.notecook.Model.User;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.ImageHelper;
import com.example.notecook.ViewModel.UserViewModel;
import com.example.notecook.databinding.FragmentFrgEditProfilBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

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
    private static GoogleSignInClient mGoogleSignInClient;
    private static GoogleSignInOptions gso;
    private final int STORAGE_PERMISSION_CODE = 23;
    private final int GALLERY_REQUEST_CODE = 24;
    private FragmentFrgEditProfilBinding binding;
    private TextView txt_save;
    private UserDatasource mUserDatasource;
    private User getUser;
    private MainActivity m;
    private SharedPreferences sharedPreferences;
    private UserViewModel userVM;
    private FragmentActivity fragmentActivity;


    public Frg_EditProfil() {
        // Required empty public constructor
    }

    public static void logOut(Activity activity) {
        signOut(activity);
        Intent lointent = new Intent(activity, Login.class);
        activity.startActivity(lointent);
        saveToken("", activity);
        TAG_CONNEXION = 0;
        TAG_CONNEXION_LOCAL = "";
        //Constants.alertDialog.cancel();
        activity.finish();
    }


    private static void signOut(Activity activity) {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(activity, task -> {
                    // ...
                });
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

        MainActivity.showImageUsers(user, binding.iconEditprofil);

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

        Constants.navAction((AppCompatActivity) getActivity(), Frg_EditProfil.this, Vp2);

        binding.backBtn.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.detach(Frg_EditProfil.this);
            fragmentTransaction.commitNow();
            Vp2.setCurrentItem(4, false);
        });

        binding.editIconProfil.setOnClickListener(view -> captureImage(view, Frg_EditProfil.this));

        binding.logOut.setOnClickListener(view -> {
            SweetAlertDialog ppDialog = new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE);
            ppDialog.setTitleText("Déconnecter");
            ppDialog.setCancelable(true);
            ppDialog.setConfirmButton("non", sweetAlertDialog -> {

                Toast.makeText(getActivity(), "Non clicker", Toast.LENGTH_SHORT).show();
                ppDialog.dismissWithAnimation();

            });
            ppDialog.setCancelButton("oui", sweetAlertDialog -> {
                logOut(getActivity());
            });
            ppDialog.show();
        });


        return binding.getRoot();
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
        if (selectedDate != null) {
            calendar.setTime(selectedDate);
        }

        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                view.getContext(),
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        // Set selected date to the TextView
                        String selectedDate = String.format(Locale.US, "%02d/%02d/%04d", day, month + 1, year);
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
            String Path = ImageHelper.saveImageToInternalStorage(requireContext(), bitmap, "UserImages");
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
                            userVM.updateUserImageRemote(user.getUsername(), bitmap, oldPathImage, "").observe(getViewLifecycleOwner(), new Observer<String>() {
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
