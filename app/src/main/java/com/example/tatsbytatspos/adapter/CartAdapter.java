package com.example.tatsbytatspos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatsbytatspos.R;
import com.example.tatsbytatspos.data.Cart;
import com.example.tatsbytatspos.model.OrderItem;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private Context context;
    private List<OrderItem> cartItems;
    private Cart cart;

    public CartAdapter(Context context, List<OrderItem> cartItems) {
        this.context = context;
        this.cartItems = cartItems;
        this.cart = Cart.getInstance(context);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        OrderItem item = cartItems.get(position);

        holder.productNameTextView.setText(item.getProductName());

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault());
        holder.unitPriceTextView.setText(currencyFormat.format(item.getUnitPrice()));
        holder.quantityTextView.setText(String.valueOf(item.getQuantity()));
        holder.subtotalTextView.setText(currencyFormat.format(item.getSubtotal()));

        holder.removeButton.setOnClickListener(v -> {
            cart.removeItem(item);
            cartItems.remove(position);
            notifyDataSetChanged();
        });

        holder.increaseButton.setOnClickListener(v -> {
            int newQuantity = item.getQuantity() + 1;
            cart.updateItemQuantity(item, newQuantity);
            notifyItemChanged(position);
        });

        holder.decreaseButton.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                int newQuantity = item.getQuantity() - 1;
                cart.updateItemQuantity(item, newQuantity);
                notifyItemChanged(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cartItems.size();
    }

    public void updateItems(List<OrderItem> items) {
        this.cartItems = items;
        notifyDataSetChanged();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        TextView productNameTextView;
        TextView unitPriceTextView;
        TextView quantityTextView;
        TextView subtotalTextView;
        ImageButton removeButton;
        ImageButton increaseButton;
        ImageButton decreaseButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            productNameTextView = itemView.findViewById(R.id.productNameTextView);
            unitPriceTextView = itemView.findViewById(R.id.unitPriceTextView);
            quantityTextView = itemView.findViewById(R.id.quantityTextView);
            subtotalTextView = itemView.findViewById(R.id.subtotalTextView);
            removeButton = itemView.findViewById(R.id.removeButton);
            increaseButton = itemView.findViewById(R.id.increaseButton);
            decreaseButton = itemView.findViewById(R.id.decreaseButton);
        }
    }
}