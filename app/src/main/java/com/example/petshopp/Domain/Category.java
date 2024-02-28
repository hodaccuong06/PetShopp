package com.example.petshopp.Domain;

public class Category {
    String Title;
    String Image;

    public Category() {
    }

    public Category(String title, String image) {
        Title = title;
        Image = image;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }


}
