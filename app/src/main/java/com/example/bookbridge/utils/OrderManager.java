package com.example.bookbridge.utils;

import com.example.bookbridge.models.Book;
import com.example.bookbridge.models.Order;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Utility class to manage orders across the application
 */
public class OrderManager {
    
    // Static list to hold all orders
    private static List<Order> orders = new ArrayList<>();
    
    // Static map to hold the books associated with each order ID
    private static Map<String, List<Book>> orderBooks = new HashMap<>();
    
    // Static list to hold book IDs associated with orders
    private static List<Integer> orderedBookIds = new ArrayList<>();
    
    /**
     * Create a new order from cart items
     * @param paymentMethod the payment method used (cod, upi, card, etc.)
     * @return the created order
     */
    public static Order createOrderFromCart(String paymentMethod) {
        // Generate a unique order ID
        String orderId = "order_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);
        
        // Get current date
        Date orderDate = Calendar.getInstance().getTime();
        
        // Create new order
        Order order = new Order(orderId, orderDate, "pending", paymentMethod);
        
        // Add this order to our list
        orders.add(order);
        
        // Get all books from cart and add their IDs to ordered books
        Map<Book, Integer> cartBooks = CartManager.getCartBooks();
        
        // Create a list to store books for this order
        List<Book> booksInOrder = new ArrayList<>();
        
        for (Map.Entry<Book, Integer> entry : cartBooks.entrySet()) {
            Book book = entry.getKey();
            int quantity = entry.getValue();
            
            // Add each book to the order books list (considering quantity)
            for (int i = 0; i < quantity; i++) {
                booksInOrder.add(book);
                orderedBookIds.add(book.getId());
            }
        }
        
        // Store the books for this order
        orderBooks.put(orderId, booksInOrder);
        
        return order;
    }
    
    /**
     * Get all orders
     * @return list of all orders
     */
    public static List<Order> getOrders() {
        // If there are no orders yet, add some demo orders
        if (orders.isEmpty()) {
            createDemoOrders();
        }
        return orders;
    }
    
    /**
     * Get books for a specific order
     * @param orderId the order ID
     * @return list of books in the order
     */
    public static List<Book> getBooksForOrder(String orderId) {
        List<Book> books = orderBooks.get(orderId);
        return books != null ? books : new ArrayList<>();
    }
    
    /**
     * Check if a book ID exists in ordered books
     * @param bookId the book ID to check
     * @return true if the book has been ordered
     */
    public static boolean isBookOrdered(int bookId) {
        return orderedBookIds.contains(bookId);
    }
    
    /**
     * Create some demo orders for testing
     */
    private static void createDemoOrders() {
        Calendar calendar = Calendar.getInstance();
        
        // First order
        Order order1 = new Order();
        order1.setOrderId("174198219937");
        calendar.set(2025, Calendar.MARCH, 15);
        order1.setOrderDate(calendar.getTime());
        order1.setStatus("delivered");
        order1.setPaymentMethod("cod");
        orders.add(order1);
        
        // Create demo books for first order
        List<Book> books1 = new ArrayList<>();
        Book demoBook1 = BookManager.getBookById(1);
        if (demoBook1 != null) {
            books1.add(demoBook1);
            orderedBookIds.add(demoBook1.getId());
        }
        orderBooks.put(order1.getOrderId(), books1);
        
        // Second order 
        Order order2 = new Order();
        order2.setOrderId("174199174163");
        calendar.set(2025, Calendar.MARCH, 18);
        order2.setOrderDate(calendar.getTime());
        order2.setStatus("in transit");
        order2.setPaymentMethod("upi");
        orders.add(order2);
        
        // Create demo books for second order
        List<Book> books2 = new ArrayList<>();
        Book demoBook2 = BookManager.getBookById(2);
        if (demoBook2 != null) {
            books2.add(demoBook2);
            orderedBookIds.add(demoBook2.getId());
        }
        orderBooks.put(order2.getOrderId(), books2);
    }
} 