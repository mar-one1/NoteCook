package com.example.notecook.Fragement;

import static com.example.notecook.MainActivity.encod;
import static com.example.notecook.Utils.Constants.All_Ingredients_Recipe;
import static com.example.notecook.Utils.Constants.list_recipe;
import static com.example.notecook.Utils.Constants.user_login;
import static com.example.notecook.Utils.Constants.user_login_local;

import android.Manifest;
import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.notecook.Api.ValidationError;
import com.example.notecook.Data.UserDatasource;
import com.example.notecook.Model.Ingredients;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.User;
import com.example.notecook.R;
import com.example.notecook.Repo.UserRepository;
import com.example.notecook.Utils.InputValidator;
import com.example.notecook.Utils.levelRecipe;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.ViewModel.UserViewModel;
import com.example.notecook.databinding.FragmentAddRecipeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

public class add_recipe extends Fragment {

    private static final int CAMERA_REQUEST = 1888;
    private final int STORAGE_PERMISSION_CODE = 23;
    private final int GALLERY_REQUEST_CODE = 24;
    FragmentAddRecipeBinding binding;
    private RecipeViewModel recipeVM;
    private UserViewModel userVM;
    private InputValidator inputValidator;
    private List<Recipe> recipes;

    public add_recipe() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAddRecipeBinding.inflate(inflater, container, false);
        int bnvId = R.id.bottom_nav;
        BottomNavigationView btnV = getActivity().findViewById(bnvId);
        recipes = new ArrayList<>();
        recipeVM = new RecipeViewModel(getContext(),getActivity());
        userVM = new UserViewModel(getContext(),getActivity());
        inputValidator = new InputValidator();
        // Get the values of the enum
        levelRecipe[] values = levelRecipe.values();
//
//        // Create an array of display names
        String[] displayNames = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            displayNames[i] = values[i].name();
        }

        binding.addIconRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage(getContext());
            }
        });

        // Create an ArrayAdapter
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, displayNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.levelRecipe.setAdapter(adapter);

        List<String> ingredientNames = new ArrayList<>();
// Iterate over All_Ingredients_Recipe to collect all ingredient names
        for (Ingredients ingredient : All_Ingredients_Recipe) {
            ingredientNames.add(ingredient.getNome());
        }

// Create an ArrayAdapter
        ArrayAdapter<String> adapterIngredients = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, ingredientNames);

// Set the adapter to your ListView or RecyclerView
        binding.spIngredients.setAdapter(adapterIngredients);
        // Set the ArrayAdapter to the Spinner


        binding.dtRecipeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickhid(binding.detailRecipeLy);
            }
        });

        binding.stepTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickhid(binding.stepLy);
            }
        });

        binding.btnAddRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                InputValidator inp = new InputValidator();
                if (inp.isValidAddRecipe(binding.editTextRecipeName, binding.editTextInstructions, binding.edtDetail)) {
                    Bitmap bitmap = ((BitmapDrawable) binding.addIconRecipe.getDrawable()).getBitmap();
                    Recipe recipe = new Recipe(binding.editTextRecipeName.getText().toString(), null, 0, user_login.getUser().getId_User());
                    Log.d("TAG", "" + user_login.getUser().getId_User());
                    recipeVM.postRecipe(recipe, bitmap);
                    recipe.setIcon_recipe(encod(bitmap));
                    int i;
                    if (user_login_local.getUser() != null && user_login_local.getUser().getId_User() != 0) {
                        i = recipeVM.postRecipeLocal(recipe, user_login_local.getUser().getId_User());
                    } else {
                        userVM.getUserLocal(user_login.getUser().getUsername(), "success");
                        i = recipeVM.postRecipeLocal(recipe, user_login_local.getUser().getId_User());
                    }
                    if (i != 0) {
                        Toast.makeText(getContext(), "recipe add successly in localy", Toast.LENGTH_SHORT).show();
//                        recipes = list_recipe.getValue();
//                        recipes.add(recipe);
//                        list_recipe.setValue(recipes);
                    }
                }
            }
        });

        binding.btnPlusTime.setOnClickListener(view -> {
            clickPlus(binding.txtTotTime, binding.btnPlusTime, binding.btnMoinsTime);
        });
        binding.btnMoinsTime.setOnClickListener(view -> {
            clickMoins(binding.txtTotTime, binding.btnMoinsTime, binding.btnMoinsTime);
        });
        binding.btnPlusTimesp.setOnClickListener(view -> {
            clickPlus(binding.txtTotTiemsp, binding.btnPlusTimesp, binding.btnMoinsTimesp);
        });
        binding.btnMoinsTimesp.setOnClickListener(view -> {
            clickMoins(binding.txtTotTiemsp, binding.btnMoinsTimesp, binding.btnMoinsTimesp);
        });
        binding.btnPlusCal.setOnClickListener(view -> {
            clickPlus(binding.txtTotCal, binding.btnPlusCal, binding.btnMoinsCal);
        });
        binding.btnMoinsCal.setOnClickListener(view -> {
            clickMoins(binding.txtTotCal, binding.btnMoinsCal, binding.btnMoinsCal);
        });

        btnV.setOnNavigationItemSelectedListener(
                item -> {
                    FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.detach(add_recipe.this);
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
                    MainFragment.viewPager2.setCurrentItem(i, false);
                    return false;
                });
        // Inflate the layout for this fragmentè
        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                binding.addIconRecipe.setImageBitmap(photo);
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    binding.addIconRecipe.setImageURI(selectedImageUri);
                }
            }
        }
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


    private void expand(LinearLayout linearLayout) {
        linearLayout.setVisibility(View.VISIBLE);

        final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        linearLayout.measure(widthSpec, heightSpec);

        ValueAnimator mAnimator = slideAnimator(0, linearLayout.getMeasuredHeight(), linearLayout);
        mAnimator.start();
    }

    private void collapse(LinearLayout linearLayout) {
        int finalHeight = linearLayout.getHeight();

        ValueAnimator mAnimator = slideAnimator(finalHeight, 0, linearLayout);

        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                //Height=0, but it set visibility to GONE
                linearLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }

        });
        mAnimator.start();
    }

    private ValueAnimator slideAnimator(int start, int end, LinearLayout linearLayout) {

        ValueAnimator animator = ValueAnimator.ofInt(start, end);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                //Update Height
                int value = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
                layoutParams.height = value;
                linearLayout.setLayoutParams(layoutParams);
            }
        });
        return animator;
    }

    private void clickhid(LinearLayout linearLayout) {
        if (linearLayout.getVisibility() == View.GONE) {
            expand(linearLayout);
        } else {
            collapse(linearLayout);
        }
    }

    private void clickMoins(TextView textView, Button buttonPlus, Button buttonMoins) {
        int t = Integer.parseInt(textView.getText().toString());
        if (t <= 0) {
            buttonMoins.setEnabled(false);
        } else
            t--;
        textView.setText("" + t);
    }

    private void clickPlus(TextView textView, Button button, Button buttonMoins) {
        int t = Integer.parseInt(textView.getText().toString());
        buttonMoins.setEnabled(true);
        t++;
        textView.setText("" + t);
    }

    private User getLocalUser(String username) {
        UserDatasource userDatasource = new UserDatasource(getContext());
        userDatasource.open();

        User user = userDatasource.select_User_BYUsername(username);
        userDatasource.close();
        return user;
    }
}