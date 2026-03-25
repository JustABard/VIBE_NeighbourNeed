package com.example.shopassist.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.shopassist.ShopAssistApp;
import com.example.shopassist.models.CustomerAccessibilitySettings;
import com.example.shopassist.models.Message;
import com.example.shopassist.models.RequestItem;
import com.example.shopassist.models.Review;
import com.example.shopassist.models.ShoppingRequest;
import com.example.shopassist.models.StoreItem;
import com.example.shopassist.models.User;
import com.example.shopassist.utils.PasswordUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MockRepository {

    private static MockRepository instance;

    private final AppDatabaseHelper databaseHelper;

    private MockRepository() {
        databaseHelper = new AppDatabaseHelper(ShopAssistApp.getAppContext());
    }

    public static MockRepository getInstance() {
        if (instance == null) {
            instance = new MockRepository();
        }
        return instance;
    }

    public User registerUser(String name, String email, String password, String role) {
        if (emailExists(email)) {
            return null;
        }

        try {
            String salt = PasswordUtils.generateSalt();
            String passwordHash = PasswordUtils.hashPassword(password, salt);

            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("name", name);
            values.put("email", email);
            values.put("phone", "Not added");
            values.put("role", role);
            values.put("password_hash", passwordHash);
            values.put("salt", salt);
            values.put("verified_shopper", 0);
            long insertedId = db.insert(AppDatabaseHelper.TABLE_USERS, null, values);
            if (insertedId == -1) {
                return null;
            }
            if ("Customer".equalsIgnoreCase(role)) {
                saveCustomerAccessibilitySettings(email, new CustomerAccessibilitySettings(false, false, false, false));
            }
            return getUserByEmail(email);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean emailExists(String email) {
        return getUserByEmail(email) != null;
    }

    public User authenticateUser(String email, String password) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                AppDatabaseHelper.TABLE_USERS,
                new String[]{"id", "name", "email", "phone", "role", "password_hash", "salt", "verified_shopper"},
                "email = ?",
                new String[]{email},
                null,
                null,
                null
        );

        User user = null;
        if (cursor.moveToFirst()) {
            try {
                String storedHash = cursor.getString(cursor.getColumnIndexOrThrow("password_hash"));
                String storedSalt = cursor.getString(cursor.getColumnIndexOrThrow("salt"));
                if (PasswordUtils.verifyPassword(password, storedHash, storedSalt)) {
                    user = mapUser(cursor);
                }
            } catch (Exception ignored) {
            }
        }
        cursor.close();
        return user;
    }

    public void resetDemoData() {
        databaseHelper.resetDatabase();
    }

    public ArrayList<StoreItem> getStoreCatalog() {
        ArrayList<StoreItem> items = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                AppDatabaseHelper.TABLE_STORE_ITEMS,
                new String[]{"id", "name", "category", "quantity", "notes", "price", "image_url"},
                null,
                null,
                null,
                null,
                "name ASC"
        );

        while (cursor.moveToNext()) {
            items.add(new StoreItem(
                    cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("category")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                    cursor.getString(cursor.getColumnIndexOrThrow("notes")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("price")),
                    cursor.getString(cursor.getColumnIndexOrThrow("image_url"))
            ));
        }
        cursor.close();
        return items;
    }

    public ShoppingRequest createCustomerRequest(String customerName, String customerEmail,
                                                 ArrayList<RequestItem> items, String deliverySlot,
                                                 String locationText, String customerNotes) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        String requestCode = getNextRequestCode(db);
        double total = 0;
        for (RequestItem item : items) {
            total += item.getLineTotal();
        }

        db.beginTransaction();
        try {
            ContentValues requestValues = new ContentValues();
            requestValues.put("request_code", requestCode);
            requestValues.put("customer_email", customerEmail);
            requestValues.put("shopper_email", "");
            requestValues.put("delivery_slot", deliverySlot);
            requestValues.put("location_text", locationText);
            requestValues.put("status", ShoppingRequest.STATUS_POSTED);
            requestValues.put("estimated_total", total);
            requestValues.put("customer_notes", customerNotes);
            requestValues.put("created_at", System.currentTimeMillis());
            db.insert(AppDatabaseHelper.TABLE_REQUESTS, null, requestValues);

            for (RequestItem item : items) {
                ContentValues itemValues = new ContentValues();
                itemValues.put("request_code", requestCode);
                itemValues.put("store_item_id", item.getStoreItemId());
                itemValues.put("name", item.getName());
                itemValues.put("category", item.getCategory());
                itemValues.put("quantity", item.getQuantity());
                itemValues.put("notes", item.getNotes());
                itemValues.put("estimated_price", item.getEstimatedPrice());
                itemValues.put("image_url", item.getImageUrl());
                itemValues.put("is_store_item", item.isStoreItem() ? 1 : 0);
                db.insert(AppDatabaseHelper.TABLE_REQUEST_ITEMS, null, itemValues);
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }

        return getRequestById(requestCode);
    }

    public ShoppingRequest getLatestRequestForCustomer(String customerEmail) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                AppDatabaseHelper.TABLE_REQUESTS,
                null,
                "customer_email = ?",
                new String[]{customerEmail},
                null,
                null,
                "id DESC",
                "1"
        );

        ShoppingRequest request = null;
        if (cursor.moveToFirst()) {
            request = mapRequest(cursor);
        }
        cursor.close();
        return request;
    }

    public ShoppingRequest getRequestById(String requestId) {
        if (requestId == null || requestId.trim().isEmpty()) {
            return null;
        }

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                AppDatabaseHelper.TABLE_REQUESTS,
                null,
                "request_code = ?",
                new String[]{requestId},
                null,
                null,
                null,
                "1"
        );

        ShoppingRequest request = null;
        if (cursor.moveToFirst()) {
            request = mapRequest(cursor);
        }
        cursor.close();
        return request;
    }

    public ArrayList<ShoppingRequest> getOpenRequests() {
        ArrayList<ShoppingRequest> requests = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                AppDatabaseHelper.TABLE_REQUESTS,
                null,
                "status = ?",
                new String[]{ShoppingRequest.STATUS_POSTED},
                null,
                null,
                "id DESC"
        );

        while (cursor.moveToNext()) {
            requests.add(mapRequest(cursor));
        }
        cursor.close();
        return requests;
    }

    public ShoppingRequest getLatestActiveRequestForShopper(String shopperEmail) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                AppDatabaseHelper.TABLE_REQUESTS,
                null,
                "shopper_email = ? AND status != ? AND status != ?",
                new String[]{shopperEmail, ShoppingRequest.STATUS_COMPLETED, ShoppingRequest.STATUS_CANCELLED},
                null,
                null,
                "id DESC",
                "1"
        );

        ShoppingRequest request = null;
        if (cursor.moveToFirst()) {
            request = mapRequest(cursor);
        }
        cursor.close();
        return request;
    }

    public boolean acceptRequest(String requestId, String shopperName, String shopperEmail) {
        User shopper = getUserByEmail(shopperEmail);
        if (shopper == null || !shopper.isVerifiedShopper()) {
            return false;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("shopper_email", shopperEmail);
        values.put("status", ShoppingRequest.STATUS_ACCEPTED);
        int rows = db.update(
                AppDatabaseHelper.TABLE_REQUESTS,
                values,
                "request_code = ? AND status = ? AND (shopper_email IS NULL OR shopper_email = '')",
                new String[]{requestId, ShoppingRequest.STATUS_POSTED}
        );
        return rows > 0;
    }

    public boolean requestReassignment(String requestId) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("shopper_email", "");
        values.put("status", ShoppingRequest.STATUS_POSTED);
        int rows = db.update(
                AppDatabaseHelper.TABLE_REQUESTS,
                values,
                "request_code = ? AND status = ?",
                new String[]{requestId, ShoppingRequest.STATUS_ACCEPTED}
        );
        return rows > 0;
    }

    public boolean updateRequestStatus(String requestId, String newStatus) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("status", newStatus);
        int rows = db.update(
                AppDatabaseHelper.TABLE_REQUESTS,
                values,
                "request_code = ?",
                new String[]{requestId}
        );
        return rows > 0;
    }

    public void cancelRequest(String requestId) {
        updateRequestStatus(requestId, ShoppingRequest.STATUS_CANCELLED);
    }

    public ArrayList<User> getUsers() {
        ArrayList<User> users = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                AppDatabaseHelper.TABLE_USERS,
                new String[]{"id", "name", "email", "phone", "role", "verified_shopper"},
                null,
                null,
                null,
                null,
                "name ASC"
        );

        while (cursor.moveToNext()) {
            users.add(mapUser(cursor));
        }
        cursor.close();
        return users;
    }

    public User getUserByEmail(String email) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                AppDatabaseHelper.TABLE_USERS,
                new String[]{"id", "name", "email", "phone", "role", "verified_shopper"},
                "email = ?",
                new String[]{email},
                null,
                null,
                null,
                "1"
        );

        User user = null;
        if (cursor.moveToFirst()) {
            user = mapUser(cursor);
        }
        cursor.close();
        return user;
    }

    public ArrayList<Message> getMessagesForRequest(String requestId) {
        ArrayList<Message> messages = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                AppDatabaseHelper.TABLE_MESSAGES,
                new String[]{"request_code", "sender_email", "receiver_email", "message_text", "sent_at"},
                "request_code = ?",
                new String[]{requestId},
                null,
                null,
                "id ASC"
        );

        while (cursor.moveToNext()) {
            messages.add(new Message(
                    cursor.getString(cursor.getColumnIndexOrThrow("request_code")),
                    cursor.getString(cursor.getColumnIndexOrThrow("sender_email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("receiver_email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("message_text")),
                    cursor.getString(cursor.getColumnIndexOrThrow("sent_at"))
            ));
        }
        cursor.close();
        return messages;
    }

    public void addMessage(String requestId, String senderId, String receiverId, String text) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("request_code", requestId);
        values.put("sender_email", senderId);
        values.put("receiver_email", receiverId);
        values.put("message_text", text);
        values.put("sent_at", new SimpleDateFormat("dd MMM HH:mm", Locale.getDefault()).format(new Date()));
        db.insert(AppDatabaseHelper.TABLE_MESSAGES, null, values);
    }

    public Review getReviewForRequest(String requestId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                AppDatabaseHelper.TABLE_REVIEWS,
                new String[]{"request_code", "customer_email", "shopper_email", "rating", "thank_you_message"},
                "request_code = ?",
                new String[]{requestId},
                null,
                null,
                null,
                "1"
        );

        Review review = null;
        if (cursor.moveToFirst()) {
            review = new Review(
                    cursor.getString(cursor.getColumnIndexOrThrow("request_code")),
                    cursor.getString(cursor.getColumnIndexOrThrow("customer_email")),
                    cursor.getString(cursor.getColumnIndexOrThrow("shopper_email")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("rating")),
                    cursor.getString(cursor.getColumnIndexOrThrow("thank_you_message"))
            );
        }
        cursor.close();
        return review;
    }

    public void addReview(String requestId, String customerId, String shopperId, int rating, String thankYouMessage) {
        if (getReviewForRequest(requestId) != null) {
            return;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("request_code", requestId);
        values.put("customer_email", customerId);
        values.put("shopper_email", shopperId);
        values.put("rating", rating);
        values.put("thank_you_message", thankYouMessage);
        db.insert(AppDatabaseHelper.TABLE_REVIEWS, null, values);
    }

    public ArrayList<String> getReportedIssues() {
        ArrayList<String> issues = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                AppDatabaseHelper.TABLE_ISSUES,
                new String[]{"issue_text"},
                null,
                null,
                null,
                null,
                "id ASC"
        );

        while (cursor.moveToNext()) {
            issues.add(cursor.getString(cursor.getColumnIndexOrThrow("issue_text")));
        }
        cursor.close();
        return issues;
    }

    public boolean toggleShopperVerification(String email) {
        User user = getUserByEmail(email);
        if (user == null || !"Shopper".equalsIgnoreCase(user.getRole())) {
            return false;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("verified_shopper", user.isVerifiedShopper() ? 0 : 1);
        db.update(
                AppDatabaseHelper.TABLE_USERS,
                values,
                "email = ?",
                new String[]{email}
        );
        User updatedUser = getUserByEmail(email);
        return updatedUser != null && updatedUser.isVerifiedShopper();
    }

    public boolean isShopperVerified(String email) {
        User user = getUserByEmail(email);
        return user != null && user.isVerifiedShopper();
    }

    public boolean removeShopper(String email) {
        User user = getUserByEmail(email);
        if (user == null || !"Shopper".equalsIgnoreCase(user.getRole())) {
            return false;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues requestValues = new ContentValues();
            requestValues.put("shopper_email", "");
            requestValues.put("status", ShoppingRequest.STATUS_POSTED);
            db.update(
                    AppDatabaseHelper.TABLE_REQUESTS,
                    requestValues,
                    "shopper_email = ? AND status != ? AND status != ?",
                    new String[]{email, ShoppingRequest.STATUS_COMPLETED, ShoppingRequest.STATUS_CANCELLED}
            );

            db.delete(
                    AppDatabaseHelper.TABLE_MESSAGES,
                    "sender_email = ? OR receiver_email = ?",
                    new String[]{email, email}
            );

            int deleted = db.delete(
                    AppDatabaseHelper.TABLE_USERS,
                    "email = ? AND role = ?",
                    new String[]{email, "Shopper"}
            );

            db.setTransactionSuccessful();
            return deleted > 0;
        } finally {
            db.endTransaction();
        }
    }

    public CustomerAccessibilitySettings getCustomerAccessibilitySettings(String customerEmail) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                AppDatabaseHelper.TABLE_CUSTOMER_ACCESSIBILITY,
                new String[]{"large_text", "high_contrast", "simple_navigation", "tts_placeholder"},
                "customer_email = ?",
                new String[]{customerEmail},
                null,
                null,
                null,
                "1"
        );

        CustomerAccessibilitySettings settings = new CustomerAccessibilitySettings(false, false, false, false);
        if (cursor.moveToFirst()) {
            settings = new CustomerAccessibilitySettings(
                    cursor.getInt(cursor.getColumnIndexOrThrow("large_text")) == 1,
                    cursor.getInt(cursor.getColumnIndexOrThrow("high_contrast")) == 1,
                    cursor.getInt(cursor.getColumnIndexOrThrow("simple_navigation")) == 1,
                    cursor.getInt(cursor.getColumnIndexOrThrow("tts_placeholder")) == 1
            );
        }
        cursor.close();
        return settings;
    }

    public void saveCustomerAccessibilitySettings(String customerEmail, CustomerAccessibilitySettings settings) {
        SQLiteDatabase db = databaseHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("customer_email", customerEmail);
        values.put("large_text", settings.isLargeText() ? 1 : 0);
        values.put("high_contrast", settings.isHighContrast() ? 1 : 0);
        values.put("simple_navigation", settings.isSimpleNavigation() ? 1 : 0);
        values.put("tts_placeholder", settings.isTtsPlaceholder() ? 1 : 0);

        int rows = db.update(
                AppDatabaseHelper.TABLE_CUSTOMER_ACCESSIBILITY,
                values,
                "customer_email = ?",
                new String[]{customerEmail}
        );

        if (rows == 0) {
            db.insert(AppDatabaseHelper.TABLE_CUSTOMER_ACCESSIBILITY, null, values);
        }
    }

    public String getApproximateLocation(String fullLocation) {
        if (fullLocation == null || fullLocation.trim().isEmpty()) {
            return "Location not added";
        }

        String[] parts = fullLocation.split(",");
        if (parts.length >= 2) {
            return parts[parts.length - 1].trim();
        }
        return "Area hidden until request is accepted";
    }

    public String formatPrice(double value) {
        return String.format(Locale.getDefault(), "R %.2f", value);
    }

    private String getNextRequestCode(SQLiteDatabase db) {
        Cursor cursor = db.rawQuery(
                "SELECT COALESCE(MAX(CAST(SUBSTR(request_code, 5) AS INTEGER)), 999) + 1 FROM " + AppDatabaseHelper.TABLE_REQUESTS,
                null
        );
        int nextNumber = 1000;
        if (cursor.moveToFirst()) {
            nextNumber = cursor.getInt(0);
        }
        cursor.close();
        return "REQ-" + nextNumber;
    }

    private User mapUser(Cursor cursor) {
        return new User(
                String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("id"))),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("email")),
                cursor.getString(cursor.getColumnIndexOrThrow("phone")),
                cursor.getString(cursor.getColumnIndexOrThrow("role")),
                cursor.getInt(cursor.getColumnIndexOrThrow("verified_shopper")) == 1
        );
    }

    private ShoppingRequest mapRequest(Cursor cursor) {
        String requestCode = cursor.getString(cursor.getColumnIndexOrThrow("request_code"));
        String customerEmail = cursor.getString(cursor.getColumnIndexOrThrow("customer_email"));
        String shopperEmail = cursor.getString(cursor.getColumnIndexOrThrow("shopper_email"));
        User customer = getUserByEmail(customerEmail);
        User shopper = null;
        if (shopperEmail != null && !shopperEmail.trim().isEmpty()) {
            shopper = getUserByEmail(shopperEmail);
        }

        ShoppingRequest request = new ShoppingRequest(
                requestCode,
                customer != null ? customer.getName() : customerEmail,
                customerEmail,
                getItemsForRequest(requestCode),
                cursor.getString(cursor.getColumnIndexOrThrow("delivery_slot")),
                cursor.getString(cursor.getColumnIndexOrThrow("location_text")),
                cursor.getString(cursor.getColumnIndexOrThrow("status")),
                cursor.getDouble(cursor.getColumnIndexOrThrow("estimated_total")),
                cursor.getString(cursor.getColumnIndexOrThrow("customer_notes"))
        );
        request.setShopperId(shopperEmail == null ? "" : shopperEmail);
        request.setShopperName(shopper != null ? shopper.getName() : "");
        return request;
    }

    private ArrayList<RequestItem> getItemsForRequest(String requestCode) {
        ArrayList<RequestItem> items = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = db.query(
                AppDatabaseHelper.TABLE_REQUEST_ITEMS,
                new String[]{"store_item_id", "name", "category", "quantity", "notes", "estimated_price", "image_url", "is_store_item"},
                "request_code = ?",
                new String[]{requestCode},
                null,
                null,
                "id ASC"
        );

        while (cursor.moveToNext()) {
            items.add(new RequestItem(
                    cursor.getLong(cursor.getColumnIndexOrThrow("store_item_id")),
                    cursor.getString(cursor.getColumnIndexOrThrow("name")),
                    cursor.getString(cursor.getColumnIndexOrThrow("category")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("quantity")),
                    cursor.getString(cursor.getColumnIndexOrThrow("notes")),
                    cursor.getDouble(cursor.getColumnIndexOrThrow("estimated_price")),
                    cursor.getInt(cursor.getColumnIndexOrThrow("is_store_item")) == 1,
                    cursor.getString(cursor.getColumnIndexOrThrow("image_url"))
            ));
        }
        cursor.close();
        return items;
    }
}
