package com.example.bookbridge.models;

public class Category {
    private String name;
    private int iconResource;

    public Category(String name, int iconResource) {
        this.name = name;
        this.iconResource = iconResource;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconResource() {
        return iconResource;
    }

    public void setIconResource(int iconResource) {
        this.iconResource = iconResource;
    }
} 