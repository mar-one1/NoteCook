package com.example.notecook.Model;

public class Ingredients {

    private int Id;
    private String Nome;
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
