package com.example.tatsbytatspos.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "TatsByTatsPOSSession";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_USER_ID = "userId";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_ROLE = "role";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(int userId, String username, String role) {
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_ROLE, role);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }

    public boolean isManager() {
        String role = pref.getString(KEY_ROLE, "");
        return "MANAGER".equalsIgnoreCase(role);
    }

    public boolean isAdmin() {
        String role = pref.getString(KEY_ROLE, "");
        return "ADMIN".equalsIgnoreCase(role);
    }

    public boolean isCashier() {
        String role = pref.getString(KEY_ROLE, "");
        return "CASHIER".equalsIgnoreCase(role);
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, -1);
    }

    public String getUsername() {
        return pref.getString(KEY_USERNAME, null);
    }

    public String getRole() {
        return pref.getString(KEY_ROLE, null);
    }
}