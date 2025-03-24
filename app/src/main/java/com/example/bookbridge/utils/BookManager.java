package com.example.bookbridge.utils;

import com.example.bookbridge.R;
import com.example.bookbridge.models.Book;

import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to manage books across the application
 */
public class BookManager {
    
    // Static list to hold all books
    private static List<Book> allBooks = null;
    
    // Static list to track recently added books
    private static List<Book> recentlyAddedBooks = new ArrayList<>();
    
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
    
    // Get all books, initializing with example data if necessary
    public static List<Book> getAllBooks() {
        if (allBooks == null) {
            // Initialize with example data
            allBooks = new ArrayList<>();
            
            // Add example books with categories
            allBooks.add(new Book(1, "Data Structures & Algorithms", "Robert Lafore", 450, 
                "Computer Science textbook covering all fundamental algorithms and data structures", 
                R.drawable.book_dsa, false, CATEGORY_CSE));
            allBooks.add(new Book(2, "Computer Networks", "Andrew S. Tanenbaum", 380, 
                "Complete reference for computer networking concepts", 
                R.drawable.book_networks, false, CATEGORY_CSE));
            allBooks.add(new Book(3, "Digital Logic Design", "Morris Mano", 290, 
                "Comprehensive guide to digital logic and computer design", 
                R.drawable.book_digital_logic, false, CATEGORY_ECE));
            allBooks.add(new Book(4, "Operating Systems", "Galvin", 320, 
                "Essential concepts of modern operating systems", 
                R.drawable.book_os, false, CATEGORY_CSE));
            allBooks.add(new Book(5, "Microprocessors", "Ramesh Gaonkar", 280, 
                "Introduction to microprocessors and microcontrollers", 
                R.drawable.book_microprocessor, false, CATEGORY_ECE));
            allBooks.add(new Book(6, "Theory of Computation", "Michael Sipser", 340, 
                "Formal languages and automata theory", 
                R.drawable.book_toc, false, CATEGORY_CSE));
            allBooks.add(new Book(7, "Database Management", "Raghu Ramakrishnan", 390, 
                "Comprehensive guide to database systems", 
                R.drawable.book_dbms, false, CATEGORY_IT));
            allBooks.add(new Book(8, "Machine Learning", "Tom Mitchell", 520, 
                "Fundamentals of machine learning algorithms", 
                R.drawable.book_ml, false, CATEGORY_IT));
            allBooks.add(new Book(9, "Computer Graphics", "Donald Hearn", 310, 
                "Principles of 2D and 3D graphics", 
                R.drawable.book_graphics, false, CATEGORY_CSE));
            allBooks.add(new Book(10, "Artificial Intelligence", "Stuart Russell", 480, 
                "Modern approach to artificial intelligence", 
                R.drawable.book_ai, false, CATEGORY_IT));
            
            // Add software engineering book
            allBooks.add(new Book(11, "Software Engineering", "Brain Andrade", 699, 
                "Location: Bandra, Seller: Smatle, Condition: Fair, Category: Computer Science, Artificial Intelligence, Compliance, and Security", 
                R.drawable.book_ai, false, CATEGORY_CSE));

            // Add Mechanical Engineering books
            allBooks.add(new Book(12, "Thermodynamics", "P.K. Nag", 350, 
                "Principles and applications of thermodynamics", 
                R.drawable.book_toc, false, CATEGORY_MECH));
            allBooks.add(new Book(13, "Fluid Mechanics", "Frank M. White", 420, 
                "Comprehensive guide to fluid mechanics", 
                R.drawable.book_dsa, false, CATEGORY_MECH));
            
            // Add Civil Engineering books
            allBooks.add(new Book(14, "Structural Analysis", "R.C. Hibbeler", 380, 
                "Analysis of statically determinate and indeterminate structures", 
                R.drawable.book_networks, false, CATEGORY_CIVIL));
            allBooks.add(new Book(15, "Surveying", "B.C. Punmia", 310, 
                "Principles and methods of surveying", 
                R.drawable.book_digital_logic, false, CATEGORY_CIVIL));
            
            // Add EEE books
            allBooks.add(new Book(16, "Power Systems", "J.B. Gupta", 430, 
                "Generation, transmission and distribution of electrical power", 
                R.drawable.book_os, false, CATEGORY_EEE));
            allBooks.add(new Book(17, "Control Systems", "I.J. Nagrath", 390, 
                "Analysis and design of control systems", 
                R.drawable.book_microprocessor, false, CATEGORY_EEE));
            
            // Initialize recently added with the same books (in reverse order to simulate recent first)
            for (int i = allBooks.size() - 1; i >= 0; i--) {
                recentlyAddedBooks.add(allBooks.get(i));
            }
        }
        
        return allBooks;
    }
    
    // Get books by category
    public static List<Book> getBooksByCategory(String category) {
        // Make sure books are initialized
        if (allBooks == null) {
            getAllBooks();
        }
        
        // If "All" category is requested, return all books
        if (category.equals(CATEGORY_ALL)) {
            return new ArrayList<>(allBooks);
        }
        
        // Filter books by category
        List<Book> filteredBooks = new ArrayList<>();
        for (Book book : allBooks) {
            if (book.getCategory().equals(category)) {
                filteredBooks.add(book);
            }
        }
        
        return filteredBooks;
    }
    
    // Get recently added books
    public static List<Book> getRecentlyAddedBooks() {
        // Make sure books are initialized
        if (allBooks == null) {
            getAllBooks();
        }
        return recentlyAddedBooks;
    }
    
    // Get a book by ID
    public static Book getBookById(int id) {
        // Make sure books are initialized
        if (allBooks == null) {
            getAllBooks();
        }
        
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
        // Make sure books are initialized
        if (allBooks == null) {
            getAllBooks();
        }
        
        // Generate a new ID for the book
        int newId = allBooks.size() + 1;
        newBook.setId(newId);
        
        // Add to all books
        allBooks.add(newBook);
        
        // Add to beginning of recently added books
        recentlyAddedBooks.add(0, newBook);
        
        // Trim recently added books if exceeds maximum
        if (recentlyAddedBooks.size() > MAX_RECENT_BOOKS) {
            recentlyAddedBooks.remove(recentlyAddedBooks.size() - 1);
        }
    }

    // Get count of wishlisted books
    public static int getWishlistCount() {
        // Make sure books are initialized
        if (allBooks == null) {
            getAllBooks();
        }
        
        int count = 0;
        for (Book book : allBooks) {
            if (book.isWishlisted()) {
                count++;
            }
        }
        return count;
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
} 