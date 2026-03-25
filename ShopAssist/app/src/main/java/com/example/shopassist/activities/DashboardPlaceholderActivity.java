package com.example.shopassist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopassist.R;

public class DashboardPlaceholderActivity extends AppCompatActivity {

    private TextView tvDashboardTitle;
    private TextView tvDashboardMessage;
    private TextView tvNextSteps;
    private Button btnBackToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_placeholder);

        tvDashboardTitle = findViewById(R.id.tvDashboardTitle);
        tvDashboardMessage = findViewById(R.id.tvDashboardMessage);
        tvNextSteps = findViewById(R.id.tvNextSteps);
        btnBackToLogin = findViewById(R.id.btnBackToLogin);

        String userName = getIntent().getStringExtra("user_name");
        String role = getIntent().getStringExtra("role");

        if (userName == null || userName.isEmpty()) {
            userName = "Demo User";
        }

        if (role == null || role.isEmpty()) {
            role = "Customer";
        }

        tvDashboardTitle.setText(role + " Prototype");
        tvDashboardMessage.setText("Hello " + userName + ". This is a starter dashboard placeholder for the prototype.");

        String nextSteps;
        switch (role) {
            case "Shopper":
                nextSteps = "Next: open requests, request details, accept request, and update request status.";
                break;
            case "Admin":
                nextSteps = "Next: users list, reported issues, and shopper verification with mock data.";
                break;
            default:
                nextSteps = "Next: create request, add items, choose delivery slot, add location, and track progress.";
                break;
        }

        tvNextSteps.setText(nextSteps);

        btnBackToLogin.setOnClickListener(v -> {
            Intent intent = new Intent(DashboardPlaceholderActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}

