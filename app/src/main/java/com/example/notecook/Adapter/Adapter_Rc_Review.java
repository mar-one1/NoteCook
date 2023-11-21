package com.example.notecook.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notecook.Model.Review;
import com.example.notecook.R;

import java.util.List;

public class Adapter_Rc_Review extends RecyclerView.Adapter<Adapter_Rc_Review.ViewHolder> {

    private List<Review> reviews;
    private Context context;

    public Adapter_Rc_Review(List<Review> reviews, Context context) {
        this.reviews = reviews;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_row, parent, false);
        return new Adapter_Rc_Review.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Review review = reviews.get(position);

        holder.detail.setText(review.getDetail_Review_recipe());
        holder.rate.setText(String.valueOf(review.getRate_Review_recipe()));
        holder.idRecipe.setText(String.valueOf(review.getFRK_recipe()));
    }

    @Override
    public int getItemCount() {
        return reviews.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView detail, rate, idRecipe;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            detail = itemView.findViewById(R.id.textViewDetailReview);
            rate = itemView.findViewById(R.id.textViewRateReview);
            idRecipe = itemView.findViewById(R.id.textViewRecipeId);

        }

        public TextView getDetail() {
            return detail;
        }

        public void setDetail(TextView detail) {
            this.detail = detail;
        }

        public TextView getRate() {
            return rate;
        }

        public void setRate(TextView rate) {
            this.rate = rate;
        }

        public TextView getIdRecipe() {
            return idRecipe;
        }

        public void setIdRecipe(TextView idRecipe) {
            this.idRecipe = idRecipe;
        }
    }
}
