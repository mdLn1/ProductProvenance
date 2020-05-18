package com.example.productprovenance.classes;

public class Product {
    private String productName, productId;

    public Product() {
    }

    public Product(String productId, String productName) {
        this.productName = productName;
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String name) {
        this.productName = name;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String genre) {
        this.productId = genre;
    }
}
