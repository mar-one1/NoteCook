package com.example.notecook.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Model.Nutrition;
import com.example.notecook.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;


public class Adapter_RC_Nutrition extends RecyclerView.Adapter<Adapter_RC_Nutrition.ViewHolder> {

    private Context context;
    private List<Nutrition> nutrition ;

    public Adapter_RC_Nutrition(List<Nutrition> nutrition,Context context) {
        this.context = context;
        this.nutrition = nutrition;
    }


    @Override
    public ViewHolder onCreateViewHolder(@androidx.annotation.NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.nutrition_row, parent, false);
        return new Adapter_RC_Nutrition.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@androidx.annotation.NonNull ViewHolder holder, int position) {
        Nutrition nutrition1 = nutrition.get(position);

        if (nutrition1 == null) {
            // Handle the null case, e.g., set default values or skip binding
            holder.nut_cal.setText("N/A");
            holder.nut_Pro.setText("N/A");
            holder.nut_Fat.setText("N/A");
            holder.nut_carb.setText("N/A");
            return;
        }

        // Populate the views with the actual data
        holder.nut_cal.setText(String.format("%.2f", nutrition1.getCalories()));
        holder.nut_Pro.setText(String.format("%.2f", nutrition1.getProtein()));
        holder.nut_Fat.setText(String.format("%.2f", nutrition1.getFat()));
        holder.nut_carb.setText(String.format("%.2f", nutrition1.getCarbs()));
    }


    @Override
    public int getItemCount() {
        return nutrition.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        Button nut_cal,nut_Pro,nut_Fat,nut_carb;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nut_cal    = itemView.findViewById(R.id.Nutrition_cal);
            nut_Pro = itemView.findViewById(R.id.Nutrition_Pro);
            nut_Fat = itemView.findViewById(R.id.Nutrition_Fat);
            nut_carb = itemView.findViewById(R.id.Nutrition_carb);
        }

        public Button getNut_cal() {
            return nut_cal;
        }

        public void setNut_cal(Button nut_cal) {
            this.nut_cal = nut_cal;
        }

        public Button getNut_Fat() {
            return nut_Fat;
        }

        public void setNut_Fat(Button nut_Fat) {
            this.nut_Fat = nut_Fat;
        }

        public Button getNut_Pro() {
            return nut_Pro;
        }

        public void setNut_Pro(Button nut_Pro) {
            this.nut_Pro = nut_Pro;
        }

        public Button getNut_carb() {
            return nut_carb;
        }

        public void setNut_carb(Button nut_carb) {
            this.nut_carb = nut_carb;
        }
    }

}
