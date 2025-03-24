package com.example.bookbridge;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookbridge.models.Book;
import com.example.bookbridge.utils.BookManager;

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