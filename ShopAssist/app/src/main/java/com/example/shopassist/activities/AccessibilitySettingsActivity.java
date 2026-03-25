package com.example.shopassist.activities;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.example.shopassist.R;
import com.example.shopassist.data.MockRepository;
import com.example.shopassist.models.CustomerAccessibilitySettings;
import com.example.shopassist.utils.SessionManager;

public class AccessibilitySettingsActivity extends AppCompatActivity {

    private SwitchCompat switchLargeText;
    private SwitchCompat switchHighContrast;
    private SwitchCompat switchSimpleNavigation;
    private SwitchCompat switchTtsPlaceholder;
    private Button btnSaveAccessibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accessibility_settings);

        if (!SessionManager.isCustomerSession(this)) {
            Toast.makeText(this, "Accessibility settings are available for customer accounts only.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        switchLargeText = findViewById(R.id.switchLargeText);
        switchHighContrast = findViewById(R.id.switchHighContrast);
        switchSimpleNavigation = findViewById(R.id.switchSimpleNavigation);
        switchTtsPlaceholder = findViewById(R.id.switchTtsPlaceholder);
        btnSaveAccessibility = findViewById(R.id.btnSaveAccessibility);

        bindCurrentValues();
        btnSaveAccessibility.setOnClickListener(v -> saveSettings());
    }

    private void bindCurrentValues() {
        String customerEmail = SessionManager.getUserEmail(this);
        CustomerAccessibilitySettings settings = MockRepository.getInstance().getCustomerAccessibilitySettings(customerEmail);
        switchLargeText.setChecked(settings.isLargeText());
        switchHighContrast.setChecked(settings.isHighContrast());
        switchSimpleNavigation.setChecked(settings.isSimpleNavigation());
        switchTtsPlaceholder.setChecked(settings.isTtsPlaceholder());
    }

    private void saveSettings() {
        String customerEmail = SessionManager.getUserEmail(this);
        MockRepository.getInstance().saveCustomerAccessibilitySettings(
                customerEmail,
                new CustomerAccessibilitySettings(
                        switchLargeText.isChecked(),
                        switchHighContrast.isChecked(),
                        switchSimpleNavigation.isChecked(),
                        switchTtsPlaceholder.isChecked()
                )
        );

        finish();
    }
}
