package com.example.notecook.Model;

import com.google.gson.annotations.SerializedName;

public class Recipe {

    @SerializedName("id")
    private int id_recipe;
    @SerializedName("name")
    private String Nom_recipe;
    //@SerializedName("icon")
    private byte[] Icon_recipe;
    @SerializedName("fav")
    private int Fav;
    @SerializedName("userId")
    private int Frk_user;
    @SerializedName("icon")
    private String pathimagerecipe;
    @SerializedName("unique_key")
    private String unique_key_recipe;

    public Recipe() {
    }

    public Recipe(String nom_recipe, byte[] icon_recipe, int fav, int frk_user) {
        Nom_recipe = nom_recipe;
        Icon_recipe = icon_recipe;
        Fav = fav;
        Frk_user = frk_user;
    }

    public Recipe(String nom_recipe, byte[] icon_recipe, int fav, int frk_user ,String unique_key_recipe) {
        Nom_recipe = nom_recipe;
        Icon_recipe = icon_recipe;
        Fav = fav;
        Frk_user = frk_user;
        this.unique_key_recipe = unique_key_recipe;
    }

    public String getUnique_key_recipe() {
        return unique_key_recipe;
    }

    public void setUnique_key_recipe(String unique_key_recipe) {
        this.unique_key_recipe = unique_key_recipe;
    }

    public String getPathimagerecipe() {
        return pathimagerecipe;
    }

    public void setPathimagerecipe(String pathimagerecipe) {
        this.pathimagerecipe = pathimagerecipe;
    }

    public int getId_recipe() {
        return id_recipe;
    }

    public void setId_recipe(int id_recipe) {
        this.id_recipe = id_recipe;
    }

    public String getNom_recipe() {
        return Nom_recipe;
    }

    public void setNom_recipe(String nom_recipe) {
        Nom_recipe = nom_recipe;
    }

    public byte[] getIcon_recipe() {
        return Icon_recipe;
    }

    public void setIcon_recipe(byte[] icon_recipe) {
        Icon_recipe = icon_recipe;
    }

    public int getFav() {
        return Fav;
    }

    public void setFav(int fav) {
        Fav = fav;
    }

    public int getFrk_user() {
        return Frk_user;
    }

    public void setFrk_user(int frk_user) {
        Frk_user = frk_user;
    }

}
