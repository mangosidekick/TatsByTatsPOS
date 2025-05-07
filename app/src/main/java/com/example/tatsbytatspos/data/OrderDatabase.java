package com.example.tatsbytatspos.data;

import static com.example.tatsbytatspos.data.ProductDatabase.TABLE_NAME;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class OrderDatabase extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "order_database.db";
    private static final int DATABASE_VERSION = 1;

    // Order Table
    private static final String TABLE_ORDERS = "orders";
    private static final String COLUMN_ORDER_ID = "order_id";
    private static final String COLUMN_ORDER_NUMBER = "order_number";
    private static final String COLUMN_PAYMENT_METHOD = "payment_method";
    private static final String COLUMN_DATE = "date";
    private static final String COLUMN_TIME = "time";

    // Order Items Table
    private static final String TABLE_ORDER_ITEMS = "order_items";
    private static final String COLUMN_ITEM_ID = "item_id";
    private static final String COLUMN_PRODUCT_NAME = "product_name";
    private static final String COLUMN_PRODUCT_PRICE = "product_price";
    private static final String COLUMN_QUANTITY = "quantity";

    public OrderDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createOrdersTable = "CREATE TABLE " + TABLE_ORDERS + " (" +
                COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PAYMENT_METHOD + " TEXT, " +
                COLUMN_DATE + " TEXT, " +
                COLUMN_TIME + " TEXT)";

        String createOrderItemsTable = "CREATE TABLE " + TABLE_ORDER_ITEMS + " (" +
                COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ORDER_ID + " INTEGER, " +
                COLUMN_PRODUCT_NAME + " TEXT, " +
                COLUMN_PRODUCT_PRICE + " REAL, " +
                COLUMN_QUANTITY + " INTEGER, " +
                "FOREIGN KEY(" + COLUMN_ORDER_ID + ") REFERENCES " + TABLE_ORDERS + "(" + COLUMN_ORDER_ID + "))";

        db.execSQL(createOrdersTable);
        db.execSQL(createOrderItemsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDER_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ORDERS);
        onCreate(db);
    }

    // Insert an order and return the new order ID
    public boolean insertOrder(String orderNumber, String name, double price, int quantity, String paymentMethod, String date, String time) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_NUMBER, orderNumber);
        values.put(COLUMN_PRODUCT_NAME, name);
        values.put(COLUMN_PRODUCT_PRICE, price);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_PAYMENT_METHOD, paymentMethod);
        values.put(COLUMN_DATE, date);
        values.put(COLUMN_TIME, time);
        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    // Insert a product item for an order
    public boolean insertOrderItem(long orderId, String name, double price, int quantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ORDER_ID, orderId);
        values.put(COLUMN_PRODUCT_NAME, name);
        values.put(COLUMN_PRODUCT_PRICE, price);
        values.put(COLUMN_QUANTITY, quantity);
        return db.insert(TABLE_ORDER_ITEMS, null, values) != -1;
    }

    // Get all orders (can be expanded for full history support)
    public Cursor getAllOrders() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ORDERS + " ORDER BY " + COLUMN_ORDER_ID + " DESC", null);
    }

    // Get items for a specific order
    public Cursor getItemsForOrder(long orderId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_ORDER_ITEMS + " WHERE " + COLUMN_ORDER_ID + " = ?", new String[]{String.valueOf(orderId)});
    }
}
