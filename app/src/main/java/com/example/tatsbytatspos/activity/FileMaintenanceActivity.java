package com.example.tatsbytatspos.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatsbytatspos.R;
import com.example.tatsbytatspos.adapter.FileMaintenanceAdapter;
import com.example.tatsbytatspos.data.Database;
import com.example.tatsbytatspos.model.Product;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class FileMaintenanceActivity extends BaseActivity {

    @Override
    protected boolean hasAccess() {
        // Only managers/admins can access file maintenance
        return isManager();
    }

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private ImageButton sideBarButton;
    private RecyclerView recyclerView;
    private FileMaintenanceAdapter adapter;
    private NavigationView navigationView;
    private TextView tvSelectedCount;
    private List<Product> productList;
    private Database db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_maintenance);

        // Initialize database
        db = new Database(this);

        // Initialize views
        drawerLayout = findViewById(R.id.drawer_layout);
        toolbar = findViewById(R.id.toolbar);
        sideBarButton = findViewById(R.id.sideBarButton);
        navigationView = findViewById(R.id.navigationView);
        recyclerView = findViewById(R.id.recyclerView);
        tvSelectedCount = findViewById(R.id.tvSelectedCount);

        // Set up the Toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        // Sidebar button opens drawer
        sideBarButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        // Navigation drawer item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                startActivity(new Intent(FileMaintenanceActivity.this, MainActivity.class));
            } else if (id == R.id.nav_history) {
                startActivity(new Intent(FileMaintenanceActivity.this, OrderHistory.class));
            } else if (id == R.id.nav_inventory) {
                Toast.makeText(FileMaintenanceActivity.this, "Already on this screen!", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_settings) {
                startActivity(new Intent(FileMaintenanceActivity.this, SettingsActivity.class));
            } else if (id == R.id.nav_user_management && isManager()) {
                startActivity(new Intent(FileMaintenanceActivity.this, UserManagementActivity.class));
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            return true;
        });

        // Set up RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        productList = new ArrayList<>();
        adapter = new FileMaintenanceAdapter(this, productList, this::showEditPriceDialog);
        recyclerView.setAdapter(adapter);

        // Set up reset buttons
        findViewById(R.id.btnResetSelected).setOnClickListener(v -> resetSelectedProducts());
        findViewById(R.id.btnResetAll).setOnClickListener(v -> resetAllProducts());

        // Load products
        loadProducts();
    }

    private void loadProducts() {
        productList.clear();
        Cursor cursor = null;
        try {
            cursor = db.getAllProducts();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(Database.COLUMN_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(Database.COLUMN_NAME));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(Database.COLUMN_PRICE));
                    int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(Database.COLUMN_QUANTITY));
                    byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(Database.COLUMN_IMAGE));

                    Product product = new Product(id, name, price, quantity, image);
                    productList.add(product);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        adapter.notifyDataSetChanged();
        updateSelectedCount();
    }

    private void showEditPriceDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_edit_price, null);
        EditText etPrice = view.findViewById(R.id.etPrice);
        etPrice.setText(String.valueOf(product.getPrice()));

        builder.setView(view)
                .setTitle("Edit Price for " + product.getName())
                .setPositiveButton("Update", (dialog, which) -> {
                    try {
                        double newPrice = Double.parseDouble(etPrice.getText().toString());
                        if (newPrice < 0) {
                            Toast.makeText(this, "Price cannot be negative", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        boolean success = db.updateProduct(
                                product.getId(),
                                product.getName(),
                                newPrice,
                                product.getQuantity(),
                                product.getImage(),
                                true
                        );

                        if (success) {
                            Toast.makeText(this, "Price updated successfully", Toast.LENGTH_SHORT).show();
                            loadProducts();
                        } else {
                            Toast.makeText(this, "Failed to update price", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Invalid price format", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void resetSelectedProducts() {
        List<Product> selectedProducts = adapter.getSelectedProducts();
        if (selectedProducts.isEmpty()) {
            Toast.makeText(this, "No products selected", Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("Reset Selected Products")
                .setMessage("Are you sure you want to reset the quantity of selected products to 0?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean allSuccess = true;
                    for (Product product : selectedProducts) {
                        boolean success = db.updateProduct(
                                product.getId(),
                                product.getName(),
                                product.getPrice(),
                                0, // Reset quantity to 0
                                product.getImage(),
                                true
                        );
                        if (!success) {
                            allSuccess = false;
                        }
                    }

                    if (allSuccess) {
                        Toast.makeText(this, "Selected products reset successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Some products failed to reset", Toast.LENGTH_SHORT).show();
                    }

                    adapter.clearSelection();
                    loadProducts();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void resetAllProducts() {
        new AlertDialog.Builder(this)
                .setTitle("Reset All Products")
                .setMessage("Are you sure you want to reset the quantity of ALL products to 0?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean allSuccess = true;
                    for (Product product : productList) {
                        boolean success = db.updateProduct(
                                product.getId(),
                                product.getName(),
                                product.getPrice(),
                                0, // Reset quantity to 0
                                product.getImage(),
                                true
                        );
                        if (!success) {
                            allSuccess = false;
                        }
                    }

                    if (allSuccess) {
                        Toast.makeText(this, "All products reset successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Some products failed to reset", Toast.LENGTH_SHORT).show();
                    }

                    adapter.clearSelection();
                    loadProducts();
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void updateSelectedCount() {
        int count = adapter.getSelectedProducts().size();
        tvSelectedCount.setText(String.format("Selected: %d", count));
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProducts();
    }
}