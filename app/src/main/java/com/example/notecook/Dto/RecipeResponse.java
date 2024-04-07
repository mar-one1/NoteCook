package com.example.notecook.Dto;

import com.example.notecook.Model.Detail_Recipe;
import com.example.notecook.Model.Ingredients;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.Review;
import com.example.notecook.Model.Step;
import com.example.notecook.Model.User;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeResponse {
    @SerializedName("recipe")
    private Recipe recipe;
    @SerializedName("user")
    private User user;
    @SerializedName("detailRecipe")
    private Detail_Recipe detail_recipe;
    @SerializedName("ingredients")
    private List<Ingredients> Ingredients;
    @SerializedName("reviews")
    private List<Review> Reviews;
    @SerializedName("steps")
    private List<Step> steps;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public Detail_Recipe getDetail_recipe() {
        return detail_recipe;
    }

    public void setDetail_recipe(Detail_Recipe detail_recipe) {
        this.detail_recipe = detail_recipe;
    }

    public List<Ingredients> getIngredients() {
        return Ingredients;
    }

    public void setIngredients(List<Ingredients> ingredients) {
        Ingredients = ingredients;
    }

    public List<Review> getReviews() {
        return Reviews;
    }

    public void setReviews(List<Review> reviews) {
        Reviews = reviews;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }
}
