package com.example.notecook.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Model.Ingredients;
import com.example.notecook.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Rc_Ingredents extends RecyclerView.Adapter<Adapter_Rc_Ingredents.ViewHolder> {

    private List<Ingredients> list_ingredients;
    private ArrayList<Ingredients> listidIngredient =new ArrayList<>();

    public Adapter_Rc_Ingredents(List<Ingredients> list_ingredients) {
        this.list_ingredients = list_ingredients;
    }


    @Override
    public Adapter_Rc_Ingredents.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_ingredient_row, parent, false);
        return new Adapter_Rc_Ingredents.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(Adapter_Rc_Ingredents.@NonNull ViewHolder holder, int position) {
        Ingredients Ingredients = list_ingredients.get(position);

        holder.getDetail().setText(String.valueOf(Ingredients.getNome()));
        holder.getPoid().setText(String.valueOf(Ingredients.getPoid_unite()));
        //holder.getUnite().setText(String.valueOf(Ingredients.get()));

        CheckBox ckb = holder.itemView.findViewById(R.id.checkBox);
        ckb.setOnClickListener(view -> {
             if(ckb.isChecked())
             {
                 listidIngredient.add(Ingredients);
                 Toast.makeText(holder.itemView.getContext(), "count : "+listidIngredient.size(), Toast.LENGTH_SHORT).show();
             }
             else {
                 listidIngredient.remove(Ingredients);
                 Toast.makeText(holder.itemView.getContext(), "count : "+listidIngredient.size(), Toast.LENGTH_SHORT).show();
             }
        });
    }

    @Override
    public int getItemCount() {
        return list_ingredients.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView poid, unite;
        CheckBox detail;
        //Button btn_del;
        private RecyclerView RvSVC;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            detail = itemView.findViewById(R.id.checkBox);
            poid = itemView.findViewById(R.id.Poid_ing);
            unite = itemView.findViewById(R.id.unite);
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
