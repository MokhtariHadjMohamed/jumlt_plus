package com.hadjmohamed.oran_agro;

import android.net.Uri;

public class Product {

    private String idProduct;
    private int Quantite, PrixUnitaire, PrixCarton, IDCategorie;
    private Uri image;
    private String NameProduct, description;

    public Product() {
    }

    public Product(String idProduct, int quantite, int PrixUnitaire, int PrixCarton,
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

    public void setQuantite(int quantite) {
        Quantite = quantite;
    }

    public int getPrixUnitaire() {
        return PrixUnitaire;
    }

    public void setPrixUnitaire(int prixUnitaire) {
        PrixUnitaire = prixUnitaire;
    }

    public int getPrixCarton() {
        return PrixCarton;
    }

    public void setPrixCarton(int prixCarton) {
        PrixCarton = prixCarton;
    }

    public int getIDCategorie() {
        return IDCategorie;
    }

    public void setIDCategorie(int IDCategorie) {
        this.IDCategorie = IDCategorie;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public String getNameProduct() {
        return NameProduct;
    }

    public void setNameProduct(String nameProduct) {
        NameProduct = nameProduct;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}
