package com.example.tatsbytatspos.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.tatsbytatspos.R;
import com.example.tatsbytatspos.database.DatabaseHelper;
import com.example.tatsbytatspos.activity.OrderHistory;

public class TransactionFragment extends DialogFragment {
    private static final String ARG_ORDER_SUMMARY = "order_summary";
    private static final String ARG_ORDER_TOTAL = "order_total";
    private static final String ARG_PAYMENT_METHOD = "payment_method";
    private static final String ARG_AMOUNT_PAID = "amount_paid";
    private static final String ARG_CHANGE = "change";
    private DatabaseHelper dbHelper;

    public static TransactionFragment newInstance(String summary, String totalSummary, String paymentMethod, double amountPaid, double change) {
        TransactionFragment fragment = new TransactionFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_SUMMARY, summary);
        args.putString(ARG_ORDER_TOTAL, totalSummary);
        args.putString(ARG_PAYMENT_METHOD, paymentMethod);
        args.putDouble(ARG_AMOUNT_PAID, amountPaid);
        args.putDouble(ARG_CHANGE, change);
        fragment.setArguments(args);
        return fragment;
    }



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Initialize database helper with proper error handling
        try {
            dbHelper = new DatabaseHelper(requireContext());
        } catch (Exception e) {
            // Log the error but continue - we'll check dbHelper before using it
        }
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        TextView orderTextView = view.findViewById(R.id.order_summary_text);
        TextView totalTextView = view.findViewById(R.id.total);
        TextView paymentMethodText = view.findViewById(R.id.payment_method);
        TextView amountPaidText = view.findViewById(R.id.amount_paid);
        TextView changeText = view.findViewById(R.id.change);

        if (getArguments() != null) {
            String summary = getArguments().getString(ARG_ORDER_SUMMARY);
            String totalSummary = getArguments().getString(ARG_ORDER_TOTAL);
            String paymentMethod = getArguments().getString(ARG_PAYMENT_METHOD);
            double amountPaid = getArguments().getDouble(ARG_AMOUNT_PAID);
            double change = getArguments().getDouble(ARG_CHANGE);

            orderTextView.setText(summary);
            totalTextView.setText(totalSummary);
            paymentMethodText.setText("Payment Method: " + paymentMethod);
            amountPaidText.setText(String.format("Amount Paid: ₱%.2f", amountPaid));
            changeText.setText(String.format("Change: ₱%.2f", change));
        }


        //exit button
        ImageButton exit = view.findViewById(R.id.exit);
        exit.setOnClickListener(v -> dismiss());

        Button confirm = view.findViewById(R.id.confirm);
        confirm.setOnClickListener(v -> {
            try {
                // Get order details from arguments
                if (getArguments() == null) return;

                String summary = getArguments().getString(ARG_ORDER_SUMMARY);
                String totalStr = getArguments().getString(ARG_ORDER_TOTAL, "0");
                String paymentMethod = getArguments().getString(ARG_PAYMENT_METHOD);
                double totalAmount = Double.parseDouble(totalStr.replaceAll("[^\\d.]|\\.(?!\\d)", ""));

                // Ensure database helper is initialized
                if (dbHelper == null) {
                    dbHelper = new DatabaseHelper(requireContext());
                }

                // Get amount paid and change from arguments
                double amountPaid = getArguments().getDouble(ARG_AMOUNT_PAID);
                double change = getArguments().getDouble(ARG_CHANGE);

                // Save order to database with payment details
                long orderId = dbHelper.insertOrder(summary, totalAmount, paymentMethod, "Completed", amountPaid, change);

                if (orderId != -1) {
                    // Show success message
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Transaction completed successfully!", Toast.LENGTH_LONG).show();
                    }

                    // Close all dialogs and refresh order history
                    FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                    for (Fragment fragment : fragmentManager.getFragments()) {
                        if (fragment instanceof DialogFragment && fragment.isAdded() && !fragment.isRemoving()) {
                            ((DialogFragment) fragment).dismiss();
                        }
                    }

                    // Refresh the order history if applicable
                    if (getActivity() instanceof OrderHistory) {
                        ((OrderHistory) getActivity()).refreshOrderList();
                    }
                } else {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to save transaction", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                if (getContext() != null) {
                    Toast.makeText(getContext(), "Error completing transaction: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

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
            // Ensure dialog can't be dismissed by clicking outside
            getDialog().setCanceledOnTouchOutside(false);
        }
    }
}