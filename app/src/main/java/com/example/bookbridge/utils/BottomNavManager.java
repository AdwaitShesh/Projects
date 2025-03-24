package com.example.bookbridge.utils;

import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.bookbridge.CartActivity;
import com.example.bookbridge.MainActivity;
import com.example.bookbridge.ProfileActivity;
import com.example.bookbridge.R;
import com.example.bookbridge.SearchActivity;
import com.example.bookbridge.WishlistActivity;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;

/**
 * Utility class to manage bottom navigation bar consistently across all activities
 */
public class BottomNavManager {

    private static final int WISHLIST_REQUEST_CODE = 1001;

    /**
     * Set up the bottom navigation for any activity
     * @param activity The activity that contains the bottom navigation
     * @param bottomNavigationView The BottomNavigationView to set up
     * @param selectedItemId The ID of the item to select
     */
    public static void setupBottomNavigation(Activity activity, BottomNavigationView bottomNavigationView, int selectedItemId) {
        // Set the selected item
        bottomNavigationView.setSelectedItemId(selectedItemId);
        
        // Set up item selection listener
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_home) {
                if (!(activity instanceof MainActivity)) {
                    Intent intent = new Intent(activity, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    activity.startActivity(intent);
                }
                return true;
            } else if (itemId == R.id.nav_search) {
                if (!(activity instanceof SearchActivity)) {
                    Intent intent = new Intent(activity, SearchActivity.class);
                    activity.startActivity(intent);
                }
                return true;
            } else if (itemId == R.id.nav_cart) {
                if (!(activity instanceof CartActivity)) {
                    Intent intent = new Intent(activity, CartActivity.class);
                    activity.startActivity(intent);
                }
                return true;
            } else if (itemId == R.id.nav_wishlist) {
                if (!(activity instanceof WishlistActivity)) {
                    Intent intent = new Intent(activity, WishlistActivity.class);
                    activity.startActivityForResult(intent, WISHLIST_REQUEST_CODE);
                }
                return true;
            } else if (itemId == R.id.nav_profile) {
                if (!(activity instanceof ProfileActivity)) {
                    Intent intent = new Intent(activity, ProfileActivity.class);
                    activity.startActivity(intent);
                }
                return true;
            }
            
            return false;
        });
        
        // Update badges
        updateBadges(bottomNavigationView);
    }
    
    /**
     * Updates the badges on cart and wishlist icons
     * @param bottomNavigationView The BottomNavigationView to update badges on
     */
    public static void updateBadges(@NonNull BottomNavigationView bottomNavigationView) {
        // Update wishlist badge
        BadgeDrawable wishlistBadge = bottomNavigationView.getOrCreateBadge(R.id.nav_wishlist);
        int wishlistCount = BookManager.getWishlistCount();
        if (wishlistCount > 0) {
            wishlistBadge.setVisible(true);
            wishlistBadge.setNumber(wishlistCount);
            // Make sure to use the filled icon when wishlist has items
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
        
        // Update cart badge
        BadgeDrawable cartBadge = bottomNavigationView.getOrCreateBadge(R.id.nav_cart);
        int cartCount = CartManager.getCartItemCount();
        if (cartCount > 0) {
            cartBadge.setVisible(true);
            cartBadge.setNumber(cartCount);
        } else {
            cartBadge.setVisible(false);
        }
    }
} 