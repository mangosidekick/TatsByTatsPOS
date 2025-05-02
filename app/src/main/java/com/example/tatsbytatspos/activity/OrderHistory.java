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
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderHistory extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ImageButton sideBarButton;
    private RecyclerView recyclerView;
    private OrdersAdapter ordersAdapter;
    private List<Orders> orderList;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        sideBarButton = findViewById(R.id.sideBarButton);
        navigationView = findViewById(R.id.navigationView);
        recyclerView = findViewById(R.id.menuRecyclerView);

        // Set up the Toolbar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // Sidebar button opens drawer
        sideBarButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Navigation drawer item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(OrderHistory.this, MainActivity.class));
            } else if (id == R.id.nav_history) {
                //startActivity(new Intent(OrderHistory.this, OrderHistory.class));
                Toast.makeText(OrderHistory.this, "Already on this screen!", Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, "History clicked", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_inventory) {
                startActivity(new Intent(OrderHistory.this, Inventory.class));
                //Toast.makeText(MainActivity.this, "Inventory clicked", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Set up RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 1); // 1 columns
        recyclerView.setLayoutManager(gridLayoutManager);

        // Sample product list
        orderList = new ArrayList<>();
        for (int i = 1; i <= 6; i++) {
            orderList.add(new Orders("Order " + i, i,i));
        }

        OrdersAdapter ordersAdapter = new OrdersAdapter(this, orderList);
        recyclerView.setAdapter(ordersAdapter);
    }
}
