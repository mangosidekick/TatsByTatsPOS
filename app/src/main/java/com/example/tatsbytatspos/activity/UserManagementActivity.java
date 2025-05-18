package com.example.tatsbytatspos.activity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tatsbytatspos.R;
import com.example.tatsbytatspos.adapter.UserAdapter;
import com.example.tatsbytatspos.data.Database;
import com.example.tatsbytatspos.model.User;
import com.example.tatsbytatspos.utils.SessionManager;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class UserManagementActivity extends BaseActivity {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private Database database;
    private List<User> userList;
    private FloatingActionButton fabAddUser;

    @Override
    protected boolean hasAccess() {
        return sessionManager.isAdmin(); // Only admin can access this activity
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_management);

        database = new Database(this);
        userList = new ArrayList<>();

        // Initialize views
        recyclerView = findViewById(R.id.recyclerViewUsers);
        fabAddUser = findViewById(R.id.fabAddUser);

        // Set up FAB click listener
        fabAddUser.setOnClickListener(v -> showAddUserDialog());

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(userList, this::showEditUserDialog, this::deleteUser);
        recyclerView.setAdapter(userAdapter);

        // Setup FAB
        fabAddUser.setOnClickListener(v -> showAddUserDialog());

        // Load users
        loadUsers();
    }

    private void loadUsers() {
        userList.clear();
        Cursor cursor = null;
        try {
            cursor = database.getAllUsers();
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int id = cursor.getInt(cursor.getColumnIndexOrThrow(Database.COLUMN_USER_ID));
                    String username = cursor.getString(cursor.getColumnIndexOrThrow(Database.COLUMN_USERNAME));
                    String role = cursor.getString(cursor.getColumnIndexOrThrow(Database.COLUMN_USER_ROLE));
                    User user = new User(username, "", role);
                    user.setId(id);
                    userList.add(user);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Toast.makeText(this, "Error loading users: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        userAdapter.notifyDataSetChanged();
    }

    private void showAddUserDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_user, null);

        EditText etUsername = view.findViewById(R.id.etUsername);
        EditText etPassword = view.findViewById(R.id.etPassword);
        EditText etRole = view.findViewById(R.id.etRole);

        builder.setView(view)
                .setTitle("Add New User")
                .setPositiveButton("Add", (dialog, which) -> {
                    String username = etUsername.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    String role = etRole.getText().toString().trim().toUpperCase();

                    if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
                        Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean success = database.addUser(username, password, role);
                    if (success) {
                        loadUsers();
                        Toast.makeText(this, "User added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to add user", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void showEditUserDialog(User user) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_add_edit_user, null);

        EditText etUsername = view.findViewById(R.id.etUsername);
        EditText etPassword = view.findViewById(R.id.etPassword);
        EditText etRole = view.findViewById(R.id.etRole);

        etUsername.setText(user.getUsername());
        etRole.setText(user.getRole());

        builder.setView(view)
                .setTitle("Edit User")
                .setPositiveButton("Update", (dialog, which) -> {
                    String username = etUsername.getText().toString().trim();
                    String password = etPassword.getText().toString().trim();
                    String role = etRole.getText().toString().trim().toUpperCase();

                    if (username.isEmpty() || role.isEmpty()) {
                        Toast.makeText(this, "Username and role are required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    boolean success = database.updateUser(user.getId(), username, password, role);
                    if (success) {
                        loadUsers();
                        Toast.makeText(this, "User updated successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to update user", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteUser(User user) {
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete this user?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    boolean success = database.deleteUser(user.getId());
                    if (success) {
                        loadUsers();
                        Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to delete user", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }
}