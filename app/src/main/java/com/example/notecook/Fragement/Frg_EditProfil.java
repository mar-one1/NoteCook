package com.example.notecook.Fragement;

import static com.example.notecook.Utils.Constants.TAG_CHARGEMENT_VALIDE;
import static com.example.notecook.Utils.Constants.captureImage;
import static com.example.notecook.Utils.Constants.saveToken;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
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
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import com.example.notecook.Activity.Login;
import com.example.notecook.Activity.MainActivity;
import com.example.notecook.Data.UserDatasource;
import com.example.notecook.Model.User;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.ImageHelper;
import com.example.notecook.ViewModel.SharedRecipeViewModel;
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

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Frg_EditProfil extends Fragment {

    private FragmentFrgEditProfilBinding binding;
    private UserDatasource mUserDatasource;

    private UserViewModel userVM;
    private SharedRecipeViewModel viewModel;

    private ActivityResultLauncher<Void> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;

    private static GoogleSignInClient mGoogleSignInClient;
    private static GoogleSignInOptions gso;

    public Frg_EditProfil() {}

    // ================= LOGOUT =================
    public static void logOut(Activity activity) {

        signOut(activity);

        Intent intent = new Intent(activity, Login.class);
        activity.startActivity(intent);

        saveToken("", activity);
        activity.finish();
    }

    private static void signOut(Activity activity) {

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(activity, gso);

        mGoogleSignInClient.signOut();
    }

    // ================= ON CREATE =================
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentFrgEditProfilBinding.inflate(inflater, container, false);

        viewModel = new ViewModelProvider(requireActivity())
                .get(SharedRecipeViewModel.class);

        userVM = new UserViewModel(requireContext(), requireActivity(), viewModel);

        User user = null;

        if (viewModel.getUserLogin().getValue() != null) {
            user = viewModel.getUserLogin().getValue().getUser();
        }

        if (user != null) {
            binding.Nome.setText(user.getFirstname());
            binding.myEditText.setText(user.getLastname());
            binding.txtBirth.setText(user.getBirthday());
            binding.txtEmail.setText(user.getEmail());
            binding.txtPhone.setText(user.getPhonenumber());

            MainActivity.showImageUsers(user, binding.iconEditprofil);
        }

        // ================= DATE =================
        binding.txtBirth.setOnClickListener(this::showDatePickerDialog);

        // ================= SAVE =================
        binding.TxtBtnSave.setOnClickListener(view -> {

            SweetAlertDialog dialog =
                    new SweetAlertDialog(getContext(), SweetAlertDialog.SUCCESS_TYPE);

            dialog.setTitleText("Save");

            dialog.setConfirmButton("Non", sweetAlertDialog -> {
                dialog.dismissWithAnimation();
            });

            dialog.setCancelButton("Oui", sweetAlertDialog -> {
                updateUser();
                dialog.dismissWithAnimation();
            });

            dialog.show();
        });

        // ================= BACK =================
        binding.backBtn.setOnClickListener(v -> detach());

        // ================= LOGOUT =================
        binding.logOut.setOnClickListener(v -> {

            SweetAlertDialog dialog =
                    new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE);

            dialog.setTitleText("Déconnecter");

            dialog.setConfirmButton("Non", sweetAlertDialog -> dialog.dismissWithAnimation());

            dialog.setCancelButton("Oui", sweetAlertDialog -> logOut(getActivity()));

            dialog.show();
        });

        // ================= IMAGE PICKERS =================
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                bitmap -> {
                    if (bitmap != null) {
                        binding.editIconProfil.setImageBitmap(bitmap);
                    }
                });

        galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        binding.editIconProfil.setImageURI(uri);
                    }
                });

        binding.editIconProfil.setOnClickListener(v ->
                Constants.captureImage(getContext(), new Constants.ImagePickerListener() {
                    @Override
                    public void onCameraSelected() {
                        cameraLauncher.launch(null);
                    }

                    @Override
                    public void onGallerySelected() {
                        galleryLauncher.launch("image/*");
                    }
                })
        );

        return binding.getRoot();
    }

    // ================= UPDATE USER =================
    private void updateUser() {

        try {

            if (viewModel.getUserLogin().getValue() == null) return;

            User oldUser = viewModel.getUserLogin().getValue().getUser();

            if (oldUser == null) return;

            mUserDatasource = new UserDatasource(getContext());

            String nom = binding.Nome.getText().toString();
            String prenom = binding.myEditText.getText().toString();
            String naissance = binding.txtBirth.getText().toString();
            String mail = binding.txtEmail.getText().toString();
            String tel = binding.txtPhone.getText().toString();

            Drawable d = binding.iconEditprofil.getDrawable();
            Bitmap bitmap = null;

            if (d instanceof BitmapDrawable) {
                bitmap = ((BitmapDrawable) d).getBitmap();
            }

            String path = null;

            if (bitmap != null) {
                path = ImageHelper.saveImageToInternalStorage(
                        requireContext(),
                        bitmap,
                        "UserImages"
                );
            }

            User newUser = new User(
                    oldUser.getId_User(),
                    oldUser.getUsername(),
                    nom,
                    prenom,
                    naissance,
                    mail,
                    null,
                    tel,
                    oldUser.getPassWord(),
                    "active",
                    oldUser.getGrade(),
                    path
            );

            int result = mUserDatasource.UpdateUserByUsername(
                    newUser,
                    oldUser.getUsername()
            );

            if (result == 1) {

                viewModel.getUserLogin().getValue().setUser(newUser);

                Constants.AffichageMessage(
                        TAG_CHARGEMENT_VALIDE,
                        "",
                        (AppCompatActivity) getContext()
                );

            } else {

                Constants.DisplayErrorMessage(
                        (AppCompatActivity) getContext(),
                        "Update failed"
                );
            }

            // remote update (safe)
            if (bitmap != null) {

                userVM.UpdateUser(newUser, bitmap)
                        .observe(getViewLifecycleOwner(), user -> {

                            if (user != null) {

                                viewModel.getUserLogin().getValue().setUser(user);

                                if (getActivity() != null) {

                                    requireActivity()
                                            .getSupportFragmentManager()
                                            .beginTransaction()
                                            .detach(this)
                                            .commit();
                                }
                            }
                        });
            }

        } catch (Exception e) {
            Log.e("EDIT_PROFILE", String.valueOf(e));
        }
    }

    // ================= DATE PICKER =================
    public void showDatePickerDialog(View view) {

        Calendar calendar = Calendar.getInstance();

        DatePickerDialog dialog = new DatePickerDialog(
                view.getContext(),
                (DatePicker datePicker, int year, int month, int day) -> {

                    String date = String.format(
                            Locale.US,
                            "%02d/%02d/%04d",
                            day,
                            month + 1,
                            year
                    );

                    binding.txtBirth.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        dialog.show();
    }

    // ================= DETACH =================
    private void detach() {
            if (getActivity() != null) {
                requireActivity()
                        .getSupportFragmentManager()
                        .popBackStack();
            }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // CRITICAL: This prevents the memory leak
        binding = null;
    }
}