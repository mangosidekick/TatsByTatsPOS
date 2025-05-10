package com.example.tatsbytatspos.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.tatsbytatspos.R;
import com.example.tatsbytatspos.database.DatabaseHelper;
import com.example.tatsbytatspos.model.Orders;

public class OrderHistoryFragment extends DialogFragment {
    private static final String ARG_ORDER_ID = "order_id";
    private int orderId;
    private DatabaseHelper dbHelper;

    public static OrderHistoryFragment newInstance(int orderId) {
        OrderHistoryFragment fragment = new OrderHistoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_ORDER_ID, orderId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderId = getArguments().getInt(ARG_ORDER_ID, -1);
        }

        try {
            dbHelper = new DatabaseHelper(requireContext());
        } catch (Exception e) {
            // Log the error but continue
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_order_history, container, false);
        ImageButton exit = view.findViewById(R.id.exit);
        exit.setOnClickListener(v -> dismiss());

        // Find the TextView to display order details
        TextView orderDetailsTextView = view.findViewById(R.id.order_details);

        // Load and display order details
        if (orderId != -1 && dbHelper != null) {
            Orders order = dbHelper.getOrderById(orderId);
            if (order != null) {
                // Format and display order details
                StringBuilder details = new StringBuilder();
                details.append("Order #").append(order.getId()).append("\n\n");
                details.append("Items:\n").append(order.getOrderSummary()).append("\n\n");
                details.append("Total Amount: ₱").append(String.format("%.2f", order.getTotalAmount())).append("\n");
                details.append("Payment Method: ").append(order.getPaymentMethod()).append("\n");
                details.append("Status: ").append(order.getPaymentStatus());

                orderDetailsTextView.setText(details.toString());
            } else {
                orderDetailsTextView.setText("Order details not found");
            }
        } else {
            orderDetailsTextView.setText("No order selected");
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }
}
