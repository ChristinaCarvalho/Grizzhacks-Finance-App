package com.example.finance_app.models;

public class Reward {
    private String id;
    private String name;
    private double price;
    private boolean isAchieved;

    public Reward() {
        // Default constructor for Firebase
    }

    public Reward(String id, String name, double price) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.isAchieved = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isAchieved() {
        return isAchieved;
    }

    public void setAchieved(boolean achieved) {
        isAchieved = achieved;
    }
}
