package com.example.tatsbytatspos.model;

public class Orders {
    private String orderNumber;
    private int orderDate;
    private int orderTime;

    public Orders(String orderNumber, int orderDate, int orderTime) {
        this.orderNumber = orderNumber;
        this.orderDate = orderDate;
        this.orderTime = orderTime;
    }

    public String getOrderNumber() {
        return orderNumber;
    }
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public int getOrderDate() {
        return orderDate;
    }
    public void setOrderDate(int orderDate) {
        this.orderDate = orderDate;
    }

    public int getOrderTime() {
        return orderTime;
    }
    public void setOrderTime(int orderTime) {
        this.orderTime = orderTime;
    }
}