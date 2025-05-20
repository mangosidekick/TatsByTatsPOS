package com.example.tatsbytatspos.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import com.example.tatsbytatspos.R;

public class Login extends AppCompatActivity {

    EditText etUsername, etPassword;
    RadioGroup roleRadioGroup;
    RadioButton rbAdmin, rbCashier;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        roleRadioGroup = findViewById(R.id.roleRadioGroup);
        rbAdmin = findViewById(R.id.rbAdmin);
        rbCashier = findViewById(R.id.rbCashier);
        btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String username = etUsername.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            int selectedRoleId = roleRadioGroup.getCheckedRadioButtonId();

            if (username.isEmpty() || password.isEmpty() || selectedRoleId == -1) {
                Toast.makeText(this, "Please enter all fields and select a role", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRole = findViewById(selectedRoleId);
            String role = selectedRole.getText().toString();

            SharedPreferences prefs = getSharedPreferences("MyAppPrefs", MODE_PRIVATE);
            prefs.edit().putString("role", role).apply();

            Intent intent;
            if (role.equals("Admin") && username.equals("admin") && password.equals("admin123")) {
                prefs.edit().putString("role", "Admin").apply();
                startActivity(new Intent(this, MainActivity.class));
            } else if (role.equals("Cashier") && username.equals("cashier") && password.equals("cashier123")) {
                prefs.edit().putString("role", "Cashier").apply();
                startActivity(new Intent(this, MainActivity.class));
            }
        });


    }
}