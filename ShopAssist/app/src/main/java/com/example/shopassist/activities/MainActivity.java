package com.example.shopassist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.shopassist.R;
import com.example.shopassist.data.MockRepository;
import com.example.shopassist.models.User;
import com.example.shopassist.utils.AccessibilityUtils;
import com.example.shopassist.utils.SessionManager;

public class MainActivity extends AppCompatActivity {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private Button btnRegister;
    private Button btnResetDemoData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        btnResetDemoData = findViewById(R.id.btnResetDemoData);

        String lastEmail = SessionManager.getLastLoginEmail(this);
        if (!lastEmail.isEmpty()) {
            etEmail.setText(lastEmail);
        }

        btnLogin.setOnClickListener(v -> handleLogin());
        btnRegister.setOnClickListener(v ->
                startActivity(new Intent(MainActivity.this, RegisterActivity.class))
        );
        btnResetDemoData.setOnClickListener(v -> resetDemoData());

        AccessibilityUtils.applySettings(this);
    }

    private void handleLogin() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Enter email");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Enter password");
            return;
        }

        SessionManager.saveLastLoginEmail(this, email);

        User user = MockRepository.getInstance().authenticateUser(email, password);
        if (user == null) {
            Toast.makeText(this, "Login failed. Check your email and password.", Toast.LENGTH_SHORT).show();
            return;
        }

        SessionManager.saveUserSession(this, user);
        openHomeForRole(user.getRole());
        Toast.makeText(this, "Login successful", Toast.LENGTH_SHORT).show();
    }

    private void openHomeForRole(String role) {
        Intent intent;
        if ("Customer".equalsIgnoreCase(role)) {
            intent = new Intent(MainActivity.this, CustomerHomeActivity.class);
        } else if ("Shopper".equalsIgnoreCase(role)) {
            intent = new Intent(MainActivity.this, ShopperHomeActivity.class);
        } else {
            intent = new Intent(MainActivity.this, AdminDashboardActivity.class);
        }
        startActivity(intent);
    }

    private void resetDemoData() {
        MockRepository.getInstance().resetDemoData();
        Toast.makeText(this, R.string.demo_data_reset_done, Toast.LENGTH_SHORT).show();
        etEmail.setText("");
        etPassword.setText("");
    }
}
