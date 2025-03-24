package com.example.bookbridge.utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bookbridge.AuthActivity;
import com.example.bookbridge.data.User;

/**
 * SessionManager to handle user login state and store user information
 */
public class SessionManager {
    // Shared preferences file name
    private static final String PREF_NAME = "BookBridgeUserSession";
    
    // Shared preferences mode
    private static final int PRIVATE_MODE = Context.MODE_PRIVATE;
    
    // Shared preferences keys
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_ADDRESS = "address";
    
    // Singleton instance
    private static SessionManager instance;
    
    // Shared preferences
    private final SharedPreferences pref;
    private final SharedPreferences.Editor editor;
    private final Context context;
    
    private SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }
    
    /**
     * Get the singleton instance of SessionManager
     */
    public static synchronized SessionManager getInstance(Context context) {
        if (instance == null) {
            instance = new SessionManager(context.getApplicationContext());
        }
        return instance;
    }
    
    /**
     * Create login session
     */
    public void createLoginSession(User user) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, user.getId());
        editor.putString(KEY_USERNAME, user.getUsername());
        editor.putString(KEY_EMAIL, user.getEmail());
        editor.putString(KEY_MOBILE, user.getMobileNo());
        // We'll store address separately since it's not part of the User model yet
        editor.apply();
    }
    
    /**
     * Update user details
     */
    public void updateUserDetails(String username, String email, String mobile, String address) {
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_ADDRESS, address);
        editor.apply();
    }
    
    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    /**
     * Get stored user data
     */
    public User getUser() {
        if (!isLoggedIn()) return null;
        
        User user = new User(
            pref.getString(KEY_USERNAME, ""),
            pref.getString(KEY_EMAIL, ""),
            "", // Password is not stored in SharedPreferences for security
            pref.getString(KEY_MOBILE, "")
        );
        user.setId(pref.getInt(KEY_USER_ID, -1));
        return user;
    }
    
    /**
     * Get user's saved address
     */
    public String getUserAddress() {
        return pref.getString(KEY_ADDRESS, "");
    }
    
    /**
     * Clear session details
     */
    public void logout() {
        editor.clear();
        editor.apply();
    }
    
    /**
     * Check login status and redirect to login screen if not logged in
     * @param activity The activity to redirect from
     * @return true if user is logged in, false otherwise
     */
    public boolean checkLoginAndRedirect(AppCompatActivity activity) {
        if (!isLoggedIn()) {
            Intent intent = new Intent(context, AuthActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(intent);
            activity.finish();
            return false;
        }
        return true;
    }
} 