package com.hadjmohamed.oran_agro;

public class User {
    private String idUser, name, familyName, address, email, invitation, type;
    private int phone;
    private double latitude, longitude;

    public User() {
    }

    public User(String idUser, String name, String familyName, String address,
                String email, int phone, String invitation, String type, double latitude, double longitude) {
        this.idUser = idUser;
        this.name = name;
        this.familyName = familyName;
        this.address = address;
        this.email = email;
        this.phone = phone;
        this.invitation = invitation;
        this.type = type;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhone() {
        return phone;
    }

    public void setPhone(int phone) {
        this.phone = phone;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getInvitation() {
        return invitation;
    }

    public void setInvitation(String invitation) {
        this.invitation = invitation;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "User{" +
                "idUser='" + idUser + '\'' +
                ", name='" + name + '\'' +
                ", familyName='" + familyName + '\'' +
                ", address='" + address + '\'' +
                ", invitation='" + invitation + '\'' +
                ", type='" + type + '\'' +
                ", phone=" + phone +
                '}';
    }
}
