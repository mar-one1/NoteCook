package com.example.notecook.Adapter;


import static com.example.notecook.Fragement.MainFragment.viewPager2;
import static com.example.notecook.Repo.FavoritesRecipeRepository.Insert_Fav;
import static com.example.notecook.Utils.Constants.CURRENT_FULL_RECIPE;
import static com.example.notecook.Utils.Constants.CURRENT_RECIPE;
import static com.example.notecook.Utils.Constants.Detail_CurrentRecipe;
import static com.example.notecook.Utils.Constants.Favorite_CurrentRecipe;
import static com.example.notecook.Utils.Constants.Ingredients_CurrentRecipe;
import static com.example.notecook.Utils.Constants.Recipes_Fav_User;
import static com.example.notecook.Utils.Constants.Remote_nutritions;
import static com.example.notecook.Utils.Constants.Review_CurrentRecipe;
import static com.example.notecook.Utils.Constants.Steps_CurrentRecipe;
import static com.example.notecook.Utils.Constants.TAG_EDIT_RECIPE;
import static com.example.notecook.Utils.Constants.TAG_LOCAL;
import static com.example.notecook.Utils.Constants.User_CurrentRecipe;
import static com.example.notecook.Utils.Constants.user_login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Activity.MainActivity;
import com.example.notecook.Dto.RecipeResponse;
import com.example.notecook.Fragement.MainFragment;
import com.example.notecook.Model.Nutrition;
import com.example.notecook.Model.Recipe;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.FetchNutritionTask;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.ViewModel.UserViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;
import java.util.Objects;

public class Adapter_RC_RecipeDt extends RecyclerView.Adapter<Adapter_RC_RecipeDt.ViewHolder> implements FetchNutritionTask.OnNutritionFetchedListener {

    private String b;
    private List<Recipe> recipes;
    private RecipeViewModel recipeVM;
    private UserViewModel userVM;
    private Context context;
    private Activity activity;
    private FloatingActionButton Flbtn;


    public Adapter_RC_RecipeDt(Context context, Activity activity, List<Recipe> recipes, String bb) {
        this.recipes = recipes;
        b = bb;
        this.context = context;
        this.activity = activity;
        recipeVM = new RecipeViewModel(context, activity);
        userVM = new UserViewModel(context, activity);
        notifyDataSetChanged();
    }

