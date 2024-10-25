package com.example.notecook.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Fragement.MainFragment;
import com.example.notecook.Model.Category_Recipe;
import com.example.notecook.R;
import com.example.notecook.Utils.ImageHelper;


import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class Adapter_RC_MenuCat extends RecyclerView.Adapter<Adapter_RC_MenuCat.ViewHolder> {

    private List<Category_Recipe> mCategoryRecipes;
    private boolean b=true;

    public Adapter_RC_MenuCat(List<Category_Recipe> categorie_foodsA, boolean bb) {
        mCategoryRecipes = categorie_foodsA;
        b=bb;
    }


    @Override
    public Adapter_RC_MenuCat.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;
        if(b)
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categorie_row, parent, false);
        else
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_row, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Category_Recipe categories = mCategoryRecipes.get(position);
        holder.detail.setText(categories.getDetail_category());
        holder.Image.setImageDrawable(categories.getIcon_category());

        holder.Image.setOnClickListener(v -> {
            MainFragment mainFragment = new MainFragment();
        });
    }

    @Override
    public int getItemCount() {
        return mCategoryRecipes.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView Image;
        TextView detail;
        //Button btn_del;
        private RecyclerView RvSVC;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            detail = itemView.findViewById(R.id.Detail_CatFood);
            Image = itemView.findViewById(R.id.ImgV_IconCatFood);

        }

        public TextView getDetail() {
            return detail;
        }

        public ImageView getImage() {
            return Image;
        }

        public void setDetail(TextView detail) {
            this.detail = detail;
        }

        public void setImage(ImageView image) {
            Image = image;
        }
    }
}




