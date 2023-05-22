package com.example.traderfinder.Model;

import java.io.Serializable;

public class Quote implements Serializable {
    private String id;
    private String userEmail;
    private String message;
    private double price;
    private String jobId;
    private String userId;  // this field was missing

    public Quote() {
        // Default constructor required for calls to DataSnapshot.getValue(Quote.class)
    }

    public Quote(String id, String userEmail, String message, double price, String jobId, String userId) {
        this.id = id;
        this.userEmail = userEmail;
        this.message = message;
        this.price = price;
        this.jobId = jobId;
        this.userId = userId;
    }

    public String getId() {
        return id;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getMessage() {
        return message;
    }

    public double getPrice() {
        return price;
    }

    public String getJobId() {
        return jobId;
    }

    public String getUserId() {
        return userId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
