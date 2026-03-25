package com.example.shopassist.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.shopassist.R;
import com.example.shopassist.models.User;

import java.util.ArrayList;

public class UserAdminAdapter extends RecyclerView.Adapter<UserAdminAdapter.UserViewHolder> {

    public interface OnUserAdminActionListener {
        void onVerifyClick(User user);
        void onRemoveClick(User user);
    }

    private final ArrayList<User> users;
    private final OnUserAdminActionListener listener;

    public UserAdminAdapter(ArrayList<User> users, OnUserAdminActionListener listener) {
        this.users = users;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_admin, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.tvUserName.setText(user.getName());
        holder.tvUserRole.setText("Role: " + user.getRole());
        holder.tvUserEmail.setText(user.getEmail());

        if ("Shopper".equalsIgnoreCase(user.getRole())) {
            holder.btnVerify.setVisibility(View.VISIBLE);
            holder.btnVerify.setText(user.isVerifiedShopper() ? "Verified" : "Verify shopper");
            holder.btnVerify.setEnabled(!user.isVerifiedShopper());
            holder.btnVerify.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onVerifyClick(user);
                }
            });
            holder.btnRemove.setVisibility(View.VISIBLE);
            holder.btnRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveClick(user);
                }
            });
        } else {
            holder.btnVerify.setVisibility(View.GONE);
            holder.btnRemove.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName;
        TextView tvUserRole;
        TextView tvUserEmail;
        Button btnVerify;
        Button btnRemove;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvAdminUserName);
            tvUserRole = itemView.findViewById(R.id.tvAdminUserRole);
            tvUserEmail = itemView.findViewById(R.id.tvAdminUserEmail);
            btnVerify = itemView.findViewById(R.id.btnVerifyShopper);
            btnRemove = itemView.findViewById(R.id.btnRemoveShopper);
        }
    }
}
