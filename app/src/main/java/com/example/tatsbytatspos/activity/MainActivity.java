package com.example.tatsbytatspos.activity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatsbytatspos.data.Database;
import com.example.tatsbytatspos.database.DatabaseHelper;
import com.example.tatsbytatspos.fragment.PaymentFragment;
import com.example.tatsbytatspos.model.Product;
import com.example.tatsbytatspos.adapter.ProductAdapter;
import com.example.tatsbytatspos.R;
import com.example.tatsbytatspos.utils.SessionManager;
import android.view.Menu;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends BaseActivity {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ImageButton sideBarButton;
    private RecyclerView recyclerView;
    private ProductAdapter productAdapter;
    private List<Product> productList;
    private NavigationView navigationView;
    private Button confirmButton;
    private Button resetButton;
    private SessionManager sessionManager;
    Database db;
    private DatabaseHelper dbHelper;

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
        confirmButton = findViewById(R.id.confirmButton);
        resetButton = findViewById(R.id.reset_button);
        recyclerView = findViewById(R.id.menuRecyclerView);
        fragmentLayout = findViewById(R.id.fragmentLayout);

        db = new Database(this);
        dbHelper = new DatabaseHelper(this);
        sessionManager = new SessionManager(this);
        productList = new ArrayList<>();

        // Configure navigation based on user role
        configureNavigationForUserRole();

        // Set up the Toolbar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        // Sidebar button opens drawer
        sideBarButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Navigation drawer item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                if (sessionManager.isCashier()) {
                    startActivity(new Intent(MainActivity.this, CreateOrderActivity.class));
                } else {
                    Toast.makeText(MainActivity.this, "Already on this screen!", Toast.LENGTH_SHORT).show();
                }
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(MainActivity.this, OrderHistory.class));
            } else if (id == R.id.nav_inventory) {
                if (sessionManager.isAdmin() || sessionManager.isManager()) {
                    startActivity(new Intent(MainActivity.this, Inventory.class));
                }
            } else if (id == R.id.nav_settings) {
                if (sessionManager.isAdmin() || sessionManager.isManager()) {
                    startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                }
            } else if (id == R.id.nav_user_management && sessionManager.isAdmin()) {
                startActivity(new Intent(MainActivity.this, UserManagementActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });


        // Set up RecyclerView
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2); // 2 columns
        recyclerView.setLayoutManager(gridLayoutManager);

        boolean showStarButton = false;
        boolean showInventoryQuantity = false;

        productAdapter = new ProductAdapter(this, productList, showStarButton, showInventoryQuantity, null);
        recyclerView.setAdapter(productAdapter);

        loadProductsFromDatabase();

    }

    @Override
    protected boolean hasAccess() {
        return true; // All roles can access main activity
    }

    private void configureNavigationForUserRole() {
        Menu navMenu = navigationView.getMenu();

        // First, make all menu items visible
        showAllMenuItems(navMenu);

        // Configure navigation based on user role
        if (sessionManager.isCashier()) {
            // Cashiers can only access home and order history
            hideMenuItem(navMenu, R.id.nav_inventory);
            hideMenuItem(navMenu, R.id.nav_settings);
            hideMenuItem(navMenu, R.id.nav_user_management);

            // Update toolbar title to indicate cashier mode
            toolbar.setTitle("POS System (Cashier Mode)");
        } else if (sessionManager.isManager()) {
            // Managers can access everything except user management
            hideMenuItem(navMenu, R.id.nav_user_management);
            toolbar.setTitle("POS System (Manager Mode)");
        } else if (sessionManager.isAdmin()) {
            // Admins have full access
            toolbar.setTitle("POS System (Admin Mode)");
        } else {
            // Default case - restrict access
            hideMenuItem(navMenu, R.id.nav_inventory);
            hideMenuItem(navMenu, R.id.nav_settings);
            hideMenuItem(navMenu, R.id.nav_user_management);
        }
    }

    private void hideMenuItem(Menu menu, int itemId) {
        MenuItem item = menu.findItem(itemId);
        if (item != null) {
            item.setVisible(false);
        }
    }

    private void showAllMenuItems(Menu menu) {
        for (int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            if (item != null) {
                item.setVisible(true);
            }
        }
    }

    private void loadProductsFromDatabase() {
        if (productList == null) {
            productList = new ArrayList<>();
        } else {
            productList.clear();
        }

        Cursor cursor = db.getAllProducts();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
                double price = cursor.getDouble(cursor.getColumnIndexOrThrow("price"));
                int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
                byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow("image"));

                productList.add(new Product(id, name, price, quantity, image));

            } while (cursor.moveToNext());
            cursor.close();
        }
        productAdapter.updateList(productList);

        //confirm button action
        confirmButton.setOnClickListener(v -> {
            // Check if any products are selected
            boolean hasSelectedProducts = false;
            for (Product product : productAdapter.getProductList()) {
                if (product.getOrderQuantity() > 0) {
                    hasSelectedProducts = true;
                    break;
                }
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
                boolean success = dbHelper.insertOrderWithItems(productIds, quantities, currentDateTime, paymentMethod);

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