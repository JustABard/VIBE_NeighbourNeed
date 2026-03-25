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
import com.example.shopassist.utils.AccessibilityUtils;
import com.example.shopassist.utils.SessionManager;

public class ShopperActiveRequestActivity extends AppCompatActivity {

    private TextView tvActiveTitle;
    private TextView tvActiveCustomer;
    private TextView tvActiveStatus;
    private TextView tvActiveLocation;
    private TextView tvActiveNotes;
    private TextView tvActiveSlot;
    private RecyclerView recyclerActiveItems;
    private Button btnAdvanceStatus;
    private Button btnOpenChat;
    private Button btnBackShopper;

    private ShoppingRequest request;
    private String shopperEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopper_active_request);

        tvActiveTitle = findViewById(R.id.tvActiveRequestTitle);
        tvActiveCustomer = findViewById(R.id.tvActiveRequestCustomer);
        tvActiveStatus = findViewById(R.id.tvActiveRequestStatus);
        tvActiveLocation = findViewById(R.id.tvActiveRequestLocation);
        tvActiveNotes = findViewById(R.id.tvActiveRequestNotes);
        tvActiveSlot = findViewById(R.id.tvActiveRequestSlot);
        recyclerActiveItems = findViewById(R.id.recyclerActiveRequestItems);
        btnAdvanceStatus = findViewById(R.id.btnAdvanceStatus);
        btnOpenChat = findViewById(R.id.btnOpenShopperChat);
        btnBackShopper = findViewById(R.id.btnBackFromActiveRequest);

        shopperEmail = SessionManager.getUserEmail(this);
        if (shopperEmail.isEmpty()) {
            shopperEmail = "shopper@example.com";
        }

        String requestId = getIntent().getStringExtra("request_id");
        if (requestId == null || requestId.isEmpty()) {
            ShoppingRequest activeRequest = MockRepository.getInstance().getLatestActiveRequestForShopper(shopperEmail);
            requestId = activeRequest != null ? activeRequest.getRequestId() : null;
        }

        request = MockRepository.getInstance().getRequestById(requestId);
        bindRequest();

        btnAdvanceStatus.setOnClickListener(v -> advanceStatus());
        btnOpenChat.setOnClickListener(v -> openChat());
        btnBackShopper.setOnClickListener(v -> finish());

        AccessibilityUtils.applySettings(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (request != null) {
            request = MockRepository.getInstance().getRequestById(request.getRequestId());
            bindRequest();
        }
        AccessibilityUtils.applySettings(this);
    }

    private void bindRequest() {
        if (request == null) {
            Toast.makeText(this, "No active request found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvActiveTitle.setText("Active request: " + request.getRequestId());
        tvActiveCustomer.setText("Customer: " + request.getCustomerName());
        tvActiveStatus.setText("Status: " + request.getStatus());
        tvActiveLocation.setText("Delivery address: " + request.getLocationText());
        tvActiveNotes.setText("Customer notes: " + request.getCustomerNotes());
        tvActiveSlot.setText("Delivery slot: " + request.getDeliverySlot());

        recyclerActiveItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerActiveItems.setAdapter(new RequestItemAdapter(request.getItems(), false, null));

        if (ShoppingRequest.STATUS_COMPLETED.equals(request.getStatus())
                || ShoppingRequest.STATUS_CANCELLED.equals(request.getStatus())) {
            btnAdvanceStatus.setVisibility(View.GONE);
        } else {
            btnAdvanceStatus.setVisibility(View.VISIBLE);
            btnAdvanceStatus.setText(getAdvanceButtonLabel(request.getStatus()));
        }
    }

    private String getAdvanceButtonLabel(String status) {
        if (ShoppingRequest.STATUS_ACCEPTED.equals(status)) {
            return "Start shopping";
        }
        if (ShoppingRequest.STATUS_SHOPPING_STARTED.equals(status)) {
            return "Mark out for delivery";
        }
        if (ShoppingRequest.STATUS_OUT_FOR_DELIVERY.equals(status)) {
            return "Mark delivered";
        }
        if (ShoppingRequest.STATUS_DELIVERED.equals(status)) {
            return "Complete request";
        }
        return "Advance status";
    }

    private void advanceStatus() {
        String nextStatus;
        String currentStatus = request.getStatus();

        if (ShoppingRequest.STATUS_ACCEPTED.equals(currentStatus)) {
            nextStatus = ShoppingRequest.STATUS_SHOPPING_STARTED;
        } else if (ShoppingRequest.STATUS_SHOPPING_STARTED.equals(currentStatus)) {
            nextStatus = ShoppingRequest.STATUS_OUT_FOR_DELIVERY;
        } else if (ShoppingRequest.STATUS_OUT_FOR_DELIVERY.equals(currentStatus)) {
            nextStatus = ShoppingRequest.STATUS_DELIVERED;
        } else if (ShoppingRequest.STATUS_DELIVERED.equals(currentStatus)) {
            nextStatus = ShoppingRequest.STATUS_COMPLETED;
        } else {
            Toast.makeText(this, "No further status updates available", Toast.LENGTH_SHORT).show();
            return;
        }

        MockRepository.getInstance().updateRequestStatus(request.getRequestId(), nextStatus);
        request = MockRepository.getInstance().getRequestById(request.getRequestId());
        bindRequest();
        Toast.makeText(this, "Status updated to " + nextStatus, Toast.LENGTH_SHORT).show();
    }

    private void openChat() {
        Intent intent = new Intent(ShopperActiveRequestActivity.this, ChatActivity.class);
        intent.putExtra("request_id", request.getRequestId());
        startActivity(intent);
    }
}
