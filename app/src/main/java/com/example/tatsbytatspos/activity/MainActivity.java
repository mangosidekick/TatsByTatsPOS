package com.example.tatsbytatspos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatsbytatspos.fragment.PaymentFragment;
import com.example.tatsbytatspos.model.Product;
import com.example.tatsbytatspos.adapter.ProductAdapter;
import com.example.tatsbytatspos.R;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ImageButton sideBarButton;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private NavigationView navigationView;
    private Button confirmButton;
    private Button resetButton;
    private FrameLayout fragmentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        sideBarButton = findViewById(R.id.sideBarButton);
        navigationView = findViewById(R.id.navigationView);
        confirmButton = findViewById(R.id.confirm_button);
        resetButton = findViewById(R.id.resetButton);
        recyclerView = findViewById(R.id.menuRecyclerView);
        fragmentLayout = findViewById(R.id.fragment_layout);

        // Set up the Toolbar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // Sidebar button opens drawer
        sideBarButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Navigation drawer item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                Toast.makeText(MainActivity.this, "Already on this screen!", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(MainActivity.this, OrderHistory.class));
                //Toast.makeText(MainActivity.this, "History clicked", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_inventory) {
                startActivity(new Intent(MainActivity.this, Inventory.class));
                //Toast.makeText(MainActivity.this, "Inventory clicked", Toast.LENGTH_SHORT).show();
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Confirm button action
        confirmButton.setOnClickListener(v ->
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout),new PaymentFragment()).commit();

        // Reset button action
        resetButton.setOnClickListener(v ->
                Toast.makeText(MainActivity.this, "Order reset!", Toast.LENGTH_SHORT).show());

        // Set up RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 2 columns
        recyclerView.setLayoutManager(gridLayoutManager);

        // Sample product list
        productList = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            productList.add(new Product(R.drawable.product_image, "Product " + i, 19.99 + i));
        }

        boolean showStarButton = false;

        productAdapter = new ProductAdapter(productList, showStarButton);
        recyclerView.setAdapter(productAdapter);
    }
}
