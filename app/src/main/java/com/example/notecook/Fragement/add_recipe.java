package com.example.notecook.Fragement;

import static com.example.notecook.Activity.MainActivity.decod;
import static com.example.notecook.Activity.MainActivity.encod;
import static com.example.notecook.Utils.Constants.AffichageMessage;
import static com.example.notecook.Utils.Constants.All_Ingredients_Recipe;
import static com.example.notecook.Utils.Constants.CURRENT_FULL_RECIPE;
import static com.example.notecook.Utils.Constants.CURRENT_RECIPE;
import static com.example.notecook.Utils.Constants.TAG_EDIT_RECIPE;
import static com.example.notecook.Utils.Constants.clickMoins;
import static com.example.notecook.Utils.Constants.clickPlus;
import static com.example.notecook.Utils.Constants.isConnected;
import static com.example.notecook.Utils.Constants.list_recipe;
import static com.example.notecook.Utils.Constants.navAction;
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
import android.graphics.drawable.Drawable;
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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;

import com.example.notecook.Data.UserDatasource;
import com.example.notecook.Dto.RecipeRequest;
import com.example.notecook.Dto.RecipeResponse;
import com.example.notecook.Model.Detail_Recipe;
import com.example.notecook.Model.Ingredients;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.Review;
import com.example.notecook.Model.Step;
import com.example.notecook.Model.User;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.ImageHelper;
import com.example.notecook.Utils.InputValidator;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.ViewModel.UserViewModel;
import com.example.notecook.databinding.FragmentAddRecipeBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

public class add_recipe extends Fragment {

