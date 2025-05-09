package com.hadjmohamed.oran_agro;

public class ProductOrder {
    private String idOrder;
    private String idProduct;
    private String idClient;
    private int quantity;
    private String orderSituation;
    private String productName;
    private Float productPrice;

    public ProductOrder() {
    }

    public ProductOrder(String idOrder, String idProduct, String idClient,
                        int quantity, String orderSituation,
                        String productName, Float productPrice) {
        this.idOrder = idOrder;
        this.idProduct = idProduct;
        this.idClient = idClient;
        this.quantity = quantity;
        this.orderSituation = orderSituation;
        this.productName = productName;
        this.productPrice = productPrice;
    }

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public String getIdProduct() {
        return idProduct;
    }

    public void setIdProduct(String idProduct) {
        this.idProduct = idProduct;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getOrderSituation() {
        return orderSituation;
    }

    public void setOrderSituation(String orderSituation) {
        this.orderSituation = orderSituation;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Float getProductPrice() {
        return productPrice;
    }

    public void setProductPrice(Float productPrice) {
        this.productPrice = productPrice;
    }

    @Override
    public String toString() {
        return "ProductOrder{" +
                "idOrder='" + idOrder + '\'' +
                ", idProduct=" + idProduct +
                ", idClient='" + idClient + '\'' +
                ", quantity=" + quantity +
                ", orderSituation='" + orderSituation + '\'' +
                ", productName='" + productName + '\'' +
                ", productPrice=" + productPrice +
                '}';
    }
}
