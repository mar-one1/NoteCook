package com.example.notecook.Adapter;


import static com.example.notecook.Activity.MainActivity.TAG_LOCAL;
import static com.example.notecook.Fragement.MainFragment.flBtn;
import static com.example.notecook.Fragement.MainFragment.viewPager2;


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

import com.example.notecook.Dto.RecipeResponse;
import com.example.notecook.Fragement.Acceuill_Frg;
import com.example.notecook.Fragement.MainFragment;
import com.example.notecook.Model.Nutrition;
import com.example.notecook.Model.Recipe;
import com.example.notecook.R;
import com.example.notecook.Repo.FavoritesRecipeRepository;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.FetchNutritionTask;
import com.example.notecook.ViewModel.SharedRecipeViewModel;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.ViewModel.UserViewModel;

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
    private SharedRecipeViewModel viewModel;
    private FavoritesRecipeRepository favoritesRecipeRepository;


    public Adapter_RC_RecipeDt(Context context, Activity activity,SharedRecipeViewModel viewModel, List<Recipe> recipes, String bb) {
        this.recipes = recipes;
        b = bb;
        this.context = context;
        this.activity = activity;
        this.viewModel = viewModel;
        this.favoritesRecipeRepository=new FavoritesRecipeRepository(context,activity,viewModel);
        recipeVM = new RecipeViewModel(context, activity,viewModel);
        userVM = new UserViewModel(context, activity,viewModel);
        notifyDataSetChanged();
    }

    public Adapter_RC_RecipeDt() {
    }

    @Override
    public Adapter_RC_RecipeDt.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_row, parent, false);
        return new Adapter_RC_RecipeDt.ViewHolder(view);
    }

    public void addRecipes(List<Recipe> newRecipes) {
        int start = recipes.size();
        recipes.addAll(newRecipes);
        notifyItemRangeInserted(start, newRecipes.size());
    }

    public void setRecipes(List<Recipe> newRecipes) {
        this.recipes.clear();          // نحيد القديم
        this.recipes.addAll(newRecipes); // نحط الجديد
        notifyDataSetChanged();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        holder.detail.setText(recipe.getNom_recipe());
        holder.txt_rate.setText(String.valueOf(recipe.getFav()));
        Constants.showImageRecipes(recipeVM,recipe,holder.Image,context);
        if (Objects.equals(b, TAG_LOCAL)) {
            holder.txt_time.setText("Local");
            holder.pin.setVisibility(View.VISIBLE);
        } else {
            holder.txt_time.setText("Remote");
            holder.pin.setVisibility(View.GONE);
        }

        holder.pin.setOnClickListener(view -> {
            FragmentActivity fragmentActivity = (FragmentActivity) view.getContext();
            flBtn = fragmentActivity.findViewById(R.id.floating_action_button);
            viewModel.setCurrentRecipe(recipe);
            viewModel.setTagEditRecipe(true);
            recipeVM.getFullRecipeLocal(recipe).observe(fragmentActivity, new Observer<RecipeResponse>() {
                @Override
                public void onChanged(RecipeResponse recipeResponse) {
                    if (recipeResponse != null) {
                        //viewPager2.setCurrentItem(1);
                        fetchRecipe(recipeResponse);
                        viewModel.setCurrentFullRecipe(recipeResponse);
                        flBtn.callOnClick();
                    }
                    Constants.dismissLoadingDialog();
                }
            });
        });

        holder.heat.setOnClickListener(view -> {
            Drawable pic = holder.heat.getDrawable().getCurrent();
            Toast.makeText(view.getContext(), "" + pic, Toast.LENGTH_SHORT).show();
            if (Acceuill_Frg.defaultImagelike.getConstantState().equals(holder.heat.getDrawable().getConstantState())) {
                holder.heat.setImageDrawable(Acceuill_Frg.defaultImagelike);
            } else {
                holder.heat.setImageDrawable(Acceuill_Frg.defaultImagelike);
                viewModel.getRecipesFavUser().add(recipe);
                favoritesRecipeRepository.Insert_Fav(viewModel.getUserLogin().getValue().getUser().getId_User(), recipe.getId_recipe());
            }
        });

        holder.Image.setOnClickListener(v -> {
            viewModel.setTagEditRecipe(false);
            // Get the FragmentActivity associated with the context of the clicked view
            FragmentActivity fragmentActivity = (FragmentActivity) v.getContext();
            if (viewModel.getCurrentRecipe().getValue() != recipe) {
                Constants.loading_ui(context, activity, "Chargement Recipe");
                if (!Objects.equals(b, TAG_LOCAL)) {
                    //CURRENT_RECIPE = recipe;
                    recipeVM.getFullRecipeApi(recipe.getId_recipe()).observe(fragmentActivity, new Observer<RecipeResponse>() {
                        @Override
                        public void onChanged(RecipeResponse recipe) {
                            if (recipe != null) {
                                //viewPager2.setCurrentItem(1);
                                fetchRecipe(recipe);
                                viewModel.setCurrentFullRecipe(recipe);
                                // Fetch nutrition for "apple"
                                fetchNutritionData(viewModel.getCurrentFullRecipe().getValue().getRecipe().getNom_recipe(), 100);
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
                                viewModel.setCurrentFullRecipe(recipeResponse);
                                viewModel.setUserCurrentRecipe(viewModel.getUserLogin().getValue().getUser());
                                fetchNutritionData(viewModel.getCurrentFullRecipe().getValue().getRecipe().getNom_recipe(), 100, "g");
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

            viewModel.getCurrentFullRecipe().getValue().setNutrition(nutrition);
            viewModel.setRemoteNutritions(nutrition);
            Log.e("nutrition", nutritionInfo);
            Log.e("nutrition", String.valueOf(nutrition.getCarbs()));

        } else {
            Log.e("nutrition", "Failed to fetch nutrition data.");
        }
    }

    private void fetchRecipe(RecipeResponse recipeResponse) {
        viewModel.setUserCurrentRecipe(recipeResponse.getUser());
        viewModel.setCurrentRecipe(recipeResponse.getRecipe());
        viewModel.setDetailCurrentRecipe(recipeResponse.getDetail_recipe());
        viewModel.setStepsCurrentRecipe(recipeResponse.getSteps());
        viewModel.setReviewCurrentRecipe(recipeResponse.getReviews());
        viewModel.setIngredientsCurrentRecipe(recipeResponse.getIngredients());
        viewModel.setFavoriteCurrentRecipe(recipeResponse.getFavs());
    }

    @Override
    public int getItemCount() {
        return recipes == null ? 0 : recipes.size();
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
