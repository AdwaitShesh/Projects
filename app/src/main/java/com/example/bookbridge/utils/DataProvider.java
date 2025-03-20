package com.example.bookbridge.utils;

import com.example.bookbridge.R;
import com.example.bookbridge.models.Book;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DataProvider {

    public static List<Book> getSampleBooks() {
        List<Book> books = new ArrayList<>();
        
        // DSA Book
        books.add(new Book(
                UUID.randomUUID().toString(),
                "Data Structures and Algorithms",
                "Robert Lafore",
                "A comprehensive guide to DSA concepts with practical examples.",
                "Computer Science",
                499.00,
                R.drawable.book_dsa,
                "New",
                "seller1"
        ));
        
        // Construction Engineering Book
        books.add(new Book(
                UUID.randomUUID().toString(),
                "Construction Engineering",
                "S. L. Kumar",
                "Covers principles and practices of modern construction engineering.",
                "Engineering",
                750.00,
                R.drawable.book_construction,
                "Used - Like New",
                "seller2"
        ));
        
        // Digital Electronics Book
        books.add(new Book(
                UUID.randomUUID().toString(),
                "Digital Electronics",
                "William Kleitz",
                "An introduction to digital electronics design and applications.",
                "Electronics",
                600.00,
                R.drawable.book_electronics,
                "Used - Good",
                "seller3"
        ));
        
        // Mathematics Book
        books.add(new Book(
                UUID.randomUUID().toString(),
                "Engineering Mathematics",
                "B.S. Grewal",
                "A comprehensive guide to mathematics for engineering students.",
                "Mathematics",
                550.00,
                R.drawable.book_mathematics,
                "New",
                "seller4"
        ));
        
        // Add more sample books as needed
        
        return books;
    }
    
    public static List<String> getBookCategories() {
        List<String> categories = new ArrayList<>();
        categories.add("All");
        categories.add("Computer Science");
        categories.add("Engineering");
        categories.add("Electronics");
        categories.add("Mathematics");
        categories.add("Physics");
        categories.add("Chemistry");
        categories.add("Biology");
        return categories;
    }
} 