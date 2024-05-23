package com.example.notecook.Model;

import com.google.gson.annotations.SerializedName;

public class Ingredients {

    @SerializedName("id")
    private int Id;
    @SerializedName("ingredient")
    private String Nome;
    @SerializedName("poidIngredient")
    private double Poid_unite;
    @SerializedName("unite")
    private String unit;
    @SerializedName("recipeId")
    private int Frk_recipe;
    private Double NutritionServing;

    public Ingredients() {
    }

    public Ingredients(String nome, double poid_unite) {
        Nome = nome;
        Poid_unite = poid_unite;
    }

    public int getFrk_recipe() {
        return Frk_recipe;
    }

    public void setFrk_recipe(int frk_recipe) {
        Frk_recipe = frk_recipe;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getNome() {
        return Nome;
    }

    public void setNome(String nome) {
        Nome = nome;
    }

    public double getPoid_unite() {
        return Poid_unite;
    }

    public void setPoid_unite(double poid_unite) {
        Poid_unite = poid_unite;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
