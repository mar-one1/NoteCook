package com.example.notecook.Model;

public class ServingSize {
    private int ProductID;
    private double GramsPerServing;

    public ServingSize() {
    }

    public ServingSize(double gramsPerServing) {
        GramsPerServing = gramsPerServing;
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    public double getGramsPerServing() {
        return GramsPerServing;
    }

    public void setGramsPerServing(double gramsPerServing) {
        GramsPerServing = gramsPerServing;
    }
}
