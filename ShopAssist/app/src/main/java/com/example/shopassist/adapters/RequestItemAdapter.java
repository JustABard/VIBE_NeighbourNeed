package com.example.shopassist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopassist.R;
import com.bumptech.glide.Glide;
import com.example.shopassist.models.RequestItem;

import java.util.ArrayList;
import java.util.Locale;

public class RequestItemAdapter extends RecyclerView.Adapter<RequestItemAdapter.ItemViewHolder> {

    public interface OnRemoveClickListener {
        void onRemoveClick(int position);
    }

    private final ArrayList<RequestItem> items;
    private final boolean allowRemove;
    private final OnRemoveClickListener removeClickListener;

    public RequestItemAdapter(ArrayList<RequestItem> items, boolean allowRemove, OnRemoveClickListener removeClickListener) {
        this.items = items;
        this.allowRemove = allowRemove;
        this.removeClickListener = removeClickListener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_item, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        RequestItem item = items.get(position);

        holder.tvName.setText(item.getName());
        holder.tvDetails.setText("Qty: " + item.getQuantity() + " | Category: " + safeText(item.getCategory()));
        holder.tvNotes.setText("Notes: " + safeText(item.getNotes()));
        holder.tvPrice.setText(String.format(Locale.getDefault(), "Estimated: R %.2f", item.getLineTotal()));
        holder.tvType.setText(item.isStoreItem() ? "Store item" : "Custom item");

        if (item.getImageUrl() != null && !item.getImageUrl().trim().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.bg_catalog_image_placeholder)
                    .error(R.drawable.bg_catalog_image_placeholder)
                    .centerCrop()
                    .into(holder.ivItemImage);
        } else {
            holder.ivItemImage.setImageResource(R.drawable.bg_catalog_image_placeholder);
        }

        if (allowRemove) {
            holder.btnRemove.setVisibility(View.VISIBLE);
            holder.btnRemove.setOnClickListener(v -> {
                if (removeClickListener != null) {
                    removeClickListener.onRemoveClick(position);
                }
            });
        } else {
            holder.btnRemove.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String safeText(String value) {
        if (value == null || value.trim().isEmpty()) {
            return "None";
        }
        return value;
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView tvName;
        TextView tvType;
        TextView tvDetails;
        TextView tvNotes;
        TextView tvPrice;
        ImageView ivItemImage;
        Button btnRemove;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivItemImage = itemView.findViewById(R.id.ivItemImage);
            tvName = itemView.findViewById(R.id.tvItemName);
            tvType = itemView.findViewById(R.id.tvItemType);
            tvDetails = itemView.findViewById(R.id.tvItemDetails);
            tvNotes = itemView.findViewById(R.id.tvItemNotes);
            tvPrice = itemView.findViewById(R.id.tvItemPrice);
            btnRemove = itemView.findViewById(R.id.btnRemoveItem);
        }
    }
}
