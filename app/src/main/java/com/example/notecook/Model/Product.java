package com.example.notecook.Model;

public class Product {

    private int ProductID;
    private String ProductDescription;

    public Product() {
    }

    public Product(String productDescription) {
        ProductDescription = productDescription;
    }

    public int getProductID() {
        return ProductID;
    }

    public void setProductID(int productID) {
        ProductID = productID;
    }

    public String getProductDescription() {
        return ProductDescription;
    }

    public void setProductDescription(String productDescription) {
        ProductDescription = productDescription;
    }
}
