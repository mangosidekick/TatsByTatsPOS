package com.example.tatsbytatspos.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatsbytatspos.data.Database;
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
    private androidx.appcompat.widget.SearchView searchView;
    Database db;

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
        Menu menu = navigationView.getMenu();
        confirmButton = findViewById(R.id.confirmButton);
        resetButton = findViewById(R.id.reset_button);
        recyclerView = findViewById(R.id.menuRecyclerView);
        fragmentLayout = findViewById(R.id.fragmentLayout);
        searchView = findViewById(R.id.searchView);

        // Set up search functionality
        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (productAdapter != null) {
                    productAdapter.filter(newText);
                }
                return true;
            }
        });
        searchView = findViewById(R.id.searchView);

        // Set up search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                productAdapter.filter(newText);
                return true;
            }
        });

        db = new Database(this);
        productList = new ArrayList<>();

        // Set up the Toolbar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // Sidebar button opens drawer
        sideBarButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
        String role = prefs.getString("role", "");

        if ("Cashier".equals(role)) {
            menu.findItem(R.id.nav_file_maintenance).setVisible(false); // Hide from view
        }

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
            } else if (id == R.id.nav_file_maintenance) {
                if ("Admin".equals(role)) {
                    startActivity(new Intent(MainActivity.this, FileMaintenance.class)
                            .putExtra("role", role));
                } else {
                    Toast.makeText(this, "Access denied: Admins only", Toast.LENGTH_SHORT).show();
                }
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });


        // Set up RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 5); // 2 columns
        recyclerView.setLayoutManager(gridLayoutManager);

        boolean showStarButton = false;
        boolean showInventoryQuantity = false;

        productAdapter = new ProductAdapter(this, productList, showStarButton, showInventoryQuantity, null, null);
        recyclerView.setAdapter(productAdapter);

        loadProductsFromDatabase();

    }

    private void loadProductsFromDatabase() {
        if (productList == null) {
            productList = new ArrayList<>();
        } else {
            productList.clear();
        }

        // Use getVisibleProducts to only show products that aren't hidden
        Cursor cursor = db.getVisibleProducts();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow("image"));

                // Check if product is out of stock
                if (quantity <= 0) {
                    Toast.makeText(this, name + " is out of stock!", Toast.LENGTH_SHORT).show();
                }

                Product product = new Product(id, name, price, quantity, image);
                productList.add(product);

            } while (cursor.moveToNext());
            cursor.close();
        }
        productAdapter.updateList(productList);

        //confirm button action
        confirmButton.setOnClickListener(v -> {
            // Check if any products are selected and validate stock
            boolean hasSelectedProducts = false;
            boolean hasOutOfStockItems = false;

            for (Product product : productAdapter.getProductList()) {
                if (product.getOrderQuantity() > 0) {
                    hasSelectedProducts = true;

                    // Check if there's enough inventory
                    if (product.getOrderQuantity() > product.getQuantity()) {
                        Toast.makeText(MainActivity.this, product.getName() + " is out of stock! Only " +
                                product.getQuantity() + " available.", Toast.LENGTH_SHORT).show();
                        hasOutOfStockItems = true;
                    }
                }
            }

            // Don't proceed if there are out-of-stock items
            if (hasOutOfStockItems) {
                return;
            }

            if (!hasSelectedProducts) {
                Toast.makeText(MainActivity.this, "Please select at least one product", Toast.LENGTH_SHORT).show();
                return;
            }

            // Generate order summary
            StringBuilder summary = new StringBuilder();
            StringBuilder totalSummary = new StringBuilder();

            double total = 0.0;

            for (Product product : productAdapter.getProductList()) {
                if (product.getOrderQuantity() > 0) {
                    double itemTotal = product.getPrice() * product.getOrderQuantity();
                    summary.append(product.getName())
                            .append(" x")
                            .append(product.getOrderQuantity())
                            .append(" = ₱")
                            .append(String.format(Locale.getDefault(), "%.2f", itemTotal))
                            .append("\n");
                    total += itemTotal;
                }
            }
            totalSummary.append("Total: ₱")
                    .append(String.format(Locale.getDefault(), "%.2f", total));

            // Show payment fragment with order summary
            PaymentFragment popup = PaymentFragment.newInstance(
                    summary.toString(),
                    totalSummary.toString()
            );

            // Set a listener to handle payment confirmation
            popup.setOnPaymentConfirmedListener((paymentMethod) -> {
                if (paymentMethod == null || paymentMethod.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Invalid payment method", Toast.LENGTH_SHORT).show();
                    return;
                }


                // Collect selected products
                List<Integer> productIds = new ArrayList<>();
                List<Integer> quantities = new ArrayList<>();

                for (Product product : productAdapter.getProductList()) {
                    if (product.getOrderQuantity() > 0) {
                        productIds.add(product.getId());
                        quantities.add(product.getOrderQuantity());
                    }
                }

                // Get current datetime
                String currentDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

                // Insert the order with the confirmed payment method
                boolean success = db.insertOrderWithItems(productIds, quantities, currentDateTime, paymentMethod);

                if (success) {
                    Toast.makeText(MainActivity.this, "Order completed successfully!", Toast.LENGTH_SHORT).show();
                    resetOrder(); // Reset after successful order
                } else {
                    Toast.makeText(MainActivity.this, "Failed to complete order. Please try again.", Toast.LENGTH_SHORT).show();
                }
            });

            popup.show(getSupportFragmentManager(), "myPaymentTag");
        });


        // Reset button action
        resetButton.setOnClickListener(v -> {
            resetOrder();
            Toast.makeText(MainActivity.this, "Order reset!", Toast.LENGTH_SHORT).show();
        });

    }

    // Method to reset all product order quantities
    private void resetOrder() {
        if (productAdapter != null && productAdapter.getProductList() != null) {
            for (Product product : productAdapter.getProductList()) {
                product.setOrderQuantity(0);
            }
            productAdapter.notifyDataSetChanged();
        }
    }
}