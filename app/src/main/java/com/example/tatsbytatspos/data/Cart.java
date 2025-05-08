package com.example.tatsbytatspos.data;

import android.content.Context;
import com.example.tatsbytatspos.model.Order;
import com.example.tatsbytatspos.model.OrderItem;
import com.example.tatsbytatspos.model.Product;
import com.example.tatsbytatspos.data.Database;

import java.util.ArrayList;
import java.util.List;

public class Cart {
    private Order currentOrder;
    private OrderDatabase orderDatabase;
    private Database productDatabase;
    private static Cart instance;

    private Cart(Context context) {
        orderDatabase = new OrderDatabase(context);
        productDatabase = new Database(context);
        currentOrder = new Order();
    }

    public static synchronized Cart getInstance(Context context) {
        if (instance == null) {
            instance = new Cart(context);
        }
        return instance;
    }

    public void addItem(Product product, int quantity) {
        // Check if product already exists in cart
        for (OrderItem item : currentOrder.getOrderItems()) {
            if (item.getProductId() == product.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return;
            }
        }

        // Add new item
        OrderItem newItem = new OrderItem(
                currentOrder.getOrderId(),
                product.getId(),
                quantity,
                product.getPrice(),
                product.getName()
        );
        currentOrder.addOrderItem(newItem);
    }

    public void removeItem(OrderItem item) {
        currentOrder.removeOrderItem(item);
    }

    public void updateItemQuantity(OrderItem item, int quantity) {
        item.setQuantity(quantity);
        currentOrder.calculateTotal();
    }

    public List<OrderItem> getItems() {
        return currentOrder.getOrderItems();
    }

    public double getTotal() {
        return currentOrder.getTotalAmount();
    }

    public void clear() {
        currentOrder = new Order();
    }

    public boolean processOrder() {
        try {
            // Insert order first
            long orderId = orderDatabase.insertOrder(
                    currentOrder.getTotalAmount(),
                    currentOrder.getStatus()
            );

            if (orderId == -1) return false;

            // Set the order ID
            currentOrder.setOrderId(orderId);

            // Insert all order items
            for (OrderItem item : currentOrder.getOrderItems()) {
                boolean success = orderDatabase.insertOrderItem(
                        orderId,
                        item.getProductId(),
                        item.getQuantity(),
                        item.getUnitPrice()
                );

                if (!success) return false;

                // Update product quantity in inventory
                Product product = getProductById(item.getProductId());
                if (product != null) {
                    int newQuantity = product.getQuantity() - item.getQuantity();
                    productDatabase.updateProduct(
                            product.getId(),
                            product.getName(),
                            product.getPrice(),
                            newQuantity,
                            product.getImage()
                    );
                }
            }

            // Clear the cart after successful order
            clear();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Product getProductById(int productId) {
        android.database.Cursor cursor = productDatabase.getAllProducts();
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(Database.COLUMN_ID));
                if (id == productId) {
                    String name = cursor.getString(cursor.getColumnIndexOrThrow(Database.COLUMN_NAME));
                    double price = cursor.getDouble(cursor.getColumnIndexOrThrow(Database.COLUMN_PRICE));
                    int quantity = cursor.getInt(cursor.getColumnIndexOrThrow(Database.COLUMN_QUANTITY));
                    byte[] image = cursor.getBlob(cursor.getColumnIndexOrThrow(Database.COLUMN_IMAGE));
                    cursor.close();
                    return new Product(id, name, price, quantity, image);
                }
            } while (cursor.moveToNext());
            cursor.close();
        }
        return null;
    }
}