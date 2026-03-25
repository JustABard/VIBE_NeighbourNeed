package com.example.shopassist.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.shopassist.models.RequestItem;
import com.example.shopassist.utils.PasswordUtils;

import java.util.ArrayList;

public class AppDatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "shopassist.db";
    public static final int DATABASE_VERSION = 3;

    public static final String TABLE_USERS = "access_users";
    public static final String TABLE_STORE_ITEMS = "store_items";
    public static final String TABLE_REQUESTS = "shopping_requests";
    public static final String TABLE_REQUEST_ITEMS = "request_items";
    public static final String TABLE_MESSAGES = "messages";
    public static final String TABLE_REVIEWS = "reviews";
    public static final String TABLE_ISSUES = "reported_issues";
    public static final String TABLE_CUSTOMER_ACCESSIBILITY = "customer_accessibility";

    public AppDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "email TEXT UNIQUE NOT NULL," +
                "phone TEXT," +
                "role TEXT NOT NULL," +
                "password_hash TEXT NOT NULL," +
                "salt TEXT NOT NULL," +
                "verified_shopper INTEGER DEFAULT 0" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_STORE_ITEMS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "name TEXT NOT NULL," +
                "category TEXT," +
                "quantity INTEGER DEFAULT 1," +
                "notes TEXT," +
                "price REAL NOT NULL," +
                "image_url TEXT," +
                "is_store_item INTEGER DEFAULT 1" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_REQUESTS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "request_code TEXT UNIQUE NOT NULL," +
                "customer_email TEXT NOT NULL," +
                "shopper_email TEXT," +
                "delivery_slot TEXT NOT NULL," +
                "location_text TEXT NOT NULL," +
                "status TEXT NOT NULL," +
                "estimated_total REAL NOT NULL," +
                "customer_notes TEXT," +
                "created_at INTEGER NOT NULL" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_REQUEST_ITEMS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "request_code TEXT NOT NULL," +
                "store_item_id INTEGER DEFAULT 0," +
                "name TEXT NOT NULL," +
                "category TEXT," +
                "quantity INTEGER NOT NULL," +
                "notes TEXT," +
                "estimated_price REAL NOT NULL," +
                "image_url TEXT," +
                "is_store_item INTEGER DEFAULT 0" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_MESSAGES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "request_code TEXT NOT NULL," +
                "sender_email TEXT NOT NULL," +
                "receiver_email TEXT NOT NULL," +
                "message_text TEXT NOT NULL," +
                "sent_at TEXT NOT NULL" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_REVIEWS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "request_code TEXT UNIQUE NOT NULL," +
                "customer_email TEXT NOT NULL," +
                "shopper_email TEXT NOT NULL," +
                "rating INTEGER NOT NULL," +
                "thank_you_message TEXT" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_ISSUES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "issue_text TEXT NOT NULL" +
                ")");

        db.execSQL("CREATE TABLE " + TABLE_CUSTOMER_ACCESSIBILITY + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "customer_email TEXT UNIQUE NOT NULL," +
                "large_text INTEGER DEFAULT 0," +
                "high_contrast INTEGER DEFAULT 0," +
                "simple_navigation INTEGER DEFAULT 0," +
                "tts_placeholder INTEGER DEFAULT 0" +
                ")");

        seedInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUSTOMER_ACCESSIBILITY);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ISSUES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REVIEWS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUEST_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REQUESTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORE_ITEMS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        onCreate(db);
    }

    private void seedInitialData(SQLiteDatabase db) {
        try {
            insertUser(db, "Mary Customer", "mary@example.com", "082 555 0101", "Customer", "Password123!", false);
            insertUser(db, "Peter Volunteer", "peter@example.com", "082 555 0102", "Shopper", "Password123!", true);
            insertUser(db, "Aisha Volunteer", "aisha@example.com", "082 555 0103", "Shopper", "Password123!", false);
            insertUser(db, "Admin User", "admin@example.com", "082 555 0104", "Admin", "Admin123!", true);
        } catch (Exception ignored) {
        }

        seedStoreCatalog(db);

        ContentValues requestValues = new ContentValues();
        requestValues.put("request_code", "REQ-1000");
        requestValues.put("customer_email", "mary@example.com");
        requestValues.put("shopper_email", "");
        requestValues.put("delivery_slot", "Today 16:00 - 18:00");
        requestValues.put("location_text", "21 Rose Avenue, Johannesburg");
        requestValues.put("status", "Posted");
        requestValues.put("estimated_total", 76.50);
        requestValues.put("customer_notes", "Please ring the gate bell.");
        requestValues.put("created_at", System.currentTimeMillis());
        db.insert(TABLE_REQUESTS, null, requestValues);

        ArrayList<RequestItem> starterItems = new ArrayList<>();
        starterItems.add(new RequestItem("Tea Bags", "Pantry", 2, "Any mild brand", 22.00, false));
        starterItems.add(new RequestItem(0L, "Milk 2L", "Dairy", 1, "Low fat", 32.50, true, getCategoryImageUrl("Dairy")));
        for (RequestItem item : starterItems) {
            insertRequestItem(db, "REQ-1000", item);
        }

        insertIssue(db, "Late delivery complaint from customer #C102");
        insertIssue(db, "Shopper profile pending verification document review");
        insertIssue(db, "Customer asked for easier text size controls");
    }

    private void seedStoreCatalog(SQLiteDatabase db) {
        String[][] items = new String[][]{
                {"Brown Bread", "Bakery", "Fresh daily loaf", "18.99"},
                {"Whole Wheat Bread", "Bakery", "High fibre loaf", "22.50"},
                {"White Bread", "Bakery", "Soft sandwich loaf", "17.99"},
                {"Hot Dog Rolls", "Bakery", "6 pack", "24.99"},
                {"Hamburger Buns", "Bakery", "Sesame seed buns", "27.99"},
                {"Croissants", "Bakery", "4 pack butter croissants", "34.50"},
                {"Blueberry Muffins", "Bakery", "4 pack", "36.99"},
                {"Bagels", "Bakery", "6 pack", "31.50"},
                {"Milk 2L", "Dairy", "Low fat", "32.50"},
                {"Long Life Milk 1L", "Dairy", "Shelf stable carton", "16.99"},
                {"Cheddar Cheese", "Dairy", "700 g block", "69.99"},
                {"Butter 500g", "Dairy", "Salted butter", "48.50"},
                {"Eggs 18 Pack", "Dairy", "Large eggs", "54.99"},
                {"Cream Cheese", "Dairy", "250 g tub", "29.99"},
                {"Vanilla Yoghurt", "Dairy", "1 kg tub", "39.99"},
                {"Apples 1kg", "Fruit & Veg", "Crisp red apples", "28.99"},
                {"Bananas 1kg", "Fruit & Veg", "Easy to ripen", "24.99"},
                {"Oranges 1kg", "Fruit & Veg", "Juicy navels", "32.99"},
                {"Potatoes 2kg", "Fruit & Veg", "Family bag", "42.50"},
                {"Onions 1kg", "Fruit & Veg", "Brown onions", "21.99"},
                {"Tomatoes 1kg", "Fruit & Veg", "Salad tomatoes", "29.99"},
                {"Carrots 1kg", "Fruit & Veg", "Fresh carrots", "18.99"},
                {"Spinach Bundle", "Fruit & Veg", "Leafy greens", "16.50"},
                {"Lettuce", "Fruit & Veg", "Crisp iceberg", "19.99"},
                {"Cucumber", "Fruit & Veg", "Long cucumber", "14.99"},
                {"Broccoli", "Fruit & Veg", "1 large head", "23.99"},
                {"Avocado Pack", "Fruit & Veg", "4 medium avocados", "39.99"},
                {"Lemons 6 Pack", "Fruit & Veg", "Fresh lemons", "27.99"},
                {"Rice 2kg", "Pantry", "Long grain rice", "44.50"},
                {"Pasta Penne", "Pantry", "500 g pasta", "19.99"},
                {"Spaghetti", "Pantry", "500 g pasta", "18.99"},
                {"Maize Meal 2.5kg", "Pantry", "White maize meal", "38.50"},
                {"Sugar 2kg", "Pantry", "White sugar", "41.99"},
                {"Salt 1kg", "Pantry", "Table salt", "13.50"},
                {"Black Pepper", "Pantry", "100 g grinder", "28.99"},
                {"Cooking Oil 2L", "Pantry", "Sunflower oil", "54.99"},
                {"Tea Bags 100 Pack", "Pantry", "Rooibos blend", "39.99"},
                {"Instant Coffee", "Pantry", "200 g jar", "74.99"},
                {"Baked Beans", "Pantry", "410 g can", "17.99"},
                {"Chopped Tomatoes", "Pantry", "400 g can", "15.50"},
                {"Peanut Butter", "Pantry", "400 g smooth", "29.99"},
                {"Strawberry Jam", "Pantry", "450 g jar", "26.50"},
                {"Oats 1kg", "Pantry", "Rolled oats", "31.99"},
                {"Corn Flakes", "Pantry", "750 g box", "38.99"},
                {"Flour 2.5kg", "Pantry", "Cake wheat flour", "34.99"},
                {"Brown Sugar", "Pantry", "1 kg bag", "27.50"},
                {"Chicken Breast 1kg", "Meat", "Skinless fillets", "89.99"},
                {"Beef Mince 1kg", "Meat", "Lean mince", "94.99"},
                {"Pork Sausages", "Meat", "500 g tray", "49.99"},
                {"Bacon 250g", "Meat", "Streaky rashers", "39.99"},
                {"Chicken Pieces 1kg", "Meat", "Mixed portions", "64.99"},
                {"Frozen Chicken Nuggets", "Frozen", "400 g box", "43.99"},
                {"Frozen Mixed Veg", "Frozen", "1 kg bag", "34.99"},
                {"Frozen Pizza", "Frozen", "Large margherita", "59.99"},
                {"Frozen French Fries", "Frozen", "1 kg bag", "36.99"},
                {"Ice Cream 2L", "Frozen", "Vanilla tub", "64.99"},
                {"Frozen Fish Fingers", "Frozen", "400 g pack", "49.50"},
                {"Frozen Peas", "Frozen", "1 kg bag", "32.99"},
                {"Cola 2L", "Beverages", "Original cola", "22.99"},
                {"Orange Juice 2L", "Beverages", "100 percent juice", "39.99"},
                {"Bottled Water 6 Pack", "Beverages", "500 ml bottles", "29.99"},
                {"Sparkling Water", "Beverages", "1 litre bottle", "16.99"},
                {"Energy Drink 500ml", "Beverages", "Chilled can", "19.99"},
                {"Apple Juice 1L", "Beverages", "No added sugar", "22.50"},
                {"Rooibos Tea", "Beverages", "80 tea bags", "28.99"},
                {"Biscuits", "Snacks", "Tea time biscuits", "19.99"},
                {"Potato Chips", "Snacks", "Large sharing bag", "24.99"},
                {"Salted Peanuts", "Snacks", "250 g pack", "21.99"},
                {"Chocolate Bar", "Snacks", "100 g slab", "14.99"},
                {"Granola Bars", "Snacks", "6 bars", "29.99"},
                {"Dishwashing Liquid", "Household", "750 ml bottle", "24.99"},
                {"Laundry Powder", "Household", "2 kg box", "89.99"},
                {"Fabric Softener", "Household", "2 litre bottle", "49.99"},
                {"Bleach 750ml", "Household", "Thick bleach", "21.99"},
                {"Paper Towels", "Household", "2 rolls", "28.99"},
                {"Toilet Paper 9 Pack", "Household", "Soft 2 ply", "74.99"},
                {"Hand Soap", "Personal Care", "250 ml pump bottle", "18.99"},
                {"Shampoo", "Personal Care", "400 ml bottle", "46.99"},
                {"Toothpaste", "Personal Care", "100 ml tube", "21.99"},
                {"Body Lotion", "Personal Care", "400 ml bottle", "54.50"},
                {"Bar Soap 3 Pack", "Personal Care", "Gentle care bars", "24.99"},
                {"Baby Diapers Size 4", "Baby & Pet", "40 pack", "149.99"},
                {"Baby Wipes", "Baby & Pet", "72 wipes", "24.99"},
                {"Cat Food", "Baby & Pet", "1 kg dry food", "58.99"},
                {"Dog Food", "Baby & Pet", "2 kg dry food", "92.99"},
                {"Instant Noodles", "Pantry", "5 pack", "21.99"},
                {"Tomato Sauce", "Pantry", "700 ml bottle", "23.50"},
                {"Mayonnaise", "Pantry", "750 g jar", "36.99"},
                {"Curry Powder", "Pantry", "100 g spice", "19.99"},
                {"Chicken Stock Cubes", "Pantry", "12 cubes", "13.99"},
                {"Tuna Chunks", "Pantry", "170 g can", "23.99"},
                {"Sardines", "Pantry", "155 g tin", "16.99"},
                {"Lentils 500g", "Pantry", "Brown lentils", "22.99"},
                {"Beans Mix 500g", "Pantry", "Soup mix", "24.99"},
                {"Honey 500g", "Pantry", "Pure blend", "49.99"},
                {"Wraps 8 Pack", "Bakery", "Soft flour wraps", "31.99"},
                {"Garlic", "Fruit & Veg", "3 bulb pack", "12.99"},
                {"Ginger", "Fruit & Veg", "200 g root", "15.99"},
                {"Mushrooms 250g", "Fruit & Veg", "Button mushrooms", "26.99"},
                {"Green Peppers", "Fruit & Veg", "3 pack", "19.99"}
        };

        for (String[] item : items) {
            insertStoreItem(
                    db,
                    item[0],
                    item[1],
                    1,
                    item[2],
                    Double.parseDouble(item[3]),
                    true,
                    getCategoryImageUrl(item[1])
            );
        }
    }

    private void insertUser(SQLiteDatabase db, String name, String email, String phone,
                            String role, String plainPassword, boolean verifiedShopper) throws Exception {
        String salt = PasswordUtils.generateSalt();
        String passwordHash = PasswordUtils.hashPassword(plainPassword, salt);

        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("email", email);
        values.put("phone", phone);
        values.put("role", role);
        values.put("password_hash", passwordHash);
        values.put("salt", salt);
        values.put("verified_shopper", verifiedShopper ? 1 : 0);
        db.insert(TABLE_USERS, null, values);
    }

    private void insertStoreItem(SQLiteDatabase db, String name, String category, int quantity,
                                 String notes, double price, boolean isStoreItem, String imageUrl) {
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("category", category);
        values.put("quantity", quantity);
        values.put("notes", notes);
        values.put("price", price);
        values.put("image_url", imageUrl);
        values.put("is_store_item", isStoreItem ? 1 : 0);
        db.insert(TABLE_STORE_ITEMS, null, values);
    }

    private void insertRequestItem(SQLiteDatabase db, String requestCode, RequestItem item) {
        ContentValues values = new ContentValues();
        values.put("request_code", requestCode);
        values.put("store_item_id", item.getStoreItemId());
        values.put("name", item.getName());
        values.put("category", item.getCategory());
        values.put("quantity", item.getQuantity());
        values.put("notes", item.getNotes());
        values.put("estimated_price", item.getEstimatedPrice());
        values.put("image_url", item.getImageUrl());
        values.put("is_store_item", item.isStoreItem() ? 1 : 0);
        db.insert(TABLE_REQUEST_ITEMS, null, values);
    }

    private void insertIssue(SQLiteDatabase db, String issueText) {
        ContentValues values = new ContentValues();
        values.put("issue_text", issueText);
        db.insert(TABLE_ISSUES, null, values);
    }

    public void resetDatabase() {
        SQLiteDatabase db = getWritableDatabase();
        onUpgrade(db, DATABASE_VERSION, DATABASE_VERSION);
    }

    private String getCategoryImageUrl(String category) {
        switch (category) {
            case "Bakery":
                return "https://images.pexels.com/photos/1775043/pexels-photo-1775043.jpeg?auto=compress&cs=tinysrgb&w=600";
            case "Dairy":
                return "https://images.pexels.com/photos/5946720/pexels-photo-5946720.jpeg?auto=compress&cs=tinysrgb&w=600";
            case "Fruit & Veg":
                return "https://images.pexels.com/photos/1435904/pexels-photo-1435904.jpeg?auto=compress&cs=tinysrgb&w=600";
            case "Meat":
                return "https://images.pexels.com/photos/65175/pexels-photo-65175.jpeg?auto=compress&cs=tinysrgb&w=600";
            case "Frozen":
                return "https://images.pexels.com/photos/4198019/pexels-photo-4198019.jpeg?auto=compress&cs=tinysrgb&w=600";
            case "Beverages":
                return "https://images.pexels.com/photos/2983101/pexels-photo-2983101.jpeg?auto=compress&cs=tinysrgb&w=600";
            case "Snacks":
                return "https://images.pexels.com/photos/1583884/pexels-photo-1583884.jpeg?auto=compress&cs=tinysrgb&w=600";
            case "Household":
                return "https://images.pexels.com/photos/4239091/pexels-photo-4239091.jpeg?auto=compress&cs=tinysrgb&w=600";
            case "Personal Care":
                return "https://images.pexels.com/photos/3735657/pexels-photo-3735657.jpeg?auto=compress&cs=tinysrgb&w=600";
            case "Baby & Pet":
                return "https://images.pexels.com/photos/4587997/pexels-photo-4587997.jpeg?auto=compress&cs=tinysrgb&w=600";
            default:
                return "https://images.pexels.com/photos/264636/pexels-photo-264636.jpeg?auto=compress&cs=tinysrgb&w=600";
        }
    }
}
