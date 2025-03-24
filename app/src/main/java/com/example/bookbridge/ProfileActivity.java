package com.example.bookbridge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bookbridge.fragments.OrdersFragment;
import com.example.bookbridge.fragments.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import androidx.appcompat.widget.Toolbar;

public class ProfileActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private LinearLayout tabOrders;
    private LinearLayout tabProfile;
    private View indicatorOrders;
    private View indicatorProfile;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Initialize views
        initViews();
        
        // Setup toolbar
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Profile");
        
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Setup ViewPager
        setupViewPager();
        setupTabs();
        setupBottomNavigation();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);
        tabOrders = findViewById(R.id.tab_orders);
        tabProfile = findViewById(R.id.tab_profile);
        indicatorOrders = findViewById(R.id.indicator_orders);
        indicatorProfile = findViewById(R.id.indicator_profile);
        bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set tab indicators width
        viewPager.post(() -> {
            int width = viewPager.getWidth() / 2;
            ViewGroup.LayoutParams ordersParams = indicatorOrders.getLayoutParams();
            ordersParams.width = width;
            indicatorOrders.setLayoutParams(ordersParams);

            ViewGroup.LayoutParams profileParams = indicatorProfile.getLayoutParams();
            profileParams.width = width;
            indicatorProfile.setLayoutParams(profileParams);
        });

        // Set up logout button
        findViewById(R.id.btn_logout).setOnClickListener(v -> {
            // Log out and go to Auth
            Intent intent = new Intent(ProfileActivity.this, AuthActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void setupViewPager() {
        // Create adapter for the viewpager
        ProfilePagerAdapter adapter = new ProfilePagerAdapter(this);
        viewPager.setAdapter(adapter);
        
        // Set default to first tab (Orders)
        updateTabs(0);
        
        // Add page change listener
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateTabs(position);
            }
        });
        
        // Connect TabLayout with ViewPager (hidden but needed for handling)
        if (tabLayout != null) {
            new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
                switch (position) {
                    case 0:
                        tab.setText("Orders");
                        break;
                    case 1:
                        tab.setText("Profile");
                        break;
                }
            }).attach();
        }
    }

    private void setupTabs() {
        tabOrders.setOnClickListener(v -> viewPager.setCurrentItem(0));
        tabProfile.setOnClickListener(v -> viewPager.setCurrentItem(1));
    }

    private void updateTabs(int position) {
        if (position == 0) {
            // Orders tab
            indicatorOrders.setVisibility(View.VISIBLE);
            indicatorProfile.setVisibility(View.INVISIBLE);
            setTabColors(true, false);
        } else {
            // Profile tab
            indicatorOrders.setVisibility(View.INVISIBLE);
            indicatorProfile.setVisibility(View.VISIBLE);
            setTabColors(false, true);
        }
    }

    private void setTabColors(boolean ordersSelected, boolean profileSelected) {
        // Change colors for Orders tab
        ImageView ivOrders = tabOrders.findViewById(R.id.iv_orders);
        TextView tvOrders = tabOrders.findViewById(R.id.tv_orders);
        ivOrders.setImageTintList(getColorStateList(ordersSelected ? R.color.teal_700 : R.color.gray));
        tvOrders.setTextColor(getColor(ordersSelected ? R.color.teal_700 : R.color.gray));

        // Change colors for Profile tab
        ImageView ivProfile = tabProfile.findViewById(R.id.iv_profile);
        TextView tvProfile = tabProfile.findViewById(R.id.tv_profile);
        ivProfile.setImageTintList(getColorStateList(profileSelected ? R.color.teal_700 : R.color.gray));
        tvProfile.setTextColor(getColor(profileSelected ? R.color.teal_700 : R.color.gray));
    }

    private void setupBottomNavigation() {
        bottomNavigationView.setSelectedItemId(R.id.nav_profile);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            
            if (itemId == R.id.nav_home) {
                startActivity(new Intent(this, MainActivity.class));
                return true;
            } else if (itemId == R.id.nav_search) {
                return true;
            } else if (itemId == R.id.nav_cart) {
                startActivity(new Intent(this, CartActivity.class));
                return true;
            } else if (itemId == R.id.nav_wishlist) {
                return true;
            } else if (itemId == R.id.nav_profile) {
                // Already on profile
                return true;
            }
            
            return false;
        });
    }

    private static class ProfilePagerAdapter extends FragmentStateAdapter {
        public ProfilePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            return position == 0 ? new OrdersFragment() : new ProfileFragment();
        }

        @Override
        public int getItemCount() {
            return 2;
        }
    }
} 