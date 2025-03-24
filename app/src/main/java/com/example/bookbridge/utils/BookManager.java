package com.example.bookbridge.utils;

import com.example.bookbridge.models.Book;
import com.example.bookbridge.models.Order;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class to manage books across the application
 */
public class BookManager {
    
    // Static list to hold all books
    private static List<Book> allBooks = new ArrayList<>();
    
    // Map to hold books by category
    private static Map<String, List<Book>> booksByCategory = new HashMap<>();
    
    // Static list to track recently added books
    private static List<Book> recentlyAddedBooks = new ArrayList<>();
    
    // Set to track book IDs we have already processed 
    private static Set<Integer> processedBookIds = new HashSet<>();
    
    // Flag to check if we've loaded books from OrderManager
    private static boolean orderedBooksLoaded = false;
    
    // Maximum number of books to show in the recently added section
    private static final int MAX_RECENT_BOOKS = 10;
    
    // Available categories
    public static final String CATEGORY_ALL = "All";
    public static final String CATEGORY_CSE = "CSE";
    public static final String CATEGORY_ECE = "ECE";
    public static final String CATEGORY_MECH = "Mech";
    public static final String CATEGORY_CIVIL = "Civil";
    public static final String CATEGORY_IT = "IT";
    public static final String CATEGORY_EEE = "EEE";

    // Static block to initialize the category map
    static {
        for (String category : getAllCategories()) {
            booksByCategory.put(category, new ArrayList<>());
        }
        
        // Load some demo books if none exist
        if (allBooks.isEmpty()) {
            loadDemoBooks();
        }
    }
    
    // Get all books (load from orders first if needed)
    public static List<Book> getAllBooks() {
        // Load books from OrderManager if not already done
        loadOrderedBooks();
        
        return new ArrayList<>(allBooks);
    }
    
    // Load books from OrderManager to ensure they are shown permanently
    private static void loadOrderedBooks() {
        if (!orderedBooksLoaded) {
            List<Order> orders = OrderManager.getOrders();
            for (Order order : orders) {
                List<Book> booksInOrder = OrderManager.getBooksForOrder(order.getOrderId());
                for (Book book : booksInOrder) {
                    // Only add each book once
                    if (!processedBookIds.contains(book.getId())) {
                        // Add to processed set
                        processedBookIds.add(book.getId());
                        
                        // Check if book already exists in allBooks
                        boolean bookExists = false;
                        for (Book existingBook : allBooks) {
                            if (existingBook.getId() == book.getId()) {
                                bookExists = true;
                                break;
                            }
                        }
                        
                        // Add book if it doesn't exist already
                        if (!bookExists) {
                            // Add to all books
                            allBooks.add(book);
                            
                            // Add to appropriate category
                            String category = book.getCategory();
                            if (booksByCategory.containsKey(category)) {
                                booksByCategory.get(category).add(book);
                            } else {
                                // If category doesn't exist in map, add it to All at least
                                booksByCategory.get(CATEGORY_ALL).add(book);
                            }
                            
                            // Add to recently added list
                            if (recentlyAddedBooks.size() < MAX_RECENT_BOOKS) {
                                recentlyAddedBooks.add(book);
                            }
                        }
                    }
                }
            }
            
            orderedBooksLoaded = true;
        }
    }
    
    // Get books by category
    public static List<Book> getBooksByCategory(String category) {
        // Load books from OrderManager if not already done
        loadOrderedBooks();
        
        // If "All" category is requested, return all books
        if (category.equals(CATEGORY_ALL)) {
            return new ArrayList<>(allBooks);
        }
        
        // Return books from the specific category
        if (booksByCategory.containsKey(category)) {
            return new ArrayList<>(booksByCategory.get(category));
        }
        
        // Return empty list if category doesn't exist
        return new ArrayList<>();
    }
    
    // Get recently added books
    public static List<Book> getRecentlyAddedBooks() {
        // Load books from OrderManager if not already done
        loadOrderedBooks();
        
        return new ArrayList<>(recentlyAddedBooks);
    }
    
    // Get a book by ID
    public static Book getBookById(int id) {
        // Load books from OrderManager if not already done
        loadOrderedBooks();
        
        // Find the book with the specified ID
        for (Book book : allBooks) {
            if (book.getId() == id) {
                return book;
            }
        }
        
        // Book not found
        return null;
    }
    
