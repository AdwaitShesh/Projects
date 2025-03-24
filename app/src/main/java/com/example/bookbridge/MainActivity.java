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
import com.example.bookbridge.models.User;
import com.example.bookbridge.utils.BookManager;
import com.example.bookbridge.utils.SessionManager;
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

    private static final int WISHLIST_REQUEST_CODE = 1001;

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
        
        // Continue with normal flow if user is logged in
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
        
        // Update navigation header with user information
        updateNavigationHeader();
    }

    private void updateNavigationHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        
        // Get references to the views in the header
        TextView tvUsername = headerView.findViewById(R.id.tv_nav_username);
        TextView tvEmail = headerView.findViewById(R.id.tv_nav_email);
        
        // Get user info from SessionManager
        SessionManager sessionManager = SessionManager.getInstance(this);
        if (sessionManager.isLoggedIn()) {
            User user = sessionManager.getUser();
            if (user != null) {
                tvUsername.setText(user.getUsername());
                tvEmail.setText(user.getEmail());
            }
        }
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
                // Navigate to wishlist for result
                Intent intent = new Intent(MainActivity.this, WishlistActivity.class);
                startActivityForResult(intent, WISHLIST_REQUEST_CODE);
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Navigate to profile
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                startActivity(intent);
                return true;
            }
            
            return false;
        });
        
        // Add badges to navigation items
        updateWishlistBadge();
        updateCartBadge();
    }
    
    // Method to update wishlist badge
    private void updateWishlistBadge() {
        BadgeDrawable wishlistBadge = bottomNavigationView.getOrCreateBadge(R.id.nav_wishlist);
        int wishlistCount = BookManager.getWishlistCount();
        if (wishlistCount > 0) {
            wishlistBadge.setVisible(true);
            wishlistBadge.setNumber(wishlistCount);
            // Make sure to use the unfilled icon when count is 0
            Menu menu = bottomNavigationView.getMenu();
            MenuItem wishlistItem = menu.findItem(R.id.nav_wishlist);
            wishlistItem.setIcon(R.drawable.ic_favorite);
        } else {
            wishlistBadge.setVisible(false);
            // Reset to outline icon when no items in wishlist
            Menu menu = bottomNavigationView.getMenu();
            MenuItem wishlistItem = menu.findItem(R.id.nav_wishlist);
            wishlistItem.setIcon(R.drawable.ic_favorite_border);
        }
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
        // Update badges every time MainActivity becomes visible
        updateWishlistBadge();
        updateCartBadge();
        
        // Reload recent books when returning to the activity to show any newly added books
        loadRecentBooks();
    }

    private void loadCategories() {
        List<Category> categories = new ArrayList<>();
        
        // Add "All" category
        categories.add(new Category(BookManager.CATEGORY_ALL, R.drawable.ic_category_all));
        
        // Add other categories
        categories.add(new Category(BookManager.CATEGORY_CSE, R.drawable.ic_category_cs));
        categories.add(new Category(BookManager.CATEGORY_ECE, R.drawable.ic_category_ec));
        categories.add(new Category(BookManager.CATEGORY_MECH, R.drawable.ic_category_mech));
        categories.add(new Category(BookManager.CATEGORY_CIVIL, R.drawable.ic_category_civil));
        categories.add(new Category(BookManager.CATEGORY_IT, R.drawable.ic_category_it));
        categories.add(new Category(BookManager.CATEGORY_EEE, R.drawable.ic_category_eee));
        
        // Show empty message if no categories
        TextView tvEmptyCategories = findViewById(R.id.tv_empty_categories);
        if (categories.isEmpty()) {
            rvCategories.setVisibility(View.GONE);
            tvEmptyCategories.setVisibility(View.VISIBLE);
        } else {
            rvCategories.setVisibility(View.VISIBLE);
            tvEmptyCategories.setVisibility(View.GONE);
            categoryAdapter = new CategoryAdapter(this, categories);
            rvCategories.setAdapter(categoryAdapter);
        }
        
        // Set up "View All" click listeners
        View featuredViewAll = findViewById(R.id.tv_featured_view_all);
        featuredViewAll.setOnClickListener(v -> {
            // Navigate to CategoryActivity showing all books
            CategoryActivity.start(this, BookManager.CATEGORY_ALL);
        });
        
        View recentViewAll = findViewById(R.id.tv_recent_view_all);
        recentViewAll.setOnClickListener(v -> {
            // Navigate to CategoryActivity showing all books
            CategoryActivity.start(this, BookManager.CATEGORY_ALL);
        });
    }

    private void loadFeaturedBooks() {
        List<Book> featuredBooks = new ArrayList<>();
        
        // Add example featured books (all with wishlist set to false initially)
        featuredBooks.add(new Book(1, "Data Structures & Algorithms", "Robert Lafore", 450, "Computer Science textbook covering all fundamental algorithms and data structures", R.drawable.book_dsa, false));
        featuredBooks.add(new Book(2, "Computer Networks", "Andrew S. Tanenbaum", 380, "Complete reference for computer networking concepts", R.drawable.book_networks, false));
        featuredBooks.add(new Book(3, "Digital Logic Design", "Morris Mano", 290, "Comprehensive guide to digital logic and computer design", R.drawable.book_digital_logic, false));
        featuredBooks.add(new Book(4, "Operating Systems", "Galvin", 320, "Essential concepts of modern operating systems", R.drawable.book_os, false));
        
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
            // Navigate to wishlist for result
            Intent intent = new Intent(this, WishlistActivity.class);
            startActivityForResult(intent, WISHLIST_REQUEST_CODE);
        } else if (id == R.id.nav_drawer_settings) {
            Toast.makeText(this, "Settings coming soon!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_drawer_help) {
            Toast.makeText(this, "Help coming soon!", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_drawer_logout) {
            // Logout and clear session
            SessionManager sessionManager = SessionManager.getInstance(this);
            sessionManager.logout();
            
            // Go to Auth activity
            Intent intent = new Intent(this, AuthActivity.class);
            // Clear all previous activities
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == WISHLIST_REQUEST_CODE) {
            // Update wishlist badge when returning from WishlistActivity
            updateWishlistBadge();
        }
    }
    
    public void onWishlistUpdated() {
        // Update bottom navigation badge
        updateWishlistBadge();
        
        // Refresh adapters to update wishlist icons in all book listings
        if (featuredBooksAdapter != null) {
            featuredBooksAdapter.notifyDataSetChanged();
        }
        
        if (recentBooksAdapter != null) {
            recentBooksAdapter.notifyDataSetChanged();
        }
    }
}