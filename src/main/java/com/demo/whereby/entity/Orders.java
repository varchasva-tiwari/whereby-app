package com.demo.whereby.entity;

import javax.persistence.*;

@Entity
public class Orders {
    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int Id;
    private String receipt;
    private String orderId;
    private int amount;
    @OneToOne(mappedBy = "orders", cascade = CascadeType.ALL)
    private OrdersDtls ordersDtls;
    @ManyToOne
    private User user;

    public Orders() {
    }

    public Orders(int id, String receipt, String orderId, int amount, OrdersDtls ordersDtls, User user) {
        Id = id;
        this.receipt = receipt;
        this.orderId = orderId;
        this.amount = amount;
        this.ordersDtls = ordersDtls;
        this.user = user;
    }

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getReceipt() {
        return receipt;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public OrdersDtls getOrdersDtls() {
        return ordersDtls;
    }

    public void setOrdersDtls(OrdersDtls ordersDtls) {
        this.ordersDtls = ordersDtls;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
