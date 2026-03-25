package com.example.shopassist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopassist.R;
import com.example.shopassist.data.MockRepository;
import com.example.shopassist.utils.AccessibilityUtils;
import com.example.shopassist.utils.SessionManager;

public class RoleSelectionActivity extends AppCompatActivity {

    private TextView tvRoleWelcome;
    private Button btnCustomer;
    private Button btnShopper;
    private Button btnAdmin;

    private String userName;
    private String userEmail;
    private String userPassword;
    private boolean registrationFlow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role_selection);

        tvRoleWelcome = findViewById(R.id.tvRoleWelcome);
        btnCustomer = findViewById(R.id.btnCustomer);
        btnShopper = findViewById(R.id.btnShopper);
        btnAdmin = findViewById(R.id.btnAdmin);

        userName = getIntent().getStringExtra("user_name");
        userEmail = getIntent().getStringExtra("user_email");
        userPassword = getIntent().getStringExtra("user_password");
        registrationFlow = getIntent().getBooleanExtra("registration_flow", false);

        if (userName == null || userName.isEmpty()) {
            userName = "Demo User";
        }

        tvRoleWelcome.setText("Welcome, " + userName);

        btnCustomer.setOnClickListener(v -> openDashboard("Customer"));
        btnShopper.setOnClickListener(v -> openDashboard("Shopper"));
        btnAdmin.setOnClickListener(v -> openDashboard("Admin"));
        btnAdmin.setVisibility(View.GONE);

        AccessibilityUtils.applySettings(this);
    }

    private void openDashboard(String role) {
        if (!registrationFlow || userPassword == null || userPassword.isEmpty()) {
            Toast.makeText(this, "Registration details are missing. Please register again.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if ("Admin".equalsIgnoreCase(role)) {
            Toast.makeText(this, "Admin accounts are seeded by the app and not created from registration.", Toast.LENGTH_SHORT).show();
            return;
        }

        com.example.shopassist.models.User user = MockRepository.getInstance().registerUser(userName, userEmail, userPassword, role);
        if (user == null) {
            Toast.makeText(this, "That email is already registered.", Toast.LENGTH_SHORT).show();
            return;
        }

        SessionManager.saveUserSession(this, user);

        Intent intent;
        if ("Customer".equals(role)) {
            intent = new Intent(RoleSelectionActivity.this, CustomerHomeActivity.class);
        } else if ("Shopper".equals(role)) {
            intent = new Intent(RoleSelectionActivity.this, ShopperHomeActivity.class);
        } else if ("Admin".equals(role)) {
            intent = new Intent(RoleSelectionActivity.this, AdminDashboardActivity.class);
        } else {
            intent = new Intent(RoleSelectionActivity.this, DashboardPlaceholderActivity.class);
        }

        intent.putExtra("user_name", userName);
        intent.putExtra("role", role);
        startActivity(intent);
        finish();
    }
}
