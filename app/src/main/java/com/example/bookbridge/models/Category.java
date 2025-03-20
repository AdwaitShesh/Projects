package com.example.bookbridge.models;

public class Category {
    private String name;
    private int iconResId;
    private String count;

    public Category(String name, int iconResId, String count) {
        this.name = name;
        this.iconResId = iconResId;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public String getCount() {
        return count;
    }
} 