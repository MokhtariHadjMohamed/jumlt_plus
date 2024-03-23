package com.hadjmohamed.oran_agro;

import android.net.Uri;

import java.util.HashMap;
import java.util.Objects;

public class Product {

    private String idProduct;
    private int Quantite, IDCategorie;
    private Float PrixUnitaire, PrixCarton;
    private String NameProduct;

    public Product() {
    }

    public Product(String idProduct, int quantite, Float PrixUnitaire, Float PrixCarton,
                   int IDCategorie, String NameProduct) {
        this.idProduct = idProduct;
        this.Quantite = quantite;
        this.PrixUnitaire = PrixUnitaire;
        this.PrixCarton = PrixCarton;
        this.IDCategorie = IDCategorie;
        this.NameProduct = NameProduct;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public int getQuantite() {
        return Quantite;
    }

    public void setQuantite(int Quantite) {
        this.Quantite = Quantite;
    }

    public Float getPrixUnitaire() {
        return PrixUnitaire;
    }

    public void setPrixUnitaire(Float PrixUnitaire) {
        this.PrixUnitaire = PrixUnitaire;
    }

    public Float getPrixCarton() {
        return PrixCarton;
    }

    public void setPrixCarton(Float PrixCarton) {
        this.PrixCarton = PrixCarton;
    }

    public int getIDCategorie() {
        return IDCategorie;
    }

    public void setIDCategorie(int IDCategorie) {
        this.IDCategorie = IDCategorie;
    }

    public String getNameProduct() {
        return NameProduct;
    }

    public void setNameProduct(String NameProduct) {
        this.NameProduct = NameProduct;
    }

    public HashMap<String, Object> toHashMap(){
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("idProduct", this.idProduct);
        hashMap.put("NameProduct", this.NameProduct);
        hashMap.put("Quantite", this.Quantite);
        hashMap.put("PrixUnitaire", this.PrixUnitaire);
        hashMap.put("PrixCarton", this.PrixCarton);
        hashMap.put("IDCategorie", this.IDCategorie);
        return hashMap;
    }
}
