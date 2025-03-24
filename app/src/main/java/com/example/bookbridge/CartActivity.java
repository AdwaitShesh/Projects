package com.example.bookbridge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookbridge.adapters.CartAdapter;
import com.example.bookbridge.models.Book;
import com.example.bookbridge.utils.BottomNavManager;
import com.example.bookbridge.utils.CartManager;
import com.example.bookbridge.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartActivity extends AppCompatActivity implements CartAdapter.CartItemListener {

    private RecyclerView rvCartItems;
    private TextView tvEmptyCart, tvSubtotal, tvDeliveryCharge, tvTotal, tvItemCount;
    private Button btnCheckout;
    private View layoutCartSummary;
    private CartAdapter cartAdapter;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if user is logged in
        SessionManager sessionManager = SessionManager.getInstance(this);
        if (!sessionManager.isLoggedIn()) {
            // Redirect to AuthActivity
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        
        setContentView(R.layout.activity_cart);

        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.my_cart);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize views
        rvCartItems = findViewById(R.id.rv_cart_items);
        tvEmptyCart = findViewById(R.id.tv_empty_cart);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        tvDeliveryCharge = findViewById(R.id.tv_delivery_charge);
        tvTotal = findViewById(R.id.tv_total);
        tvItemCount = findViewById(R.id.tv_item_count);
        btnCheckout = findViewById(R.id.btn_checkout);
        layoutCartSummary = findViewById(R.id.layout_cart_summary);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up RecyclerView
        rvCartItems.setLayoutManager(new LinearLayoutManager(this));

        // Set up bottom navigation
        setupBottomNavigation();
        
        // Load cart items
        loadCartItems();

        // Set up checkout button
        btnCheckout.setOnClickListener(v -> {
            if (CartManager.getCartItemCount() == 0) {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Get all books in cart
            List<Integer> bookIds = CartManager.getCartBookIds();
            if (!bookIds.isEmpty()) {
                // Get total cart value
                double totalAmount = CartManager.getCartTotal();
                
                // Create intent to checkout activity
                Intent intent = new Intent(CartActivity.this, CheckoutActivity.class);
                
                // If there is only one book, pass its details
                if (bookIds.size() == 1) {
                    int bookId = bookIds.get(0);
                    Book book = com.example.bookbridge.utils.BookManager.getBookById(bookId);
                    intent.putExtra("book_id", bookId);
                    intent.putExtra("book_title", book.getTitle());
                } else {
                    // For multiple books, create a summary
                    intent.putExtra("book_title", bookIds.size() + " books in your cart");
                }
                
                // Pass the total price
                intent.putExtra("book_price", totalAmount);
                startActivity(intent);
            }
        });
    }
    
    private void setupBottomNavigation() {
        // Use BottomNavManager to set up bottom navigation
        BottomNavManager.setupBottomNavigation(this, bottomNavigationView, R.id.nav_cart);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Refresh cart items in case they were modified
        loadCartItems();
        
        // Update badges
        if (bottomNavigationView != null) {
            BottomNavManager.updateBadges(bottomNavigationView);
        }
    }

    private void loadCartItems() {
        Map<Book, Integer> cartItems = CartManager.getCartBooks();
        List<Book> books = new ArrayList<>(cartItems.keySet());
        
        if (books.isEmpty()) {
            tvEmptyCart.setVisibility(View.VISIBLE);
            rvCartItems.setVisibility(View.GONE);
            layoutCartSummary.setVisibility(View.GONE);
        } else {
            tvEmptyCart.setVisibility(View.GONE);
            rvCartItems.setVisibility(View.VISIBLE);
            layoutCartSummary.setVisibility(View.VISIBLE);
            
            // Update cart adapter
            cartAdapter = new CartAdapter(this, books, cartItems, this);
            rvCartItems.setAdapter(cartAdapter);
            
            // Update summary
            updateCartSummary();
        }
    }
    
    private void updateCartSummary() {
        DecimalFormat df = new DecimalFormat("0.00");
        
        // Get subtotal
        double subtotal = CartManager.getCartTotal();
        tvSubtotal.setText(String.format(Locale.getDefault(), "₹%s", df.format(subtotal)));
        
        // Set delivery charge
        double deliveryCharge = 40.00; // Fixed delivery charge
        tvDeliveryCharge.setText(String.format(Locale.getDefault(), "₹%s", df.format(deliveryCharge)));
        
        // Calculate total
        double total = subtotal + deliveryCharge;
        tvTotal.setText(String.format(Locale.getDefault(), "₹%s", df.format(total)));
        
        // Update item count
        int itemCount = CartManager.getCartItemCount();
        tvItemCount.setText(String.format(Locale.getDefault(), "Items: %d", itemCount));
    }

    @Override
    public void onQuantityChanged() {
        updateCartSummary();
    }

    @Override
    public void onItemRemoved() {
        loadCartItems(); // Reload the entire cart UI
    }
} 