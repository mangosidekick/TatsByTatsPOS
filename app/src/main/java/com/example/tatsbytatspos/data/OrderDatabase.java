package com.example.tatsbytatspos.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OrderDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "OrderDB.db";
    private static final int DATABASE_VERSION = 1;

    // Orders table
    public static final String TABLE_ORDERS = "orders";
    public static final String COLUMN_ORDER_ID = "order_id";
    public static final String COLUMN_ORDER_DATE = "order_date";
    public static final String COLUMN_TOTAL_AMOUNT = "total_amount";
    public static final String COLUMN_STATUS = "status";

    // Order items table
    public static final String TABLE_ORDER_ITEMS = "order_items";
    public static final String COLUMN_ITEM_ID = "item_id";
    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_UNIT_PRICE = "unit_price";
    public static final String COLUMN_SUBTOTAL = "subtotal";

    public OrderDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create orders table
        String createOrdersTable = "CREATE TABLE " + TABLE_ORDERS + " (" +
                COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ORDER_DATE + " DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                COLUMN_TOTAL_AMOUNT + " REAL NOT NULL, " +
                COLUMN_STATUS + " TEXT NOT NULL)";

        // Create order items table
        String createOrderItemsTable = "CREATE TABLE " + TABLE_ORDER_ITEMS + " (" +
                COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ORDER_ID + " INTEGER NOT NULL, " +
                COLUMN_PRODUCT_ID + " INTEGER NOT NULL, " +
                COLUMN_QUANTITY + " INTEGER NOT NULL, " +
                COLUMN_UNIT_PRICE + " REAL NOT NULL, " +
                COLUMN_SUBTOTAL + " REAL NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + "), " +
                "FOREIGN KEY(" + COLUMN_PRODUCT_ID + ") REFERENCES " + ProductDatabase.TABLE_NAME + "(" + ProductDatabase.COLUMN_ID + "))";

        db.execSQL(createOrdersTable);
        db.execSQL(createOrderItemsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        onCreate(db);
    }

    // Insert a new order and return the order ID
    public long insertOrder(double totalAmount, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_TOTAL_AMOUNT, totalAmount);
        values.put(COLUMN_STATUS, status);
        return db.insert(TABLE_ORDERS, null, values);
    }

    // Insert order items
    public boolean insertOrderItem(long orderId, int productId, int quantity, double unitPrice) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_ID, orderId);
        values.put(COLUMN_PRODUCT_ID, productId);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_UNIT_PRICE, unitPrice);
        values.put(COLUMN_SUBTOTAL, quantity * unitPrice);
        long result = db.insert(TABLE_ORDER_ITEMS, null, values);
        return result != -1;
    }

    // Get all orders
    public Cursor getAllOrders() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ORDERS + " ORDER BY " + COLUMN_ORDER_DATE + " DESC", null);
    }

    // Get order items for a specific order
    public Cursor getOrderItems(long orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ORDER_ITEMS + " WHERE " + COLUMN_ORDER_ID + " = ?",
                new String[]{String.valueOf(orderId)});
    }

    // Update order status
    public boolean updateOrderStatus(long orderId, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_STATUS, status);
        int rows = db.update(TABLE_ORDERS, values, COLUMN_ORDER_ID + "=?", new String[]{String.valueOf(orderId)});
        return rows > 0;
    }
}