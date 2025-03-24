package com.example.bookbridge.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.bookbridge.MainActivity;
import com.example.bookbridge.R;
import com.example.bookbridge.adapters.OrdersAdapter;
import com.example.bookbridge.models.Order;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class OrdersFragment extends Fragment {

    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvOrders;
    private View layoutEmptyOrders;
    private Button btnShopNow;
    private OrdersAdapter ordersAdapter;
    private List<Order> orderList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_orders, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        // Initialize views
        initViews(view);
        
        // Setup RecyclerView
        setupRecyclerView();
        
        // Setup listeners
        setupListeners();
        
        // Load orders
        loadOrders();
    }

    private void initViews(View view) {
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        rvOrders = view.findViewById(R.id.rv_orders);
        layoutEmptyOrders = view.findViewById(R.id.layout_empty_orders);
        btnShopNow = view.findViewById(R.id.btn_shop_now);
    }

    private void setupRecyclerView() {
        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        ordersAdapter = new OrdersAdapter(getContext(), orderList);
        rvOrders.setAdapter(ordersAdapter);
    }

    private void setupListeners() {
        swipeRefresh.setOnRefreshListener(() -> {
            loadOrders();
            swipeRefresh.setRefreshing(false);
        });

        btnShopNow.setOnClickListener(v -> {
            // Navigate to main activity or shop section
            if (getActivity() != null) {
                getActivity().finish();
            }
        });
    }

    private void loadOrders() {
        // Clear existing orders
        orderList.clear();
        
        // Create mock orders for demonstration
        Calendar calendar = Calendar.getInstance();
        
        // First order
        Order order1 = new Order();
        order1.setOrderId("174198219937");
        calendar.set(2025, Calendar.MARCH, 15);
        order1.setOrderDate(calendar.getTime());
        order1.setStatus("pending");
        order1.setPaymentMethod("cod");
        orderList.add(order1);
        
        // Second order
        Order order2 = new Order();
        order2.setOrderId("order_174199174163_72mj4v0al");
        calendar.set(2025, Calendar.MARCH, 15);
        order2.setOrderDate(calendar.getTime());
        order2.setStatus("pending");
        order2.setPaymentMethod("upi");
        orderList.add(order2);
        
        // Update UI
        updateUI();
    }

    private void updateUI() {
        if (orderList.isEmpty()) {
            rvOrders.setVisibility(View.GONE);
            layoutEmptyOrders.setVisibility(View.VISIBLE);
        } else {
            rvOrders.setVisibility(View.VISIBLE);
            layoutEmptyOrders.setVisibility(View.GONE);
            ordersAdapter.notifyDataSetChanged();
        }
    }
} 