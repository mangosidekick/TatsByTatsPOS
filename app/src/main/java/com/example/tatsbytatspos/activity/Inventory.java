package com.example.tatsbytatspos.activity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatsbytatspos.data.ProductDatabase;
import com.example.tatsbytatspos.model.Product;
import com.example.tatsbytatspos.adapter.ProductAdapter;
import com.example.tatsbytatspos.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
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

    FloatingActionButton fab;
    ProductDatabase db;

    private Bitmap selectedImage = null;
    private ImageView imagePreviewRef; // <- Hold reference to image preview

    ActivityResultLauncher<Intent> imagePickerLauncher;

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
        fab = findViewById(R.id.fab);
        db = new ProductDatabase(this);


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

        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));

        boolean showStarButton = true;
        boolean showInventoryQuantity = true;

        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(this, productList, showStarButton, showInventoryQuantity, product -> {
            showEditProductDialog(product);

        });
        recyclerView.setAdapter(productAdapter);

        loadProductsFromDatabase();

        // Setup image picker with callback
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            selectedImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                            if (imagePreviewRef != null && selectedImage != null) {
                                imagePreviewRef.setImageBitmap(selectedImage); // Show preview
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        fab.setOnClickListener(v -> showAddProductDialog());
    }

    private void showAddProductDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_product, null);

        EditText editName = dialogView.findViewById(R.id.editName);
        EditText editPrice = dialogView.findViewById(R.id.editPrice);
        EditText editQuantity = dialogView.findViewById(R.id.editQuantity);
        imagePreviewRef = dialogView.findViewById(R.id.imagePreview); // Set reference
        Button btnPickImage = dialogView.findViewById(R.id.btnPickImage);

        btnPickImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Add", (d, which) -> {
                    String name = editName.getText().toString();
                    String priceStr = editPrice.getText().toString();
                    String quantityStr = editQuantity.getText().toString();

                    double price = Double.parseDouble(priceStr);
                    int quantity = Integer.parseInt(quantityStr);
                    byte[] imageBytes = getBytesFromBitmap(selectedImage);

                    boolean success = db.insertProduct(name, price, quantity, imageBytes);

                    if (success) {
                        Toast.makeText(this, "Product added", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Insert failed", Toast.LENGTH_SHORT).show();
                    }

                    if (success) {
                        Toast.makeText(this, "Product added", Toast.LENGTH_SHORT).show();
                        loadProductsFromDatabase();
                    } else {
                        Toast.makeText(this, "Insert failed", Toast.LENGTH_SHORT).show();
                    }


                    selectedImage = null; // Reset after adding
                    imagePreviewRef = null; // Prevent holding onto view
                })
                .setNegativeButton("Cancel", (d, which) -> {
                    selectedImage = null;
                    imagePreviewRef = null;
                })
                .create();

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.round_rectangle);
        dialog.show();
    }

    private byte[] getBytesFromBitmap(Bitmap bitmap) {
        // Resize the image to a max width/height (e.g., 300x300)
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 80, stream); // 80% quality
        return stream.toByteArray();
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
    }

    private void showEditProductDialog(Product product) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_add_product, null);

        EditText editName = dialogView.findViewById(R.id.editName);
        EditText editPrice = dialogView.findViewById(R.id.editPrice);
        EditText editQuantity = dialogView.findViewById(R.id.editQuantity);
        imagePreviewRef = dialogView.findViewById(R.id.imagePreview);
        Button btnPickImage = dialogView.findViewById(R.id.btnPickImage);

        // Pre-fill values
        editName.setText(product.getName());
        editPrice.setText(String.valueOf(product.getPrice()));
        editQuantity.setText(String.valueOf(product.getQuantity()));

        Bitmap bitmap = BitmapFactory.decodeByteArray(product.getImage(), 0, product.getImage().length);
        selectedImage = bitmap;
        imagePreviewRef.setImageBitmap(bitmap);

        btnPickImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setPositiveButton("Update", (d, which) -> {
                    String name = editName.getText().toString();
                    String priceStr = editPrice.getText().toString();
                    String quantityStr = editQuantity.getText().toString();

                    if (name.isEmpty() || priceStr.isEmpty() || quantityStr.isEmpty() || selectedImage == null) {
                        Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double price = Double.parseDouble(priceStr);
                    int quantity = Integer.parseInt(quantityStr);
                    byte[] imageBytes = getBytesFromBitmap(selectedImage);

                    boolean success = db.updateProduct(product.getId(), name, price, quantity, imageBytes);
                    if (success) {
                        Toast.makeText(this, "Product updated", Toast.LENGTH_SHORT).show();
                        loadProductsFromDatabase();
                    } else {
                        Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
                    }

                    selectedImage = null;
                    imagePreviewRef = null;
                })
                .setNegativeButton("Cancel", (d, which) -> {
                    selectedImage = null;
                    imagePreviewRef = null;
                })
                .setNeutralButton("Delete", (d, which) -> {
                    db.deleteProduct(product.getId());
                    Toast.makeText(this, "Product deleted", Toast.LENGTH_SHORT).show();
                    loadProductsFromDatabase();
                })
                .create();

        dialog.getWindow().setBackgroundDrawableResource(R.drawable.round_rectangle);
        dialog.show();
    }
}
