package com.example.tatsbytatspos.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.tatsbytatspos.R;

public class TransactionFragment extends DialogFragment {

    private static final String ARG_ORDER_SUMMARY = "order_summary";
    private static final String ARG_ORDER_TOTAL = "order_total";

    public static TransactionFragment newInstance(String summary, String totalSummary) {
        TransactionFragment fragment = new TransactionFragment();
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
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

        TextView orderTextView = view.findViewById(R.id.order_summary_text);
        if (getArguments() != null) {
            String summary = getArguments().getString(ARG_ORDER_SUMMARY);
            orderTextView.setText(summary);
        }
        TextView totalTextView = view.findViewById(R.id.total);
        if (getArguments() != null) {
            String totalSummary = getArguments().getString(ARG_ORDER_TOTAL);
            totalTextView.setText(totalSummary);
        }


        //exit button
        ImageButton exit = view.findViewById(R.id.exit);
            exit.setOnClickListener(v -> dismiss());

        Button confirm = view.findViewById(R.id.confirm);
        confirm.setOnClickListener(v -> {
                FragmentManager fragmentManager = requireActivity().getSupportFragmentManager(); // Access the activity's fragments

        for (Fragment fragment : fragmentManager.getFragments()) {
            if (fragment instanceof DialogFragment && fragment.isAdded() && !fragment.isRemoving()) {
                ((DialogFragment) fragment).dismiss();
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
}
