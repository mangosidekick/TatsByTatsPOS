package com.example.tatsbytatspos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Inventory extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ImageButton sideBarButton;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

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
                startActivity(new Intent(Inventory.this, MainActivity.class));
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(Inventory.this, OrderHistory.class));
                //Toast.makeText(MainActivity.this, "History clicked", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_inventory) {
                //startActivity(new Intent(Inventory.this, Inventory.class));
                Toast.makeText(Inventory.this, "Already on this screen!", Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, "Inventory clicked", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Set up RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 2 columns
        recyclerView.setLayoutManager(gridLayoutManager);

        // Sample product list
        productList = new ArrayList<>();
        for (int i = 1; i <= 10; i++) {
            productList.add(new Product(R.drawable.product_image, "Product " + i, 19.99 + i));
        }

        boolean showStarButton = true;

        productAdapter = new ProductAdapter(productList, showStarButton);
        recyclerView.setAdapter(productAdapter);
    }
}
