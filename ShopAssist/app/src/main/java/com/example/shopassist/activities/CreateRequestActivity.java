package com.example.shopassist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopassist.R;
import com.example.shopassist.adapters.RequestItemAdapter;
import com.example.shopassist.adapters.StoreCatalogAdapter;
import com.example.shopassist.data.MockRepository;
import com.example.shopassist.models.RequestItem;
import com.example.shopassist.models.StoreItem;
import com.example.shopassist.utils.AccessibilityUtils;

import java.util.ArrayList;
import java.util.Locale;

public class CreateRequestActivity extends AppCompatActivity {

    private EditText etItemName;
    private EditText etItemCategory;
    private EditText etItemQuantity;
    private EditText etItemNotes;
    private EditText etEstimatedPrice;
    private EditText etStoreSearch;
    private TextView tvCatalogSummary;
    private TextView tvEmptyCatalog;
    private TextView tvRequestTotal;
    private TextView tvSelectedItemsTitle;
    private RecyclerView recyclerItems;
    private RecyclerView recyclerStoreItems;
    private LinearLayout layoutCatalogSkeleton;
    private Button btnAddCustomItem;
    private Button btnNextDelivery;

    private final ArrayList<RequestItem> selectedItems = new ArrayList<>();
    private final ArrayList<StoreItem> allStoreItems = new ArrayList<>();
    private final ArrayList<StoreItem> filteredStoreItems = new ArrayList<>();
    private RequestItemAdapter itemAdapter;
    private StoreCatalogAdapter storeCatalogAdapter;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean catalogLoaded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        etItemName = findViewById(R.id.etItemName);
        etItemCategory = findViewById(R.id.etItemCategory);
        etItemQuantity = findViewById(R.id.etItemQuantity);
        etItemNotes = findViewById(R.id.etItemNotes);
        etEstimatedPrice = findViewById(R.id.etEstimatedPrice);
        etStoreSearch = findViewById(R.id.etStoreSearch);
        tvCatalogSummary = findViewById(R.id.tvCatalogSummary);
        tvEmptyCatalog = findViewById(R.id.tvEmptyCatalog);
        tvRequestTotal = findViewById(R.id.tvRequestTotal);
        tvSelectedItemsTitle = findViewById(R.id.tvSelectedItemsTitle);
        recyclerItems = findViewById(R.id.recyclerRequestItems);
        recyclerStoreItems = findViewById(R.id.recyclerStoreItems);
        layoutCatalogSkeleton = findViewById(R.id.layoutCatalogSkeleton);
        btnAddCustomItem = findViewById(R.id.btnAddCustomItem);
        btnNextDelivery = findViewById(R.id.btnNextDelivery);

        setupRecyclerView();
        setupStoreRecyclerView();
        setupStoreSearch();
        updateTotal();
        loadStoreCatalog();

        btnAddCustomItem.setOnClickListener(v -> addCustomItem());
        btnNextDelivery.setOnClickListener(v -> continueToDeliveryDetails());

