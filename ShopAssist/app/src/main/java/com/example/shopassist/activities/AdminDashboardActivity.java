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
import com.example.shopassist.adapters.UserAdminAdapter;
import com.example.shopassist.data.MockRepository;
import com.example.shopassist.models.User;
import com.example.shopassist.utils.AccessibilityUtils;
import com.example.shopassist.utils.SessionManager;

import java.util.ArrayList;

public class AdminDashboardActivity extends AppCompatActivity {

    private RecyclerView recyclerUsers;
    private TextView tvReportedIssues;
    private Button btnAdminLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        recyclerUsers = findViewById(R.id.recyclerAdminUsers);
        tvReportedIssues = findViewById(R.id.tvReportedIssuesList);
        btnAdminLogout = findViewById(R.id.btnAdminLogout);

        bindUsers();
        bindIssues();

        btnAdminLogout.setOnClickListener(v -> {
            SessionManager.clearSession(AdminDashboardActivity.this);
            startActivity(new Intent(AdminDashboardActivity.this, MainActivity.class));
            finish();
        });

        AccessibilityUtils.applySettings(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        bindUsers();
        bindIssues();
        AccessibilityUtils.applySettings(this);
    }

    private void bindUsers() {
        ArrayList<User> users = MockRepository.getInstance().getUsers();
        recyclerUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerUsers.setAdapter(new UserAdminAdapter(users, new UserAdminAdapter.OnUserAdminActionListener() {
            @Override
            public void onVerifyClick(User user) {
                boolean verified = MockRepository.getInstance().toggleShopperVerification(user.getEmail());
                Toast.makeText(AdminDashboardActivity.this, verified ? "Shopper verified" : "Verification update failed", Toast.LENGTH_SHORT).show();
                bindUsers();
            }

            @Override
            public void onRemoveClick(User user) {
                boolean removed = MockRepository.getInstance().removeShopper(user.getEmail());
                Toast.makeText(AdminDashboardActivity.this, removed ? "Shopper removed" : "Could not remove shopper", Toast.LENGTH_SHORT).show();
                bindUsers();
            }
        }));
    }

    private void bindIssues() {
        ArrayList<String> issues = MockRepository.getInstance().getReportedIssues();
        StringBuilder builder = new StringBuilder();
        for (String issue : issues) {
            builder.append("- ").append(issue).append("\n");
        }
        tvReportedIssues.setText(builder.toString().trim());
    }
}
