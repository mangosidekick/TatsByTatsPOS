package com.example.tatsbytatspos.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.tatsbytatspos.model.Orders;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "TatsByTatsPOS.db";
    private static final int DATABASE_VERSION = 1;

    // Table name
    private static final String TABLE_ORDERS = "orders";

    // Column names
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ORDER_SUMMARY = "order_summary";
    private static final String COLUMN_TOTAL_AMOUNT = "total_amount";
    private static final String COLUMN_PAYMENT_METHOD = "payment_method";
    private static final String COLUMN_PAYMENT_STATUS = "payment_status";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    // Create table SQL query
    private static final String CREATE_TABLE_ORDERS = "CREATE TABLE " + TABLE_ORDERS + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_ORDER_SUMMARY + " TEXT,"
            + COLUMN_TOTAL_AMOUNT + " REAL,"
            + COLUMN_PAYMENT_METHOD + " TEXT,"
            + COLUMN_PAYMENT_STATUS + " TEXT,"
            + COLUMN_TIMESTAMP + " DATETIME DEFAULT CURRENT_TIMESTAMP"
            + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ORDERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // A more graceful upgrade approach that preserves data when possible
        try {
            // For future version upgrades, add migration logic here
            // Example: if (oldVersion < 2) { /* add new columns or tables */ }

            // If no specific migration path, fall back to recreating the database
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
            onCreate(db);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long insertOrder(String orderSummary, double totalAmount, String paymentMethod, String paymentStatus) {
        long id = -1;
        SQLiteDatabase db = null;
        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COLUMN_ORDER_SUMMARY, orderSummary);
            values.put(COLUMN_TOTAL_AMOUNT, totalAmount);
            values.put(COLUMN_PAYMENT_METHOD, paymentMethod);
            values.put(COLUMN_PAYMENT_STATUS, paymentStatus);

            id = db.insert(TABLE_ORDERS, null, values);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return id;
    }

    public List<Orders> getAllOrders() {
        List<Orders> orders = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ORDERS + " ORDER BY " + COLUMN_TIMESTAMP + " DESC";

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.getReadableDatabase();
            cursor = db.rawQuery(selectQuery, null);

            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int orderSummaryIndex = cursor.getColumnIndexOrThrow(COLUMN_ORDER_SUMMARY);
                    int totalAmountIndex = cursor.getColumnIndexOrThrow(COLUMN_TOTAL_AMOUNT);
                    int paymentMethodIndex = cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_METHOD);
                    int paymentStatusIndex = cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_STATUS);
                    int idIndex = cursor.getColumnIndexOrThrow(COLUMN_ID);

                    Orders order = new Orders(
                            cursor.getString(orderSummaryIndex),
                            cursor.getDouble(totalAmountIndex),
                            cursor.getString(paymentMethodIndex),
                            cursor.getString(paymentStatusIndex)
                    );
                    order.setId(cursor.getInt(idIndex));
                    orders.add(order);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return orders;
    }

    public int updateOrder(int orderId, String orderSummary, double totalAmount, String paymentMethod, String paymentStatus) {
        int rowsAffected = 0;
        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            ContentValues values = new ContentValues();

            values.put(COLUMN_ORDER_SUMMARY, orderSummary);
            values.put(COLUMN_TOTAL_AMOUNT, totalAmount);
            values.put(COLUMN_PAYMENT_METHOD, paymentMethod);
            values.put(COLUMN_PAYMENT_STATUS, paymentStatus);

            rowsAffected = db.update(TABLE_ORDERS, values, COLUMN_ID + "=?",
                    new String[]{String.valueOf(orderId)});
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return rowsAffected;
    }

    public Orders getOrderById(int orderId) {
        Orders order = null;
        SQLiteDatabase db = null;
        Cursor cursor = null;

        try {
            db = this.getReadableDatabase();
            cursor = db.query(TABLE_ORDERS, null, COLUMN_ID + "=?",
                    new String[]{String.valueOf(orderId)}, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int orderSummaryIndex = cursor.getColumnIndexOrThrow(COLUMN_ORDER_SUMMARY);
                int totalAmountIndex = cursor.getColumnIndexOrThrow(COLUMN_TOTAL_AMOUNT);
                int paymentMethodIndex = cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_METHOD);
                int paymentStatusIndex = cursor.getColumnIndexOrThrow(COLUMN_PAYMENT_STATUS);

                order = new Orders(
                        cursor.getString(orderSummaryIndex),
                        cursor.getDouble(totalAmountIndex),
                        cursor.getString(paymentMethodIndex),
                        cursor.getString(paymentStatusIndex)
                );
                order.setId(orderId);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return order;
    }

    public boolean deleteOrder(int orderId) {
        boolean success = false;
        SQLiteDatabase db = null;

        try {
            db = this.getWritableDatabase();
            success = db.delete(TABLE_ORDERS, COLUMN_ID + "=?", new String[]{String.valueOf(orderId)}) > 0;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        return success;
    }
}