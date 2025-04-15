package com.example.tatsbytatspos;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatsbytatspos.R;
import com.example.tatsbytatspos.Orders;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private List<Orders> orderList;

    public OrdersAdapter(List<Orders> orderList) {
        this.orderList = orderList;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_cards, parent, false); // 'item_product' is the layout you provided
        return new OrderViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Orders currentOrder = orderList.get(position);
        holder.orderNumber.setText(currentOrder.getOrder());
        holder.orderDate.setText(currentOrder.getDate());
        holder.orderTime.setText(currentOrder.getTime());
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderNumber, orderDate, orderTime;
        public OrderViewHolder(View itemView) {
            super(itemView);
            orderNumber = itemView.findViewById(R.id.order_number);
            orderDate = itemView.findViewById(R.id.order_date);
            orderTime = itemView.findViewById(R.id.order_time);
        }
    }
}