        AccessibilityUtils.applySettings(this);
    }

    private void setupRecyclerView() {
        itemAdapter = new RequestItemAdapter(selectedItems, true, position -> {
            selectedItems.remove(position);
            itemAdapter.notifyItemRemoved(position);
            updateTotal();
        });

        recyclerItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerItems.setAdapter(itemAdapter);
    }

    private void setupStoreRecyclerView() {
        storeCatalogAdapter = new StoreCatalogAdapter(this::addStoreItem);
        recyclerStoreItems.setLayoutManager(new LinearLayoutManager(this));
        recyclerStoreItems.setAdapter(storeCatalogAdapter);
    }

    private void setupStoreSearch() {
        etStoreSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!catalogLoaded) {
                    return;
                }
                filterStoreItems(s.toString());
            }
        });
    }

    private void loadStoreCatalog() {
        showCatalogLoading(true);
        handler.postDelayed(() -> {
            allStoreItems.clear();
            allStoreItems.addAll(MockRepository.getInstance().getStoreCatalog());
            catalogLoaded = true;
            showCatalogLoading(false);
            filterStoreItems(etStoreSearch.getText().toString());
        }, 450);
    }

    private void addCustomItem() {
        String name = etItemName.getText().toString().trim();
        String category = etItemCategory.getText().toString().trim();
        String quantityText = etItemQuantity.getText().toString().trim();
        String notes = etItemNotes.getText().toString().trim();
        String priceText = etEstimatedPrice.getText().toString().trim();

        if (TextUtils.isEmpty(name)) {
            etItemName.setError("Enter an item name");
            return;
        }

        int quantity = 1;
        if (!TextUtils.isEmpty(quantityText)) {
            try {
                quantity = Integer.parseInt(quantityText);
            } catch (NumberFormatException ignored) {
                quantity = 1;
            }
        }

        double estimatedPrice = 20.00;
        if (!TextUtils.isEmpty(priceText)) {
            try {
                estimatedPrice = Double.parseDouble(priceText);
            } catch (NumberFormatException ignored) {
                estimatedPrice = 20.00;
            }
        }

        RequestItem item = new RequestItem(0L, name, category, quantity, notes, estimatedPrice, false, "");
        selectedItems.add(item);
        itemAdapter.notifyItemInserted(selectedItems.size() - 1);
        clearCustomItemFields();
        updateTotal();
    }

    private void addStoreItem(StoreItem selectedStoreItem) {
        selectedItems.add(selectedStoreItem.toRequestItem());
        itemAdapter.notifyItemInserted(selectedItems.size() - 1);
        updateTotal();
        Toast.makeText(this, selectedStoreItem.getName() + " added to request", Toast.LENGTH_SHORT).show();
    }

    private void continueToDeliveryDetails() {
        if (selectedItems.isEmpty()) {
            Toast.makeText(this, "Add at least one item first", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent(CreateRequestActivity.this, DeliveryDetailsActivity.class);
        intent.putExtra("request_items", selectedItems);
        intent.putExtra("estimated_total", calculateTotal());
        startActivity(intent);
    }

    private void clearCustomItemFields() {
        etItemName.setText("");
        etItemCategory.setText("");
        etItemQuantity.setText("");
        etItemNotes.setText("");
        etEstimatedPrice.setText("");
    }

    private void filterStoreItems(String query) {
        filteredStoreItems.clear();
        String normalizedQuery = query == null ? "" : query.trim().toLowerCase(Locale.getDefault());

        if (normalizedQuery.isEmpty()) {
            filteredStoreItems.addAll(allStoreItems);
        } else {
            for (StoreItem item : allStoreItems) {
                if (matchesQuery(item, normalizedQuery)) {
                    filteredStoreItems.add(item);
                }
            }
        }

        storeCatalogAdapter.updateItems(filteredStoreItems);
        tvCatalogSummary.setText(String.format(
                Locale.getDefault(),
                "Showing %d of %d store items",
                filteredStoreItems.size(),
                allStoreItems.size()
        ));
        tvEmptyCatalog.setVisibility(filteredStoreItems.isEmpty() ? View.VISIBLE : View.GONE);
        recyclerStoreItems.setVisibility(filteredStoreItems.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private boolean matchesQuery(StoreItem item, String query) {
        return contains(item.getName(), query)
                || contains(item.getCategory(), query)
                || contains(item.getNotes(), query);
    }

    private boolean contains(String value, String query) {
        return value != null && value.toLowerCase(Locale.getDefault()).contains(query);
    }

    private void showCatalogLoading(boolean isLoading) {
        layoutCatalogSkeleton.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        recyclerStoreItems.setVisibility(isLoading ? View.GONE : View.VISIBLE);
        tvEmptyCatalog.setVisibility(View.GONE);
        tvCatalogSummary.setText(isLoading ? "Loading store items..." : tvCatalogSummary.getText());
    }

    private double calculateTotal() {
        double total = 0;
        for (RequestItem item : selectedItems) {
            total += item.getLineTotal();
        }
        return total;
    }

    private void updateTotal() {
        tvRequestTotal.setText(String.format(Locale.getDefault(), "Estimated total: R %.2f", calculateTotal()));
        tvSelectedItemsTitle.setText(String.format(Locale.getDefault(), "Selected basket (%d)", selectedItems.size()));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }
}
