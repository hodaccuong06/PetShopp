package com.example.petshopp.Domain;

public class SearchHistory {
    private String Keyword;
    private long Timestamp;
    private String phone;

    public SearchHistory(String keyword) {}

    public SearchHistory(String keyword, long timestamp) {
        Keyword = keyword;
        Timestamp = timestamp;
    }

    public String getKeyword() {
        return Keyword;
    }

    public void setKeyword(String keyword) {
        Keyword = keyword;
    }

    public long getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(long timestamp) {
        Timestamp = timestamp;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
