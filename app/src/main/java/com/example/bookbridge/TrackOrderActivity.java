package com.example.bookbridge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
        // Add warehouse marker (green)
        addMarker(BANGALORE, "Warehouse", R.drawable.ic_marker_green);

        // Add current location marker (blue)
        addMarker(CURRENT_LOCATION, "Current Location", R.drawable.ic_marker_blue);

        // Add destination marker (orange)
        addMarker(KOLKATA, "Destination", R.drawable.ic_marker_orange);

        // Draw route line
        Polyline line = new Polyline(mapView);
        List<GeoPoint> points = new ArrayList<>();
        points.add(BANGALORE);
        points.add(CURRENT_LOCATION);
        points.add(KOLKATA);
        line.setPoints(points);
        line.setColor(getResources().getColor(R.color.teal_700));
        line.setWidth(5f);
        mapView.getOverlayManager().add(line);
    }

    private void addMarker(GeoPoint point, String title, int iconResId) {
        Marker marker = new Marker(mapView);
        marker.setPosition(point);
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        marker.setTitle(title);
        marker.setIcon(getDrawable(iconResId));
        mapView.getOverlays().add(marker);
    }

    private void loadOrderDetails() {
        // Mock data - replace with real data from your backend
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/M/yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("dd/M/yyyy 'at' hh:mm a", Locale.getDefault());

        tvOrderId.setText("Order #" + orderId);
        tvDeliveryDate.setText("31/3/2025");
        tvStatus.setText("In Transit");
        
        tvWarehouseLocation.setText("Bangalore Warehouse");
        tvOrderPlacedTime.setText("22/3/2025 at 10:30 AM");
        
        tvSortingCenter.setText("Bangalore Sorting Center");
        tvOrderProcessedTime.setText("23/3/2025 at 2:45 PM");
        
        tvDestination.setText("En Route to Kolkata");
        tvInTransitTime.setText("24/3/2025 at 9:15 AM");
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