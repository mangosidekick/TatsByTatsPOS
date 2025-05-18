package com.example.tatsbytatspos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatsbytatspos.R;
import com.example.tatsbytatspos.model.Product;

import java.util.ArrayList;
import java.util.List;

public class FileMaintenanceAdapter extends RecyclerView.Adapter<FileMaintenanceAdapter.ViewHolder> {
    private final List<Product> products;
    private final Context context;
    private final OnEditPriceClickListener editPriceListener;
    private final List<Product> selectedProducts;

    public interface OnEditPriceClickListener {
        void onEditPriceClick(Product product);
    }

    public FileMaintenanceAdapter(Context context, List<Product> products, OnEditPriceClickListener editPriceListener) {
        this.context = context;
        this.products = products;
        this.editPriceListener = editPriceListener;
        this.selectedProducts = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_file_maintenance, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);

        holder.tvProductName.setText(product.getName());
        holder.tvPrice.setText(String.format("₱%.2f", product.getPrice()));
        holder.tvQuantity.setText(String.valueOf(product.getQuantity()));

        holder.btnEditPrice.setOnClickListener(v -> {
            if (editPriceListener != null) {
                editPriceListener.onEditPriceClick(product);
            }
        });

        holder.checkBox.setChecked(selectedProducts.contains(product));
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedProducts.contains(product)) {
                    selectedProducts.add(product);
                }
            } else {
                selectedProducts.remove(product);
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public List<Product> getSelectedProducts() {
        return selectedProducts;
    }

    public void clearSelection() {
        selectedProducts.clear();
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvProductName;
        TextView tvPrice;
        TextView tvQuantity;
        Button btnEditPrice;
        CheckBox checkBox;

        ViewHolder(View itemView) {
            super(itemView);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvQuantity = itemView.findViewById(R.id.tvQuantity);
            btnEditPrice = itemView.findViewById(R.id.btnEditPrice);
            checkBox = itemView.findViewById(R.id.checkBox);
        }
    }
}