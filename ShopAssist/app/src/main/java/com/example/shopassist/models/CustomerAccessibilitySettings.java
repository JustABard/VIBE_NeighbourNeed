package com.example.shopassist.models;

public class CustomerAccessibilitySettings {

    private final boolean largeText;
    private final boolean highContrast;
    private final boolean simpleNavigation;
    private final boolean ttsPlaceholder;

    public CustomerAccessibilitySettings(boolean largeText, boolean highContrast,
                                         boolean simpleNavigation, boolean ttsPlaceholder) {
        this.largeText = largeText;
        this.highContrast = highContrast;
        this.simpleNavigation = simpleNavigation;
        this.ttsPlaceholder = ttsPlaceholder;
    }

    public boolean isLargeText() {
        return largeText;
    }

    public boolean isHighContrast() {
        return highContrast;
    }

    public boolean isSimpleNavigation() {
        return simpleNavigation;
    }

    public boolean isTtsPlaceholder() {
        return ttsPlaceholder;
    }
}