    // Add a new book and update the recently added list
    public static void addBook(Book newBook) {
        // Load books from OrderManager if not already done
        loadOrderedBooks();
        
        // Generate a new ID for the book if not already set
        if (newBook.getId() <= 0) {
            int newId = allBooks.size() + 1;
            newBook.setId(newId);
        }
        
        // Check if book with this ID already exists
        for (Book book : allBooks) {
            if (book.getId() == newBook.getId()) {
                // Book already exists, update it instead
                book.setTitle(newBook.getTitle());
                book.setAuthor(newBook.getAuthor());
                book.setPrice(newBook.getPrice());
                book.setDescription(newBook.getDescription());
                book.setImageResource(newBook.getImageResource());
                book.setImageUrl(newBook.getImageUrl());
                book.setCategory(newBook.getCategory());
                return;
            }
        }
        
        // Add to all books
        allBooks.add(newBook);
        
        // Add to processed set
        processedBookIds.add(newBook.getId());
        
        // Add to appropriate category
        String category = newBook.getCategory();
        if (booksByCategory.containsKey(category)) {
            booksByCategory.get(category).add(newBook);
        } else {
            // If category doesn't exist in map, add it to All at least
            booksByCategory.get(CATEGORY_ALL).add(newBook);
        }
        
        // Add to beginning of recently added books
        recentlyAddedBooks.add(0, newBook);
        
        // Trim recently added books if exceeds maximum
        if (recentlyAddedBooks.size() > MAX_RECENT_BOOKS) {
            recentlyAddedBooks.remove(recentlyAddedBooks.size() - 1);
        }
    }

    // Get count of wishlisted books
    public static int getWishlistCount() {
        // Load books from OrderManager if not already done
        loadOrderedBooks();
        
        int count = 0;
        for (Book book : allBooks) {
            if (book.isWishlisted()) {
                count++;
            }
        }
        return count;
    }
    
    // Check if there are any books in a specific category
    public static boolean hasBooks(String category) {
        // Load books from OrderManager if not already done
        loadOrderedBooks();
        
        if (category.equals(CATEGORY_ALL)) {
            return !allBooks.isEmpty();
        }
        
        if (booksByCategory.containsKey(category)) {
            return !booksByCategory.get(category).isEmpty();
        }
        
        return false;
    }
    
    // Clear all books (for testing purposes only - not used in production)
    public static void clearAllBooks() {
        allBooks.clear();
        recentlyAddedBooks.clear();
        processedBookIds.clear();
        orderedBooksLoaded = false;
        for (String category : getAllCategories()) {
            if (booksByCategory.containsKey(category)) {
                booksByCategory.get(category).clear();
            }
        }
    }
    
    // Get all available categories
    public static List<String> getAllCategories() {
        List<String> categories = new ArrayList<>();
        categories.add(CATEGORY_ALL);
        categories.add(CATEGORY_CSE);
        categories.add(CATEGORY_ECE);
        categories.add(CATEGORY_MECH);
        categories.add(CATEGORY_CIVIL);
        categories.add(CATEGORY_IT);
        categories.add(CATEGORY_EEE);
        return categories;
    }

    /**
     * Get featured books with a limit
     * @param limit Maximum number of books to return
     * @return List of featured books
     */
    public static List<Book> getFeaturedBooks(int limit) {
        // Load books from OrderManager if not already done
        loadOrderedBooks();
        
        // For this demo, we'll treat all books as potential featured books
        List<Book> featured = new ArrayList<>();
        List<Book> all = getAllBooks();
        
        // Take up to limit books
        for (int i = 0; i < Math.min(limit, all.size()); i++) {
            featured.add(all.get(i));
        }
        
        return featured;
    }
    
    /**
     * Get recent books with a limit
     * @param limit Maximum number of books to return
     * @return List of recently added books
     */
    public static List<Book> getRecentBooks(int limit) {
        // Load books from OrderManager if not already done
        loadOrderedBooks();
        
        // We'll use the recentlyAddedBooks list
        List<Book> recent = new ArrayList<>();
        
        // Take up to limit books
        for (int i = 0; i < Math.min(limit, recentlyAddedBooks.size()); i++) {
            recent.add(recentlyAddedBooks.get(i));
        }
        
        return recent;
    }
    
    /**
     * Load some sample books for the demo
     */
    private static void loadDemoBooks() {
        // Demo books will only be loaded if no books exist
        if (!allBooks.isEmpty()) {
            return;
        }
        
        // Sample CSE books
        Book dataStructures = new Book(1, "Data Structures & Algorithms", "Robert Lafore", 599.0, 
                "A comprehensive guide to data structures and algorithms in C++.", 
                "https://m.media-amazon.com/images/I/41WCjcotzAL._SY264_BO1,204,203,200_QL40_FMwebp_.jpg", 
                false, CATEGORY_CSE);
        
        Book javaComplete = new Book(2, "Java: The Complete Reference", "Herbert Schildt", 899.0, 
                "The definitive guide to Java programming language.", 
                "https://m.media-amazon.com/images/I/51U2YvFJG+L._SY344_BO1,204,203,200_.jpg", 
                false, CATEGORY_CSE);
        
        // Sample ECE books
        Book digitalCircuits = new Book(3, "Digital Circuits and Design", "S. Salivahanan", 450.0, 
                "This book covers principles of digital electronics and circuit design.", 
                "https://m.media-amazon.com/images/I/51H8cWb31+L._SY344_BO1,204,203,200_.jpg", 
                false, CATEGORY_ECE);
        
        // Add books
        addBook(dataStructures);
        addBook(javaComplete);
        addBook(digitalCircuits);
    }
} 