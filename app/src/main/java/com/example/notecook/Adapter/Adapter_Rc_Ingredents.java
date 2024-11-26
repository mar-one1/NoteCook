package com.example.notecook.Adapter;

import static com.example.notecook.Utils.Constants.TAG_EDIT_RECIPE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Model.Ingredients;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Rc_Ingredents extends RecyclerView.Adapter<Adapter_Rc_Ingredents.ViewHolder> {

    private List<Ingredients> listidIngredient = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;

    public Adapter_Rc_Ingredents(List<Ingredients> list_ingredients, Context context) {
        this.context = context;
        this.listidIngredient = list_ingredients;
        updateData(list_ingredients);
    }


    // Method to update the dataset with new data
    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Ingredients> newData) {
        listidIngredient = new ArrayList<>();
        listidIngredient.addAll(newData);
        notifyDataSetChanged(); // Notify the adapter that the dataset has changed
    }

    @Override
    public Adapter_Rc_Ingredents.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_ingredient_row, parent, false);
        return new Adapter_Rc_Ingredents.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Ingredients Ingredients = listidIngredient.get(position);
        holder.getDetail().setText(String.valueOf(Ingredients.getNome()));
        holder.getPoid().setText(String.valueOf(Ingredients.getPoid_unite()));
        String unitText = (Ingredients.getUnit() == null) ? "/unite" : "/" + Ingredients.getUnit();
        holder.getUnite().setText(unitText);
        holder.btn_del.setVisibility(View.GONE);
        CheckBox ckb = holder.itemView.findViewById(R.id.checkBox);
        ckb.setOnClickListener(view -> {
            if (ckb.isChecked()) {
                listidIngredient.add(Ingredients);
                Toast.makeText(holder.itemView.getContext(), "count : " + listidIngredient.size(), Toast.LENGTH_SHORT).show();
            } else {
                listidIngredient.remove(Ingredients);
                Toast.makeText(holder.itemView.getContext(), "count : " + listidIngredient.size(), Toast.LENGTH_SHORT).show();
            }
        });

        if (TAG_EDIT_RECIPE) {
            holder.btn_del.setVisibility(View.VISIBLE);
        }else holder.btn_del.setVisibility(View.GONE);

        holder.btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listidIngredient.remove(position);
                updateData(listidIngredient);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listidIngredient.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView poid, unite;
        CheckBox detail;
        ImageView btn_del;
        private RecyclerView RvSVC;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            detail = itemView.findViewById(R.id.checkBox);
            poid = itemView.findViewById(R.id.Poid_ing);
            unite = itemView.findViewById(R.id.unite);
            RvSVC = itemView.findViewById(R.id.recyclerViewIngredients);
            btn_del = itemView.findViewById(R.id.ImgV_delete);
        }

        public ImageView getBtn_del() {
            return btn_del;
        }

        public void setBtn_del(ImageView btn_del) {
            this.btn_del = btn_del;
        }

        public TextView getPoid() {
            return poid;
        }

        public void setPoid(TextView poid) {
            this.poid = poid;
        }

        public TextView getUnite() {
            return unite;
        }

        public void setUnite(TextView unite) {
            this.unite = unite;
        }

        public CheckBox getDetail() {
            return detail;
        }

        public void setDetail(CheckBox detail) {
            this.detail = detail;
        }
    }
}
