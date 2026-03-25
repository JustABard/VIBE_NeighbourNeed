package com.example.shopassist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopassist.R;
import com.example.shopassist.adapters.ShoppingRequestAdapter;
import com.example.shopassist.data.MockRepository;
import com.example.shopassist.models.ShoppingRequest;
import com.example.shopassist.utils.AccessibilityUtils;
import com.example.shopassist.utils.SessionManager;

import java.util.ArrayList;

public class ShopperHomeActivity extends AppCompatActivity {

    private TextView tvShopperWelcome;
    private TextView tvShopperSummary;
    private RecyclerView recyclerOpenRequests;
    private Button btnViewAcceptedRequest;
    private Button btnShopperLogout;

    private String userName;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopper_home);

        tvShopperWelcome = findViewById(R.id.tvShopperWelcome);
        tvShopperSummary = findViewById(R.id.tvShopperSummary);
        recyclerOpenRequests = findViewById(R.id.recyclerOpenRequests);
        btnViewAcceptedRequest = findViewById(R.id.btnViewAcceptedRequest);
        btnShopperLogout = findViewById(R.id.btnShopperLogout);

        loadCurrentUser();
        bindOpenRequests();

        btnViewAcceptedRequest.setOnClickListener(v -> openActiveRequest());
        btnShopperLogout.setOnClickListener(v -> {
            SessionManager.clearSession(ShopperHomeActivity.this);
            startActivity(new Intent(ShopperHomeActivity.this, MainActivity.class));
            finish();
        });

        AccessibilityUtils.applySettings(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindOpenRequests();
        AccessibilityUtils.applySettings(this);
    }

    private void loadCurrentUser() {
        userName = SessionManager.getUserName(this);
        userEmail = SessionManager.getUserEmail(this);
        if (userName.isEmpty()) {
            userName = "Shopper Demo";
        }
        if (userEmail.isEmpty()) {
            userEmail = "shopper@example.com";
        }
        tvShopperWelcome.setText("Welcome, " + userName);
    }

    private void bindOpenRequests() {
        ArrayList<ShoppingRequest> openRequests = MockRepository.getInstance().getOpenRequests();
        ShoppingRequest myActiveRequest = MockRepository.getInstance().getLatestActiveRequestForShopper(userEmail);
        boolean verifiedShopper = MockRepository.getInstance().isShopperVerified(userEmail);

        if (myActiveRequest != null) {
            tvShopperSummary.setText("You have an active request: " + myActiveRequest.getRequestId() + " (" + myActiveRequest.getStatus() + ")");
            btnViewAcceptedRequest.setEnabled(true);
        } else if (!verifiedShopper) {
            tvShopperSummary.setText("Your shopper account is waiting for admin verification. You can browse requests, but acceptance stays locked until verified.");
            btnViewAcceptedRequest.setEnabled(false);
        } else {
            tvShopperSummary.setText("Browse open volunteer requests below. Exact customer address stays hidden until you accept.");
            btnViewAcceptedRequest.setEnabled(false);
        }

        recyclerOpenRequests.setLayoutManager(new LinearLayoutManager(this));
        recyclerOpenRequests.setAdapter(new ShoppingRequestAdapter(openRequests, true, request -> {
            Intent intent = new Intent(ShopperHomeActivity.this, RequestDetailsActivity.class);
            intent.putExtra("request_id", request.getRequestId());
            startActivity(intent);
        }));
    }

    private void openActiveRequest() {
        if (!MockRepository.getInstance().isShopperVerified(userEmail)) {
            Toast.makeText(this, "Your shopper account must be verified by an admin first.", Toast.LENGTH_SHORT).show();
            return;
        }

        ShoppingRequest request = MockRepository.getInstance().getLatestActiveRequestForShopper(userEmail);
        if (request == null) {
            Toast.makeText(this, "No active shopper request found", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(ShopperHomeActivity.this, ShopperActiveRequestActivity.class);
        intent.putExtra("request_id", request.getRequestId());
        startActivity(intent);
    }
}
