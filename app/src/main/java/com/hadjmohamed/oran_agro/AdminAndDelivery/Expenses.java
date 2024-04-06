package com.hadjmohamed.oran_agro.AdminAndDelivery;

public class Expenses {
    private String uid, userUid;
    private String name;
    private float price;
    private String type;

    public Expenses(String uid, String userUid, String name, float price, String type) {
        this.uid = uid;
        this.userUid = userUid;
        this.name = name;
        this.price = price;
        this.type = type;
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

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
