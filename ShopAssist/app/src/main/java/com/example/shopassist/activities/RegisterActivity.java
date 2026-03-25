package com.example.shopassist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopassist.R;
import com.example.shopassist.data.MockRepository;
import com.example.shopassist.utils.AccessibilityUtils;

import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName;
    private EditText etEmail;
    private EditText etPassword;
    private Button btnCreateAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etEmail = findViewById(R.id.etRegisterEmail);
        etPassword = findViewById(R.id.etRegisterPassword);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);

        btnCreateAccount.setOnClickListener(v -> handleRegister());

        AccessibilityUtils.applySettings(this);
    }

    private void handleRegister() {
        String fullName = etFullName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("Enter full name");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter password");
            return;
        }

        if (MockRepository.getInstance().emailExists(email)) {
            Toast.makeText(this, "That email is already registered. Please log in instead.", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(RegisterActivity.this, RoleSelectionActivity.class);
        intent.putExtra("user_name", fullName);
        intent.putExtra("user_email", email);
        intent.putExtra("user_password", password);
        intent.putExtra("registration_flow", true);
        startActivity(intent);
        finish();
    }
}
