package com.example.notecook.Model;

import com.google.gson.annotations.SerializedName;

public class Ingredients {

    @SerializedName("id")
    private int Id;
    @SerializedName("ingredient")
    private String Nome;
    @SerializedName("poidIngredient")
    private double Poid_unite;
    private Double NutritionServing;


    public Ingredients() {
    }

    public Ingredients(String nome, double poid_unite) {
        Nome = nome;
        Poid_unite = poid_unite;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {Id = id;}

    public void setPoid_unite(double poid_unite) {Poid_unite = poid_unite;}

    public String getNome() {
        return Nome;
    }

    public double getPoid_unite() {
        return Poid_unite;
    }

    public void setNome(String nome) {
        Nome = nome;
    }


}
