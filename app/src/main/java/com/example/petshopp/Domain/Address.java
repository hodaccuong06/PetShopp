package com.example.petshopp.Domain;

public class Address {
    String Name;
    String PhoneNumber;
    String Street;

    public Address() {
    }

    public Address(String name, String phoneNumber, String street) {
        Name = name;
        PhoneNumber = phoneNumber;
        Street = street;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getStreet() {
        return Street;
    }

    public void setStreet(String street) {
        Street = street;
    }

}
