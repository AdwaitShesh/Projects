package com.example.bookbridge.utils;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.bookbridge.R;
import com.example.bookbridge.models.Book;

/**
 * Utility class for handling image loading across the app
 */
public class ImageUtils {

    private static final String TAG = "ImageUtils";

    /**
     * Load a book cover image into an ImageView
     * @param context Context for Glide
     * @param imageView The target ImageView
     * @param book The Book object containing image data
     */
    public static void loadBookCover(Context context, ImageView imageView, Book book) {
        if (context == null || imageView == null || book == null) {
            Log.e(TAG, "Invalid parameters passed to loadBookCover");
            return;
        }

        try {
            // Base request options for all image types
            RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.book_placeholder)
                    .error(R.drawable.book_placeholder)
                    .centerCrop();

            if (book.hasValidImageUrl()) {
                // Load from URL if available
                Log.d(TAG, "Loading image from URL: " + book.getImageUrl());
                Glide.with(context)
                        .load(book.getImageUrl())
                        .apply(requestOptions)
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable disk caching to avoid issues
                        .into(imageView);
            } else if (book.hasValidImageResource()) {
                // Load from resource ID 
                Log.d(TAG, "Loading image from resource: " + book.getImageResource());
                Glide.with(context)
                        .load(book.getImageResource())
                        .apply(requestOptions)
                        .diskCacheStrategy(DiskCacheStrategy.NONE) // Disable disk caching to avoid issues
                        .into(imageView);
            } else {
                // Fallback to placeholder if no image is available
                Log.d(TAG, "No image available, using placeholder");
                imageView.setImageResource(R.drawable.book_placeholder);
            }
        } catch (Exception e) {
            // If anything goes wrong, set the placeholder
            Log.e(TAG, "Error loading image: " + e.getMessage());
            imageView.setImageResource(R.drawable.book_placeholder);
        }
    }
} 