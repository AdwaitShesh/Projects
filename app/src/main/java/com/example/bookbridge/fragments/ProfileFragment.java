package com.example.bookbridge.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.bookbridge.R;
import com.google.android.material.snackbar.Snackbar;

public class ProfileFragment extends Fragment {

    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etAddress;
    private Button btnSave;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        // Initialize views
        initViews(view);
        
        // Load user data
        loadUserData();
        
        // Setup listeners
        setupListeners();
        
        return view;
    }

    private void initViews(View view) {
        etName = view.findViewById(R.id.et_name);
        etEmail = view.findViewById(R.id.et_email);
        etPhone = view.findViewById(R.id.et_phone);
        etAddress = view.findViewById(R.id.et_address);
        btnSave = view.findViewById(R.id.btn_save);
    }

    private void loadUserData() {
        // TODO: In a real app, fetch user data from database or shared preferences
        // For demo purposes, populate with dummy data
        etName.setText("John Doe");
        etEmail.setText("john.doe@example.com");
        etPhone.setText("+1 234 567 8900");
        etAddress.setText("123 Book Street, Library City, 12345");
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> {
            // Validate inputs
            if (validateInputs()) {
                // TODO: In a real app, save data to database or shared preferences
                saveUserData();
                Snackbar.make(v, "Profile updated successfully", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;
        
        // Check name
        if (etName.getText().toString().trim().isEmpty()) {
            etName.setError("Name cannot be empty");
            isValid = false;
        }
        
        // Check email
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            etEmail.setError("Email cannot be empty");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError("Invalid email format");
            isValid = false;
        }
        
        // Check phone
        if (etPhone.getText().toString().trim().isEmpty()) {
            etPhone.setError("Phone cannot be empty");
            isValid = false;
        }
        
        // Check address
        if (etAddress.getText().toString().trim().isEmpty()) {
            etAddress.setError("Address cannot be empty");
            isValid = false;
        }
        
        return isValid;
    }

    private void saveUserData() {
        // In a real app, this would save to a database or shared preferences
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        
        // For demo, just show a toast
        Toast.makeText(getContext(), "Profile saved for " + name, Toast.LENGTH_SHORT).show();
    }
} 