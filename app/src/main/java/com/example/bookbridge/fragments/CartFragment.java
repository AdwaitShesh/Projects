package com.example.bookbridge.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bookbridge.R;

public class CartFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate a simple layout with a centered TextView
        View view = inflater.inflate(R.layout.fragment_placeholder, container, false);
        
        TextView placeholderText = view.findViewById(R.id.placeholderText);
        placeholderText.setText("Cart Fragment\nComing Soon!");
        
        return view;
    }
} 