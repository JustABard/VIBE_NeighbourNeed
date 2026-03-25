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

public class RequestDetailsActivity extends AppCompatActivity {

    private TextView tvTitle;
    private TextView tvStatus;
    private TextView tvCustomer;
    private TextView tvSlot;
    private TextView tvLocation;
    private TextView tvNotes;
    private TextView tvTotal;
    private RecyclerView recyclerItems;
    private Button btnAcceptRequest;
    private Button btnOpenAcceptedRequest;
    private Button btnBackToShopperHome;

    private ShoppingRequest request;
    private String shopperName;
    private String shopperEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_details);

        tvTitle = findViewById(R.id.tvRequestDetailTitle);
        tvStatus = findViewById(R.id.tvRequestDetailStatus);
        tvCustomer = findViewById(R.id.tvRequestDetailCustomer);
        tvSlot = findViewById(R.id.tvRequestDetailSlot);
        tvLocation = findViewById(R.id.tvRequestDetailLocation);
        tvNotes = findViewById(R.id.tvRequestDetailNotes);
        tvTotal = findViewById(R.id.tvRequestDetailTotal);
        recyclerItems = findViewById(R.id.recyclerRequestDetailItems);
        btnAcceptRequest = findViewById(R.id.btnAcceptRequest);
        btnOpenAcceptedRequest = findViewById(R.id.btnOpenAcceptedRequest);
        btnBackToShopperHome = findViewById(R.id.btnBackToShopperHome);

        shopperName = SessionManager.getUserName(this);
        shopperEmail = SessionManager.getUserEmail(this);
        if (shopperName.isEmpty()) {
            shopperName = "Shopper Demo";
        }
        if (shopperEmail.isEmpty()) {
            shopperEmail = "shopper@example.com";
        }

        String requestId = getIntent().getStringExtra("request_id");
        request = MockRepository.getInstance().getRequestById(requestId);

        bindRequest();

        btnAcceptRequest.setOnClickListener(v -> acceptRequest());
        btnOpenAcceptedRequest.setOnClickListener(v -> openAcceptedRequest());
        btnBackToShopperHome.setOnClickListener(v -> finish());

        AccessibilityUtils.applySettings(this);
    }

    private void bindRequest() {
        if (request == null) {
            Toast.makeText(this, "Request not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        boolean verifiedShopper = MockRepository.getInstance().isShopperVerified(shopperEmail);

        tvTitle.setText("Request " + request.getRequestId());
        tvStatus.setText("Status: " + request.getStatus());
        tvCustomer.setText("Customer: " + request.getCustomerName());
        tvSlot.setText("Delivery slot: " + request.getDeliverySlot());
        if (!verifiedShopper) {
            tvNotes.setText("Customer notes: verification required before you can accept this request.");
        } else {
            tvNotes.setText("Customer notes: " + request.getCustomerNotes());
        }
        tvTotal.setText("Estimated total: " + MockRepository.getInstance().formatPrice(request.getEstimatedTotal()));

        boolean acceptedByCurrentShopper = shopperEmail.equalsIgnoreCase(request.getShopperId());
        if (acceptedByCurrentShopper) {
            tvLocation.setText("Exact location: " + request.getLocationText());
        } else {
            tvLocation.setText("Approximate area: " + MockRepository.getInstance().getApproximateLocation(request.getLocationText()));
        }

        recyclerItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerItems.setAdapter(new RequestItemAdapter(request.getItems(), false, null));

        boolean canAccept = ShoppingRequest.STATUS_POSTED.equals(request.getStatus()) && verifiedShopper;
        btnAcceptRequest.setVisibility(canAccept ? View.VISIBLE : View.GONE);
        btnOpenAcceptedRequest.setVisibility(acceptedByCurrentShopper ? View.VISIBLE : View.GONE);
    }

    private void acceptRequest() {
        if (!MockRepository.getInstance().isShopperVerified(shopperEmail)) {
            Toast.makeText(this, "Admin verification is required before accepting requests.", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean accepted = MockRepository.getInstance().acceptRequest(request.getRequestId(), shopperName, shopperEmail);
        if (!accepted) {
            Toast.makeText(this, "This request was already accepted by another shopper", Toast.LENGTH_SHORT).show();
            request = MockRepository.getInstance().getRequestById(request.getRequestId());
            bindRequest();
            return;
        }

        Toast.makeText(this, "Request accepted", Toast.LENGTH_SHORT).show();
        openAcceptedRequest();
    }

    private void openAcceptedRequest() {
        Intent intent = new Intent(RequestDetailsActivity.this, ShopperActiveRequestActivity.class);
        intent.putExtra("request_id", request.getRequestId());
        startActivity(intent);
        finish();
    }
}
