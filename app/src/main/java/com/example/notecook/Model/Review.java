package com.example.notecook.Model;

import com.google.gson.annotations.SerializedName;

public class Review {

    @SerializedName("id")
    private int id_review;
    @SerializedName("detailReview")
    private String Detail_Review_recipe;
    @SerializedName("rateReview")
    private float Rate_Review_recipe;
    @SerializedName("recipeId")
    private int FRK_recipe;

    public Review() {
    }

    public Review(String detail_Review_recipe, float rate_Review_recipe, int FRK_recipe) {
        Detail_Review_recipe = detail_Review_recipe;
        Rate_Review_recipe = rate_Review_recipe;
        this.FRK_recipe = FRK_recipe;
    }

    public int getId_review() {
        return id_review;
    }

    public void setId_review(int id_review) {
        this.id_review = id_review;
    }

    public String getDetail_Review_recipe() {
        return Detail_Review_recipe;
    }

    public void setDetail_Review_recipe(String detail_Review_recipe) {
        Detail_Review_recipe = detail_Review_recipe;
    }

    public int getFRK_recipe() {
        return FRK_recipe;
    }

    public void setFRK_recipe(int FRK_recipe) {
        this.FRK_recipe = FRK_recipe;
    }

    public float getRate_Review_recipe() {
        return Rate_Review_recipe;
    }

    public void setRate_Review_recipe(float rate_Review_recipe) {
        Rate_Review_recipe = rate_Review_recipe;
    }
}
