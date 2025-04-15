package com.example.tatsbytatspos;

public class Orders {

    private String order;
    private int date;
    private int time;

    public Orders(String order, int date, int time) {
        this.order = order;
        this.date = date;
        this.time= time;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
