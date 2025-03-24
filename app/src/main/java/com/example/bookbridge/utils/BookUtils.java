package com.example.bookbridge.utils;

import android.content.Context;
import android.util.Log;

import com.example.bookbridge.R;

import java.util.Random;

/**
 * Utility class for book-related operations
 */
public class BookUtils {
    
    /**
     * Returns a random book cover resource ID from the available book covers
     * 
     * @param context Context to access resources
     * @return Resource ID for a random book cover
     */
    public static int getRandomBookCoverResourceId(Context context) {
        int[] bookCovers = {
            R.drawable.book_dsa,
            R.drawable.book_networks,
            R.drawable.book_digital_logic,
            R.drawable.book_os,
            R.drawable.book_microprocessor,
            R.drawable.book_toc
        };
        
        int randomIndex = new Random().nextInt(bookCovers.length);
        int resourceId = bookCovers[randomIndex];
        
        Log.d("BookUtils", "Selected random book cover resource ID: " + resourceId);
        return resourceId;
    }
} 