package com.demo.whereby.entity;

import javax.persistence.*;

@Entity
public class OrdersDtls {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private int id;
        private String  razorpay_payment_id;
        private String  razorpay_order_id;
        private String  razorpay_signature;
        @OneToOne
        private Orders orders;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRazorpay_payment_id() {
        return razorpay_payment_id;
    }

    public void setRazorpay_payment_id(String razorpay_payment_id) {
        this.razorpay_payment_id = razorpay_payment_id;
    }

    public String getRazorpay_order_id() {
        return razorpay_order_id;
    }

    public void setRazorpay_order_id(String razorpay_order_id) {
        this.razorpay_order_id = razorpay_order_id;
    }

    public String getRazorpay_signature() {
        return razorpay_signature;
    }

    public void setRazorpay_signature(String razorpay_signature) {
        this.razorpay_signature = razorpay_signature;
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }

    public OrdersDtls(int id, String razorpay_payment_id, String razorpay_order_id, String razorpay_signature, Orders orders) {
        this.id = id;
        this.razorpay_payment_id = razorpay_payment_id;
        this.razorpay_order_id = razorpay_order_id;
        this.razorpay_signature = razorpay_signature;
        this.orders = orders;
    }

    public OrdersDtls() {
    }
}
