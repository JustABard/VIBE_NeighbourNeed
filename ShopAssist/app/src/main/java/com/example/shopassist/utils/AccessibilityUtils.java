package com.example.shopassist.utils;

import android.app.Activity;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.shopassist.data.MockRepository;
import com.example.shopassist.models.CustomerAccessibilitySettings;

public class AccessibilityUtils {

    private AccessibilityUtils() {
    }

    public static void applySettings(Activity activity) {
        if (!SessionManager.isCustomerSession(activity)) {
            return;
        }

        String customerEmail = SessionManager.getUserEmail(activity);
        CustomerAccessibilitySettings settings = MockRepository.getInstance().getCustomerAccessibilitySettings(customerEmail);

        View root = activity.findViewById(android.R.id.content);
        if (root != null) {
            applyRecursively(root, settings.isLargeText(), settings.isHighContrast());
        }
    }

    private static void applyRecursively(View view, boolean largeText, boolean highContrast) {
        if (highContrast) {
            view.setBackgroundColor(Color.BLACK);
        }

        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            if (highContrast) {
                textView.setTextColor(Color.WHITE);
                textView.setHintTextColor(Color.LTGRAY);
            }
            if (largeText) {
                float originalSize = textView.getTag() instanceof Float
                        ? (Float) textView.getTag()
                        : textView.getTextSize();
                textView.setTag(originalSize);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, originalSize * 1.15f);
            }
        }

        if (view instanceof Button && highContrast) {
            ((Button) view).setTextColor(Color.WHITE);
        }

        if (view instanceof EditText && highContrast) {
            ((EditText) view).setTextColor(Color.WHITE);
        }

        if (view instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) view;
            for (int i = 0; i < group.getChildCount(); i++) {
                applyRecursively(group.getChildAt(i), largeText, highContrast);
            }
        }
    }
}
