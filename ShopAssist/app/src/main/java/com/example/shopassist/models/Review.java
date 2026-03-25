package com.example.shopassist.models;

import java.io.Serializable;

public class Review implements Serializable {

    private String requestId;
    private String customerId;
    private String shopperId;
    private int rating;
    private String thankYouMessage;

    public Review(String requestId, String customerId, String shopperId, int rating, String thankYouMessage) {
        this.requestId = requestId;
        this.customerId = customerId;
        this.shopperId = shopperId;
        this.rating = rating;
        this.thankYouMessage = thankYouMessage;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getShopperId() {
        return shopperId;
    }

    public int getRating() {
        return rating;
    }

    public String getThankYouMessage() {
        return thankYouMessage;
    }
}

