package com.hadjmohamed.oran_agro.AdminAndDelivery;

import com.hadjmohamed.oran_agro.Product;

import java.util.List;

public class Sale {
    private String uid;
    private String uidClient;
    private String uidEmployee;

    private List<Product> products;
    private int total;
    private int totalPayed;

    public Sale() {
    }

    public Sale(String uid, String uidClient, String uidEmployee, List<Product> products, int total, int totalPayed) {
        this.uid = uid;
        this.uidClient = uidClient;
        this.uidEmployee = uidEmployee;
        this.products = products;
        this.total = total;
        this.totalPayed = totalPayed;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUidClient() {
        return uidClient;
    }

    public void setUidClient(String uidClient) {
        this.uidClient = uidClient;
    }

    public String getUidEmployee() {
        return uidEmployee;
    }

    public void setUidEmployee(String uidEmployee) {
        this.uidEmployee = uidEmployee;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getTotalPayed() {
        return totalPayed;
    }

    public void setTotalPayed(int totalPayed) {
        this.totalPayed = totalPayed;
    }
}
