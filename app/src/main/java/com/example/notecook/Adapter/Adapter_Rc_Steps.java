package com.example.notecook.Adapter;

import static com.example.notecook.Api.env.BASE_URL;
import static com.example.notecook.Utils.Constants.TAG_MY;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Activity.MainActivity;
import com.example.notecook.Model.Step;
import com.example.notecook.R;
import com.example.notecook.Utils.Constants;
import com.example.notecook.Utils.ImageHelper;
import com.example.notecook.ViewModel.RecipeViewModel;
import com.example.notecook.ViewModel.StepViewModel;
import com.google.android.material.textview.MaterialTextView;
import com.squareup.picasso.Picasso;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class Adapter_Rc_Steps extends RecyclerView.Adapter<Adapter_Rc_Steps.ViewHolder> {
    private List<Step> steps;
    private Context context;
    private StepViewModel stepVM;

    public Adapter_Rc_Steps(List<Step> steps,Context context) {
        this.steps = steps;
        this.context = context;
    }
    public Adapter_Rc_Steps(List<Step> steps,Context context,StepViewModel stepVM) {
        this.steps = steps;
        this.context = context;
        this.stepVM = stepVM;
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
        Constants.showImageSteps(stepVM,step,holder.imgStep);
        if(step.getImage_step()!=null && !step.getImage_step().isEmpty()) holder.imgStep.setImageBitmap(ImageHelper.loadImageFromPath(step.getImage_step()));
        holder.textViewTimeStep.setVisibility(View.GONE);
        holder.txtViewTimeStep.setText(String.valueOf(step.getTime_step()));
        holder.linearlayout.setVisibility(View.GONE);
        holder.linearlayoutPlay.setVisibility(View.GONE);
        holder.btn_del.setVisibility(View.GONE);

        if (TAG_MY) {
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
        ImageView imgStep;
        MaterialTextView txtViewTimeStep;
        LinearLayout linearlayout;
        LinearLayout linearlayoutPlay;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOrderStep = itemView.findViewById(R.id.order_step);
            textViewDetailStep = itemView.findViewById(R.id.detail_step);
            textViewTimeStep = itemView.findViewById(R.id.edit_time);
            btn_del = itemView.findViewById(R.id.btn_del_step);
            txtViewTimeStep = itemView.findViewById(R.id.txt_aff);
            imgStep = itemView.findViewById(R.id.img_step);
            linearlayout = itemView.findViewById(R.id.ly_scrole);
            linearlayoutPlay = itemView.findViewById(R.id.ly_play);
        }
    }

}

