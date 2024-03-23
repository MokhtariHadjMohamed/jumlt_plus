package com.hadjmohamed.oran_agro.AdminAndDelivery;

import com.hadjmohamed.oran_agro.User;

import java.util.HashMap;

public class Client extends User {

    private float totalDebt, paidDebt;

    public Client(String idUser, String name, String familyName, String address, String email, int phone, String invitation, String type, double latitude, double longitude, float totalDebt, float paidDebt) {
        super(idUser, name, familyName, address, email, phone, invitation, type, latitude, longitude);
        this.totalDebt = totalDebt;
        this.paidDebt = paidDebt;
    }

    public Client() {
    }

    public float getTotalDebt() {
        return totalDebt;
    }

    public void setTotalDebt(float totalDebt) {
        this.totalDebt = totalDebt;
    }

    public float getPaidDebt() {
        return paidDebt;
    }

    public void setPaidDebt(float paidDebt) {
        this.paidDebt = paidDebt;
    }

}
