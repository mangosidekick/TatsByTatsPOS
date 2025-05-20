package com.example.tatsbytatspos.model;

public class Product {
    private int id;
    private String name;
    private double price;
    private int quantity;
    private int orderquantity;
    private byte[] image;
    private boolean hidden;

    public Product(int id, String name, double price, int quantity, byte[] image) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
        this.hidden = false;
    }

    public Product(int id, String name, double price, int quantity, byte[] image, boolean hidden) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.image = image;
        this.hidden = hidden;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setImage(byte[] image) {
        this.image = image;
    }

    public void setOrderQuantity(int orderquantity) {
        this.orderquantity = orderquantity;
    }

    public int getOrderQuantity() {
        return orderquantity;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isHidden() {
        return hidden;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public byte[] getImage() { return image; }
}