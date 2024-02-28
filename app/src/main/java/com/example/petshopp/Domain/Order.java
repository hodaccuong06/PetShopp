package com.example.petshopp.Domain;

import java.util.List;

public class Order {
    public String name;
    private String phonenumber;
    private String address;
    private double totalAmount;
    private double shipper;
    public double sumtotalAmount;
    private List<CartItem> products;
    private long timestamp;
    private String tinhTrang;
    String phone, orderId;

    public Order() {
    }

    public Order( String phone, String orderId, String name, String phonenumber, String address, double totalAmount, double shipper, double sumtotalAmount, List<CartItem> products, long timestamp, String tinhTrang) {
        this.name = name;
        this.phonenumber = phonenumber;
        this.address = address;
        this.totalAmount = totalAmount;
        this.shipper = shipper;
        this.sumtotalAmount = sumtotalAmount;
        this.products = products;
        this.timestamp = timestamp;
        this.tinhTrang = tinhTrang;
        this.phone = phone;
        this.orderId = orderId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public double getShipper() {
        return shipper;
    }

    public void setShipper(double shipper) {
        this.shipper = shipper;
    }

    public double getSumtotalAmount() {
        return sumtotalAmount;
    }

    public void setSumtotalAmount(double sumtotalAmount) {
        this.sumtotalAmount = sumtotalAmount;
    }

    public List<CartItem> getProducts() {
        return products;
    }

    public void setProducts(List<CartItem> products) {
        this.products = products;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getTinhTrang() {
        return tinhTrang;
    }

    public void setTinhTrang(String tinhTrang) {
        this.tinhTrang = tinhTrang;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}

