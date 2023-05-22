package com.example.traderfinder;

public class TradesmanUserDetails {
    private String firstName;
    private String lastName;
    private String location;
    private String phoneNumber;
    private String email;
    private String pastJobImageUrl;
    private String certificationImageUrl;

    public TradesmanUserDetails() {
        // Required empty public constructor
    }

    public TradesmanUserDetails(String firstName, String lastName, String location, String phoneNumber, String email, String pastJobImageUrl, String certificationImageUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.location = location;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.pastJobImageUrl = pastJobImageUrl;
        this.certificationImageUrl = certificationImageUrl;
    }

    // Getters and Setters
    // ...

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


    public String getPastJobImageUrl() {
        return pastJobImageUrl;
    }

    public void setPastJobImageUrl(String pastJobImageUrl) {
        this.pastJobImageUrl = pastJobImageUrl;
    }

    public String getCertificationImageUrl() {
        return certificationImageUrl;
    }

    public void setCertificationImageUrl(String certificationImageUrl) {
        this.certificationImageUrl = certificationImageUrl;
    }
}
