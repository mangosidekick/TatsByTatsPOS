package com.example.tatsbytatspos.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatsbytatspos.R;
import com.example.tatsbytatspos.adapter.OrderItemAdapter;
import com.example.tatsbytatspos.adapter.ProductAdapter;
import com.example.tatsbytatspos.data.Database;
import com.example.tatsbytatspos.database.DatabaseHelper;
import com.example.tatsbytatspos.fragment.PaymentFragment;
import com.example.tatsbytatspos.model.OrderItem;
import com.example.tatsbytatspos.model.Product;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CreateOrderActivity extends AppCompatActivity {
    private RecyclerView productsRecyclerView;
    private RecyclerView orderItemsRecyclerView;
    private ProductAdapter productAdapter;
    private OrderItemAdapter orderItemAdapter;
    private List<Product> productList;
    private List<OrderItem> orderItems;
    private TextView orderReferenceNumber;
    private TextView totalAmount;
    private EditText searchProducts;
    private Button proceedToPayment;
    private Database db;
    private DatabaseHelper dbHelper;
    private String currentOrderRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_order);

        initializeViews();
        setupRecyclerViews();
        generateOrderReference();
        loadProducts();
        setupListeners();
    }

    private void initializeViews() {
        productsRecyclerView = findViewById(R.id.productsRecyclerView);
        orderItemsRecyclerView = findViewById(R.id.orderItemsRecyclerView);
        orderReferenceNumber = findViewById(R.id.orderReferenceNumber);
        totalAmount = findViewById(R.id.totalAmount);
        searchProducts = findViewById(R.id.searchProducts);
        proceedToPayment = findViewById(R.id.proceedToPayment);
        db = new Database(this);
        dbHelper = new DatabaseHelper(this);
        productList = new ArrayList<>();
        orderItems = new ArrayList<>();
    }

    private void setupRecyclerViews() {
        // Setup products grid
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        productsRecyclerView.setLayoutManager(gridLayoutManager);
        productAdapter = new ProductAdapter(this, productList, false, false, product -> {
            int quantity = product.getOrderQuantity();
            if (quantity > 0) {
                addOrUpdateOrderItem(product, quantity);
            } else {
                removeOrderItem(product);
            }
            updateTotalAmount();
        });
        productsRecyclerView.setAdapter(productAdapter);

        // Setup order items list
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        orderItemsRecyclerView.setLayoutManager(linearLayoutManager);
        orderItemAdapter = new OrderItemAdapter(this, orderItems);
        orderItemsRecyclerView.setAdapter(orderItemAdapter);
    }

    private void generateOrderReference() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        currentOrderRef = "ORD-" + sdf.format(new Date());
        orderReferenceNumber.setText(String.format("Order #%s", currentOrderRef));
    }

    private void loadProducts() {
        try {
            Cursor cursor = db.getAllProducts();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(Database.COLUMN_ID));
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(Database.COLUMN_NAME));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(Database.COLUMN_PRICE));
                    int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(Database.COLUMN_QUANTITY));
                    byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(Database.COLUMN_IMAGE));

                    productList.add(new Product(id, name, price, quantity, image));
                } while (cursor.moveToNext());
                cursor.close();
            }
            productAdapter.updateList(productList);
        } catch (Exception e) {
            Toast.makeText(this, "Error loading products: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void setupListeners() {
        searchProducts.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterProducts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        proceedToPayment.setOnClickListener(v -> {
            if (orderItems.isEmpty()) {
                Toast.makeText(this, "Please add items to the order", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create order summary
            StringBuilder summary = new StringBuilder();
            for (OrderItem item : orderItems) {
                summary.append(item.getProductName())
                        .append(" x")
                        .append(item.getQuantity())
                        .append(" = ₱")
                        .append(String.format(Locale.getDefault(), "%.2f", item.getQuantity() * item.getUnitPrice()))
                        .append("\n");
            }

            PaymentFragment paymentFragment = new PaymentFragment();
            Bundle args = new Bundle();
            args.putString("order_summary", summary.toString());
            args.putString("order_total", String.format(Locale.getDefault(), "₱%.2f", calculateTotal()));
            paymentFragment.setArguments(args);

            // Set payment listener to handle order completion
            paymentFragment.setOnPaymentConfirmedListener(paymentMethod -> {
                // Prepare product IDs and quantities for database
                List<Integer> productIds = new ArrayList<>();
                List<Integer> quantities = new ArrayList<>();

                for (OrderItem item : orderItems) {
                    productIds.add(item.getProductId());
                    quantities.add(item.getQuantity());
                }

                // Get current date/time for order
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                String orderDate = sdf.format(new Date());

                // Insert order with items
                boolean success = dbHelper.insertOrderWithItems(productIds, quantities, orderDate, paymentMethod);

                if (!success) {
                    Toast.makeText(CreateOrderActivity.this, "Failed to save order", Toast.LENGTH_SHORT).show();
                }
            });

            paymentFragment.show(getSupportFragmentManager(), "PaymentFragment");
        });
    }

    private void filterProducts(String query) {
        List<Product> filteredList = new ArrayList<>();
        for (Product product : productList) {
            if (product.getName().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(product);
            }
        }
        productAdapter.updateList(filteredList);
    }

    private void addOrUpdateOrderItem(Product product, int quantity) {
        boolean itemExists = false;
        for (OrderItem item : orderItems) {
            if (item.getProductId() == product.getId()) {
                item.setQuantity(quantity);
                itemExists = true;
                break;
            }
        }

        if (!itemExists) {
            // Using 0 as a temporary orderId, will be set when order is created
            OrderItem newItem = new OrderItem(0, product.getId(), quantity, product.getPrice(), product.getName());
            orderItems.add(newItem);
        }

        orderItemAdapter.notifyDataSetChanged();
    }

    private void removeOrderItem(Product product) {
        orderItems.removeIf(item -> item.getProductId() == product.getId());
        orderItemAdapter.notifyDataSetChanged();
    }

    private void updateTotalAmount() {
        double total = calculateTotal();
        totalAmount.setText(String.format(Locale.getDefault(), "Total: ₱%.2f", total));
    }

    private double calculateTotal() {
        double total = 0;
        for (OrderItem item : orderItems) {
            total += item.getUnitPrice() * item.getQuantity();
        }
        return total;
    }
}