package com.marketapp.rob.markettus.Model;

public class Cart {

    private String pid, name, price, quantity, discount, sellerName, sid;

    public Cart() {

    }

    public Cart(String pid, String name, String price, String quantity, String discount, String sellerName, String sid) {
        this.pid = pid;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.discount = discount;
        this.sellerName = sellerName;
        this.sid = sid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getSellerName() {
        return sellerName;
    }

    public void setSellerName(String sellerName) {
        this.sellerName = sellerName;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }
}
