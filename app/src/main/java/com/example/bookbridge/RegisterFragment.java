package com.example.bookbridge;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.bookbridge.data.AppDatabase;
import com.example.bookbridge.data.User;
import com.example.bookbridge.data.UserDao;
import com.example.bookbridge.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class RegisterFragment extends Fragment {

    private TextInputLayout tilUsername, tilEmail, tilPassword, tilMobileNo;
    private TextInputEditText etUsername, etEmail, etPassword, etMobileNo;
    private MaterialButton btnRegister;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        tilUsername = view.findViewById(R.id.tilUsername);
        tilEmail = view.findViewById(R.id.tilEmail);
        tilPassword = view.findViewById(R.id.tilPassword);
        tilMobileNo = view.findViewById(R.id.tilMobileNo);

        etUsername = view.findViewById(R.id.etUsername);
        etEmail = view.findViewById(R.id.etEmail);
        etPassword = view.findViewById(R.id.etPassword);
        etMobileNo = view.findViewById(R.id.etMobileNo);

        btnRegister = view.findViewById(R.id.btnRegister);

        // Set up button click listener
        btnRegister.setOnClickListener(v -> {
            if (validateInputs()) {
                registerUser();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate username
        String username = etUsername != null && etUsername.getText() != null 
                ? etUsername.getText().toString().trim() 
                : "";
        if (username.isEmpty()) {
            tilUsername.setError(getString(R.string.username_required));
            isValid = false;
        } else {
            tilUsername.setError(null);
        }

        // Validate email
        String email = etEmail != null && etEmail.getText() != null 
                ? etEmail.getText().toString().trim() 
                : "";
        if (email.isEmpty()) {
            tilEmail.setError(getString(R.string.email_required));
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError(getString(R.string.invalid_email));
            isValid = false;
        } else {
            tilEmail.setError(null);
        }

        // Validate password
        String password = etPassword != null && etPassword.getText() != null 
                ? etPassword.getText().toString().trim() 
                : "";
        if (password.isEmpty()) {
            tilPassword.setError(getString(R.string.password_required));
            isValid = false;
        } else if (password.length() < 6) {
            tilPassword.setError(getString(R.string.password_length));
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        // Validate mobile number
        String mobileNo = etMobileNo != null && etMobileNo.getText() != null 
                ? etMobileNo.getText().toString().trim() 
                : "";
        if (mobileNo.isEmpty()) {
            tilMobileNo.setError(getString(R.string.mobile_required));
            isValid = false;
        } else if (mobileNo.length() != 10) {
            tilMobileNo.setError(getString(R.string.invalid_mobile));
            isValid = false;
        } else {
            tilMobileNo.setError(null);
        }

        return isValid;
    }

    private void registerUser() {
        String username = etUsername != null && etUsername.getText() != null 
                ? etUsername.getText().toString().trim() 
                : "";
        String email = etEmail != null && etEmail.getText() != null 
                ? etEmail.getText().toString().trim() 
                : "";
        String password = etPassword != null && etPassword.getText() != null 
                ? etPassword.getText().toString().trim() 
                : "";
        String mobileNo = etMobileNo != null && etMobileNo.getText() != null 
                ? etMobileNo.getText().toString().trim() 
                : "";

        // Execute database operations on a background thread
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            UserDao userDao = db.userDao();
            
            // Check if user already exists
            User existingUser = userDao.getUserByEmailOrUsername(email, username);

            // Update UI on the main thread
            requireActivity().runOnUiThread(() -> {
                if (existingUser != null) {
                    Toast.makeText(requireContext(), R.string.user_exists, Toast.LENGTH_SHORT).show();
                    // Navigate to login tab
                    if (getActivity() instanceof AuthActivity) {
                        ViewPager2 viewPager = getActivity().findViewById(R.id.viewPager);
                        if (viewPager != null) {
                            viewPager.setCurrentItem(1);
                        }
                    }
                    return;
                }

                // Create new user on background thread
                executor.execute(() -> {
                    User user = new User(username, email, password, mobileNo);
                    userDao.insert(user);
                    
                    // Get the inserted user with the ID
                    User insertedUser = userDao.getUserByEmailOrUsername(email, username);
                    
                    // Update UI on main thread
                    requireActivity().runOnUiThread(() -> {
                        // Save user session
                        if (insertedUser != null) {
                            SessionManager sessionManager = SessionManager.getInstance(requireContext());
                            sessionManager.createLoginSession(insertedUser);
                        }
                        
                        Toast.makeText(requireContext(), R.string.register_success, Toast.LENGTH_SHORT).show();
                        // Navigate to main activity
                        Intent intent = new Intent(requireContext(), MainActivity.class);
                        startActivity(intent);
                        requireActivity().finish();
                    });
                });
            });
        });
    }
} 