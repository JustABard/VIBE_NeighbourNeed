package com.example.shopassist.models;

public class StoreItem {

    private final long id;
    private final String name;
    private final String category;
    private final int quantity;
    private final String notes;
    private final double price;
    private final String imageUrl;

    public StoreItem(long id, String name, String category, int quantity, String notes, double price, String imageUrl) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.quantity = quantity;
        this.notes = notes;
        this.price = price;
        this.imageUrl = imageUrl;
    }

    public long getId() {
        return id;
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

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public RequestItem toRequestItem() {
        return new RequestItem(id, name, category, 1, notes, price, true, imageUrl);
    }
}
