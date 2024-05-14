package com.example.notecook.Dto;

import com.example.notecook.Model.Detail_Recipe;
import com.example.notecook.Model.Ingredients;
import com.example.notecook.Model.Recipe;
import com.example.notecook.Model.Review;
import com.example.notecook.Model.Step;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeRequest {

    @SerializedName("recipe")
    private Recipe recipe;
    @SerializedName("detailRecipe")
    private Detail_Recipe detail_recipe;
    @SerializedName("ingredients")
    private List<Ingredients> Ingredients;
    @SerializedName("steps")
    private List<Step> steps;
    // Parameter to track if the data has been added locally
    private boolean addedToLocal;

    // Parameter to track if the data has been added remotely
    private boolean addedToRemote;

    public boolean isAddedToLocal() {
        return addedToLocal;
    }

    public void setAddedToLocal(boolean addedToLocal) {
        this.addedToLocal = addedToLocal;
    }

    public boolean isAddedToRemote() {
        return addedToRemote;
    }

    public void setAddedToRemote(boolean addedToRemote) {
        this.addedToRemote = addedToRemote;
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

    public List<Step> getSteps() {
        return steps;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

}
