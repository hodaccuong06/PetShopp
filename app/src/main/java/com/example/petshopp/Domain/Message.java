package com.example.petshopp.Domain;

public class Message {
    private String mobile;
    private final String message;
    private final String date;
    private final String time;

    public Message(String message, String date, String time) {
        // Required empty constructor for Firebase
        this.message = message;
        this.date = date;
        this.time = time;
    }

    public Message(String mobile, String message, String date, String time) {
        this.mobile = mobile;
        this.message = message;
        this.date = date;
        this.time = time;
    }

    public String getMobile() {
        return mobile;
    }

    public String getMessage() {
        return message;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }
}


