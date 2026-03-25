package com.example.shopassist.models;

import java.io.Serializable;

public class RequestItem implements Serializable {

    private long storeItemId;
    private String name;
    private String category;
    private int quantity;
    private String notes;
    private double estimatedPrice;
    private boolean storeItem;
    private String imageUrl;

    public RequestItem(String name, String category, int quantity, String notes, double estimatedPrice, boolean storeItem) {
        this(0L, name, category, quantity, notes, estimatedPrice, storeItem, "");
    }

    public RequestItem(long storeItemId, String name, String category, int quantity, String notes,
                       double estimatedPrice, boolean storeItem, String imageUrl) {
        this.storeItemId = storeItemId;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.notes = notes;
        this.estimatedPrice = estimatedPrice;
        this.storeItem = storeItem;
        this.imageUrl = imageUrl;
    }

    public long getStoreItemId() {
        return storeItemId;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getNotes() {
        return notes;
    }

    public double getEstimatedPrice() {
        return estimatedPrice;
    }

    public boolean isStoreItem() {
        return storeItem;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getLineTotal() {
        return estimatedPrice * quantity;
    }
}
