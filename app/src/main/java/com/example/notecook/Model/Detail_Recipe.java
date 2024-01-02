package com.example.notecook.Model;

import com.google.gson.annotations.SerializedName;

public class Detail_Recipe {

    @SerializedName("id")
    private int id_detail_recipe;
    @SerializedName("detail")
    private String Dt_recipe;
    @SerializedName("time")
    private int Time;
    @SerializedName("calories")
    private int cal;
    @SerializedName("rate")
    private int rate;
    @SerializedName("level")
    private String Level;
    @SerializedName("recipeId")
    private int Frk_recipe;

    public Detail_Recipe() {
    }



    public Detail_Recipe(int time, int cal, int rate, String level, int frk_recipe) {
        Time = time;
        this.cal = cal;
        this.rate = rate;
        Level = level;
        Frk_recipe = frk_recipe;
    }

    public Detail_Recipe(String dt_recipe, int time, int cal, int rate, String level, int frk_recipe) {
        Dt_recipe = dt_recipe;
        Time = time;
        this.cal = cal;
        this.rate = rate;
        Level = level;
        Frk_recipe = frk_recipe;
        Dt_recipe=dt_recipe;
    }

    public String getDt_recipe() {
        return Dt_recipe;
    }

    public void setDt_recipe(String dt_recipe) {
        Dt_recipe = dt_recipe;
    }

    public int getId_detail_recipe() {
        return id_detail_recipe;
    }

    public void setId_detail_recipe(int id_detail_recipe) {
        this.id_detail_recipe = id_detail_recipe;
    }

    public int getTime() {
        return Time;
    }

    public void setTime(int time) {
        Time = time;
    }

    public int getCal() {
        return cal;
    }

    public void setCal(int cal) {
        this.cal = cal;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }

    public int getFrk_recipe() {
        return Frk_recipe;
    }

    public void setFrk_recipe(int frk_recipe) {
        Frk_recipe = frk_recipe;
    }

    public String getLevel() {
        return Level;
    }

    public void setLevel(String level) {
        Level = level;
    }
}
