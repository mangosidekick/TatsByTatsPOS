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

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private Context context;
    private List<Orders> ordersList;
    private FragmentManager fragmentManager;

    public OrdersAdapter(Context context, List<Orders> ordersList,FragmentManager fragmentManager ) {
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
        holder.orderNumberText.setText(order.getOrderNumber());
        holder.orderDateText.setText("Date: " + order.getOrderDate());
        holder.orderTimeText.setText("Time: " + order.getOrderTime());

        /*
         confirmButton.setOnClickListener(v->{
             PaymentFragment popup = new PaymentFragment();
             popup.show(getSupportFragmentManager(), "myPaymentTag");
         });
         */
        //this is to make the thingies clickable and show the fragment
        holder.itemView.setOnClickListener(v -> {
            // Handle the click event here
            //Toast.makeText(context, "Clicked: ", Toast.LENGTH_SHORT).show();
            OrderHistoryFragment popup = new OrderHistoryFragment();
            popup.show(fragmentManager, "myHistoryTag");
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

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderNumberText = itemView.findViewById(R.id.order_number);
            orderDateText = itemView.findViewById(R.id.order_date);
            orderTimeText = itemView.findViewById(R.id.order_time);
        }
    }
}