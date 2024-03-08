package com.example.notecook.Fragement;

import com.google.gson.annotations.SerializedName;

public class Favorite_User_Recipe {
    @SerializedName("favRecipe_id")
    private int id;
    @SerializedName("FRK_user")
    private int userId;
    @SerializedName("FRK_recipe")
    private int recipeId;

    // Constructors, getters, and setters
    public Favorite_User_Recipe() {}

    public Favorite_User_Recipe(int userId, int recipeId) {
        this.userId = userId;
        this.recipeId = recipeId;
    }

    // Getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(int recipeId) {
        this.recipeId = recipeId;
    }
}

