package com.example.shopassist.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopassist.R;
import com.example.shopassist.data.MockRepository;
import com.example.shopassist.models.Review;
import com.example.shopassist.models.ShoppingRequest;
import com.example.shopassist.utils.AccessibilityUtils;
import com.example.shopassist.utils.SessionManager;

public class ReviewActivity extends AppCompatActivity {

    private RatingBar ratingBar;
    private EditText etThankYouMessage;
    private Button btnSubmitReview;

    private ShoppingRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ratingBar = findViewById(R.id.ratingBarReview);
        etThankYouMessage = findViewById(R.id.etThankYouMessage);
        btnSubmitReview = findViewById(R.id.btnSubmitReview);

        String requestId = getIntent().getStringExtra("request_id");
        request = MockRepository.getInstance().getRequestById(requestId);

        if (request == null) {
            Toast.makeText(this, "Review request not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (!ShoppingRequest.STATUS_COMPLETED.equals(request.getStatus())) {
            Toast.makeText(this, "Reviews are only available after completion", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindExistingReview();
        btnSubmitReview.setOnClickListener(v -> submitReview());

        AccessibilityUtils.applySettings(this);
    }

    private void bindExistingReview() {
        Review review = MockRepository.getInstance().getReviewForRequest(request.getRequestId());
        if (review != null) {
            ratingBar.setRating(review.getRating());
            etThankYouMessage.setText(review.getThankYouMessage());
            ratingBar.setIsIndicator(true);
            etThankYouMessage.setEnabled(false);
            btnSubmitReview.setEnabled(false);
            btnSubmitReview.setText("Review already submitted");
        }
    }

    private void submitReview() {
        String customerEmail = SessionManager.getUserEmail(this);
        if (customerEmail.isEmpty()) {
            customerEmail = "demo@example.com";
        }

        int rating = (int) ratingBar.getRating();
        String thankYou = etThankYouMessage.getText().toString().trim();
        if (rating == 0) {
            rating = 5;
        }

        MockRepository.getInstance().addReview(
                request.getRequestId(),
                customerEmail,
                request.getShopperId(),
                rating,
                thankYou
        );

        Toast.makeText(this, "Thank-you review submitted", Toast.LENGTH_SHORT).show();
        bindExistingReview();
    }
}
