package com.hadjmohamed.oran_agro;

import android.net.Uri;

public class DeliveryBoy {

    private int id, phone;
    private String name, familyName, email, address;
    private Uri image;

    public DeliveryBoy(int id, int phone, String name, String familyName, String email, String address, Uri image) {
        this.id = id;
        this.phone = phone;
        this.name = name;
        this.familyName = familyName;
        this.email = email;
        this.address = address;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }
}
