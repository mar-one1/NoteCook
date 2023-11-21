package com.example.notecook.Model;

public class NutritionValues {

    private int ProductID;
    private int NutrionID;
    private double ValuePer100Grams;

    public NutritionValues() {
    }

    public NutritionValues(int nutrionID, double valuePer100Grams) {
        NutrionID = nutrionID;
        ValuePer100Grams = valuePer100Grams;
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    public int getNutrionID() {
        return NutrionID;
    }

    public void setNutrionID(int nutrionID) {
        NutrionID = nutrionID;
    }

    public double getValuePer100Grams() {
        return ValuePer100Grams;
    }

    public void setValuePer100Grams(double valuePer100Grams) {
        ValuePer100Grams = valuePer100Grams;
    }
}
