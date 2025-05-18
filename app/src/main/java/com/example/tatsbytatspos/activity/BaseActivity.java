package com.example.tatsbytatspos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.tatsbytatspos.utils.SessionManager;
import com.example.tatsbytatspos.activity.LoginActivity;

public abstract class BaseActivity extends AppCompatActivity {
    protected SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);

        // Check if user is logged in
        if (!sessionManager.isLoggedIn()) {
            redirectToLogin();
            return;
        }

        // Check role-based access
        if (!hasAccess()) {
            Toast.makeText(this, "Access denied. Insufficient privileges.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
    }

    protected abstract boolean hasAccess();

    protected void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    protected boolean isManager() {
        return sessionManager.isManager();
    }

    protected boolean isCashier() {
        return sessionManager.isCashier();
    }
}