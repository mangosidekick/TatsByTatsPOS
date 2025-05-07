package com.example.tatsbytatspos.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.tatsbytatspos.R;
import com.example.tatsbytatspos.activity.MainActivity;
import com.example.tatsbytatspos.activity.OrderHistory;

public class PaymentFragment extends DialogFragment {

    private static final String ARG_ORDER_SUMMARY = "order_summary";

    public static PaymentFragment newInstance(String summary) {
        PaymentFragment fragment = new PaymentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_ORDER_SUMMARY, summary);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment, container, false);
        ImageButton exit = view.findViewById(R.id.exit); // Make sure your XML has a Button with id="exit"
        exit.setOnClickListener(v -> dismiss());

        TextView orderTextView = view.findViewById(R.id.order_summary_text);
        if (getArguments() != null) {
            String summary = getArguments().getString(ARG_ORDER_SUMMARY);
            orderTextView.setText(summary);
        }

        Button paidGcash = view.findViewById(R.id.paid_gcash);
        Button paidCash = view.findViewById(R.id.paid_cash);

        paidCash.setOnClickListener(v -> showNumberInputDialog());

        paidGcash.setOnClickListener(v -> {
            TransactionFragment transactionFragment = new TransactionFragment();
            transactionFragment.show(getParentFragmentManager(), "myPaymentTag");
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

    private void showNumberInputDialog() {
        EditText input = new EditText(getContext());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Enter a number");

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Input Number")
                .setView(input)
                .setPositiveButton("Enter", (dialog, which) -> {
                    String numberStr = input.getText().toString().trim();
                    if (!numberStr.isEmpty()) {
                        int number = Integer.parseInt(numberStr);

                        // Navigate to the next fragment and pass the number
                        TransactionFragment transactionFragment = new TransactionFragment();
                        transactionFragment.show(getParentFragmentManager(), "myPaymentTag");
                        Bundle bundle = new Bundle();
                        bundle.putInt("number", number);

                    } else {
                        Toast.makeText(getContext(), "Please enter a number", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
