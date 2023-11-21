package com.example.notecook.Model;

public class Nutrition {

    private int NutritionID;
    private String NutritionDescription;

    public Nutrition() {
    }

    public Nutrition(int nutritionID, String nutritionDescription) {
        NutritionID = nutritionID;
        NutritionDescription = nutritionDescription;
    }

    public int getNutritionID() {
        return NutritionID;
    }

    public void setNutritionID(int nutritionID) {
        NutritionID = nutritionID;
    }

    public String getNutritionDescription() {
        return NutritionDescription;
    }

    public void setNutritionDescription(String nutritionDescription) {
        NutritionDescription = nutritionDescription;
    }
}
