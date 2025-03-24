package com.example.bookbridge.glide;

import android.content.Context;
import android.util.Log;

import com.bumptech.glide.GlideBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.module.AppGlideModule;
import com.bumptech.glide.request.RequestOptions;

/**
 * Custom Glide module for BookBridge app
 * This ensures Glide is properly configured for optimal image loading
 */
@GlideModule
public class BookBridgeGlideModule extends AppGlideModule {
    
    @Override
    public void applyOptions(Context context, GlideBuilder builder) {
        // Set up Glide with optimal configuration for our app
        builder.setLogLevel(Log.VERBOSE);
        
        // Apply default request options with a strategy that avoids disk caching
        // This prevents issues with VectorDrawables which cannot be cached to disk
        builder.setDefaultRequestOptions(
                new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
        );
        
        Log.d("BookBridgeGlideModule", "Glide module configured successfully");
    }
} 