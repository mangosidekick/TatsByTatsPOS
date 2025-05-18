package com.example.tatsbytatspos.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.tatsbytatspos.R;
import com.example.tatsbytatspos.data.Database;
import com.example.tatsbytatspos.utils.SessionManager;

public class SettingsActivity extends AppCompatActivity {
    private SessionManager sessionManager;
    private Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        sessionManager = new SessionManager(this);
        database = new Database(this);

        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings_container, new SettingsFragment())
                    .commit();
        }
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        private SessionManager sessionManager;
        private Database database;

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.preferences, rootKey);
            sessionManager = new SessionManager(requireContext());
            database = new Database(requireContext());

            // Setup click listeners for preferences
            setupPreferences();
        }

        private void setupPreferences() {
            // Profile settings
            Preference editProfilePref = findPreference("edit_profile");
            if (editProfilePref != null) {
                editProfilePref.setOnPreferenceClickListener(preference -> {
                    showEditProfileDialog();
                    return true;
                });
            }

            // Change password
            Preference changePasswordPref = findPreference("change_password");
            if (changePasswordPref != null) {
                changePasswordPref.setOnPreferenceClickListener(preference -> {
                    showChangePasswordDialog();
                    return true;
                });
            }

            // Logout
            Preference logoutPref = findPreference("logout");
            if (logoutPref != null) {
                logoutPref.setOnPreferenceClickListener(preference -> {
                    showLogoutConfirmationDialog();
                    return true;
                });
            }
        }

        private void showEditProfileDialog() {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_profile, null);
            EditText etUsername = view.findViewById(R.id.etUsername);
            etUsername.setText(sessionManager.getUsername());

            new AlertDialog.Builder(requireContext())
                    .setTitle("Edit Profile")
                    .setView(view)
                    .setPositiveButton("Save", (dialog, which) -> {
                        String newUsername = etUsername.getText().toString().trim();
                        if (newUsername.isEmpty()) {
                            Toast.makeText(getContext(), "Username cannot be empty", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Update username in database and session
                        if (database.updateUser(sessionManager.getUserId(), newUsername, "", sessionManager.getRole())) {
                            sessionManager.createLoginSession(sessionManager.getUserId(), newUsername, sessionManager.getRole());
                            Toast.makeText(getContext(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to update profile", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        private void showChangePasswordDialog() {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_password, null);
            EditText etCurrentPassword = view.findViewById(R.id.etCurrentPassword);
            EditText etNewPassword = view.findViewById(R.id.etNewPassword);
            EditText etConfirmPassword = view.findViewById(R.id.etConfirmPassword);

            new AlertDialog.Builder(requireContext())
                    .setTitle("Change Password")
                    .setView(view)
                    .setPositiveButton("Save", (dialog, which) -> {
                        String currentPassword = etCurrentPassword.getText().toString();
                        String newPassword = etNewPassword.getText().toString();
                        String confirmPassword = etConfirmPassword.getText().toString();

                        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (!newPassword.equals(confirmPassword)) {
                            Toast.makeText(getContext(), "New passwords do not match", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // TODO: Implement password change in Database class
                        // if (database.changePassword(sessionManager.getUserId(), currentPassword, newPassword)) {
                        //     Toast.makeText(getContext(), "Password changed successfully", Toast.LENGTH_SHORT).show();
                        // } else {
                        //     Toast.makeText(getContext(), "Failed to change password", Toast.LENGTH_SHORT).show();
                        // }
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        }

        private void showLogoutConfirmationDialog() {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        sessionManager.logout();
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    })
                    .setNegativeButton("No", null)
                    .show();
        }
    }
}