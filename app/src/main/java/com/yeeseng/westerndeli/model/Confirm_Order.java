package com.yeeseng.westerndeli.model;

public class Confirm_Order {

    private String Address;
    private int EstimateTime;
    private String OrderTime;
    private int TotalPortions;
    private double TotalPrice;

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public int getEstimateTime() {
        return EstimateTime;
    }

    public void setEstimateTime(int estimateTime) {
        EstimateTime = estimateTime;
    }

    public String getOrderTime() {
        return OrderTime;
    }

    public void setOrderTime(String orderTime) {
        OrderTime = orderTime;
    }

    public int getTotalPortions() {
        return TotalPortions;
    }

    public void setTotalPortions(int totalPortions) {
        TotalPortions = totalPortions;
    }

    public double getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        TotalPrice = totalPrice;
    }
}
