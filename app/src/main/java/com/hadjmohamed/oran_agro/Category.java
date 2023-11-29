package com.hadjmohamed.oran_agro;

public class Category {

    private int idSubCategory;
    private String Name;
    private int idCategory;

    private String code;

    public Category() {
    }

    public Category(String name, int idCategory, String code) {
        Name = name;
        this.idCategory = idCategory;
        this.code = code;
    }

    public Category(int idSubCategory, String Name, int idCategory) {
        this.idSubCategory = idSubCategory;
        this.Name = Name;
        this.idCategory = idCategory;
    }

    public int getIdSubCategory() {
        return idSubCategory;
    }

    public void setIdSubCategory(int idSubCategory) {
        this.idSubCategory = idSubCategory;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getIdCategory() {
        return idCategory;
    }

    public void setIdCategory(int idCategory) {
        this.idCategory = idCategory;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
