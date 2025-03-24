package com.example.bookbridge;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookbridge.adapters.CategoryAdapter;
import com.example.bookbridge.adapters.FeaturedBooksAdapter;
import com.example.bookbridge.adapters.RecentBooksAdapter;
import com.example.bookbridge.models.Book;
import com.example.bookbridge.models.Category;
import com.example.bookbridge.utils.BookManager;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNavigationView;
    private Toolbar toolbar;
    private FloatingActionButton fabSellBook;
    
    private RecyclerView rvCategories;
    private RecyclerView rvFeaturedBooks;
    private RecyclerView rvRecentBooks;
    
    private CategoryAdapter categoryAdapter;
    private FeaturedBooksAdapter featuredBooksAdapter;
    private RecentBooksAdapter recentBooksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        initViews();
        
        // Set up toolbar
        setSupportActionBar(toolbar);
        
        // Set up navigation drawer
        setupNavigationDrawer();
        
        // Set up bottom navigation
        setupBottomNavigation();
        
        // Set up sell book FAB
        fabSellBook.setVisibility(View.VISIBLE);
        fabSellBook.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SellBookActivity.class);
            startActivity(intent);
        });
        
        // Load data
        loadCategories();
        loadFeaturedBooks();
        loadRecentBooks();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        fabSellBook = findViewById(R.id.fab_sell_book);
        
        rvCategories = findViewById(R.id.rv_categories);
        rvFeaturedBooks = findViewById(R.id.rv_featured_books);
        rvRecentBooks = findViewById(R.id.rv_recent_books);
        
        // Set up RecyclerViews
        rvCategories.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvFeaturedBooks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvRecentBooks.setLayoutManager(new GridLayoutManager(this, 2));
    }

    private void setupNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        
        navigationView.setNavigationItemSelectedListener(this);
        
        // Set user info in nav header
        View headerView = navigationView.getHeaderView(0);
        TextView tvUserName = headerView.findViewById(R.id.tv_nav_username);
        TextView tvUserEmail = headerView.findViewById(R.id.tv_nav_email);
        
        // TODO: Get actual user data from database or shared preferences
        tvUserName.setText("Engineering Student");
        tvUserEmail.setText("student@example.com");
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_home) {
                // Already on home, do nothing
                return true;
            } else if (itemId == R.id.nav_search) {
                // Launch SearchActivity
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_cart) {
                // Navigate to cart
                Intent intent = new Intent(MainActivity.this, CartActivity.class);
                startActivity(intent);
                return true;
            } else if (itemId == R.id.nav_wishlist) {
                Toast.makeText(this, "Wishlist coming soon!", Toast.LENGTH_SHORT).show();
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Navigate to profile
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            
            return false;
        });
        
        // Add badge to wishlist icon
        BadgeDrawable wishlistBadge = bottomNavigationView.getOrCreateBadge(R.id.nav_wishlist);
        wishlistBadge.setVisible(true);
        wishlistBadge.setNumber(3); // Example number of wishlist items
        
        // Add badge to cart icon
        updateCartBadge();
    }
    
    // Method to update cart badge
    private void updateCartBadge() {
        BadgeDrawable cartBadge = bottomNavigationView.getOrCreateBadge(R.id.nav_cart);
        int cartCount = com.example.bookbridge.utils.CartManager.getCartItemCount();
        if (cartCount > 0) {
            cartBadge.setVisible(true);
            cartBadge.setNumber(cartCount);
        } else {
            cartBadge.setVisible(false);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Update cart badge every time MainActivity becomes visible
        updateCartBadge();
        
        // Reload recent books when returning to the activity to show any newly added books
        loadRecentBooks();
    }

    private void loadCategories() {
        List<Category> categories = new ArrayList<>();
        
        // Add example categories
        categories.add(new Category("CSE", R.drawable.ic_category_cs));
        categories.add(new Category("ECE", R.drawable.ic_category_ec));
        categories.add(new Category("Mech", R.drawable.ic_category_mech));
        categories.add(new Category("Civil", R.drawable.ic_category_civil));
        categories.add(new Category("IT", R.drawable.ic_category_it));
        categories.add(new Category("EEE", R.drawable.ic_category_eee));
        
        categoryAdapter = new CategoryAdapter(this, categories);
        rvCategories.setAdapter(categoryAdapter);
    }

    private void loadFeaturedBooks() {
        List<Book> featuredBooks = new ArrayList<>();
        
        // Add example featured books
        featuredBooks.add(new Book(1, "Data Structures & Algorithms", "Robert Lafore", 450, "Computer Science textbook covering all fundamental algorithms and data structures", R.drawable.book_dsa, true));
        featuredBooks.add(new Book(2, "Computer Networks", "Andrew S. Tanenbaum", 380, "Complete reference for computer networking concepts", R.drawable.book_networks, true));
        featuredBooks.add(new Book(3, "Digital Logic Design", "Morris Mano", 290, "Comprehensive guide to digital logic and computer design", R.drawable.book_digital_logic, false));
        featuredBooks.add(new Book(4, "Operating Systems", "Galvin", 320, "Essential concepts of modern operating systems", R.drawable.book_os, true));
        
        featuredBooksAdapter = new FeaturedBooksAdapter(this, featuredBooks);
        rvFeaturedBooks.setAdapter(featuredBooksAdapter);
    }

    private void loadRecentBooks() {
        // Get recently added books from BookManager instead of creating a new list
        List<Book> recentBooks = BookManager.getRecentlyAddedBooks();
        
        recentBooksAdapter = new RecentBooksAdapter(this, recentBooks);
        rvRecentBooks.setAdapter(recentBooksAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.action_notifications) {
            Toast.makeText(this, "Notifications coming soon!", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_messages) {
            Toast.makeText(this, "Messages coming soon!", Toast.LENGTH_SHORT).show();
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        
        if (id == R.id.nav_drawer_home) {
            // Already on home screen
        } else if (id == R.id.nav_drawer_my_orders) {
            Toast.makeText(this, "My Orders coming soon!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_drawer_my_listings) {
            Toast.makeText(this, "My Listings coming soon!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_drawer_wishlist) {
            Toast.makeText(this, "Wishlist coming soon!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_drawer_settings) {
            Toast.makeText(this, "Settings coming soon!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_drawer_help) {
            Toast.makeText(this, "Help coming soon!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_drawer_logout) {
            // Logout functionality
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
            finish();
        }
        
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}