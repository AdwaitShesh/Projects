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

import com.example.bookbridge.data.AppDatabase;
import com.example.bookbridge.data.User;
import com.example.bookbridge.data.UserDao;
import com.example.bookbridge.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class LoginFragment extends Fragment {

    private TextInputLayout tilUsernameOrEmail, tilPassword;
    private TextInputEditText etUsernameOrEmail, etPassword;
    private MaterialButton btnLogin;
    private final Executor executor = Executors.newSingleThreadExecutor();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize views
        tilUsernameOrEmail = view.findViewById(R.id.tilUsernameOrEmail);
        tilPassword = view.findViewById(R.id.tilPassword);

        etUsernameOrEmail = view.findViewById(R.id.etUsernameOrEmail);
        etPassword = view.findViewById(R.id.etPassword);

        btnLogin = view.findViewById(R.id.btnLogin);

        // Set up button click listener
        btnLogin.setOnClickListener(v -> {
            if (validateInputs()) {
                loginUser();
            }
        });
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate username/email
        if (etUsernameOrEmail.getText() == null || etUsernameOrEmail.getText().toString().trim().isEmpty()) {
            tilUsernameOrEmail.setError(getString(R.string.username_required));
            isValid = false;
        } else {
            tilUsernameOrEmail.setError(null);
        }

        // Validate password
        if (etPassword.getText() == null || etPassword.getText().toString().trim().isEmpty()) {
            tilPassword.setError(getString(R.string.password_required));
            isValid = false;
        } else {
            tilPassword.setError(null);
        }

        return isValid;
    }

    private void loginUser() {
        String usernameOrEmail = etUsernameOrEmail != null && etUsernameOrEmail.getText() != null 
                ? etUsernameOrEmail.getText().toString().trim() 
                : "";
        String password = etPassword != null && etPassword.getText() != null 
                ? etPassword.getText().toString().trim() 
                : "";

        // Execute database operations on a background thread
        executor.execute(() -> {
            AppDatabase db = AppDatabase.getInstance(requireContext());
            UserDao userDao = db.userDao();
            
            // Attempt to login
            User user = userDao.login(usernameOrEmail, password);

            // Update UI on the main thread
            requireActivity().runOnUiThread(() -> {
                if (user != null) {
                    // Save user session
                    SessionManager sessionManager = SessionManager.getInstance(requireContext());
                    sessionManager.createLoginSession(user);
                    
                    Toast.makeText(requireContext(), R.string.login_success, Toast.LENGTH_SHORT).show();
                    // Navigate to main activity
                    Intent intent = new Intent(requireContext(), MainActivity.class);
                    startActivity(intent);
                    requireActivity().finish();
                } else {
                    Toast.makeText(requireContext(), R.string.invalid_credentials, Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
} 