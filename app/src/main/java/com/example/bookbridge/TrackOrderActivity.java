package com.example.bookbridge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bookbridge.models.Book;
import com.example.bookbridge.models.Order;
import com.example.bookbridge.utils.OrderManager;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class TrackOrderActivity extends AppCompatActivity {

    private MapView mapView;
    private String orderId;
    private Toolbar toolbar;

    // Mock locations (you should replace these with real data from your backend)
    private static final GeoPoint BANGALORE = new GeoPoint(12.9716, 77.5946);
    private static final GeoPoint KOLKATA = new GeoPoint(22.5726, 88.3639);
    private static final GeoPoint CURRENT_LOCATION = new GeoPoint(17.3850, 82.9870);

    // TextViews for order details
    private TextView tvOrderId;
    private TextView tvDeliveryDate;
    private TextView tvStatus;
    private TextView tvWarehouseLocation;
    private TextView tvOrderPlacedTime;
    private TextView tvSortingCenter;
    private TextView tvOrderProcessedTime;
    private TextView tvDestination;
    private TextView tvInTransitTime;

    public static void start(Context context, String orderId) {
        Intent intent = new Intent(context, TrackOrderActivity.class);
        intent.putExtra("order_id", orderId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Initialize OpenStreetMap configuration
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));

        setContentView(R.layout.activity_track_order);

        // Get order ID from intent
        orderId = getIntent().getStringExtra("order_id");

        // Initialize views
        initViews();

        // Setup map
        setupMap();

        // Load order details
        loadOrderDetails();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Track Order");
        }

        mapView = findViewById(R.id.map_view);

        // Initialize TextViews
        tvOrderId = findViewById(R.id.tv_order_id);
        tvDeliveryDate = findViewById(R.id.tv_delivery_date);
        tvStatus = findViewById(R.id.tv_status);
        tvWarehouseLocation = findViewById(R.id.tv_warehouse_location);
        tvOrderPlacedTime = findViewById(R.id.tv_order_placed_time);
        tvSortingCenter = findViewById(R.id.tv_sorting_center);
        tvOrderProcessedTime = findViewById(R.id.tv_order_processed_time);
        tvDestination = findViewById(R.id.tv_destination);
        tvInTransitTime = findViewById(R.id.tv_in_transit_time);
    }

    private void setupMap() {
        // Configure map
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true);

        // Get map controller
        IMapController mapController = mapView.getController();
        mapController.setZoom(5.0);

        // Center the map on the current location
        mapController.setCenter(CURRENT_LOCATION);

        // Add markers and route
        addMarkersAndRoute();
    }

    private void addMarkersAndRoute() {
        // Create markers
        Marker warehouseMarker = new Marker(mapView);
        warehouseMarker.setPosition(BANGALORE);
        warehouseMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        warehouseMarker.setTitle("Warehouse");
        mapView.getOverlays().add(warehouseMarker);

        Marker destinationMarker = new Marker(mapView);
        destinationMarker.setPosition(KOLKATA);
        destinationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        destinationMarker.setTitle("Destination");
        mapView.getOverlays().add(destinationMarker);

        Marker currentLocationMarker = new Marker(mapView);
        currentLocationMarker.setPosition(CURRENT_LOCATION);
        currentLocationMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        currentLocationMarker.setTitle("Current Location");
        mapView.getOverlays().add(currentLocationMarker);

        // Create route line
        Polyline routeLine = new Polyline();
        List<GeoPoint> routePoints = new ArrayList<>();
        routePoints.add(BANGALORE);
        routePoints.add(CURRENT_LOCATION);
        routePoints.add(KOLKATA);
        routeLine.setPoints(routePoints);
        mapView.getOverlays().add(routeLine);
    }

    private void loadOrderDetails() {
        // Find the order with matching ID
        Order order = null;
        List<Order> allOrders = OrderManager.getOrders();
        for (Order o : allOrders) {
            if (o.getOrderId().equals(orderId)) {
                order = o;
                break;
            }
        }
        
        // Format dates
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd/MM/yyyy 'at' hh:mm a", Locale.getDefault());
        
        if (order != null) {
            // Order found, display order details
            tvOrderId.setText("Order #" + order.getOrderId());
            tvStatus.setText(order.getStatus());
            
            // Get order date
            Date orderDate = order.getOrderDate();
            
            // Calculate delivery date (5 days after order date)
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(orderDate);
            calendar.add(Calendar.DAY_OF_MONTH, 5);
            Date deliveryDate = calendar.getTime();
            
            tvDeliveryDate.setText(dateFormat.format(deliveryDate));
            
            // Display tracking timeline
            tvOrderPlacedTime.setText(timeFormat.format(orderDate));
            
            // Calculate processing date (1 day after order date)
            calendar.setTime(orderDate);
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date processingDate = calendar.getTime();
            tvOrderProcessedTime.setText(timeFormat.format(processingDate));
            
            // Calculate in-transit date (2 days after order date)
            calendar.setTime(orderDate);
            calendar.add(Calendar.DAY_OF_MONTH, 2);
            Date transitDate = calendar.getTime();
            tvInTransitTime.setText(timeFormat.format(transitDate));
            
            // Display location details
            tvWarehouseLocation.setText("Warehouse");
            tvSortingCenter.setText("Sorting Center");
            tvDestination.setText("En Route to Destination");
            
            // Get books for this order
            List<Book> booksInOrder = OrderManager.getBooksForOrder(order.getOrderId());
            if (!booksInOrder.isEmpty()) {
                // Update toolbar title with book information
                if (getSupportActionBar() != null) {
                    if (booksInOrder.size() == 1) {
                        getSupportActionBar().setTitle("Track: " + booksInOrder.get(0).getTitle());
                    } else {
                        getSupportActionBar().setTitle("Track: " + booksInOrder.size() + " Books");
                    }
                }
            }
        } else {
            // Order not found, display default/demo data
            tvOrderId.setText("Order #" + orderId);
            tvDeliveryDate.setText("31/03/2025");
            tvStatus.setText("In Transit");
            
            tvWarehouseLocation.setText("Bangalore Warehouse");
            tvOrderPlacedTime.setText("22/03/2025 at 10:30 AM");
            
            tvSortingCenter.setText("Bangalore Sorting Center");
            tvOrderProcessedTime.setText("23/03/2025 at 2:45 PM");
            
            tvDestination.setText("En Route to Kolkata");
            tvInTransitTime.setText("24/03/2025 at 9:15 AM");
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
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
} 