package com.example.notecook.Adapter;

import static com.example.notecook.Utils.Constants.TAG_EDIT_RECIPE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Model.Ingredients;
import com.example.notecook.Model.Step;
import com.example.notecook.R;
import com.google.android.material.textview.MaterialTextView;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Rc_Steps extends RecyclerView.Adapter<Adapter_Rc_Steps.ViewHolder> {
    private List<Step> steps;
    private Context context;

    public Adapter_Rc_Steps(List<Step> steps,Context context) {
        this.steps = steps;
        this.context = context;
    }

    // Method to update the dataset with new data
    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Step> newData) {
        steps = new ArrayList<>();
        steps.addAll(newData);
        notifyDataSetChanged(); // Notify the adapter that the dataset has changed
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_frg_step_recipe, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Step step = steps.get(position);
        holder.textViewDetailStep.setText(step.getDetail_step());
        holder.textViewOrderStep.setText(steps.size() + "/" + (position + 1));
        holder.textViewTimeStep.setVisibility(View.GONE);
        holder.textViewTxtTimeStep.setText(String.valueOf(step.getTime_step()));
        holder.linearlayout.setVisibility(View.GONE);
        holder.linearlayoutPlay.setVisibility(View.GONE);

        if (TAG_EDIT_RECIPE) {
            holder.btn_del.setVisibility(View.VISIBLE);
        }else holder.btn_del.setVisibility(View.GONE);
        holder.btn_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                steps.remove(step);
                updateData(steps);
            }
        });
    }

    // Method to get the data list
    public List<Step> getDataList() {
        return steps;
    }
    @Override
    public int getItemCount() {
        return steps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDetailStep;
        TextView textViewOrderStep;
        EditText textViewTimeStep;
        Button btn_del;
        MaterialTextView textViewTxtTimeStep;
        LinearLayout linearlayout;
        LinearLayout linearlayoutPlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOrderStep = itemView.findViewById(R.id.order_step);
            textViewDetailStep = itemView.findViewById(R.id.detail_step);
            textViewTimeStep = itemView.findViewById(R.id.edit_time);
            btn_del = itemView.findViewById(R.id.btn_del_step);
            textViewTxtTimeStep = itemView.findViewById(R.id.txt_aff);
            linearlayout = itemView.findViewById(R.id.ly_scrole);
            linearlayoutPlay = itemView.findViewById(R.id.ly_play);
        }

        public Button getBtn_del() {
            return btn_del;
        }

        public void setBtn_del(Button btn_del) {
            this.btn_del = btn_del;
        }

        public TextView getTextViewDetailStep() {
            return textViewDetailStep;
        }

        public void setTextViewDetailStep(TextView textViewDetailStep) {
            this.textViewDetailStep = textViewDetailStep;
        }


        public TextView getTextViewOrderStep() {
            return textViewOrderStep;
        }

        public void setTextViewOrderStep(TextView textViewOrderStep) {
            this.textViewOrderStep = textViewOrderStep;
        }
    }

}

