package com.example.tatsbytatspos.model;

public class OrderItem {
    private long itemId;
    private long orderId;
    private int productId;
    private int quantity;
    private double unitPrice;
    private double subtotal;
    private String productName; // For display purposes

    public OrderItem() {
    }

    public OrderItem(long orderId, int productId, int quantity, double unitPrice, String productName) {
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.productName = productName;
        calculateSubtotal();
    }

    public long getItemId() {
        return itemId;
    }

    public void setItemId(long itemId) {
        this.itemId = itemId;
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        calculateSubtotal();
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
        calculateSubtotal();
    }

    public double getSubtotal() {
        return subtotal;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    private void calculateSubtotal() {
        this.subtotal = this.quantity * this.unitPrice;
    }
}