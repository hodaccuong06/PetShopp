package com.example.petshopp.Domain;

public class Product {
    private String Name;
    private String Shortdes;
    private String Fulldescription;
    private String Price;
    private String Image;
    private String MenuId;
    private String Slider;

    public Product() {
    }

    public Product(String name, String shortdes, String fulldescription, String price, String image, String menuId, String slider) {
        Name = name;
        Shortdes = shortdes;
        Fulldescription = fulldescription;
        Price = price;
        Image = image;
        MenuId = menuId;
        Slider = slider;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getShortdes() {
        return Shortdes;
    }

    public void setShortdes(String shortdes) {
        Shortdes = shortdes;
    }

    public String getFulldescription() {
        return Fulldescription;
    }

    public void setFulldescription(String fulldescription) {
        Fulldescription = fulldescription;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getMenuId() {
        return MenuId;
    }

    public void setMenuId(String menuId) {
        MenuId = menuId;
    }

    public String getSlider() {
        return Slider;
    }

    public void setSlider(String slider) {
        Slider = slider;
    }
}