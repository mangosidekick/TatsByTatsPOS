package com.example.tatsbytatspos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tatsbytatspos.R;
import com.example.tatsbytatspos.fragment.OrderHistoryFragment;
import com.example.tatsbytatspos.model.Orders;

import java.util.List;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private Context context;
    private List<Orders> ordersList;
    private FragmentManager fragmentManager;

    public OrdersAdapter(Context context, List<Orders> ordersList, FragmentManager fragmentManager) {
        this.context = context;
        this.ordersList = ordersList;
        this.fragmentManager = fragmentManager;
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        Date orderDate = new Date(order.getOrderDate());

        holder.orderNumberText.setText("Order #" + order.getId());
        holder.orderDateText.setText(dateFormat.format(orderDate));
        holder.orderTimeText.setText(timeFormat.format(orderDate));
        holder.orderAmountText.setText(String.format(Locale.getDefault(), "₱%.2f", order.getTotalAmount()));
        holder.paymentInfoText.setText(order.getPaymentMethod() + " (" + order.getPaymentStatus() + ")");

        holder.itemView.setOnClickListener(v -> {
            try {
                OrderHistoryFragment popup = OrderHistoryFragment.newInstance(order.getId());
                popup.show(fragmentManager, "orderHistoryDialog");
                Toast.makeText(context, "Loading order #" + order.getId(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(context, "Error opening order details: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });
    }

    @Override
    public int getItemCount() {
        return ordersList.size();
    }

    public static class OrderViewHolder extends RecyclerView.ViewHolder {

        TextView orderNumberText;
        TextView orderDateText;
        TextView orderTimeText;
        TextView orderAmountText;
        TextView paymentInfoText;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumberText = itemView.findViewById(R.id.order_number);
            orderDateText = itemView.findViewById(R.id.order_date);
            orderTimeText = itemView.findViewById(R.id.order_time);
            orderAmountText = itemView.findViewById(R.id.order_amount);
            paymentInfoText = itemView.findViewById(R.id.payment_info);
        }
    }
}