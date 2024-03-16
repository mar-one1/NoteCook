package com.example.notecook.Adapter;


import static com.example.notecook.Api.ApiClient.BASE_URL;
import static com.example.notecook.Fragement.MainFragment.viewPager2;
import static com.example.notecook.MainActivity.Insert_Fav;
import static com.example.notecook.MainActivity.decod;
import static com.example.notecook.Utils.Constants.CURRENT_RECIPE;
import static com.example.notecook.Utils.Constants.Recipes_Fav_User;
import static com.example.notecook.Utils.Constants.TAG_LOCAL;
import static com.example.notecook.Utils.Constants.user_login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.MainActivity;
import com.example.notecook.Model.Recipe;
import com.example.notecook.R;
import com.example.notecook.Repo.RecipeRepository;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Adapter_RC_RecipeDt extends RecyclerView.Adapter<Adapter_RC_RecipeDt.ViewHolder> {

    private String b;
    private List<Recipe> recipes;
    private MainActivity m = new MainActivity();
    private RecipeRepository recipeRepo;

    public Adapter_RC_RecipeDt(Context context, List<Recipe> recipes1, String bb) {
        recipes = recipes1;
        b = bb;
        recipeRepo = new RecipeRepository(context);
    }


    @Override
    public Adapter_RC_RecipeDt.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_row, parent, false);
        return new Adapter_RC_RecipeDt.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe recipe = recipes.get(position);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable defaultImagelike = holder.itemView.getResources().getDrawable(R.drawable.ic_baseline_favorite_24);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable defaultImagenot = holder.itemView.getResources().getDrawable(R.drawable.ic_favorite_border_black_24dp);
        holder.detail.setText(recipe.getNom_recipe());
        holder.txt_rate.setText(String.valueOf(recipe.getFav()));
        if (recipe.getIcon_recipe() != null && !Arrays.toString(recipe.getIcon_recipe()).equals("")) {
            holder.Image.setImageBitmap(decod(recipe.getIcon_recipe()));
        } else if (recipe.getPathimagerecipe() != null) {
            //holder.Image.setImageBitmap(decod(recipe.getIcon_recipe()));
            String url = BASE_URL + "data/uploads/" + recipe.getPathimagerecipe();
            Picasso.get()
                    .load(url)
                    .error(R.drawable.eror_image_download)
                    .memoryPolicy(MemoryPolicy.NO_STORE)
                    .into(holder.Image);
        }

        if (Objects.equals(b, TAG_LOCAL))
            holder.txt_time.setText("Local");
        else holder.txt_time.setText("Remote");

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
            if (CURRENT_RECIPE != recipe) {
                CURRENT_RECIPE = recipe;
                recipeRepo.getFullRecipeApi(recipe.getId_recipe(), v.getContext());
            } else viewPager2.setCurrentItem(1, false);
        });

    }


    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView Image;
        TextView detail, txt_time, txt_rate;
        ImageView heat;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            detail = itemView.findViewById(R.id.nom_recipe);
            txt_time = itemView.findViewById(R.id.time_recipe);
            txt_rate = itemView.findViewById(R.id.rate_recipe);
            Image = itemView.findViewById(R.id.ImgV_IconCatFood);
            heat = itemView.findViewById(R.id.heart_recipe);

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
