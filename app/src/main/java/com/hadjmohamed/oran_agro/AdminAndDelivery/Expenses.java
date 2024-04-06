package com.hadjmohamed.oran_agro.AdminAndDelivery;

public class Expenses {
    private String uid;
    private String name;
    private float price;

    public Expenses(String uid, String name, float price) {
        this.uid = uid;
        this.name = name;
        this.price = price;
    }

    public Expenses() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }
}
