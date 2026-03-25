package com.example.shopassist.models;

import java.io.Serializable;
import java.util.ArrayList;

public class ShoppingRequest implements Serializable {

    public static final String STATUS_POSTED = "Posted";
    public static final String STATUS_ACCEPTED = "Accepted";
    public static final String STATUS_SHOPPING_STARTED = "Shopping Started";
    public static final String STATUS_OUT_FOR_DELIVERY = "Out for Delivery";
    public static final String STATUS_DELIVERED = "Delivered";
    public static final String STATUS_COMPLETED = "Completed";
    public static final String STATUS_CANCELLED = "Cancelled";

    private String requestId;
    private String customerName;
    private String customerEmail;
    private String shopperId;
    private String shopperName;
    private ArrayList<RequestItem> items;
    private String deliverySlot;
    private String locationText;
    private String status;
    private double estimatedTotal;
    private String customerNotes;

    public ShoppingRequest(String requestId, String customerName, String customerEmail,
                           ArrayList<RequestItem> items, String deliverySlot, String locationText,
                           String status, double estimatedTotal, String customerNotes) {
        this.requestId = requestId;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.items = items;
        this.deliverySlot = deliverySlot;
        this.locationText = locationText;
        this.status = status;
        this.estimatedTotal = estimatedTotal;
        this.customerNotes = customerNotes;
        this.shopperId = "";
        this.shopperName = "";
    }

    public String getRequestId() {
        return requestId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getShopperId() {
        return shopperId;
    }

    public void setShopperId(String shopperId) {
        this.shopperId = shopperId;
    }

    public String getShopperName() {
        return shopperName;
    }

    public void setShopperName(String shopperName) {
        this.shopperName = shopperName;
    }

    public ArrayList<RequestItem> getItems() {
        return items;
    }

    public String getDeliverySlot() {
        return deliverySlot;
    }

    public String getLocationText() {
        return locationText;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getEstimatedTotal() {
        return estimatedTotal;
    }

    public String getCustomerNotes() {
        return customerNotes;
    }
}

