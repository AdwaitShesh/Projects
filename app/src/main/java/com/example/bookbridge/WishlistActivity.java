package com.example.bookbridge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookbridge.adapters.WishlistAdapter;
import com.example.bookbridge.models.Book;
import com.example.bookbridge.utils.BookManager;
import com.example.bookbridge.utils.BottomNavManager;
import com.example.bookbridge.utils.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;

public class WishlistActivity extends AppCompatActivity {

    private RecyclerView rvWishlist;
    private TextView tvEmptyWishlist;
    private WishlistAdapter wishlistAdapter;
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
        
        setContentView(R.layout.activity_wishlist);

        // Initialize views
        initViews();

        // Setup bottom navigation
        setupBottomNavigation();
        
        // Load wishlisted books
        loadWishlistedBooks();
    }

    private void initViews() {
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Wishlist");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rvWishlist = findViewById(R.id.rv_wishlist);
        tvEmptyWishlist = findViewById(R.id.tv_empty_wishlist);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set up RecyclerView
        rvWishlist.setLayoutManager(new LinearLayoutManager(this));
        wishlistAdapter = new WishlistAdapter(this);
        rvWishlist.setAdapter(wishlistAdapter);
    }
    
    private void setupBottomNavigation() {
        // Use BottomNavManager to set up bottom navigation
        BottomNavManager.setupBottomNavigation(this, bottomNavigationView, R.id.nav_wishlist);
    }

    private void loadWishlistedBooks() {
        List<Book> allBooks = BookManager.getAllBooks();
        List<Book> wishlistedBooks = new ArrayList<>();

        // Filter wishlisted books
        for (Book book : allBooks) {
            if (book.isWishlisted()) {
                wishlistedBooks.add(book);
            }
        }

        // Update UI
        if (wishlistedBooks.isEmpty()) {
            tvEmptyWishlist.setVisibility(View.VISIBLE);
            rvWishlist.setVisibility(View.GONE);
        } else {
            tvEmptyWishlist.setVisibility(View.GONE);
            rvWishlist.setVisibility(View.VISIBLE);
            wishlistAdapter.setBooks(wishlistedBooks);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload wishlist when returning to activity
        loadWishlistedBooks();
        
        // Update badges
        if (bottomNavigationView != null) {
            BottomNavManager.updateBadges(bottomNavigationView);
        }
    }

    @Override
    public void onBackPressed() {
        // When going back to MainActivity, notify it to update the wishlist badge
        setResult(RESULT_OK);
        super.onBackPressed();
    }

    /**
     * Called when a book is removed from the wishlist
     * This ensures the parent activity is properly notified
     */
    public void onBookRemovedFromWishlist() {
        // Set result to notify MainActivity that changes were made
        setResult(RESULT_OK);
        
        // Update badges on remove
        if (bottomNavigationView != null) {
            BottomNavManager.updateBadges(bottomNavigationView);
        }
    }
} 