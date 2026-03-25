package com.example.shopassist.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopassist.R;
import com.example.shopassist.data.MockRepository;
import com.example.shopassist.models.RequestItem;
import com.example.shopassist.models.ShoppingRequest;
import com.example.shopassist.utils.AccessibilityUtils;
import com.example.shopassist.utils.SessionManager;

import java.util.ArrayList;
import java.util.Locale;

public class DeliveryDetailsActivity extends AppCompatActivity {

    private Spinner spinnerDeliverySlot;
    private EditText etLocation;
    private EditText etCustomerNotes;
    private TextView tvDeliveryTotal;
    private Button btnUseSuggestedLocation;
    private Button btnConfirmRequest;

    private ArrayList<RequestItem> requestItems;
    private double estimatedTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery_details);

        spinnerDeliverySlot = findViewById(R.id.spinnerDeliverySlot);
        etLocation = findViewById(R.id.etLocation);
        etCustomerNotes = findViewById(R.id.etCustomerNotes);
        tvDeliveryTotal = findViewById(R.id.tvDeliveryTotal);
        btnUseSuggestedLocation = findViewById(R.id.btnUseSuggestedLocation);
        btnConfirmRequest = findViewById(R.id.btnConfirmRequest);

        requestItems = readRequestItemsFromIntent();
        estimatedTotal = getIntent().getDoubleExtra("estimated_total", 0);

        setupDeliverySlotSpinner();
        tvDeliveryTotal.setText(String.format(Locale.getDefault(), "Estimated grocery total: R %.2f", estimatedTotal));

        btnUseSuggestedLocation.setOnClickListener(v ->
                etLocation.setText("Suggested from phone location: 15 Palm Street, Johannesburg")
        );

        btnConfirmRequest.setOnClickListener(v -> confirmRequest());

        AccessibilityUtils.applySettings(this);
    }

    private void setupDeliverySlotSpinner() {
        String[] slots = {
                "Today 14:00 - 16:00",
                "Today 16:00 - 18:00",
                "Tomorrow 09:00 - 11:00",
                "Tomorrow 12:00 - 14:00"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, slots);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDeliverySlot.setAdapter(adapter);
    }

    private void confirmRequest() {
        String locationText = etLocation.getText().toString().trim();
        String customerNotes = etCustomerNotes.getText().toString().trim();
        String deliverySlot = spinnerDeliverySlot.getSelectedItem().toString();

        if (requestItems == null || requestItems.isEmpty()) {
            Toast.makeText(this, "No request items found", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(locationText)) {
            etLocation.setError("Enter or confirm a delivery location");
            return;
        }

        String userName = SessionManager.getUserName(this);
        String userEmail = SessionManager.getUserEmail(this);
        if (userName.isEmpty()) {
            userName = "Demo User";
        }
        if (userEmail.isEmpty()) {
            userEmail = "demo@example.com";
        }

        ShoppingRequest request = MockRepository.getInstance().createCustomerRequest(
                userName,
                userEmail,
                requestItems,
                deliverySlot,
                locationText,
                customerNotes
        );

        Toast.makeText(this, "Request posted successfully", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(DeliveryDetailsActivity.this, CustomerRequestActivity.class);
        intent.putExtra("request_id", request.getRequestId());
        startActivity(intent);
        finish();
    }

    @SuppressWarnings("unchecked")
    private ArrayList<RequestItem> readRequestItemsFromIntent() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return (ArrayList<RequestItem>) getIntent().getSerializableExtra("request_items", ArrayList.class);
        }
        return (ArrayList<RequestItem>) getIntent().getSerializableExtra("request_items");
    }
}
