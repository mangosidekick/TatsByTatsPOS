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

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.tatsbytatspos.R;

public class PaymentFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment, container, false);

        // Now find views from the inflated view
        Button button = view.findViewById(R.id.paid_cash);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TransactionFragment transactionFragment = new TransactionFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                //transaction.replace(R.id.fragment_container, transactionFragment); // idk if we need this
                transaction.addToBackStack(null); // Optional
                transaction.commit();
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
