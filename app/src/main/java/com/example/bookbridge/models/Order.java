package com.example.bookbridge.models;

import java.util.Date;

public class Order {
    private String orderId;
    private Date orderDate;
    private String status;
    private String paymentMethod;

    public Order() {
        // Default constructor
    }

    public Order(String orderId, Date orderDate, String status, String paymentMethod) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.status = status;
        this.paymentMethod = paymentMethod;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
} 