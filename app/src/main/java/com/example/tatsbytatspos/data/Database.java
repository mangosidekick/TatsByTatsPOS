package com.example.tatsbytatspos.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.List;

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
    public static final String COLUMN_HIDDEN = "hidden";

    // Orders table
    public static final String ORDER_TABLE_NAME = "orders";
    public static final String COLUMN_ORDER_ID = "order_id";
    public static final String COLUMN_ORDER_DATETIME = "order_datetime";
    public static final String COLUMN_ORDER_PAYMENT = "order_payment";

    // Order items table
    public static final String ORDER_ITEMS_TABLE_NAME = "order_items";
    public static final String COLUMN_ITEM_ID = "item_id";
    public static final String COLUMN_ORDER_ITEM_ORDER_ID = "order_id";
    public static final String COLUMN_ORDER_ITEM_PRODUCT_ID = "product_id";
    public static final String COLUMN_ORDER_ITEM_QUANTITY = "order_quantity";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        try {
            SQLiteDatabase db = this.getWritableDatabase();
            if (db != null) {
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProductTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_PRICE + " REAL NOT NULL, " +
                COLUMN_QUANTITY + " INTEGER NOT NULL, " +
                COLUMN_IMAGE + " BLOB, " +
                COLUMN_HIDDEN + " INTEGER DEFAULT 0)";

        // Orders table
        String createOrdersTableSQL = "CREATE TABLE " + ORDER_TABLE_NAME + " (" +
                COLUMN_ORDER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ORDER_DATETIME + " TEXT NOT NULL, " +
                COLUMN_ORDER_PAYMENT + " TEXT NOT NULL)";

        // Order items table
        String createOrderItemsTableSQL = "CREATE TABLE " + ORDER_ITEMS_TABLE_NAME + " (" +
                COLUMN_ITEM_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_ORDER_ITEM_ORDER_ID + " INTEGER NOT NULL, " +
                COLUMN_ORDER_ITEM_PRODUCT_ID + " INTEGER NOT NULL, " +
                COLUMN_ORDER_ITEM_QUANTITY + " INTEGER NOT NULL, " +
                "FOREIGN KEY(" + COLUMN_ORDER_ITEM_ORDER_ID + ") REFERENCES " + ORDER_TABLE_NAME + "(" + COLUMN_ORDER_ID + "), " +
                "FOREIGN KEY(" + COLUMN_ORDER_ITEM_PRODUCT_ID + ") REFERENCES " + TABLE_NAME + "(" + COLUMN_ID + "))";

        db.execSQL(createProductTableSQL);
        db.execSQL(createOrdersTableSQL);
        db.execSQL(createOrderItemsTableSQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // Insert a product
    public boolean insertProduct(String name, double price, int quantity, byte[] image, boolean isHidden) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_IMAGE, image);
        values.put(COLUMN_HIDDEN, 0); // Not hidden by default
        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    public boolean insertOrderWithItems(List<Integer> productIds, List<Integer> quantities, String dateTime, String paymentMethod) {
        if (productIds.size() != quantities.size()) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            // Insert into orders table
            ContentValues orderValues = new ContentValues();
            orderValues.put(COLUMN_ORDER_DATETIME, dateTime);
            orderValues.put(COLUMN_ORDER_PAYMENT, paymentMethod);
            long orderId = db.insert(ORDER_TABLE_NAME, null, orderValues);
            if (orderId == -1) return false;

            // Insert each product into order_items
            for (int i = 0; i < productIds.size(); i++) {
                int productId = productIds.get(i);
                int quantity = quantities.get(i);

                // Check stock
                Cursor cursor = db.rawQuery("SELECT " + COLUMN_QUANTITY + " FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=?", new String[]{String.valueOf(productId)});
                if (cursor.moveToFirst()) {
                    int currentStock = cursor.getInt(0);
                    if (currentStock < quantity) {
                        cursor.close();
                        db.endTransaction();
                        return false; // Not enough stock
                    }

                    // Reduce stock
                    ContentValues updateValues = new ContentValues();
                    updateValues.put(COLUMN_QUANTITY, currentStock - quantity);
                    db.update(TABLE_NAME, updateValues, COLUMN_ID + "=?", new String[]{String.valueOf(productId)});
                    cursor.close();
                } else {
                    db.endTransaction();
                    return false; // Product not found
                }

                // Add to order_items
                ContentValues itemValues = new ContentValues();
                itemValues.put(COLUMN_ORDER_ITEM_ORDER_ID, orderId);
                itemValues.put(COLUMN_ORDER_ITEM_PRODUCT_ID, productId);
                itemValues.put(COLUMN_ORDER_ITEM_QUANTITY, quantity);
                db.insert(ORDER_ITEMS_TABLE_NAME, null, itemValues);
            }

            db.setTransactionSuccessful();
            return true;
        } finally {
            db.endTransaction();
        }
    }

    // Get all products
    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME, null);
    }

    // Get visible products (not hidden)
    public Cursor getVisibleProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_HIDDEN + "=0", null);
    }

    // Update product by ID
    public boolean updateProduct(int id, String name, double price, int quantity, byte[] image, boolean hidden) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("price", price);
        values.put("quantity", quantity);
        values.put("image", image);
        values.put("hidden", hidden ? 1 : 0);
        int rows = db.update("products", values, "id=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    // Update product by ID (without changing image)
    public boolean updateProduct(int id, String name, double price, int quantity, boolean hidden) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("price", price);
        values.put("quantity", quantity);
        values.put("hidden", hidden ? 1 : 0);
        int rows = db.update("products", values, "id=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    // Toggle product visibility
    public boolean toggleProductVisibility(int id, boolean hidden) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("hidden", hidden ? 1 : 0);
        int rows = db.update("products", values, "id=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    // Update product quantity
    public boolean updateProductQuantity(int id, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", newQuantity);
        int rows = db.update("products", values, "id=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    public boolean deleteProduct(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        int rows = db.delete("products", "id=?", new String[]{String.valueOf(id)});
        return rows > 0;
    }

    // Get all orders with product details
    public Cursor getAllOrdersWithProductDetails() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT " +
                "o." + COLUMN_ORDER_ID + " AS order_id, " +
                "o." + COLUMN_ORDER_DATETIME + " AS order_datetime, " +
                "o." + COLUMN_ORDER_PAYMENT + " AS payment_method, " +
                "p." + COLUMN_NAME + " AS product_name, " +
                "p." + COLUMN_PRICE + " AS product_price, " +
                "i." + COLUMN_ORDER_ITEM_QUANTITY + " AS quantity_ordered " +
                "FROM " + ORDER_TABLE_NAME + " o " +
                "JOIN " + ORDER_ITEMS_TABLE_NAME + " i ON o." + COLUMN_ORDER_ID + " = i." + COLUMN_ORDER_ITEM_ORDER_ID + " " +
                "JOIN " + TABLE_NAME + " p ON i." + COLUMN_ORDER_ITEM_PRODUCT_ID + " = p." + COLUMN_ID + " " +
                "ORDER BY o." + COLUMN_ORDER_ID + " DESC";

        return db.rawQuery(query, null);
    }
}

