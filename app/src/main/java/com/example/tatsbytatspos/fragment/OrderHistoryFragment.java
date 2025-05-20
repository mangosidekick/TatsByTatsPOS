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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
            try {
                Orders order = dbHelper.getOrderById(orderId);
                if (order != null) {
                    // Format and display order details with better formatting
                    StringBuilder details = new StringBuilder();
                    details.append("ORDER #").append(order.getId()).append("\n");
                    details.append("Transaction ID: ").append(String.format("%08d", order.getId())).append("\n");
                    details.append("Date: ").append(new SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
                            .format(new Date())).append("\n\n");
                    details.append("ITEMS:\n");

                    // Format the order summary for better readability
                    String orderSummary = order.getOrderSummary();
                    if (orderSummary != null && !orderSummary.isEmpty()) {
                        // Split by line breaks if they exist
                        String[] items = orderSummary.split("\\n");
                        for (String item : items) {
                            details.append("• ").append(item).append("\n");
                        }
                    } else {
                        details.append("No items found\n");
                    }

                    details.append("\nTOTAL AMOUNT: ₱").append(String.format("%.2f", order.getTotalAmount())).append("\n\n");
                    details.append("PAYMENT METHOD: ").append(order.getPaymentMethod()).append("\n\n");
                    details.append("STATUS: ").append(order.getPaymentStatus());

                    orderDetailsTextView.setText(details.toString());

                    // Log success for debugging
                    Toast.makeText(getContext(), "Order details loaded successfully", Toast.LENGTH_SHORT).show();
                } else {
                    orderDetailsTextView.setText("Order details not found. Order ID: " + orderId);
                    Toast.makeText(getContext(), "Order not found in database", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                orderDetailsTextView.setText("Error loading order details: " + e.getMessage());
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        } else {
            orderDetailsTextView.setText("No order selected or database error");
            if (orderId == -1) {
                Toast.makeText(getContext(), "Invalid order ID", Toast.LENGTH_SHORT).show();
            }
            if (dbHelper == null) {
                Toast.makeText(getContext(), "Database connection error", Toast.LENGTH_SHORT).show();
            }
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