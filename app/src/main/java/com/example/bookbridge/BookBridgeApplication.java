package com.example.bookbridge;

import android.app.Application;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.MemoryCategory;

/**
 * Main Application class for BookBridge
 * Handles application-wide initialization
 */
public class BookBridgeApplication extends Application {
    
    private static final String TAG = "BookBridgeApplication";
    
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Glide with optimal configuration
        initializeGlide();
        
        Log.d(TAG, "Application initialized");
    }
    
    /**
     * Initialize Glide with optimal configuration for image loading
     */
    private void initializeGlide() {
        try {
            // Set higher memory category for better caching
            Glide.get(this).setMemoryCategory(MemoryCategory.HIGH);
            
            // Pre-load placeholder images
            Glide.with(this)
                .load(R.drawable.book_placeholder)
                .preload();
            
            Log.d(TAG, "Glide initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Glide: " + e.getMessage());
        }
    }
    
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        
        // Clear Glide memory cache when system is low on memory
        try {
            Glide.get(this).clearMemory();
            Log.d(TAG, "Cleared Glide memory cache due to low memory");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing Glide memory cache: " + e.getMessage());
        }
    }
} 