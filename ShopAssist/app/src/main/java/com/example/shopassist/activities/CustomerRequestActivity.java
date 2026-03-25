package com.example.shopassist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopassist.R;
import com.example.shopassist.adapters.RequestItemAdapter;
import com.example.shopassist.data.MockRepository;
import com.example.shopassist.models.ShoppingRequest;
import com.example.shopassist.models.Review;
import com.example.shopassist.utils.AccessibilityUtils;
import com.example.shopassist.utils.SessionManager;

import java.util.Locale;

public class CustomerRequestActivity extends AppCompatActivity {

    private TextView tvRequestId;
    private TextView tvRequestStatus;
    private TextView tvRequestSlot;
    private TextView tvRequestLocation;
    private TextView tvRequestTotal;
    private TextView tvAssignedShopper;
    private TextView tvPrivacyNote;
    private RecyclerView recyclerRequestItems;
    private Button btnCancelRequest;
    private Button btnRequestReassignment;
    private Button btnViewShopperProfile;
    private Button btnOpenCustomerChat;
    private Button btnLeaveReview;
    private Button btnBackHome;

    private ShoppingRequest currentRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_request);

        tvRequestId = findViewById(R.id.tvRequestId);
        tvRequestStatus = findViewById(R.id.tvRequestStatus);
        tvRequestSlot = findViewById(R.id.tvRequestSlot);
        tvRequestLocation = findViewById(R.id.tvRequestLocation);
        tvRequestTotal = findViewById(R.id.tvRequestTotalDetails);
        tvAssignedShopper = findViewById(R.id.tvAssignedShopper);
        tvPrivacyNote = findViewById(R.id.tvPrivacyNote);
        recyclerRequestItems = findViewById(R.id.recyclerCurrentRequestItems);
        btnCancelRequest = findViewById(R.id.btnCancelRequest);
        btnRequestReassignment = findViewById(R.id.btnRequestReassignment);
        btnViewShopperProfile = findViewById(R.id.btnViewShopperProfile);
        btnOpenCustomerChat = findViewById(R.id.btnOpenCustomerChat);
        btnLeaveReview = findViewById(R.id.btnLeaveReview);
        btnBackHome = findViewById(R.id.btnBackCustomerHome);

        loadCurrentRequest();
        bindRequest();

        btnCancelRequest.setOnClickListener(v -> cancelCurrentRequest());
        btnRequestReassignment.setOnClickListener(v -> requestReassignment());
        btnViewShopperProfile.setOnClickListener(v -> openShopperProfile());
        btnOpenCustomerChat.setOnClickListener(v -> openChat());
        btnLeaveReview.setOnClickListener(v -> openReview());
        btnBackHome.setOnClickListener(v -> {
            startActivity(new Intent(CustomerRequestActivity.this, CustomerHomeActivity.class));
            finish();
        });

        AccessibilityUtils.applySettings(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadCurrentRequest();
        bindRequest();
        AccessibilityUtils.applySettings(this);
    }

    private void loadCurrentRequest() {
        String userEmail = SessionManager.getUserEmail(this);
        if (userEmail.isEmpty()) {
            userEmail = "demo@example.com";
        }
        currentRequest = MockRepository.getInstance().getLatestRequestForCustomer(userEmail);
    }

    private void bindRequest() {
        if (currentRequest == null) {
            Toast.makeText(this, "No customer request found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvRequestId.setText("Request ID: " + currentRequest.getRequestId());
        tvRequestStatus.setText("Status: " + currentRequest.getStatus());
        tvRequestSlot.setText("Delivery slot: " + currentRequest.getDeliverySlot());
        tvRequestLocation.setText("Your delivery location: " + currentRequest.getLocationText());
        tvRequestTotal.setText(String.format(Locale.getDefault(), "Estimated total: R %.2f", currentRequest.getEstimatedTotal()));

        if (currentRequest.getShopperName() == null || currentRequest.getShopperName().isEmpty()) {
            tvAssignedShopper.setText("Assigned shopper: No shopper has accepted this request yet.");
        } else {
            tvAssignedShopper.setText("Assigned shopper: " + currentRequest.getShopperName());
        }

        tvPrivacyNote.setText("Privacy note: shoppers should only see exact location details after they accept the request.");

        recyclerRequestItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerRequestItems.setAdapter(new RequestItemAdapter(currentRequest.getItems(), false, null));

        boolean canCancel = ShoppingRequest.STATUS_POSTED.equals(currentRequest.getStatus())
                || ShoppingRequest.STATUS_ACCEPTED.equals(currentRequest.getStatus());
        btnCancelRequest.setVisibility(canCancel ? View.VISIBLE : View.GONE);

        boolean canReassign = ShoppingRequest.STATUS_ACCEPTED.equals(currentRequest.getStatus());
        btnRequestReassignment.setVisibility(canReassign ? View.VISIBLE : View.GONE);

        boolean hasShopper = currentRequest.getShopperId() != null && !currentRequest.getShopperId().isEmpty();
        btnViewShopperProfile.setVisibility(hasShopper ? View.VISIBLE : View.GONE);

        boolean canMessage = hasShopper
                && !ShoppingRequest.STATUS_POSTED.equals(currentRequest.getStatus())
                && !ShoppingRequest.STATUS_CANCELLED.equals(currentRequest.getStatus());
        btnOpenCustomerChat.setVisibility(canMessage ? View.VISIBLE : View.GONE);

        Review review = MockRepository.getInstance().getReviewForRequest(currentRequest.getRequestId());
        boolean canReview = ShoppingRequest.STATUS_COMPLETED.equals(currentRequest.getStatus()) && review == null;
        btnLeaveReview.setVisibility(canReview ? View.VISIBLE : View.GONE);
    }

    private void cancelCurrentRequest() {
        if (currentRequest == null) {
            return;
        }

        MockRepository.getInstance().cancelRequest(currentRequest.getRequestId());
        currentRequest.setStatus(ShoppingRequest.STATUS_CANCELLED);
        tvRequestStatus.setText("Status: " + currentRequest.getStatus());
        btnCancelRequest.setVisibility(View.GONE);
        Toast.makeText(this, "Request cancelled", Toast.LENGTH_SHORT).show();
    }

    private void requestReassignment() {
        if (currentRequest == null) {
            return;
        }

        boolean updated = MockRepository.getInstance().requestReassignment(currentRequest.getRequestId());
        if (!updated) {
            Toast.makeText(this, "Reassignment is only available before shopping starts", Toast.LENGTH_SHORT).show();
            return;
        }

        currentRequest = MockRepository.getInstance().getRequestById(currentRequest.getRequestId());
        bindRequest();
        Toast.makeText(this, "Request returned to open shopper pool", Toast.LENGTH_SHORT).show();
    }

    private void openShopperProfile() {
        Intent intent = new Intent(CustomerRequestActivity.this, ShopperProfileActivity.class);
        intent.putExtra("request_id", currentRequest.getRequestId());
        startActivity(intent);
    }

    private void openChat() {
        Intent intent = new Intent(CustomerRequestActivity.this, ChatActivity.class);
        intent.putExtra("request_id", currentRequest.getRequestId());
        startActivity(intent);
    }

    private void openReview() {
        Intent intent = new Intent(CustomerRequestActivity.this, ReviewActivity.class);
        intent.putExtra("request_id", currentRequest.getRequestId());
        startActivity(intent);
    }
}
