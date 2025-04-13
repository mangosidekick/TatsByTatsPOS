package com.example.tatsbytatspos;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ImageButton sideBarButton;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private NavigationView navigationView;
    private Button confirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        //NAV BAR STUFF RAAAAAAGHHH

        //WORK ON THIS LATERRRRRR

        drawerLayout = findViewById(R.id.drawer_layout);
        sideBarButton = findViewById(R.id.sideBarButton);
        navigationView = findViewById(R.id.navigationView);

        sideBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.nav_home) {
                    Toast.makeText(MainActivity.this, "Home clicked", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_history) {
                    Toast.makeText(MainActivity.this, "Order History clicked", Toast.LENGTH_SHORT).show();
                } else if (id == R.id.nav_inventory) {
                    Toast.makeText(MainActivity.this, "Inventory clicked", Toast.LENGTH_SHORT).show();
                }

                // DO NOT close the drawer here (per your request)
                return false;
            }
        });

        //test
        confirmButton = findViewById(R.id.confirm_button);

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "Inventory clicked", Toast.LENGTH_SHORT).show();
            }
        });

        sideBarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        //recycle view stuff
        // Initialize RecyclerView and set up the LayoutManager
        recyclerView = findViewById(R.id.menuRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        int numberOfColumns = 2; // Set the number of columns you want
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, numberOfColumns);
        recyclerView.setLayoutManager(gridLayoutManager);

        // Initialize the product list
        productList = new ArrayList<>();
        productList.add(new Product(R.drawable.product_image, "Product 1", 29.99));
        productList.add(new Product(R.drawable.product_image, "Product 2", 19.99));
        productList.add(new Product(R.drawable.product_image, "Product 3", 39.99));
        productList.add(new Product(R.drawable.product_image, "Product 3", 39.99));
        productList.add(new Product(R.drawable.product_image, "Product 3", 39.99));
        productList.add(new Product(R.drawable.product_image, "Product 3", 39.99));
        productList.add(new Product(R.drawable.product_image, "Product 3", 39.99));
        productList.add(new Product(R.drawable.product_image, "Product 3", 39.99));
        productList.add(new Product(R.drawable.product_image, "Product 3", 39.99));
        productList.add(new Product(R.drawable.product_image, "Product 3", 39.99));

        // Set up the adapter
        productAdapter = new ProductAdapter(productList);
        recyclerView.setAdapter(productAdapter);
    }
}

