package com.example.shopassist.activities;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopassist.R;
import com.example.shopassist.data.MockRepository;
import com.example.shopassist.models.ShoppingRequest;
import com.example.shopassist.models.User;
import com.example.shopassist.utils.AccessibilityUtils;

public class ShopperProfileActivity extends AppCompatActivity {

    private TextView tvShopperName;
    private TextView tvShopperEmail;
    private TextView tvShopperPhone;
    private TextView tvShopperVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopper_profile);

        tvShopperName = findViewById(R.id.tvProfileShopperName);
        tvShopperEmail = findViewById(R.id.tvProfileShopperEmail);
        tvShopperPhone = findViewById(R.id.tvProfileShopperPhone);
        tvShopperVerified = findViewById(R.id.tvProfileShopperVerified);

        String requestId = getIntent().getStringExtra("request_id");
        ShoppingRequest request = MockRepository.getInstance().getRequestById(requestId);

        if (request == null || request.getShopperId() == null || request.getShopperId().isEmpty()) {
            Toast.makeText(this, "No shopper profile available yet", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        User shopper = MockRepository.getInstance().getUserByEmail(request.getShopperId());
        if (shopper == null) {
            Toast.makeText(this, "Shopper not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvShopperName.setText(shopper.getName());
        tvShopperEmail.setText("Email: " + shopper.getEmail());
        tvShopperPhone.setText("Phone: " + shopper.getPhone());
        tvShopperVerified.setText("Verified shopper: " + (shopper.isVerifiedShopper() ? "Yes" : "No"));

        AccessibilityUtils.applySettings(this);
    }
}
