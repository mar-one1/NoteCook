package com.example.notecook.Fragement;

import static com.example.notecook.Utils.Constants.AffichageMessage;
import static com.example.notecook.Utils.Constants.captureImage;
import static com.example.notecook.Utils.Constants.clickMoins;
import static com.example.notecook.Utils.Constants.clickPlus;
import static com.example.notecook.Utils.Constants.isConnected;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.notecook.Adapter.Adapter_Rc_Ingredents;
import com.example.notecook.Adapter.Adapter_Rc_Steps;
import com.example.notecook.Data.UserDatasource;
import com.example.notecook.Dto.RecipeResponse;
import com.example.notecook.Model.Detail_Recipe;
import com.example.notecook.Model.Ingredients;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.Step;
import com.example.notecook.Model.User;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.ImageHelper;
import com.example.notecook.Utils.ImagePickerManager;
import com.example.notecook.Utils.InputValidator;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.ViewModel.SharedRecipeViewModel;
import com.example.notecook.ViewModel.StepViewModel;
import com.example.notecook.ViewModel.UserViewModel;
import com.example.notecook.databinding.FragmentAddRecipeBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import javax.annotation.Nullable;

public class add_recipe extends Fragment {

    private static final int CAMERA_REQUEST = 1888;
    private static RecipeResponse recipeR;
    private final int GALLERY_REQUEST_CODE = 24;
    FragmentAddRecipeBinding binding;
    private RecipeViewModel recipeVM;
    private UserViewModel userVM;
    private StepViewModel stepVM;
    private List<Step> stepsList = new ArrayList<>();
    private List<Ingredients> ingredientsList = new ArrayList<>();
    private ImageView currentTargetImageView;
    private SharedRecipeViewModel viewModel;