    @Override
    public Adapter_RC_RecipeDt.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_row, parent, false);
        return new Adapter_RC_RecipeDt.ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        Drawable defaultImagelike = holder.itemView.getResources().getDrawable(R.drawable.ic_baseline_favorite_24);
        Drawable defaultImagenot = holder.itemView.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp);
        holder.detail.setText(recipe.getNom_recipe());
        holder.txt_rate.setText(String.valueOf(recipe.getFav()));

        MainActivity.showImageRecipes(recipeVM,recipe,holder.Image);

        if (Objects.equals(b, TAG_LOCAL)) {
            holder.txt_time.setText("Local");
            holder.pin.setVisibility(View.VISIBLE);
        } else {
            holder.txt_time.setText("Remote");
            holder.pin.setVisibility(View.GONE);
        }

        holder.pin.setOnClickListener(view -> {
            FragmentActivity fragmentActivity = (FragmentActivity) view.getContext();
            Flbtn = fragmentActivity.findViewById(R.id.floating_action_button);
            CURRENT_RECIPE = recipe;
            TAG_EDIT_RECIPE = true;
            recipeVM.getFullRecipeLocal(recipe).observe(fragmentActivity, new Observer<RecipeResponse>() {
                @Override
                public void onChanged(RecipeResponse recipeResponse) {
                    if (recipeResponse != null) {
                        //viewPager2.setCurrentItem(1);
                        fetchRecipe(recipeResponse);
                        CURRENT_FULL_RECIPE = recipeResponse;
                        //MainFragment.viewPager2.setCurrentItem(1, false);
                        Flbtn.callOnClick();
                    }
                    Constants.dismissLoadingDialog();
                }
            });
        });

        holder.heat.setOnClickListener(view -> {
            Drawable pic = holder.heat.getDrawable().getCurrent();
            Toast.makeText(view.getContext(), "" + pic, Toast.LENGTH_SHORT).show();
            if (defaultImagelike.getConstantState().equals(holder.heat.getDrawable().getConstantState())) {
                holder.heat.setImageDrawable(defaultImagenot);
            } else {
                holder.heat.setImageDrawable(defaultImagelike);
                Recipes_Fav_User.add(recipe);
                Insert_Fav(user_login.getUser().getId_User(), recipe.getId_recipe());
            }
        });

        holder.Image.setOnClickListener(v -> {
            TAG_EDIT_RECIPE = false;
            // Get the FragmentActivity associated with the context of the clicked view
            FragmentActivity fragmentActivity = (FragmentActivity) v.getContext();
            if (CURRENT_RECIPE != recipe) {
                Constants.loading_ui(context, activity, "Chargement Recipe");
                if (!Objects.equals(b, TAG_LOCAL)) {
                    //CURRENT_RECIPE = recipe;
                    recipeVM.getFullRecipeApi(recipe.getId_recipe()).observe(fragmentActivity, new Observer<RecipeResponse>() {
                        @Override
                        public void onChanged(RecipeResponse recipe) {
                            if (recipe != null) {
                                //viewPager2.setCurrentItem(1);
                                fetchRecipe(recipe);
                                CURRENT_FULL_RECIPE = recipe;
                                // Fetch nutrition for "apple"
                                fetchNutritionData(CURRENT_FULL_RECIPE.getRecipe().getNom_recipe(), 100);
                                MainFragment.viewPager2.setCurrentItem(1, false);

                            }
                            Constants.dismissLoadingDialog();
                        }
                    });

                } else {
                    recipeVM.getFullRecipeLocal(recipe).observe(fragmentActivity, new Observer<RecipeResponse>() {
                        @Override
                        public void onChanged(RecipeResponse recipeResponse) {
                            if (recipeResponse != null) {
                                //viewPager2.setCurrentItem(1);
                                fetchRecipe(recipeResponse);
                                CURRENT_FULL_RECIPE = recipeResponse;
                                User_CurrentRecipe = user_login.getUser();
                                fetchNutritionData(CURRENT_FULL_RECIPE.getRecipe().getNom_recipe(), 100, "g");
                                MainFragment.viewPager2.setCurrentItem(1, false);
                            }
                            Constants.dismissLoadingDialog();
                        }
                    });
                }
            } else {
                viewPager2.setCurrentItem(1, false);
                Constants.dismissLoadingDialog();
            }
        });
    }

    // Method to fetch nutrition data with custom serving size
    public void fetchNutritionData(String query, double servingSize, String ServingUnit) {
        new FetchNutritionTask(this, servingSize, ServingUnit).execute(query);
    }// Method to fetch nutrition data with custom serving size

    public void fetchNutritionData(String query, double servingSize) {
        new FetchNutritionTask(this, servingSize).execute(query);
    }

    @Override
    public void onNutritionFetched(Nutrition nutrition) {
        if (nutrition != null) {
            // Calculate the nutrition based on the custom serving size entered by the user
            double customCalories = nutrition.getCalories() * nutrition.getServingSize() / 100;
            double customProtein = nutrition.getProtein() * nutrition.getServingSize() / 100;
            double customFat = nutrition.getFat() * nutrition.getServingSize() / 100;
            double customCarbs = nutrition.getCarbs() * nutrition.getServingSize() / 100;

            // Display updated nutrition info
            String nutritionInfo = "Name: " + nutrition.getDescription() + "\n" +
                    "Calories: " + customCalories + " kcal\n" +
                    "Protein: " + customProtein + " g\n" +
                    "Fat: " + customFat + " g\n" +
                    "Carbs: " + customCarbs + " g\n" +
                    "Serving Size: " + nutrition.getServingSize() + " " + nutrition.getServingSizeUnit();

            CURRENT_FULL_RECIPE.setNutrition(nutrition);
            Remote_nutritions.setValue(nutrition);
            Log.e("nutrition", nutritionInfo);
            Log.e("nutrition", String.valueOf(nutrition.getCarbs()));

        } else {
            Log.e("nutrition", "Failed to fetch nutrition data.");
        }
    }

    private void fetchRecipe(RecipeResponse recipeResponse) {
        User_CurrentRecipe = recipeResponse.getUser();
        CURRENT_RECIPE = recipeResponse.getRecipe();
        Detail_CurrentRecipe = recipeResponse.getDetail_recipe();
        Steps_CurrentRecipe = recipeResponse.getSteps();
        Review_CurrentRecipe = recipeResponse.getReviews();
        Ingredients_CurrentRecipe.setValue(recipeResponse.getIngredients());
        Review_CurrentRecipe = recipeResponse.getReviews();
        Favorite_CurrentRecipe = recipeResponse.getFavs();
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView Image;
        TextView detail, txt_time, txt_rate;
        ImageView heat, pin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            detail = itemView.findViewById(R.id.nom_recipe);
            txt_time = itemView.findViewById(R.id.time_recipe);
            txt_rate = itemView.findViewById(R.id.rate_recipe);
            Image = itemView.findViewById(R.id.ImgV_IconCatFood);
            heat = itemView.findViewById(R.id.heart_recipe);
            pin = itemView.findViewById(R.id.edit_recipe);

        }

        public ImageView getPin() {
            return pin;
        }

        public void setPin(ImageView pin) {
            this.pin = pin;
        }

        public TextView getTxt_time() {
            return txt_time;
        }

        public void setTxt_time(TextView txt_time) {
            this.txt_time = txt_time;
        }

        public TextView getTxt_rate() {
            return txt_rate;
        }

        public void setTxt_rate(TextView txt_rate) {
            this.txt_rate = txt_rate;
        }

        public TextView getDetail() {
            return detail;
        }

        public void setDetail(TextView detail) {
            this.detail = detail;
        }

        public ImageView getImage() {
            return Image;
        }

        public void setImage(ImageView image) {
            Image = image;
        }

        public ImageView getHeat() {
            return heat;
        }

        public void setHeat(ImageView heat) {
            this.heat = heat;
        }
    }


}
