package com.example.notecook.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Fragement.Frg_Step_Recipe;
import com.example.notecook.Model.Step;
import com.example.notecook.R;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public class Adapter_Rc_Steps extends RecyclerView.Adapter<Adapter_Rc_Steps.ViewHolder> {
    private List<Step> steps;

    public Adapter_Rc_Steps(List<Step> steps) {
        this.steps = steps;
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
        holder.textViewDetailStep.setText(steps.get(position).getDetail_step());
        holder.textViewOrderStep.setText(steps.size() + "/" + (position+1));
        holder.textViewTimeStep.setText(step.getTime_step());
    }

    @Override
    public int getItemCount() {
        return steps.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textViewDetailStep;
        TextView textViewTimeStep;
        TextView textViewOrderStep;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewOrderStep = itemView.findViewById(R.id.order_step);
            textViewDetailStep = itemView.findViewById(R.id.detail_step);
            textViewTimeStep = itemView.findViewById(R.id.edit_time);
        }
    }

}