    ActivityResultLauncher<Void> cameraLauncher;
    ActivityResultLauncher<String> galleryLauncher;

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
        viewModel = new ViewModelProvider(requireActivity()).get(SharedRecipeViewModel.class);
        viewModel.setTagMy(true);
        recipeVM = new RecipeViewModel(getContext(), getActivity(),viewModel);
        userVM = new UserViewModel(getContext(), getActivity(),viewModel);
        stepVM = new StepViewModel(getContext(), getActivity(),viewModel);
        Constants.level(binding.levelRecipe, getContext());
                cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicturePreview(),
                bitmap -> {
                    if (bitmap != null) {
                        binding.addIconRecipe.setImageBitmap(bitmap);
                    }
                });

                galleryLauncher = registerForActivityResult(
                new ActivityResultContracts.GetContent(),
                uri -> {
                    if (uri != null) {
                        binding.addIconRecipe.setImageURI(uri);
                    }
                });

                binding.addIconRecipe.setOnClickListener(v -> {

                    Constants.captureImage(getContext(), new Constants.ImagePickerListener() {

                        @Override
                        public void onCameraSelected() {
                            cameraLauncher.launch(null);
                        }

                        @Override
                        public void onGallerySelected() {
                            galleryLauncher.launch("image/*");
                        }
                    });
            });

        binding.addIconStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTargetImageView = binding.addIconStep;
                captureImage(v.getContext(), add_recipe.this);
            }
        });

        binding.addIngredients.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewModel.getAllIngredientsRecipe() != null && !viewModel.getAllIngredientsRecipe().isEmpty()) {
                    Ingredients ingredient = viewModel.getAllIngredientsRecipe().get(binding.spIngredients.getSelectedItemPosition());
                    Adapter_Rc_Ingredents adapter = (Adapter_Rc_Ingredents) binding.recyclerViewIngredients.getAdapter();
                    if (adapter != null) ingredientsList = adapter.getDataList();
                    if (!ingredientsList.contains(ingredient)) {
                        ingredientsList.add(ingredient);
                        Constants.bindingRcV_Ingredients(binding.recyclerViewIngredients, ingredientsList, getContext(),viewModel);
                    } else Constants.showSnackPar(v, "this ingredient in the list!!!");
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
                if (!binding.txtTotTiemsp.getText().toString().equals("0") && !binding.edtDetail.getText().toString().isEmpty()) {
                    String imageUrl = ImageHelper.saveImageToInternalStorage(view.getContext(), ImageHelper.drawableToBitmap(binding.addIconStep.getDrawable()), "Steps");
                    Step step = new Step(binding.edtDetail.getText().toString(), imageUrl, Integer.parseInt(binding.txtTotTiemsp.getText().toString()), 0);
                    Adapter_Rc_Steps adapter = (Adapter_Rc_Steps) binding.recyclerViewSteps.getAdapter();
                    if (adapter != null)
                        stepsList = adapter.getDataList();
                    stepsList.add(step);
                    Constants.bindingRcV_Steps(binding.recyclerViewSteps, stepsList, getContext(),viewModel);
                    binding.txtTotTiemsp.setText("0");
                    binding.edtDetail.setText("");
                    binding.addIconStep.setImageDrawable(view.getResources().getDrawable(R.drawable.add_photo_profil));
                } else Constants.showToast(getContext(), "step vide!!");
            }
        });

        if (Boolean.TRUE.equals(viewModel.getTagEditRecipe().getValue())) {
            fullRecipeDetails(Objects.requireNonNull(viewModel.getCurrentFullRecipe().getValue()));
            binding.btnAddRecipe.setText("Update");
        }

        recipeR = new RecipeResponse();
        binding.btnAddRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Boolean.TRUE.equals(viewModel.getTagEditRecipe().getValue()))
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
    public void onDestroyView() {
        super.onDestroyView();
        viewModel.setCurrentFullRecipe(new RecipeResponse());
        viewModel.setTagEditRecipe(false);
        viewModel.setTagMy(false);
        binding = null;
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
        Constants.showImageRecipes(recipeVM, recipeR.getRecipe(), binding.addIconRecipe,getContext());

        // Set the ingredients (assuming you're using a RecyclerView or ListView for ingredients)
        ingredientsList.clear();
        ingredientsList.addAll(recipeR.getIngredients());
        Constants.bindingRcV_Ingredients(binding.recyclerViewIngredients, ingredientsList, getContext(),viewModel);
        // Update the adapter to show ingredients

        // Set the steps (assuming you're using a RecyclerView or ListView for steps)
        stepsList.clear();
        stepsList.addAll(recipeR.getSteps());
        Constants.bindingRcV_Steps(binding.recyclerViewSteps, stepsList, getContext(),viewModel);  // Update the adapter to show steps
    }


    private void insertRecipe() {
        InputValidator inp = new InputValidator();
        String randomKey = UUID.randomUUID().toString();
        if (recipeR.isAddedToRemote() && recipeR.isAddedToLocal()) {

            Constants.showToast(getContext(), "Recipe is success added before!!!");
        } else if (inp.isValidAddRecipe(binding.editTextRecipeName, binding.editTextInstructions)) {
            Bitmap bitmap = ((BitmapDrawable) binding.addIconRecipe.getDrawable()).getBitmap();
            Detail_Recipe detail_recipe = new Detail_Recipe();
            detail_recipe.setDt_recipe(binding.editTextInstructions.getText().toString());
            detail_recipe.setTime(Integer.parseInt(binding.txtTotTime.getText().toString()));
            detail_recipe.setCal(Integer.parseInt(binding.txtTotCal.getText().toString()));
            detail_recipe.setLevel(binding.levelRecipe.getSelectedItem().toString());
            recipeR.setDetail_recipe(detail_recipe);
            Adapter_Rc_Ingredents adapter = (Adapter_Rc_Ingredents) binding.recyclerViewIngredients.getAdapter();
            if (adapter != null) {
                ingredientsList = adapter.getDataList();
            }
            Adapter_Rc_Steps adapterSteps = (Adapter_Rc_Steps) binding.recyclerViewSteps.getAdapter();
            if (adapterSteps != null)
                stepsList = adapterSteps.getDataList();
            recipeR.setIngredients(ingredientsList);
            recipeR.setSteps(stepsList);

            if (isConnected(getContext()) && !recipeR.isAddedToRemote())
                if (viewModel.getUserLogin().getValue() !=null  && viewModel.getUserLogin().getValue().getUser() != null) {
                    Recipe recipe = new Recipe(binding.editTextRecipeName.getText().toString(), null, 0, viewModel.getUserLogin().getValue().getUser().getId_User(), randomKey);
                    postRecipeToRemote(recipeR, recipe, bitmap);
                }
            Recipe recipe = new Recipe(binding.editTextRecipeName.getText().toString(), null, 0, 0, randomKey);
            String pathImage = ImageHelper.saveImageToInternalStorage(getContext(), bitmap, "RecipeImages");
            recipe.setPathimagerecipe(pathImage);
            if (viewModel.getUserLoginLocal().getValue()!=null && viewModel.getUserLoginLocal().getValue().getUser() != null && viewModel.getUserLoginLocal().getValue().getUser().getId_User() != 0)
                recipe.setFrk_user(viewModel.getUserLoginLocal().getValue().getUser().getId_User());
            else {
                userVM.getUserLocal(Constants.getUserInput(requireContext()), "success");
                detach();
                recipe.setFrk_user(viewModel.getUserLoginLocal().getValue().getUser().getId_User());
            }
            if (!recipeR.isAddedToLocal())
                postRecipeToLocal(recipeR, recipe);
        }
    }

    public void IngredientToSp(Spinner sp) {
        List<String> ingredientNames = new ArrayList<>();
        // Iterate over All_Ingredients_Recipe to collect all ingredient names
        for (Ingredients ingredient : viewModel.getAllIngredientsRecipe()) {
            String name = ingredient.getNome();
            if (name != null && !ingredientNames.contains(name)) {
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
                if (recipeR.isAddedToLocal() && recipeR.isAddedToRemote()) {
                    MainFragment.viewPager2.setCurrentItem(4);
                    detach();
                }
            }
        });
    }

    private void updateRecipe() {
        // 1️⃣ Update local recipe object
        viewModel.getCurrentFullRecipe().getValue().getRecipe().setNom_recipe(binding.editTextRecipeName.getText().toString());
        Bitmap bitmap = ImageHelper.drawableToBitmap(binding.addIconRecipe.getDrawable());
        Bitmap resizeBitmap =ImageHelper.resizeBitmap(bitmap,2);

        viewModel.getCurrentFullRecipe().getValue().getDetail_recipe().setDt_recipe(binding.editTextInstructions.getText().toString());
        viewModel.getCurrentFullRecipe().getValue().getDetail_recipe().setTime(parseIntSafe(binding.txtTotTime.getText().toString()));
        viewModel.getCurrentFullRecipe().getValue().getDetail_recipe().setCal(parseIntSafe(binding.txtTotCal.getText().toString()));
        viewModel.getCurrentFullRecipe().getValue().getDetail_recipe().setLevel(binding.levelRecipe.getSelectedItem().toString());

        // 2️⃣ Get ingredients and steps
        Adapter_Rc_Ingredents adapterIng = (Adapter_Rc_Ingredents) binding.recyclerViewIngredients.getAdapter();
        viewModel.getCurrentFullRecipe().getValue().setIngredients(adapterIng != null ? adapterIng.getDataList() : new ArrayList<>());

        Adapter_Rc_Steps adapterSteps = (Adapter_Rc_Steps) binding.recyclerViewSteps.getAdapter();
        viewModel.getCurrentFullRecipe().getValue().setSteps(adapterSteps != null ? adapterSteps.getDataList() : new ArrayList<>());

        // 3️⃣ Update local DB
        recipeVM.updateFullRecipeLocal(viewModel.getCurrentFullRecipe().getValue()).observe(requireActivity(), recipeResponse -> {
            if (recipeResponse == null) {
                AffichageMessage("error", "Error updating local recipe!", getActivity());
                return;
            }

            // 4️⃣ Update local recipe image if exists
            if (resizeBitmap != null) {
                recipeVM.updateImageRecipeLocal(resizeBitmap, viewModel.getCurrentFullRecipe().getValue().getRecipe().getId_recipe());
            }

            // 5️⃣ Update recipe remotely
            recipeVM.updateFullRemoteRecipe(recipeResponse).observe(requireActivity(), remoteResult -> {
                if (remoteResult == null || remoteResult.isEmpty()) {
                    AffichageMessage("error", "Error updating remote recipe!", getActivity());
                    return;
                }
                uploadRecipeImages(recipeResponse, bitmap);
            });
        });
    }

    /** Upload main recipe image and all step images, then show success */
    private void uploadRecipeImages(RecipeResponse recipe, Bitmap mainBitmap) {
        if (mainBitmap != null) {
            recipeVM.uploadRemoteRecipeImage(recipe.getRecipe().getUnique_key_recipe(), mainBitmap)
                    .observe(requireActivity(), mainImageUrl -> {
                        recipe.getRecipe().setPathimagerecipe(mainImageUrl);
                        uploadStepImages(recipe);
                    });
        } else {
            uploadStepImages(recipe);
        }
    }

    /** Upload all step images and finish when done */
    private void uploadStepImages(RecipeResponse recipe) {
        List<Step> steps = recipe.getSteps();
        if (steps.isEmpty()) {
            finishUpdate(recipe);
            return;
        }

        AtomicInteger completed = new AtomicInteger(0);
        for (Step step : steps) {
            String url = step.getImage_step();
            stepVM.postImageStepRemote(url, ImageHelper.loadImageFromPath(url))
                    .observe(requireActivity(), uploadedUrl -> {
                        step.setImage_step(uploadedUrl);
                        if (completed.incrementAndGet() == steps.size()) {
                            finishUpdate(recipe);
                        }
                    });
        }
    }

    /** Finalize update */
    private void finishUpdate(RecipeResponse recipe) {
        viewModel.getCurrentFullRecipe().getValue().setRecipe(recipe.getRecipe());
        Constants.AffichageMessage("success", "", requireActivity());
        detach();
    }

    /** Safe integer parsing */
    private int parseIntSafe(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return 0;
        }
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
                    if (recipeR.isAddedToLocal() && recipeR.isAddedToRemote()) {
                        MainFragment.viewPager2.setCurrentItem(4, false);
                        detach();
                    }
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
                currentTargetImageView.setImageBitmap(photo);
            } else if (requestCode == GALLERY_REQUEST_CODE) {
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    currentTargetImageView.setImageURI(selectedImageUri);
                }
            }
        }
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

    //TODO VERIFY THE RECYCLE OF RECIPE NO SHOWING IN ADD MODE WHERE THE LIST IS EMPTY
    private User getLocalUser(String username) {
        UserDatasource userDatasource = new UserDatasource(getContext());
        userDatasource.open();

        User user = userDatasource.select_User_BYUsername(username);
        userDatasource.close();
        return user;
    }

}