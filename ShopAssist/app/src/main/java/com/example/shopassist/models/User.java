package com.example.shopassist.models;

import java.io.Serializable;

public class User implements Serializable {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String role;
    private boolean verifiedShopper;

    public User(String id, String name, String email, String phone, String role, boolean verifiedShopper) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.role = role;
        this.verifiedShopper = verifiedShopper;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getRole() {
        return role;
    }

    public boolean isVerifiedShopper() {
        return verifiedShopper;
    }
}

