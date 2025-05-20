package com.example.tatsbytatspos.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatsbytatspos.R;
import com.example.tatsbytatspos.model.OrderItem;

import java.util.List;
import java.util.Locale;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.OrderItemViewHolder> {
    private final List<OrderItem> orderItems;
    private Context context;
    private OnItemDeleteListener deleteListener;

    public OrderItemAdapter(Context context, List<OrderItem> orderItems) {
        this.context = context;
        this.orderItems = orderItems;
    }

    public interface OnItemDeleteListener {
        void onItemDelete(int position);
    }

    public void setOnItemDeleteListener(OnItemDeleteListener listener) {
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public OrderItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_order_item, parent, false);
        return new OrderItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderItemViewHolder holder, int position) {
        OrderItem item = orderItems.get(position);
        holder.bind(item);

        holder.deleteButton.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onItemDelete(position);
                orderItems.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, orderItems.size());
            }
        });
    }

    @Override
    public int getItemCount() {
        return orderItems.size();
    }

    static class OrderItemViewHolder extends RecyclerView.ViewHolder {
        private final TextView productName;
        private final TextView quantity;
        private final TextView price;
        private final TextView subtotal;
        private final ImageButton deleteButton;

        public OrderItemViewHolder(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            quantity = itemView.findViewById(R.id.quantity);
            price = itemView.findViewById(R.id.price);
            subtotal = itemView.findViewById(R.id.subtotal);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }

        public void bind(OrderItem item) {
            productName.setText(item.getProductName());
            quantity.setText(String.format(Locale.getDefault(), "x%d", item.getQuantity()));
            price.setText(String.format(Locale.getDefault(), "₱%.2f", item.getUnitPrice()));
            subtotal.setText(String.format(Locale.getDefault(), "₱%.2f",
                    item.getUnitPrice() * item.getQuantity()));
        }
    }
}