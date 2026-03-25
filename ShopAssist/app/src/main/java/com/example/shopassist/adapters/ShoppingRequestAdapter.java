package com.example.shopassist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopassist.R;
import com.example.shopassist.data.MockRepository;
import com.example.shopassist.models.ShoppingRequest;

import java.util.ArrayList;

public class ShoppingRequestAdapter extends RecyclerView.Adapter<ShoppingRequestAdapter.RequestViewHolder> {

    public interface OnRequestActionListener {
        void onOpenDetails(ShoppingRequest request);
    }

    private final ArrayList<ShoppingRequest> requests;
    private final boolean hideExactLocation;
    private final OnRequestActionListener listener;

    public ShoppingRequestAdapter(ArrayList<ShoppingRequest> requests, boolean hideExactLocation, OnRequestActionListener listener) {
        this.requests = requests;
        this.hideExactLocation = hideExactLocation;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RequestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping_request, parent, false);
        return new RequestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RequestViewHolder holder, int position) {
        ShoppingRequest request = requests.get(position);

        holder.tvRequestId.setText(request.getRequestId());
        holder.tvCustomer.setText("Customer: " + request.getCustomerName());
        holder.tvStatus.setText("Status: " + request.getStatus());
        holder.tvSlot.setText("Slot: " + request.getDeliverySlot());
        holder.tvTotal.setText("Estimated total: " + MockRepository.getInstance().formatPrice(request.getEstimatedTotal()));

        if (hideExactLocation) {
            holder.tvLocation.setText("Location: " + MockRepository.getInstance().getApproximateLocation(request.getLocationText()));
        } else {
            holder.tvLocation.setText("Location: " + request.getLocationText());
        }

        holder.btnOpenRequest.setOnClickListener(v -> {
            if (listener != null) {
                listener.onOpenDetails(request);
            }
        });
    }

    @Override
    public int getItemCount() {
        return requests.size();
    }

    static class RequestViewHolder extends RecyclerView.ViewHolder {
        TextView tvRequestId;
        TextView tvCustomer;
        TextView tvStatus;
        TextView tvSlot;
        TextView tvLocation;
        TextView tvTotal;
        Button btnOpenRequest;

        public RequestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRequestId = itemView.findViewById(R.id.tvRequestCardId);
            tvCustomer = itemView.findViewById(R.id.tvRequestCardCustomer);
            tvStatus = itemView.findViewById(R.id.tvRequestCardStatus);
            tvSlot = itemView.findViewById(R.id.tvRequestCardSlot);
            tvLocation = itemView.findViewById(R.id.tvRequestCardLocation);
            tvTotal = itemView.findViewById(R.id.tvRequestCardTotal);
            btnOpenRequest = itemView.findViewById(R.id.btnOpenRequest);
        }
    }
}

