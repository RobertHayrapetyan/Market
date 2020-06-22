package com.marketapp.rob.markettus.Model;

public class Products {

    private String name;
    private String image;
    private String price;
    private String date;
    private String time;
    private String pid;
    private String category;
    private String description;
    private String sellerName;
    private String sid;
    private String state;

    public Products() {

    }

    public Products(String name, String image, String price, String date, String time, String pid, String category, String description, String sellerName, String sid, String state) {
        this.name = name;
        this.image = image;
        this.price = price;
        this.date = date;
        this.time = time;
        this.pid = pid;
        this.category = category;
        this.description = description;
        this.sellerName = sellerName;
        this.sid = sid;
        this.state = state;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}