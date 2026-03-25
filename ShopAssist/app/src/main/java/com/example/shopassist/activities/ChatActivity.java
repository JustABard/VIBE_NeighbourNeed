package com.example.shopassist.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopassist.R;
import com.example.shopassist.adapters.MessageAdapter;
import com.example.shopassist.data.MockRepository;
import com.example.shopassist.models.ShoppingRequest;
import com.example.shopassist.utils.AccessibilityUtils;
import com.example.shopassist.utils.SessionManager;

public class ChatActivity extends AppCompatActivity {

    private TextView tvChatTitle;
    private RecyclerView recyclerMessages;
    private EditText etChatMessage;
    private Button btnSendMessage;

    private String currentEmail;
    private String requestId;
    private ShoppingRequest request;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        tvChatTitle = findViewById(R.id.tvChatTitle);
        recyclerMessages = findViewById(R.id.recyclerMessages);
        etChatMessage = findViewById(R.id.etChatMessage);
        btnSendMessage = findViewById(R.id.btnSendMessage);

        currentEmail = SessionManager.getUserEmail(this);
        if (currentEmail.isEmpty()) {
            currentEmail = "demo@example.com";
        }
        requestId = getIntent().getStringExtra("request_id");
        request = MockRepository.getInstance().getRequestById(requestId);

        if (request == null) {
            Toast.makeText(this, "Chat not available", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        if (ShoppingRequest.STATUS_POSTED.equals(request.getStatus())
                || ShoppingRequest.STATUS_CANCELLED.equals(request.getStatus())) {
            Toast.makeText(this, "Messaging only opens after request acceptance", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        tvChatTitle.setText("Messages for " + request.getRequestId());
        bindMessages();

        btnSendMessage.setOnClickListener(v -> sendMessage());

        AccessibilityUtils.applySettings(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindMessages();
        AccessibilityUtils.applySettings(this);
    }

    private void bindMessages() {
        recyclerMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerMessages.setAdapter(new MessageAdapter(
                MockRepository.getInstance().getMessagesForRequest(requestId),
                currentEmail
        ));
    }

    private void sendMessage() {
        String text = etChatMessage.getText().toString().trim();
        if (TextUtils.isEmpty(text)) {
            etChatMessage.setError("Enter a message");
            return;
        }

        String receiverId;
        if (currentEmail.equalsIgnoreCase(request.getCustomerEmail())) {
            receiverId = request.getShopperId();
        } else {
            receiverId = request.getCustomerEmail();
        }

        MockRepository.getInstance().addMessage(requestId, currentEmail, receiverId, text);
        etChatMessage.setText("");
        bindMessages();
    }
}
