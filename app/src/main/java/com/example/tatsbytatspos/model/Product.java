package com.example.tatsbytatspos.model;

public class Product {

    private int imageResource;
    private String name;
    private double price;
    private int quantity;
    private byte[] image;

    public Product(int imageResource, String name, double price) {
        this.imageResource = imageResource;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public byte[] getImage() { return image; }

}
