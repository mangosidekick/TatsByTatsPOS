package com.example.tatsbytatspos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tatsbytatspos.R;
import com.example.tatsbytatspos.data.Database;
import com.example.tatsbytatspos.model.User;
import com.example.tatsbytatspos.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText usernameEditText;
    private TextInputEditText passwordEditText;
    private AutoCompleteTextView roleSpinner;
    private MaterialButton loginButton;
    private Database database;
    private SessionManager sessionManager;
    private static final String[] ROLES = {"ADMIN", "MANAGER", "CASHIER"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize components
        database = new Database(this);
        sessionManager = new SessionManager(this);

        // Check if user is already logged in
        if (sessionManager.isLoggedIn()) {
            navigateToMainActivity();
            finish();
            return;
        }

        // Initialize views
        usernameEditText = findViewById(R.id.usernameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        roleSpinner = findViewById(R.id.roleSpinner);
        loginButton = findViewById(R.id.loginButton);

        // Setup role spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, ROLES);
        roleSpinner.setAdapter(adapter);

        // Setup login button click listener
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        String username = usernameEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String selectedRole = roleSpinner.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password) || TextUtils.isEmpty(selectedRole)) {
            Toast.makeText(this, "Please enter username, password, and select a role", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validate role
        if (!selectedRole.equals("ADMIN") && !selectedRole.equals("MANAGER") && !selectedRole.equals("CASHIER")) {
            Toast.makeText(this, "Please select a valid role", Toast.LENGTH_SHORT).show();
            return;
        }

        // First verify if user exists with given credentials
        String[] columns = {Database.COLUMN_USER_ID, Database.COLUMN_USERNAME, Database.COLUMN_USER_ROLE};
        String selection = Database.COLUMN_USERNAME + "=? AND " + Database.COLUMN_PASSWORD + "=?";
        String[] selectionArgs = {username, password};

        android.database.Cursor cursor = database.getReadableDatabase().query(
                Database.USERS_TABLE_NAME,
                columns,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            int userIdIndex = cursor.getColumnIndex(Database.COLUMN_USER_ID);
            int roleIndex = cursor.getColumnIndex(Database.COLUMN_USER_ROLE);

            if (userIdIndex < 0 || roleIndex < 0) {
                Toast.makeText(this, "Database error: Required columns not found", Toast.LENGTH_SHORT).show();
                cursor.close();
                return;
            }

            int userId = cursor.getInt(userIdIndex);
            String actualRole = cursor.getString(roleIndex);

            // Verify if selected role matches user's actual role
            if (!selectedRole.equals(actualRole)) {
                Toast.makeText(this, "Invalid role selected for this user", Toast.LENGTH_SHORT).show();
                cursor.close();
                return;
            }

            // Create login session
            sessionManager.createLoginSession(userId, username, actualRole);

            cursor.close();

            // Navigate to main activity
            navigateToMainActivity();
            finish();
        } else {
            Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show();
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}