package com.example.bookbridge.models;

public class Review {
    private String reviewerName;
    private String reviewText;
    private float rating;
    private String date;

    public Review(String reviewerName, String reviewText, float rating, String date) {
        this.reviewerName = reviewerName;
        this.reviewText = reviewText;
        this.rating = rating;
        this.date = date;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public String getReviewText() {
        return reviewText;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
} 