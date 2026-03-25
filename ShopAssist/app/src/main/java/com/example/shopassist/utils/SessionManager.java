package com.example.shopassist.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.shopassist.models.User;

public class SessionManager {

    private static final String PREFS_NAME = "shopassist_prefs";

    private SessionManager() {
    }

    public static void saveUserSession(Context context, User user) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit()
                .putString("user_name", user.getName())
                .putString("user_email", user.getEmail())
                .putString("selected_role", user.getRole())
                .putString("last_login_email", user.getEmail())
                .apply();
    }

    public static void clearSession(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        preferences.edit()
                .remove("user_name")
                .remove("user_email")
                .remove("selected_role")
                .apply();
    }

    public static String getUserName(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString("user_name", "");
    }

    public static String getUserEmail(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString("user_email", "");
    }

    public static String getSelectedRole(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString("selected_role", "");
    }

    public static boolean isCustomerSession(Context context) {
        return "Customer".equalsIgnoreCase(getSelectedRole(context));
    }

    public static void saveLastLoginEmail(Context context, String email) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString("last_login_email", email)
                .apply();
    }

    public static String getLastLoginEmail(Context context) {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString("last_login_email", "");
    }
}
