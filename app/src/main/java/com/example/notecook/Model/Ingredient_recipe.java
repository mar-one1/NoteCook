package com.example.notecook.Model;

import com.google.gson.annotations.SerializedName;

public class Ingredient_recipe {

    @SerializedName("id")
    private int id_Ingeredient_recipe;
    @SerializedName("recipeId")
    private int Frk_idRecipe;
    private int Frk_idIngredient;

    public Ingredient_recipe() {
    }

    public Ingredient_recipe(int frk_idRecipe, int frk_idIngredient) {
        Frk_idRecipe = frk_idRecipe;
        Frk_idIngredient = frk_idIngredient;
    }

    public void setId_Ingeredient_recipe(int id_Ingeredient_recipe) {
        this.id_Ingeredient_recipe = id_Ingeredient_recipe;
    }

    public int getId_Ingeredient_recipe() {
        return id_Ingeredient_recipe;
    }

    public int getFrk_idRecipe() {
        return Frk_idRecipe;
    }

    public void setFrk_idRecipe(int frk_idRecipe) {
        Frk_idRecipe = frk_idRecipe;
    }

    public int getFrk_idIngredient() {
        return Frk_idIngredient;
    }

    public void setFrk_idIngredient(int frk_idIngredient) {
        Frk_idIngredient = frk_idIngredient;
    }
}
