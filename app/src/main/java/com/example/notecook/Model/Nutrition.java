package com.example.notecook.Model;

import com.google.gson.annotations.SerializedName;

public class Nutrition {

    private String description;
    private double calories;
    private String caloriesUnit;
    private double protein;
    private String proteinUnit;
    private double fat;
    private String fatUnit;
    private double carbs;
    private String carbsUnit;
    @SerializedName("servingSize")
    private double servingSize;
    @SerializedName("servingSizeUnit")
    private String servingSizeUnit;

    // Constructor with serving size and unit
    public Nutrition(String description, double calories, double protein, double fat, double carbs, double servingSize, String servingUnit) {
        this.description = description;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.servingSize = servingSize;
        this.servingSizeUnit = servingUnit;
    }

    public Nutrition(String description, double calories, String caloriesUnit, double protein,String proteinUnit, double fat,String fatUnit, double carbs, String carbsUnit, double servingSize, String servingSizeUnit) {
        this.description = description;
        this.calories = calories;
        this.caloriesUnit = caloriesUnit;
        this.protein = protein;
        this.fat = fat;
        this.proteinUnit = proteinUnit;
        this.fatUnit = fatUnit;
        this.carbs = carbs;
        this.carbsUnit = carbsUnit;
        this.servingSize = servingSize;
        this.servingSizeUnit = servingSizeUnit;
    }

    // Scale nutrition to a different serving size
    public Nutrition scaleToServing(double newServingSize, String newUnit) {
        double scalingFactor = newServingSize / this.servingSize;

        return new Nutrition(
                this.description,
                this.calories * scalingFactor,
                this.protein * scalingFactor,
                this.fat * scalingFactor,
                this.carbs * scalingFactor,
                newServingSize,
                newUnit // New unit can be set here if needed
        );
    }
    public Nutrition() {
    }

    public String getCaloriesUnit() {
        return caloriesUnit;
    }

    public String getProteinUnit() {
        return proteinUnit;
    }

    public String getFatUnit() {
        return fatUnit;
    }

    public String getCarbsUnit() {
        return carbsUnit;
    }

    // Getters and Setters
    public String getDescription() {
        return description;
    }

    public double getCalories() {
        return calories;
    }

    public double getProtein() {
        return protein;
    }

    public double getFat() {
        return fat;
    }

    public double getCarbs() {
        return carbs;
    }

    public double getServingSize() {
        return servingSize;
    }

    public void setServingSize(double servingSize) {
        this.servingSize = servingSize;
    }

    public String getServingSizeUnit() {
        return servingSizeUnit;
    }

    public void setServingSizeUnit(String servingSizeUnit) {this.servingSizeUnit = servingSizeUnit;
    }

}
