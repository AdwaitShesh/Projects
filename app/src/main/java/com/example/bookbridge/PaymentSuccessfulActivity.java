package com.example.bookbridge;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookbridge.models.Book;
import com.example.bookbridge.models.Order;
import com.example.bookbridge.utils.BookManager;
import com.example.bookbridge.utils.CartManager;
import com.example.bookbridge.utils.OrderManager;

import java.text.DecimalFormat;
import java.util.Locale;

public class PaymentSuccessfulActivity extends AppCompatActivity {

    private TextView tvTotalAmount, tvBookTitle;
    private Button btnContinueShopping;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_successful);

        // Get data from intent
        double totalAmount = getIntent().getDoubleExtra("total_amount", 0.0);
        String bookTitle = getIntent().getStringExtra("book_title");
        int bookId = getIntent().getIntExtra("book_id", -1);

        // Initialize views
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvBookTitle = findViewById(R.id.tv_book_title);
        btnContinueShopping = findViewById(R.id.btn_continue_shopping);

        // Format total amount
        DecimalFormat df = new DecimalFormat("0.00");
        tvTotalAmount.setText(String.format(Locale.getDefault(), "₹%s", df.format(totalAmount)));

        // Set book title if available
        if (bookTitle != null && !bookTitle.isEmpty()) {
            tvBookTitle.setText(bookTitle);
        } else if (bookId != -1) {
            // Try to get book title from BookManager
            Book book = BookManager.getBookById(bookId);
            if (book != null) {
                tvBookTitle.setText(book.getTitle());
            }
        }
        
        // Create order from cart items
        try {
            // Determine payment method based on intent data or default to COD
            String paymentMethod = getIntent().getStringExtra("payment_method");
            if (paymentMethod == null || paymentMethod.isEmpty()) {
                paymentMethod = "cod"; // Default to cash on delivery
            }
            
            // Create the order using OrderManager
            Order order = OrderManager.createOrderFromCart(paymentMethod);
            Log.d("PaymentSuccessfulActivity", "Created new order: " + order.getOrderId());
            
            // Now it's safe to clear the cart after creating the order
            CartManager.clearCart();
        } catch (Exception e) {
            Log.e("PaymentSuccessfulActivity", "Error creating order: " + e.getMessage(), e);
        }

        // Set up Continue Shopping button
        btnContinueShopping.setOnClickListener(v -> navigateToMain());

        // Auto-navigate to MainActivity after 5 seconds
        handler.postDelayed(this::navigateToMain, 5000);
    }

    private void navigateToMain() {
        // Remove any pending callbacks to avoid multiple navigation
        handler.removeCallbacksAndMessages(null);
        
        // Navigate to MainActivity
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Remove any pending callbacks to avoid memory leaks
        handler.removeCallbacksAndMessages(null);
    }
} 