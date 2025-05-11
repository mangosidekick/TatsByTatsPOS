package com.example.tatsbytatspos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatsbytatspos.model.Orders;
import com.example.tatsbytatspos.adapter.OrdersAdapter;
import com.example.tatsbytatspos.R;
import com.example.tatsbytatspos.database.DatabaseHelper;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class OrderHistory extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ImageButton sideBarButton;
    private RecyclerView recyclerView;
    private OrdersAdapter ordersAdapter;
    private List<Orders> orderList;
    private List<Orders> filteredOrderList;
    private NavigationView navigationView;
    private DatabaseHelper dbHelper;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        try {
            // Initialize database helper
            dbHelper = new DatabaseHelper(this);

            // Initialize views
            drawerLayout = findViewById(R.id.drawer_layout);
            toolbar = findViewById(R.id.toolbar);
            sideBarButton = findViewById(R.id.sideBarButton);
            navigationView = findViewById(R.id.navigationView);
            recyclerView = findViewById(R.id.menuRecyclerView);
            searchView = findViewById(R.id.searchView);

            // Configure SearchView
            searchView.setQueryHint(getString(R.string.orders_hint));
            searchView.setMaxWidth(Integer.MAX_VALUE);
            searchView.setFocusable(false);
            searchView.setIconifiedByDefault(false);

            // Initialize RecyclerView
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
            recyclerView.setLayoutManager(gridLayoutManager);

            // Initialize order lists
            orderList = new ArrayList<>();
            filteredOrderList = new ArrayList<>();
            ordersAdapter = new OrdersAdapter(this, filteredOrderList, getSupportFragmentManager());
            recyclerView.setAdapter(ordersAdapter);

            // Load initial data
            refreshOrderList();
        } catch (Exception e) {
            Toast.makeText(this, "Error initializing app: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        // Setup SearchView
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    filterOrders(query);
                } catch (Exception e) {
                    Toast.makeText(OrderHistory.this, "Error filtering orders: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                try {
                    filterOrders(newText);
                } catch (Exception e) {
                    Toast.makeText(OrderHistory.this, "Error filtering orders: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        // Sidebar button opens drawer
        sideBarButton.setOnClickListener(v -> {
            try {
                drawerLayout.openDrawer(GravityCompat.START);
            } catch (Exception e) {
                Toast.makeText(OrderHistory.this, "Error opening menu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Navigation drawer item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            try {
                int id = item.getItemId();
                if (id == R.id.nav_home) {
                    startActivity(new Intent(OrderHistory.this, MainActivity.class));
                } else if (id == R.id.nav_history) {
                    Toast.makeText(OrderHistory.this, "Already on this screen!", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_inventory) {
                    startActivity(new Intent(OrderHistory.this, Inventory.class));
                }
                drawerLayout.closeDrawer(GravityCompat.START);
            } catch (Exception e) {
                Toast.makeText(OrderHistory.this, "Error navigating: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return true;
        });
    }

    public void refreshOrderList() {
        orderList.clear();
        // Make sure dbHelper is initialized
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(this);
        }
        // Get all orders from the database
        List<Orders> orders = dbHelper.getAllOrders();
        if (orders != null) {
            orderList.addAll(orders);
            filteredOrderList.clear();
            filteredOrderList.addAll(orderList);
        }
        ordersAdapter.notifyDataSetChanged();
    }

    private void filterOrders(String query) {
        if (query == null || query.isEmpty()) {
            filteredOrderList.clear();
            filteredOrderList.addAll(orderList);
        } else {
            String searchQuery = query.toLowerCase().trim();
            filteredOrderList.clear();
            filteredOrderList.addAll(orderList.stream()
                    .filter(order ->
                            String.valueOf(order.getId()).contains(searchQuery) ||
                                    order.getOrderSummary().toLowerCase().contains(searchQuery) ||
                                    order.getPaymentMethod().toLowerCase().contains(searchQuery) ||
                                    order.getPaymentStatus().toLowerCase().contains(searchQuery) ||
                                    String.format("%.2f", order.getTotalAmount()).contains(searchQuery))
                    .collect(Collectors.toList()));
        }
        ordersAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshOrderList();
    }
}
