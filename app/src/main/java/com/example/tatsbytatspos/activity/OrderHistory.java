package com.example.tatsbytatspos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

public class OrderHistory extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ImageButton sideBarButton;
    private RecyclerView recyclerView;
    private OrdersAdapter ordersAdapter;
    private List<Orders> orderList;
    private NavigationView navigationView;
    private DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // Initialize database helper
        dbHelper = new DatabaseHelper(this);

        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        sideBarButton = findViewById(R.id.sideBarButton);
        navigationView = findViewById(R.id.navigationView);
        recyclerView = findViewById(R.id.menuRecyclerView);

        // Sidebar button opens drawer
        sideBarButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Navigation drawer item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(OrderHistory.this, MainActivity.class));
            } else if (id == R.id.nav_history) {
                Toast.makeText(OrderHistory.this, "Already on this screen!", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_inventory) {
                startActivity(new Intent(OrderHistory.this, Inventory.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Set up RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Initialize order list
        orderList = new ArrayList<>();
        ordersAdapter = new OrdersAdapter(this, orderList, getSupportFragmentManager());
        recyclerView.setAdapter(ordersAdapter);

        // Load orders from database
        refreshOrderList();
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
        }
        ordersAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshOrderList();
    }
}
