package com.example.shopassist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopassist.R;
import com.example.shopassist.data.MockRepository;
import com.example.shopassist.models.ShoppingRequest;
import com.example.shopassist.utils.AccessibilityUtils;
import com.example.shopassist.utils.SessionManager;

public class CustomerHomeActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private TextView tvCurrentRequestSummary;
    private Button btnCreateRequest;
    private Button btnViewCurrentRequest;
    private Button btnCustomerAccessibility;
    private Button btnLogout;

    private String userName;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);

        tvWelcome = findViewById(R.id.tvCustomerWelcome);
        tvCurrentRequestSummary = findViewById(R.id.tvCurrentRequestSummary);
        btnCreateRequest = findViewById(R.id.btnCreateRequest);
        btnViewCurrentRequest = findViewById(R.id.btnViewCurrentRequest);
        btnCustomerAccessibility = findViewById(R.id.btnCustomerAccessibility);
        btnLogout = findViewById(R.id.btnCustomerLogout);

        loadUser();
        bindCurrentRequestSummary();

        btnCreateRequest.setOnClickListener(v ->
                startActivity(new Intent(CustomerHomeActivity.this, CreateRequestActivity.class))
        );

        btnViewCurrentRequest.setOnClickListener(v -> openCurrentRequest());
        btnCustomerAccessibility.setOnClickListener(v ->
                startActivity(new Intent(CustomerHomeActivity.this, AccessibilitySettingsActivity.class))
        );

        btnLogout.setOnClickListener(v -> {
            SessionManager.clearSession(CustomerHomeActivity.this);
            startActivity(new Intent(CustomerHomeActivity.this, MainActivity.class));
            finish();
        });

        AccessibilityUtils.applySettings(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindCurrentRequestSummary();
        AccessibilityUtils.applySettings(this);
    }

    private void loadUser() {
        userName = SessionManager.getUserName(this);
        userEmail = SessionManager.getUserEmail(this);

        if (userName == null || userName.isEmpty()) {
            userName = "Demo User";
        }
        if (userEmail == null || userEmail.isEmpty()) {
            userEmail = "demo@example.com";
        }

        tvWelcome.setText("Welcome, " + userName);
    }

    private void bindCurrentRequestSummary() {
        ShoppingRequest request = MockRepository.getInstance().getLatestRequestForCustomer(userEmail);

        if (request == null) {
            tvCurrentRequestSummary.setText("No active shopping request yet. Start by creating your first request.");
            btnViewCurrentRequest.setEnabled(false);
            return;
        }

        String summary = "Latest request: " + request.getRequestId()
                + "\nStatus: " + request.getStatus()
                + "\nDelivery slot: " + request.getDeliverySlot()
                + "\nEstimated total: R " + String.format("%.2f", request.getEstimatedTotal());

        tvCurrentRequestSummary.setText(summary);
        btnViewCurrentRequest.setEnabled(true);
    }

    private void openCurrentRequest() {
        ShoppingRequest request = MockRepository.getInstance().getLatestRequestForCustomer(userEmail);
        if (request == null) {
            Toast.makeText(this, "No request found yet", Toast.LENGTH_SHORT).show();
            return;
        }

        startActivity(new Intent(CustomerHomeActivity.this, CustomerRequestActivity.class));
    }
}
