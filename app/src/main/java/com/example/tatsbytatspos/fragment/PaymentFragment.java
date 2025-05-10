package com.example.tatsbytatspos.fragment;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.DialogFragment;

import com.example.tatsbytatspos.R;
import com.example.tatsbytatspos.database.DatabaseHelper;

public class PaymentFragment extends DialogFragment {
    private static final String ARG_ORDER_SUMMARY = "order_summary";
    private static final String ARG_ORDER_TOTAL = "order_total";
    private OnPaymentConfirmedListener paymentListener;
    private DatabaseHelper dbHelper;
    private String orderSummary;
    private double totalAmount;

    public interface OnPaymentConfirmedListener {
        void onPaymentConfirmed(String paymentMethod);
    }

    public void setOnPaymentConfirmedListener(OnPaymentConfirmedListener listener) {
        this.paymentListener = listener;
    }

    public static PaymentFragment newInstance(String summary, String totalSummary) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_SUMMARY, summary);
        args.putString(ARG_ORDER_TOTAL, totalSummary);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Initialize database helper only if context is available
        try {
            dbHelper = new DatabaseHelper(requireContext());
        } catch (Exception e) {
            // Log the error but continue - we'll check dbHelper before using it
        }

        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        ImageButton exit = view.findViewById(R.id.exit);
        exit.setOnClickListener(v -> dismiss());

        TextView orderTextView = view.findViewById(R.id.order_summary_text);
        TextView totalTextView = view.findViewById(R.id.total);

        if (getArguments() != null) {
            orderSummary = getArguments().getString(ARG_ORDER_SUMMARY, "");
            String totalStr = getArguments().getString(ARG_ORDER_TOTAL, "0");
            totalAmount = Double.parseDouble(totalStr.replaceAll("[^\\d.]|\\.(?!\\d)", ""));
            orderTextView.setText(orderSummary);
            totalTextView.setText(totalStr);
        }

        Button paidGcash = view.findViewById(R.id.paid_gcash);
        Button paidCash = view.findViewById(R.id.paid_cash);

        paidCash.setOnClickListener(v -> showCashPaymentDialog());

        paidGcash.setOnClickListener(v -> {
            try {
                // Verify context and database helper are available
                if (getContext() == null) {
                    return;
                }

                // Ensure database helper is initialized
                if (dbHelper == null) {
                    dbHelper = new DatabaseHelper(requireContext());
                }

                // Process payment
                long orderId = dbHelper.insertOrder(orderSummary, totalAmount, "GCash", "Completed");
                if (orderId != -1) {
                    // Notify listener if available
                    if (paymentListener != null) {
                        paymentListener.onPaymentConfirmed("GCash");
                    }
                    // Show transaction confirmation
                    if (isAdded() && !isRemoving()) {
                        showTransactionConfirmation("GCash", totalAmount, 0.0);
                    }
                } else {
                    if (getContext() != null && isAdded()) {
                        Toast.makeText(getContext(), "Failed to process payment", Toast.LENGTH_SHORT).show();
                    }
                }
            } catch (Exception e) {
                if (getContext() != null && isAdded()) {
                    Toast.makeText(getContext(), "Error processing GCash payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        }
    }

    private void showCashPaymentDialog() {
        // Verify context is available
        if (getContext() == null || !isAdded()) return;

        EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        input.setHint("Enter cash amount");

        try {
            Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.abel);
            int orangeColor = ContextCompat.getColor(getContext(), R.color.orange);

            input.setTextColor(Color.BLACK);
            input.setHintTextColor(orangeColor);
            input.setTypeface(typeface);

            AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.MyDialogTheme);
            builder.setTitle("Input Cash Amount")
                    .setView(input)
                    .setPositiveButton("Enter", (dialog, which) -> {
                        try {
                            // Verify context is still available
                            if (getContext() == null || !isAdded()) return;

                            String amountStr = input.getText().toString().trim();
                            if (!amountStr.isEmpty()) {
                                try {
                                    double cashAmount = Double.parseDouble(amountStr);
                                    if (cashAmount >= totalAmount) {
                                        double change = cashAmount - totalAmount;

                                        // Ensure database helper is initialized
                                        if (dbHelper == null) {
                                            dbHelper = new DatabaseHelper(requireContext());
                                        }

                                        // Process payment
                                        long orderId = dbHelper.insertOrder(orderSummary, totalAmount, "Cash", "Completed");
                                        if (orderId != -1) {
                                            // Notify listener if available
                                            if (paymentListener != null) {
                                                paymentListener.onPaymentConfirmed("Cash");
                                            }
                                            // Show transaction confirmation
                                            if (isAdded() && !isRemoving()) {
                                                showTransactionConfirmation("Cash", cashAmount, change);
                                            }
                                        } else {
                                            if (getContext() != null && isAdded()) {
                                                Toast.makeText(getContext(), "Failed to process payment", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    } else {
                                        Toast.makeText(getContext(), "Insufficient amount", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (NumberFormatException e) {
                                    Toast.makeText(getContext(), "Invalid amount", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
                            }
                        } catch (Exception e) {
                            if (getContext() != null && isAdded()) {
                                Toast.makeText(getContext(), "Error processing cash payment: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            builder.setNegativeButton("Cancel", null);
            builder.show();
        } catch (Exception e) {
            if (getContext() != null && isAdded()) {
                Toast.makeText(getContext(), "Dialog error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    // ✅ Moved outside — this method must NOT be nested
    private void showTransactionConfirmation(String paymentMethod, double amountPaid, double change) {
        // Verify activity and fragment state
        if (getActivity() == null || !isAdded() || isRemoving()) return;

        try {
            // Create transaction fragment
            TransactionFragment transactionFragment = TransactionFragment.newInstance(
                    orderSummary,
                    String.format("₱%.2f", totalAmount),
                    paymentMethod,
                    amountPaid,
                    change
            );

            // Use childFragmentManager to avoid fragment state loss
            if (isAdded() && !isRemoving() && !isDetached()) {
                transactionFragment.show(getParentFragmentManager(), "transactionFragmentTag");
                // Don't dismiss this fragment yet - TransactionFragment will handle dismissal after confirmation
            }
        } catch (Exception e) {
            if (getContext() != null && isAdded()) {
                Toast.makeText(getContext(), "Error showing transaction confirmation: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}
