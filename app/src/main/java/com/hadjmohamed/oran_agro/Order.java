package com.hadjmohamed.oran_agro;

import java.util.List;

public class Order {

    private String idOrder;
    private String idClient;
    private List<ProductOrder> productOrders;
    private float total;
    private String orderSituation;
    private String deliveryBoyId;

    public Order() {
    }

    public Order(String idOrder, String idClient, List<ProductOrder> productOrders, float total, String orderSituation, String deliveryBoyId) {
        this.idOrder = idOrder;
        this.idClient = idClient;
        this.productOrders = productOrders;
        this.total = total;
        this.orderSituation = orderSituation;
        this.deliveryBoyId = deliveryBoyId;
    }

    public String getIdOrder() {
        return idOrder;
    }

    public void setIdOrder(String idOrder) {
        this.idOrder = idOrder;
    }

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
    }

    public List<ProductOrder> getProductOrders() {
        return productOrders;
    }

    public void setProductOrders(List<ProductOrder> productOrders) {
        this.productOrders = productOrders;
    }

    public String getOrderSituation() {
        return orderSituation;
    }

    public void setOrderSituation(String orderSituation) {
        this.orderSituation = orderSituation;
    }

    public String getDeliveryBoyId() {
        return deliveryBoyId;
    }

    public void setDeliveryBoyId(String deliveryBoyId) {
        this.deliveryBoyId = deliveryBoyId;
    }

    public float getTotal() {
        return total;
    }

    public void setTotal(float total) {
        this.total = total;
    }
}