    private static final int CAMERA_REQUEST = 1888;
    private static RecipeResponse recipeR;
    private final int STORAGE_PERMISSION_CODE = 23;
    private final int GALLERY_REQUEST_CODE = 24;
    FragmentAddRecipeBinding binding;
    private RecipeViewModel recipeVM;
    private UserViewModel userVM;
    private InputValidator inputValidator;
    private List<Recipe> recipes;
    private List<Step> stepsList = new ArrayList<>();
    private List<Review> reviewsList = new ArrayList<>();
    private List<Ingredients> ingredientsList = new ArrayList<>();
    private BottomNavigationView btnV;


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
        recipeVM = new RecipeViewModel(getContext(), getActivity());
        userVM = new UserViewModel(getContext(), getActivity());
        inputValidator = new InputValidator();
        Constants.level(binding.levelRecipe, getContext());
        binding.addIconRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage(getContext());
            }
        });

        binding.addIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (All_Ingredients_Recipe != null && All_Ingredients_Recipe.size() != 0) {
                    ingredientsList.add(All_Ingredients_Recipe.get(binding.spIngredients.getSelectedItemPosition()));
                    Constants.bindingRcV_Ingredients(binding.recyclerViewIngredients, ingredientsList, getContext());
                }
            }
        });

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
        // TODO MAKE CONTROL OF ADD THE DETAIL
        binding.addStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!binding.txtTotTiemsp.getText().toString().equals("0") && !binding.edtDetail.getText().toString().isEmpty()) {
                    Step step = new Step(binding.edtDetail.getText().toString(), null, Integer.parseInt(binding.txtTotTiemsp.getText().toString()), 0);
                    stepsList.add(step);
                    Constants.bindingRcV_Steps(binding.recyclerViewSteps, stepsList, getContext());
                    binding.txtTotTiemsp.setText("0");
                    binding.edtDetail.setText("");
                }else Constants.showToast(getContext(),"step vide!!");
            }
        });

        if (TAG_EDIT_RECIPE) {
            fullRecipeDetails(CURRENT_FULL_RECIPE);
            binding.btnAddRecipe.setText("Update");
        }

        recipeR = new RecipeResponse();
        binding.btnAddRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TAG_EDIT_RECIPE)
                    updateRecipe();
                else insertRecipe();
            }
        });

        binding.btnPlusTime.setOnClickListener(view -> {
            clickPlus(binding.txtTotTime, binding.btnMoinsTime);
        });
        binding.btnMoinsTime.setOnClickListener(view -> {
            clickMoins(binding.txtTotTime, binding.btnMoinsTime);
        });
        binding.btnPlusTimesp.setOnClickListener(view -> {
            clickPlus(binding.txtTotTiemsp, binding.btnMoinsTimesp);
        });
        binding.btnMoinsTimesp.setOnClickListener(view -> {
            clickMoins(binding.txtTotTiemsp, binding.btnMoinsTimesp);
        });
        binding.btnPlusCal.setOnClickListener(view -> {
            clickPlus(binding.txtTotCal, binding.btnMoinsCal);
        });
        binding.btnMoinsCal.setOnClickListener(view -> {
            clickMoins(binding.txtTotCal, binding.btnMoinsCal);
        });
        IngredientToSp(binding.spIngredients);
        Constants.navAction((AppCompatActivity) getActivity(), add_recipe.this, MainFragment.viewPager2);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("TAG","onstart");
        //if (TAG_EDIT_RECIPE) {
          //  fullRecipeDetails(CURRENT_FULL_RECIPE);
            //binding.btnAddRecipe.setText("Update");
        //}
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        CURRENT_FULL_RECIPE = new RecipeResponse();
        TAG_EDIT_RECIPE = false;
    }

    private void fullRecipeDetails(RecipeResponse recipeR) {
        // Set the recipe name in the EditText field
        binding.editTextRecipeName.setText(recipeR.getRecipe().getNom_recipe());

        // Set the recipe instructions
        binding.editTextInstructions.setText(recipeR.getDetail_recipe().getDt_recipe());

        // Set the total time and calories
        binding.txtTotTime.setText(String.valueOf(recipeR.getDetail_recipe().getTime()));
        binding.txtTotCal.setText(String.valueOf(recipeR.getDetail_recipe().getCal()));

        // Set the recipe level (Spinner or other UI component)
        String level = recipeR.getDetail_recipe().getLevel();
        if (level != null) {
            // Assuming your spinner has an adapter and can select items based on text
            ArrayAdapter<String> adapter = (ArrayAdapter<String>) binding.levelRecipe.getAdapter();
            int spinnerPosition = adapter.getPosition(level);
            binding.levelRecipe.setSelection(spinnerPosition);
        }

        //Load the recipe icon if available
        if (!recipeR.getRecipe().getPathimagerecipe().isEmpty()) {
            binding.addIconRecipe.setImageBitmap(ImageHelper.loadImageFromPath(recipeR.getRecipe().getPathimagerecipe()));
        }

        // Set the ingredients (assuming you're using a RecyclerView or ListView for ingredients)
        ingredientsList.clear();
        ingredientsList.addAll(recipeR.getIngredients());
        Constants.bindingRcV_Ingredients(binding.recyclerViewIngredients, ingredientsList, getContext());
        // Update the adapter to show ingredients

        // Set the steps (assuming you're using a RecyclerView or ListView for steps)
        stepsList.clear();
        stepsList.addAll(recipeR.getSteps());
        Constants.bindingRcV_Steps(binding.recyclerViewSteps, stepsList, getContext());  // Update the adapter to show steps
    }


    private void insertRecipe() {
        InputValidator inp = new InputValidator();
        String randomKey = UUID.randomUUID().toString();
        if (recipeR.isAddedToRemote() && recipeR.isAddedToLocal()) {

            Constants.showToast(getContext(), "Recipe is success added before!!!");
        } else if (inp.isValidAddRecipe(binding.editTextRecipeName, binding.editTextInstructions, binding.edtDetail)) {
            Bitmap bitmap = ((BitmapDrawable) binding.addIconRecipe.getDrawable()).getBitmap();
            Detail_Recipe detail_recipe = new Detail_Recipe();
            detail_recipe.setDt_recipe(binding.editTextInstructions.getText().toString());
            detail_recipe.setTime(Integer.parseInt(binding.txtTotTime.getText().toString()));
            detail_recipe.setCal(Integer.parseInt(binding.txtTotCal.getText().toString()));
            detail_recipe.setLevel(binding.levelRecipe.getSelectedItem().toString());
            recipeR.setIngredients(ingredientsList);
            recipeR.setDetail_recipe(detail_recipe);
            recipeR.setSteps(stepsList);

            if (isConnected() && !recipeR.isAddedToRemote())
                if (user_login.getUser() != null) {
                    Recipe recipe = new Recipe(binding.editTextRecipeName.getText().toString(), null, 0, user_login.getUser().getId_User(), randomKey);
                    postRecipeToRemote(recipeR, recipe, bitmap);
                }
            Recipe recipe = new Recipe(binding.editTextRecipeName.getText().toString(), null, 0, 0, randomKey);
            String pathImage = ImageHelper.saveImageToInternalStorage(getContext(), bitmap, "RecipeImages");
            recipe.setPathimagerecipe(pathImage);
            if (user_login_local.getUser() != null && user_login_local.getUser().getId_User() != 0)
                recipe.setFrk_user(user_login_local.getUser().getId_User());
            else {
                userVM.getUserLocal(Constants.getUserInput(requireContext()), "success");
                recipe.setFrk_user(user_login_local.getUser().getId_User());
            }
            if (!recipeR.isAddedToLocal())
                postRecipeToLocal(recipeR, recipe);
        }
    }

    public void IngredientToSp(Spinner sp) {
        List<String> ingredientNames = new ArrayList<>();
        // Iterate over All_Ingredients_Recipe to collect all ingredient names
        for (Ingredients ingredient : All_Ingredients_Recipe) {
            String name = ingredient.getNome();
            if (name != null) {
                ingredientNames.add(name);
            }
        }
        // Create an ArrayAdapter
        ArrayAdapter<String> adapterIngredients = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, ingredientNames);

        // Set the adapter to your ListView or RecyclerView
        sp.setAdapter(adapterIngredients);
    }

    private void postRecipeToRemote(RecipeResponse recipeR, Recipe recipe, Bitmap bitmap) {
        recipeR.setRecipe(recipe);
        recipeVM.postFullRecipe(recipeR, bitmap).observe(requireActivity(), new Observer<Integer>() {
            @Override
            public void onChanged(Integer recipe) {
                if (recipe != -1)
                    add_recipe.recipeR.setAddedToRemote(true);
                Toast.makeText(getContext(), "recipe add success in Remote", Toast.LENGTH_SHORT).show();
                MainFragment.viewPager2.setCurrentItem(4);
            }
        });
    }

    private void updateRecipe() {
        CURRENT_FULL_RECIPE.getRecipe().setNom_recipe(binding.editTextRecipeName.getText().toString());
        Bitmap bitmap = ((BitmapDrawable) binding.addIconRecipe.getDrawable()).getBitmap();

        CURRENT_FULL_RECIPE.getDetail_recipe().setDt_recipe(binding.editTextInstructions.getText().toString());
        CURRENT_FULL_RECIPE.getDetail_recipe().setTime(Integer.parseInt(binding.txtTotTime.getText().toString()));
        CURRENT_FULL_RECIPE.getDetail_recipe().setCal(Integer.parseInt(binding.txtTotCal.getText().toString()));
        CURRENT_FULL_RECIPE.getDetail_recipe().setLevel(binding.levelRecipe.getSelectedItem().toString());
        CURRENT_FULL_RECIPE.setIngredients(ingredientsList);
        CURRENT_FULL_RECIPE.setSteps(stepsList);
        recipeVM.updateFullRecipeLocal(CURRENT_FULL_RECIPE).observe(requireActivity(), new Observer<RecipeResponse>() {
            @Override
            public void onChanged(RecipeResponse Recipe) {
                if (Recipe != null) {
                    recipeVM.updateImageRecipeLocal(bitmap, CURRENT_FULL_RECIPE.getRecipe().getId_recipe());
                    recipeVM.updateFullRemoteRecipe(Recipe).observe(requireActivity(), new Observer<String>() {
                        @Override
                        public void onChanged(String Result) {
                            if (Result != "") {
                                recipeVM.uploadRemoteRecipeImage(Recipe.getRecipe().getUnique_key_recipe(), bitmap).observe(requireActivity(), new Observer<String>() {
                                    @Override
                                    public void onChanged(String s) {
                                        CURRENT_FULL_RECIPE.getRecipe().setPathimagerecipe(s);
                                        Constants.AffichageMessage("success", "", requireActivity());
                                        detach();
                                    }
                                });
                            }
                        }
                    });
                } else AffichageMessage("eror", "Error fro update !!!", getActivity());
            }
        });
    }

    private void detach() {
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction.detach(add_recipe.this);
        fragmentTransaction.commitNow();
    }

    private void postRecipeToLocal(RecipeResponse recipeR, Recipe recipe) {
        recipeR.setRecipe(recipe);
        recipeVM.postFullRecipeLocal(recipeR).observe(requireActivity(), new Observer<RecipeResponse>() {
            @Override
            public void onChanged(RecipeResponse recipeResponse) {
                if (recipeResponse != null) {
                    add_recipe.recipeR.setAddedToLocal(true);
                    Toast.makeText(getContext(), "recipe add success locally", Toast.LENGTH_SHORT).show();
                }
            }
        });
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


    private User getLocalUser(String username) {
        UserDatasource userDatasource = new UserDatasource(getContext());
        userDatasource.open();

        User user = userDatasource.select_User_BYUsername(username);
        userDatasource.close();
        return user;
    }
}