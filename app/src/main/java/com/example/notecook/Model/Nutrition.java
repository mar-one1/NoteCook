package com.example.notecook.Model;

public class Nutrition {

    private String description;
    private double calories;
    private double protein;
    private double fat;
    private double carbs;
    private double servingSize;
    private String servingUnit;

    // Constructor with serving size and unit
    public Nutrition(String description, double calories, double protein, double fat, double carbs, double servingSize, String servingUnit) {
        this.description = description;
        this.calories = calories;
        this.protein = protein;
        this.fat = fat;
        this.carbs = carbs;
        this.servingSize = servingSize;
        this.servingUnit = servingUnit;
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

    public String getServingUnit() {
        return servingUnit;
    }

    public void setServingUnit(String servingUnit) {
        this.servingUnit = servingUnit;
    }
}
