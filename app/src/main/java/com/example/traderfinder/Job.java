package com.example.traderfinder;

import java.io.Serializable;

public class Job implements Serializable {
    private String id;
    private String jobTitle;
    private String jobDescription;
    private String jobStartDate;
    private String jobEndDate;
    private String jobLocation;
    private String trade;
    private String jobImage;
    private String jobStatus;
    private String userId;
    private String tradesmanId;
    private double acceptedQuotePrice; // New field
    private boolean isCompleted; // New field


    public Job() {
        // Default constructor required for calls to DataSnapshot.getValue(Job.class)
        this.tradesmanId = null;
        this.acceptedQuotePrice = 0; // Default value is 0
        this.isCompleted = false; // Default value is false
    }

    public Job(String id, String jobTitle, String jobDescription, String jobStartDate, String jobEndDate, String jobLocation, String trade, String jobImage, String jobStatus, String userId, String tradesmanId, double acceptedQuotePrice, boolean isCompleted) {
        this.id = id;
        this.jobTitle = jobTitle;
        this.jobDescription = jobDescription;
        this.jobStartDate = jobStartDate;
        this.jobEndDate = jobEndDate;
        this.jobLocation = jobLocation;
        this.trade = trade;
        this.jobImage = jobImage;
        this.jobStatus = jobStatus;
        this.userId = userId;
        this.tradesmanId = tradesmanId;
        this.acceptedQuotePrice = acceptedQuotePrice;
        this.isCompleted = isCompleted;
    }


    public String getId() {
        return id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public String getJobStartDate() {
        return jobStartDate;
    }

    public String getJobEndDate() {
        return jobEndDate;
    }

    public String getJobLocation() {
        return jobLocation;
    }

    public String getTrade() {
        return trade;
    }

    public String getJobImage() {
        return jobImage;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public String getUserId() {
        return userId;
    }

    public String getTradesmanId() {  // New getter
        return tradesmanId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public void setJobStartDate(String jobStartDate) {
        this.jobStartDate = jobStartDate;
    }

    public void setJobEndDate(String jobEndDate) {
        this.jobEndDate = jobEndDate;
    }

    public void setJobLocation(String jobLocation) {
        this.jobLocation = jobLocation;
    }

    public void setTrade(String trade) {
        this.trade = trade;
    }

    public void setJobImage(String jobImage) {
        this.jobImage = jobImage;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setTradesmanId(String tradesmanId) {  // New setter
        this.tradesmanId = tradesmanId;
    }

    public double getAcceptedQuotePrice() {
        return acceptedQuotePrice;
    }

    public void setAcceptedQuotePrice(double acceptedQuotePrice) {
        this.acceptedQuotePrice = acceptedQuotePrice;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
}
