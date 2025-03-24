package com.example.bookbridge.utils;

import com.example.bookbridge.models.Book;
import java.util.ArrayList;
import java.util.List;

public class SearchUtils {
    
    // Search books by title using string matching
    public static List<Book> searchBooks(String query, List<Book> books) {
        List<Book> results = new ArrayList<>();
        if (query == null || query.trim().isEmpty()) {
            return results;
        }
        
        String normalizedQuery = query.toLowerCase().trim();
        
        for (Book book : books) {
            String normalizedTitle = book.getTitle().toLowerCase();
            
            // Check for exact match
            if (normalizedTitle.equals(normalizedQuery)) {
                results.add(0, book); // Add at the beginning for exact matches
                continue;
            }
            
            // Check for contains match
            if (normalizedTitle.contains(normalizedQuery)) {
                results.add(book);
                continue;
            }
            
            // Check for word-by-word match
            String[] queryWords = normalizedQuery.split("\\s+");
            boolean allWordsMatch = true;
            
            for (String word : queryWords) {
                if (!normalizedTitle.contains(word)) {
                    allWordsMatch = false;
                    break;
                }
            }
            
            if (allWordsMatch) {
                results.add(book);
            }
        }
        
        return results;
    }
} 