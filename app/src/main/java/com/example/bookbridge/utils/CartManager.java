package com.example.bookbridge.utils;

import com.example.bookbridge.models.Book;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class to manage cart operations across the application
 */
public class CartManager {
    
    // Static map to hold cart items with their quantities
    private static Map<Integer, Integer> cartItems = new HashMap<>();
    
    // Add a book to cart
    public static void addToCart(int bookId) {
        Integer quantity = cartItems.get(bookId);
        if (quantity == null) {
            cartItems.put(bookId, 1);
        } else {
            cartItems.put(bookId, quantity + 1);
        }
    }
    
    // Remove a book from cart
    public static void removeFromCart(int bookId) {
        Integer quantity = cartItems.get(bookId);
        if (quantity != null) {
            if (quantity > 1) {
                cartItems.put(bookId, quantity - 1);
            } else {
                cartItems.remove(bookId);
            }
        }
    }
    
    // Remove all instances of a book from cart
    public static void removeAllFromCart(int bookId) {
        cartItems.remove(bookId);
    }
    
    // Get quantity of a book in cart
    public static int getQuantity(int bookId) {
        Integer quantity = cartItems.get(bookId);
        return quantity == null ? 0 : quantity;
    }
    
    // Check if a book is in cart
    public static boolean isInCart(int bookId) {
        return cartItems.containsKey(bookId);
    }
    
    // Get all book IDs in cart
    public static List<Integer> getCartBookIds() {
        return new ArrayList<>(cartItems.keySet());
    }
    
    // Get all books in cart with their quantities
    public static Map<Book, Integer> getCartBooks() {
        Map<Book, Integer> books = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
            Book book = BookManager.getBookById(entry.getKey());
            if (book != null) {
                books.put(book, entry.getValue());
            }
        }
        return books;
    }
    
    // Get total number of items in cart
    public static int getCartItemCount() {
        int count = 0;
        for (Integer quantity : cartItems.values()) {
            count += quantity;
        }
        return count;
    }
    
    // Get total price of all items in cart
    public static double getCartTotal() {
        double total = 0;
        for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
            Book book = BookManager.getBookById(entry.getKey());
            if (book != null) {
                // Apply 20% discount to each book price
                double discountedPrice = book.getPrice() * 0.8;
                total += discountedPrice * entry.getValue();
            }
        }
        return total;
    }
    
    // Clear the cart
    public static void clearCart() {
        cartItems.clear();
    }
} 