package com.example.tatsbytatspos.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private long orderId;
    private Date orderDate;
    private double totalAmount;
    private String status;
    private List<OrderItem> orderItems;

    public Order() {
        this.orderItems = new ArrayList<>();
        this.orderDate = new Date();
        this.status = "Pending";
    }

    public Order(long orderId, Date orderDate, double totalAmount, String status) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.totalAmount = totalAmount;
        this.status = status;
        this.orderItems = new ArrayList<>();
    }

    public long getOrderId() {
        return orderId;
    }

    public void setOrderId(long orderId) {
        this.orderId = orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }

    public void addOrderItem(OrderItem item) {
        this.orderItems.add(item);
        calculateTotal();
    }

    public void removeOrderItem(OrderItem item) {
        this.orderItems.remove(item);
        calculateTotal();
    }

    private void calculateTotal() {
        this.totalAmount = 0;
        for (OrderItem item : orderItems) {
            this.totalAmount += item.getSubtotal();
        }
    }
}