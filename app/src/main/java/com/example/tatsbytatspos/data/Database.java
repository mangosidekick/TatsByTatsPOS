package com.example.tatsbytatspos.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Database.db";
    private static final int DATABASE_VERSION = 1;

    //product table
    public static final String TABLE_NAME = "products";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_NAME = "name";
    public static final String COLUMN_PRICE = "price";
    public static final String COLUMN_QUANTITY = "quantity";
    public static final String COLUMN_IMAGE = "image";

    //order table
    public static final String ORDER_TABLE_NAME = "orders";
    public static final String COLUMN_ORDER_ID = "order_id";
    public static final String COLUMN_PRODUCT_ID = "product_id";
    public static final String COLUMN_ORDER_QUANTITY = "order_quantity";
    public static final String COLUMN_ORDER_DATETIME = "order_datetime";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProductTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_PRICE + " REAL NOT NULL, " +
                COLUMN_QUANTITY + " INTEGER NOT NULL, " +
                COLUMN_IMAGE + " BLOB)";

        String createOrderTableSQL = "CREATE TABLE " + ORDER_TABLE_NAME + " (" +
                COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_PRODUCT_ID + " INTEGER NOT NULL, " +
                COLUMN_ORDER_QUANTITY + " INTEGER NOT NULL, " +
                COLUMN_ORDER_DATETIME + " TEXT NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_PRODUCT_ID + ") REFERENCES " + TABLE_NAME + "(" + COLUMN_ID + "))";

        db.execSQL(createProductTableSQL);
        db.execSQL(createOrderTableSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert a product
    public boolean insertProduct(String name, double price, int quantity, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_IMAGE, image);
        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public boolean insertOrder(int productId, int orderQuantity, String orderDateTime) {
        SQLiteDatabase db = this.getWritableDatabase();

        // Optional: Check if product exists and quantity is sufficient
        Cursor cursor = db.rawQuery("SELECT " + COLUMN_QUANTITY + " FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=?", new String[]{String.valueOf(productId)});
        if (cursor.moveToFirst()) {
            int currentStock = cursor.getInt(0);
            if (currentStock < orderQuantity) {
                cursor.close();
                return false; // Not enough stock
            }

            // Update product quantity
            ContentValues productUpdate = new ContentValues();
            productUpdate.put(COLUMN_QUANTITY, currentStock - orderQuantity);
            db.update(TABLE_NAME, productUpdate, COLUMN_ID + "=?", new String[]{String.valueOf(productId)});

            // Insert into order table
            ContentValues orderValues = new ContentValues();
            orderValues.put(COLUMN_PRODUCT_ID, productId);
            orderValues.put(COLUMN_ORDER_QUANTITY, orderQuantity);
            orderValues.put(COLUMN_ORDER_DATETIME, orderDateTime);

            long result = db.insert(ORDER_TABLE_NAME, null, orderValues);
            cursor.close();
            return result != -1;
        }

        cursor.close();
        return false;
    }

    // Get all products
    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    // Update product by ID
    public boolean updateProduct(int id, String name, double price, int quantity, byte[] image) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("price", price);
        values.put("quantity", quantity);
        values.put("image", image);
        int rows = db.update("products", values, "id=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public boolean deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("products", "id=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }
}

