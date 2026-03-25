package com.example.shopassist.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.shopassist.R;
import com.example.shopassist.models.StoreItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class StoreCatalogAdapter extends RecyclerView.Adapter<StoreCatalogAdapter.StoreItemViewHolder> {

    public interface OnAddClickListener {
        void onAddClick(StoreItem item);
    }

    private final ArrayList<StoreItem> items = new ArrayList<>();
    private final OnAddClickListener addClickListener;

    public StoreCatalogAdapter(OnAddClickListener addClickListener) {
        this.addClickListener = addClickListener;
    }

    public void updateItems(List<StoreItem> updatedItems) {
        items.clear();
        items.addAll(updatedItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public StoreItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_store_catalog, parent, false);
        return new StoreItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoreItemViewHolder holder, int position) {
        StoreItem item = items.get(position);
        holder.tvName.setText(item.getName());
        holder.tvCategory.setText(item.getCategory());
        holder.tvNotes.setText(item.getNotes() == null || item.getNotes().trim().isEmpty()
                ? "Popular store option"
                : item.getNotes());
        holder.tvPrice.setText(String.format(Locale.getDefault(), "R %.2f", item.getPrice()));
        holder.progressBar.setVisibility(View.VISIBLE);

        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.bg_catalog_image_placeholder)
                .error(R.drawable.bg_catalog_image_placeholder)
                .centerCrop()
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        holder.progressBar.setVisibility(View.GONE);
                        return false;
                    }
                })
                .into(holder.ivProduct);

        holder.btnAdd.setOnClickListener(v -> {
            if (addClickListener != null) {
                addClickListener.onAddClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class StoreItemViewHolder extends RecyclerView.ViewHolder {

        ImageView ivProduct;
        TextView tvName;
        TextView tvCategory;
        TextView tvNotes;
        TextView tvPrice;
        ProgressBar progressBar;
        Button btnAdd;

        StoreItemViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivStoreItemImage);
            tvName = itemView.findViewById(R.id.tvStoreItemName);
            tvCategory = itemView.findViewById(R.id.tvStoreItemCategory);
            tvNotes = itemView.findViewById(R.id.tvStoreItemNotes);
            tvPrice = itemView.findViewById(R.id.tvStoreItemPrice);
            progressBar = itemView.findViewById(R.id.progressStoreImage);
            btnAdd = itemView.findViewById(R.id.btnAddStoreResult);
        }
    }
}
