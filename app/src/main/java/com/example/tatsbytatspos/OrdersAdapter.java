package com.example.tatsbytatspos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatsbytatspos.Orders;
import com.example.tatsbytatspos.R;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<Orders> ordersList;

    public OrdersAdapter(List<Orders> ordersList) {
        this.ordersList = ordersList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_cards, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Orders order = ordersList.get(position);
        holder.orderNumberText.setText(order.getOrderNumber());
        holder.orderDateText.setText("Date: " + order.getOrderDate());
        holder.orderTimeText.setText("Time: " + order.getOrderTime());
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView orderNumberText;
        TextView orderDateText;
        TextView orderTimeText;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumberText = itemView.findViewById(R.id.order_number);
            orderDateText = itemView.findViewById(R.id.order_date);
            orderTimeText = itemView.findViewById(R.id.order_time);
        }
    }
}