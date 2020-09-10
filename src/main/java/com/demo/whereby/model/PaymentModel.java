package com.demo.whereby.model;

public class PaymentModel {
    public String orderId;
    public String amount;
    public String receipt;
    public String numberOfMeeting;

    public PaymentModel() {
    }

    public PaymentModel(String orderId, String amount, String receipt, String numberOfMeeting) {
        this.orderId = orderId;
        this.amount = amount;
        this.receipt = receipt;
        this.numberOfMeeting = numberOfMeeting;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getReceipt() {
        return receipt;
    }

    public String getNumberOfMeeting() {
        return numberOfMeeting;
    }

    public void setNumberOfMeeting(String numberOfMeeting) {
        this.numberOfMeeting = numberOfMeeting;
    }

    public void setReceipt(String receipt) {
        this.receipt = receipt;
    }
}
