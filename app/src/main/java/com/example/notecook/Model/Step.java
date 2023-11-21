package com.example.notecook.Model;

import com.google.gson.annotations.SerializedName;

public class Step {
    @SerializedName("id")
    private int id_step;
    @SerializedName("detailStep")
    private String detail_step;
    @SerializedName("imageStep")
    private byte[] image_step;
    @SerializedName("timeStep")
    private int Time_step;
    @SerializedName("recipeId")
    private int FRK_recipe_step;

    public Step() {
    }

    public Step(String detail_step, byte[] image_step, int time_step, int FRK_recipe_step) {
        this.detail_step = detail_step;
        this.image_step = image_step;
        Time_step = time_step;
        this.FRK_recipe_step = FRK_recipe_step;
    }

    public int getId_step() {
        return id_step;
    }

    public void setId_step(int id_step) {
        this.id_step = id_step;
    }

    public String getDetail_step() {
        return detail_step;
    }

    public void setDetail_step(String detail_step) {
        this.detail_step = detail_step;
    }

    public byte[] getImage_step() {
        return image_step;
    }

    public void setImage_step(byte[] image_step) {
        this.image_step = image_step;
    }

    public int getTime_step() {
        return Time_step;
    }

    public void setTime_step(int time_step) {
        Time_step = time_step;
    }

    public int getFRK_recipe_step() {
        return FRK_recipe_step;
    }

    public void setFRK_recipe_step(int FRK_recipe_step) {
        this.FRK_recipe_step = FRK_recipe_step;
    }
}
