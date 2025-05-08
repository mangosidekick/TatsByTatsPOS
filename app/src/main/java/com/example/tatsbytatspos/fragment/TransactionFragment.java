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
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.tatsbytatspos.R;

public class TransactionFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);

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
