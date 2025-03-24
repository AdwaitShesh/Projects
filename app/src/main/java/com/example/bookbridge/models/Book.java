package com.example.bookbridge.models;

import java.io.Serializable;

public class Book implements Serializable {
    private int id;
    private String title;
    private String author;
    private double price;
    private String description;
    private int imageResource; // For drawable resources
    private String imageUrl; // For real image URLs
    private boolean isWishlisted;
    private String category;

    public Book(int id, String title, String author, double price, String description, int imageResource, boolean isWishlisted) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.description = description;
        this.imageResource = imageResource;
        this.imageUrl = null; // No URL for this constructor
        this.isWishlisted = isWishlisted;
        this.category = "";
    }

    public Book(int id, String title, String author, double price, String description, int imageResource, boolean isWishlisted, String category) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.description = description;
        this.imageResource = imageResource;
        this.imageUrl = null; // No URL for this constructor
        this.isWishlisted = isWishlisted;
        this.category = category;
    }
    
    public Book(int id, String title, String author, double price, String description, String imageUrl, boolean isWishlisted, String category) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.price = price;
        this.description = description;
        this.imageResource = 0; // No resource ID for this constructor
        this.imageUrl = imageUrl;
        this.isWishlisted = isWishlisted;
        this.category = category;
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
    
    // Alias methods to maintain compatibility with different naming conventions
    public int getImageResourceId() {
        return getImageResource();
    }
    
    public void setImageResourceId(int imageResourceId) {
        setImageResource(imageResourceId);
    }
    
    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    
    // Helper method to check if book has a real image URL
    public boolean hasImageUrl() {
        return imageUrl != null && !imageUrl.isEmpty();
    }

    public boolean isWishlisted() {
        return isWishlisted;
    }

    public void setWishlisted(boolean wishlisted) {
        isWishlisted = wishlisted;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Check if the book has a valid image URL
     * @return true if the book has a non-empty image URL
     */
    public boolean hasValidImageUrl() {
        return imageUrl != null && !imageUrl.isEmpty();
    }
    
    /**
     * Check if the book has a valid image resource ID
     * @return true if the book has a non-zero image resource ID
     */
    public boolean hasValidImageResource() {
        return imageResource > 0;
    }
    
    /**
     * Get the most appropriate image source for the book
     * @return Image URL if available, otherwise null
     */
    public String getEffectiveImageSource() {
        if (hasValidImageUrl()) {
            return getImageUrl();
        } else if (hasValidImageResource()) {
            return "resource://" + getImageResource();
        }
        return null;
    }
} 