package com.example.traderfinder;

public class UserDetails {

    private String firstName;
    private String lastName;
    private String location;
    private String phoneNumber;
    private String email;

    // No-argument constructor
    public UserDetails() {}

    public UserDetails(String firstName, String lastName, String location, String phoneNumber, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getLocation() {
        return location;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getEmail() {
        return email;
    }
}
