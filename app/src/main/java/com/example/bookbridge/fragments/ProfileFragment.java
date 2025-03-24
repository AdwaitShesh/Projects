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
import com.example.bookbridge.data.User;
import com.example.bookbridge.utils.SessionManager;
import com.google.android.material.snackbar.Snackbar;

public class ProfileFragment extends Fragment {

    private EditText etName;
    private EditText etEmail;
    private EditText etPhone;
    private EditText etAddress;
    private Button btnSave;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        
        // Initialize SessionManager
        sessionManager = SessionManager.getInstance(requireContext());
        
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
        // Get user data from SessionManager
        if (sessionManager.isLoggedIn()) {
            User user = sessionManager.getUser();
            if (user != null) {
                etName.setText(user.getUsername());
                etEmail.setText(user.getEmail());
                etPhone.setText(user.getMobileNo());
                etAddress.setText(sessionManager.getUserAddress());
            }
        } else {
            // User not logged in, which shouldn't happen in normal flow
            Toast.makeText(getContext(), "Error: User not logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupListeners() {
        btnSave.setOnClickListener(v -> {
            // Validate inputs
            if (validateInputs()) {
                // Save updated user data
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
        // Get updated data from inputs
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        
        // Save to SessionManager
        sessionManager.updateUserDetails(name, email, phone, address);
        
        // Show success message
        Toast.makeText(getContext(), "Profile saved for " + name, Toast.LENGTH_SHORT).show();
    }
} 