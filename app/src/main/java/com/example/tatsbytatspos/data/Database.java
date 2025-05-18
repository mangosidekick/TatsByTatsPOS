package com.example.tatsbytatspos.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.tatsbytatspos.model.User;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "Database.db";
    private static final int DATABASE_VERSION = 3;

    // Users table
    public static final String USERS_TABLE_NAME = "users";
    public static final String COLUMN_USER_ID = "user_id";
    public static final String COLUMN_USERNAME = "username";
    public static final String COLUMN_PASSWORD = "password";
    public static final String COLUMN_USER_ROLE = "role";

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
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createProductTableSQL = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT NOT NULL, " +
                COLUMN_PRICE + " REAL NOT NULL, " +
                COLUMN_QUANTITY + " INTEGER NOT NULL, " +
                COLUMN_IMAGE + " BLOB, " +
                COLUMN_HIDDEN + " INTEGER NOT NULL DEFAULT 0)";

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

        // Users table
        String createUsersTableSQL = "CREATE TABLE " + USERS_TABLE_NAME + " (" +
                COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_USERNAME + " TEXT NOT NULL UNIQUE, " +
                COLUMN_PASSWORD + " TEXT NOT NULL, " +
                COLUMN_USER_ROLE + " TEXT NOT NULL)";

        db.execSQL(createProductTableSQL);
        db.execSQL(createOrdersTableSQL);
        db.execSQL(createOrderItemsTableSQL);
        db.execSQL(createUsersTableSQL);

        // Insert default admin account
        ContentValues adminValues = new ContentValues();
        adminValues.put(COLUMN_USERNAME, "admin");
        adminValues.put(COLUMN_PASSWORD, "admin123");
        adminValues.put(COLUMN_USER_ROLE, "ADMIN");
        db.insert(USERS_TABLE_NAME, null, adminValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            // Add hidden column to products table
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_HIDDEN + " INTEGER NOT NULL DEFAULT 0");
        }
        if (oldVersion < 3) {
            // Create users table
            String createUsersTableSQL = "CREATE TABLE " + USERS_TABLE_NAME + " (" +
                    COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USERNAME + " TEXT NOT NULL UNIQUE, " +
                    COLUMN_PASSWORD + " TEXT NOT NULL, " +
                    COLUMN_USER_ROLE + " TEXT NOT NULL)";
            db.execSQL(createUsersTableSQL);

            // Insert default admin account
            ContentValues adminValues = new ContentValues();
            adminValues.put(COLUMN_USERNAME, "admin");
            adminValues.put(COLUMN_PASSWORD, "admin123");
            adminValues.put(COLUMN_USER_ROLE, "MANAGER");
            db.insert(USERS_TABLE_NAME, null, adminValues);
        }
    }

    // Insert a product
    public boolean insertProduct(String name, double price, int quantity, byte[] image) {
        return insertProduct(name, price, quantity, image, false);
    }

    // Insert a product with hidden status
    public boolean insertProduct(String name, double price, int quantity, byte[] image, boolean hidden) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name);
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_QUANTITY, quantity);
        values.put(COLUMN_IMAGE, image);
        values.put(COLUMN_HIDDEN, hidden ? 1 : 0);
        long result = db.insert(TABLE_NAME, null, values);
        return result != -1;
    }

    // User management methods
    public User authenticateUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;
        Cursor cursor = null;

        try {
            String[] columns = {COLUMN_USER_ID, COLUMN_USERNAME, COLUMN_PASSWORD, COLUMN_USER_ROLE};
            String selection = COLUMN_USERNAME + "=? AND " + COLUMN_PASSWORD + "=?";
            String[] selectionArgs = {username, password};

            cursor = db.query(USERS_TABLE_NAME, columns, selection, selectionArgs, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int idIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_ID);
                int roleIndex = cursor.getColumnIndexOrThrow(COLUMN_USER_ROLE);

                if (idIndex >= 0 && roleIndex >= 0) {
                    int id = cursor.getInt(idIndex);
                    String role = cursor.getString(roleIndex);
                    // Convert MANAGER role to ADMIN for compatibility
                    if ("MANAGER".equalsIgnoreCase(role)) {
                        role = "ADMIN";
                    }
                    user = new User(username, password, role);
                    user.setId(id);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
        }
        return user;
    }

    public Cursor getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        try {
            return db.query(USERS_TABLE_NAME,
                    new String[]{COLUMN_USER_ID, COLUMN_USERNAME, COLUMN_USER_ROLE},
                    null, null, null, null, null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean addUser(String username, String password, String role) {
        if (username == null || password == null || role == null) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username.trim());
        values.put(COLUMN_PASSWORD, password.trim());
        values.put(COLUMN_USER_ROLE, role.trim());

        try {
            long result = db.insertOrThrow(USERS_TABLE_NAME, null, values);
            return result != -1;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    public boolean updateUser(int userId, String username, String password, String role) {
        if (username == null || role == null || userId <= 0) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USERNAME, username.trim());
        if (password != null && !password.isEmpty()) {
            values.put(COLUMN_PASSWORD, password.trim());
        }
        values.put(COLUMN_USER_ROLE, role.trim());

        try {
            String whereClause = COLUMN_USER_ID + "=?";
            String[] whereArgs = {String.valueOf(userId)};
            int result = db.update(USERS_TABLE_NAME, values, whereClause, whereArgs);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    public boolean deleteUser(int userId) {
        if (userId <= 0) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            String whereClause = COLUMN_USER_ID + "=?";
            String[] whereArgs = {String.valueOf(userId)};
            int result = db.delete(USERS_TABLE_NAME, whereClause, whereArgs);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    public boolean insertOrderWithItems(List<Integer> productIds, List<Integer> quantities, String dateTime, String paymentMethod) {
        if (productIds == null || quantities == null || dateTime == null || paymentMethod == null ||
                productIds.isEmpty() || quantities.isEmpty() || productIds.size() != quantities.size()) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            // Insert into orders table
            ContentValues orderValues = new ContentValues();
            orderValues.put(COLUMN_ORDER_DATETIME, dateTime.trim());
            orderValues.put(COLUMN_ORDER_PAYMENT, paymentMethod.trim());
            long orderId = db.insertOrThrow(ORDER_TABLE_NAME, null, orderValues);
            if (orderId == -1) {
                return false;
            }

            // Insert each product into order_items
            for (int i = 0; i < productIds.size(); i++) {
                int productId = productIds.get(i);
                int quantity = quantities.get(i);

                if (productId <= 0 || quantity <= 0) {
                    throw new IllegalArgumentException("Invalid product ID or quantity");
                }

                // Check stock
                Cursor cursor = null;
                try {
                    cursor = db.rawQuery("SELECT " + COLUMN_QUANTITY + " FROM " + TABLE_NAME +
                            " WHERE " + COLUMN_ID + "=?", new String[]{String.valueOf(productId)});

                    if (cursor != null && cursor.moveToFirst()) {
                        int currentStock = cursor.getInt(0);
                        if (currentStock < quantity) {
                            throw new IllegalStateException("Not enough stock for product ID: " + productId);
                        }

                        // Reduce stock
                        ContentValues updateValues = new ContentValues();
                        updateValues.put(COLUMN_QUANTITY, currentStock - quantity);
                        int updateResult = db.update(TABLE_NAME, updateValues,
                                COLUMN_ID + "=?", new String[]{String.valueOf(productId)});

                        if (updateResult <= 0) {
                            throw new IllegalStateException("Failed to update stock for product ID: " + productId);
                        }
                    } else {
                        throw new IllegalStateException("Product not found with ID: " + productId);
                    }
                } finally {
                    if (cursor != null) {
                        cursor.close();
                    }
                }

                // Add to order_items
                ContentValues itemValues = new ContentValues();
                itemValues.put(COLUMN_ORDER_ITEM_ORDER_ID, orderId);
                itemValues.put(COLUMN_ORDER_ITEM_PRODUCT_ID, productId);
                itemValues.put(COLUMN_ORDER_ITEM_QUANTITY, quantity);
                long itemResult = db.insertOrThrow(ORDER_ITEMS_TABLE_NAME, null, itemValues);

                if (itemResult == -1) {
                    throw new IllegalStateException("Failed to insert order item for product ID: " + productId);
                }
            }

            db.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // Get all orders with product details
    public Cursor getAllOrdersWithProductDetails() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
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

            cursor = db.rawQuery(query, null);
            if (cursor != null) {
                cursor.moveToFirst();
            }
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null) {
                cursor.close();
            }
            if (db != null) {
                db.close();
            }
            return null;
        }
    }

    // Get all products
    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_HIDDEN + "=0";
            cursor = db.rawQuery(query, null);
            return cursor;
        } catch (Exception e) {
            e.printStackTrace();
            if (cursor != null) {
                cursor.close();
            }
            return null;
        }
    }

    // Update a product
    public boolean updateProduct(int id, String name, double price, int quantity, byte[] image, boolean updateInventory) {
        if (id <= 0 || name == null || name.isEmpty() || price < 0 || quantity < 0) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, name.trim());
        values.put(COLUMN_PRICE, price);
        values.put(COLUMN_QUANTITY, quantity);
        if (image != null) {
            values.put(COLUMN_IMAGE, image);
        }

        try {
            String whereClause = COLUMN_ID + "=?";
            String[] whereArgs = {String.valueOf(id)};
            int result = db.update(TABLE_NAME, values, whereClause, whereArgs);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }

    // Delete a product
    public boolean deleteProduct(int id) {
        if (id <= 0) {
            return false;
        }

        SQLiteDatabase db = this.getWritableDatabase();
        try {
            // Instead of actually deleting, we'll mark it as hidden
            ContentValues values = new ContentValues();
            values.put(COLUMN_HIDDEN, 1);

            String whereClause = COLUMN_ID + "=?";
            String[] whereArgs = {String.valueOf(id)};
            int result = db.update(TABLE_NAME, values, whereClause, whereArgs);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            db.close();
        }
    }


}

