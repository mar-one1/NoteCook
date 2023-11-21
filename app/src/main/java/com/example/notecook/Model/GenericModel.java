package com.example.notecook.Model;

public class GenericModel<T> {
    // Define a field for the unique identifier
    private T uniqueIdentifier;

    // Constructor
    public GenericModel(T uniqueIdentifier) {
        this.uniqueIdentifier = uniqueIdentifier;
    }

    public GenericModel() {
    }

    // Generic method to get the unique identifier
    public T getUniqueIdentifier() {
        return uniqueIdentifier;
    }

    // Other methods and properties for your generic model
}