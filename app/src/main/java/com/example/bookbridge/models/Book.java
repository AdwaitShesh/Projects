package com.example.bookbridge.models;

import java.io.Serializable;

public class Book implements Serializable {
    private int id;
    private String title;
    private String author;
    private double price;
    private String description;
    private int imageResource;
    private boolean isWishlisted;

    public Book(int id, String title, String author, double price, String description, int imageResource, boolean isWishlisted) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.description = description;
        this.imageResource = imageResource;
        this.isWishlisted = isWishlisted;
    }

    // Getters and Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getImageResource() {
        return imageResource;
    }

    public void setImageResource(int imageResource) {
        this.imageResource = imageResource;
    }

    public boolean isWishlisted() {
        return isWishlisted;
    }

    public void setWishlisted(boolean wishlisted) {
        isWishlisted = wishlisted;
    }
} 