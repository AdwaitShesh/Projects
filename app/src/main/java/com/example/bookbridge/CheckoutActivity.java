package com.example.bookbridge;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import com.example.bookbridge.models.Book;
import com.example.bookbridge.utils.BookManager;
import com.example.bookbridge.utils.SessionManager;
import com.example.bookbridge.data.User;
import com.example.bookbridge.utils.CartManager;
import com.example.bookbridge.utils.BottomNavManager;

import java.text.DecimalFormat;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private EditText etFullName, etAddress, etCity, etState, etPincode, etPhone;
    private RadioGroup rgPaymentMethod;
    private RadioButton rbCashOnDelivery, rbUpi, rbNetBanking, rbCreditCard;
    private TextView tvBookTitle, tvBookPrice, tvDeliveryCharge, tvTotalAmount, tvSubtotal;
    private Button btnProceedToPay;
    private Toolbar toolbar;
    
    // Payment details fields
    private EditText etUpiId;
    private LinearLayout llCreditCardDetails, llNetBankingDetails;
    private Spinner spinnerBank;

    private Book book;
    private double bookPrice;
    private String bookTitle;
    private DecimalFormat df = new DecimalFormat("0.00");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Check if user is logged in
        SessionManager sessionManager = SessionManager.getInstance(this);
        if (!sessionManager.isLoggedIn()) {
            // Redirect to AuthActivity
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);
            finish();
            return;
        }
        
        setContentView(R.layout.activity_checkout);
        
        // Initialize views
        initViews();
        
        // Set up toolbar
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Checkout");
        }
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
        
        // Setup the spinner for bank selection
        setupSpinner();
        
        // Setup payment method listeners
        setupPaymentMethodListeners();
        
        // Set up bottom navigation
        setupBottomNavigation();
        
        // Get the book details from intent with logging
        Intent receivedIntent = getIntent();
        Log.d("CheckoutActivity", "Received intent: " + receivedIntent);
        
        if (receivedIntent.hasExtra("book_id")) {
            int bookId = receivedIntent.getIntExtra("book_id", -1);
            Log.d("CheckoutActivity", "Received book ID: " + bookId);
            
            book = BookManager.getBookById(bookId);
            if (book != null) {
                Log.d("CheckoutActivity", "Found book: " + book.getTitle());
            } else {
                Log.e("CheckoutActivity", "Book not found for ID: " + bookId);
            }
        } else {
            Log.d("CheckoutActivity", "No book ID in intent");
        }
        
        // Get other details from intent
        if (receivedIntent.hasExtra("book_title")) {
            bookTitle = receivedIntent.getStringExtra("book_title");
            Log.d("CheckoutActivity", "Received book title: " + bookTitle);
        } else {
            Log.d("CheckoutActivity", "No book title in intent");
            bookTitle = "Your Purchase";
        }
        
        if (receivedIntent.hasExtra("book_price")) {
            bookPrice = receivedIntent.getDoubleExtra("book_price", 0.0);
            Log.d("CheckoutActivity", "Received book price: " + bookPrice);
        } else {
            Log.d("CheckoutActivity", "No book price in intent");
            // Try to get price from book object if available
            if (book != null) {
                bookPrice = book.getPrice() * 0.8; // 20% discount
                Log.d("CheckoutActivity", "Using book price from object: " + bookPrice);
            }
        }
        
        // Display order summary
        displayOrderSummary();
        
        // Prefill user info from session
        prefillUserInfo();
        
        // Set up button click listener for proceeding to payment
        setupPaymentButton();
    }

    private void initViews() {
        etFullName = findViewById(R.id.et_full_name);
        etAddress = findViewById(R.id.et_address);
        etCity = findViewById(R.id.et_city);
        etState = findViewById(R.id.et_state);
        etPincode = findViewById(R.id.et_pincode);
        etPhone = findViewById(R.id.et_phone);
        
        // Initialize toolbar
        toolbar = findViewById(R.id.toolbar);
        
        // Payment option radio buttons
        rgPaymentMethod = findViewById(R.id.rg_payment_method);
        rbCashOnDelivery = findViewById(R.id.rb_cash_on_delivery);
        rbUpi = findViewById(R.id.rb_upi);
        rbNetBanking = findViewById(R.id.rb_net_banking);
        rbCreditCard = findViewById(R.id.rb_credit_card);
        
        // Payment details views
        etUpiId = findViewById(R.id.et_upi_id);
        llCreditCardDetails = findViewById(R.id.ll_credit_card_details);
        llNetBankingDetails = findViewById(R.id.ll_net_banking_details);
        spinnerBank = findViewById(R.id.spinner_bank);
        
        // Order summary views
        tvBookTitle = findViewById(R.id.tv_book_title);
        tvBookPrice = findViewById(R.id.tv_book_price);
        tvDeliveryCharge = findViewById(R.id.tv_delivery_charge);
        tvTotalAmount = findViewById(R.id.tv_total_amount);
        tvSubtotal = findViewById(R.id.tv_subtotal);
        btnProceedToPay = findViewById(R.id.btn_proceed_to_pay);
    }
    
    private void setupSpinner() {
        // Create an array of bank names
        String[] banks = {getString(R.string.select_bank), "State Bank of India", "HDFC Bank", "ICICI Bank", "Axis Bank", "Bank of Baroda", "Punjab National Bank"};
        
        // Create adapter for spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, banks);
        spinnerBank.setAdapter(adapter);
    }
    
    private void setupPaymentMethodListeners() {
        rgPaymentMethod.setOnCheckedChangeListener((group, checkedId) -> {
            // Hide all payment detail fields
            etUpiId.setVisibility(View.GONE);
            llCreditCardDetails.setVisibility(View.GONE);
            llNetBankingDetails.setVisibility(View.GONE);
            
            // Show payment detail fields based on the selected payment method
            if (checkedId == R.id.rb_upi) {
                etUpiId.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rb_credit_card) {
                llCreditCardDetails.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.rb_net_banking) {
                llNetBankingDetails.setVisibility(View.VISIBLE);
            }
        });
    }

    private void displayOrderSummary() {
        if (bookTitle != null) {
            tvBookTitle.setText(bookTitle);
        }

        // Set book price
        tvBookPrice.setText(String.format(Locale.getDefault(), "₹%.2f", bookPrice));
        if (tvSubtotal != null) {
            tvSubtotal.setText(String.format(Locale.getDefault(), "₹%.2f", bookPrice));
        }

        // Set delivery charge (fixed at ₹40)
        double deliveryCharge = 40.0;
        tvDeliveryCharge.setText(String.format(Locale.getDefault(), "₹%.2f", deliveryCharge));

        // Calculate and set total amount
        double totalAmount = calculateTotalAmount();
        tvTotalAmount.setText(String.format(Locale.getDefault(), "₹%.2f", totalAmount));
    }

    private double calculateTotalAmount() {
        // Add ₹40 for delivery
        return bookPrice + 40.0;
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate full name
        if (TextUtils.isEmpty(etFullName.getText())) {
            etFullName.setError(getString(R.string.username_required));
            isValid = false;
        }

        // Validate address
        if (TextUtils.isEmpty(etAddress.getText())) {
            etAddress.setError(getString(R.string.username_required));
            isValid = false;
        }

        // Validate city
        if (TextUtils.isEmpty(etCity.getText())) {
            etCity.setError(getString(R.string.username_required));
            isValid = false;
        }

        // Validate state
        if (TextUtils.isEmpty(etState.getText())) {
            etState.setError(getString(R.string.username_required));
            isValid = false;
        }

        // Validate pincode
        if (TextUtils.isEmpty(etPincode.getText()) || etPincode.getText().length() != 6) {
            etPincode.setError(getString(R.string.username_required));
            isValid = false;
        }

        // Validate phone
        if (TextUtils.isEmpty(etPhone.getText()) || etPhone.getText().length() != 10) {
            etPhone.setError(getString(R.string.mobile_required));
            isValid = false;
        }
        
        // Validate payment method specific fields
        int selectedPaymentId = rgPaymentMethod.getCheckedRadioButtonId();
        if (selectedPaymentId == R.id.rb_upi) {
            if (TextUtils.isEmpty(etUpiId.getText())) {
                etUpiId.setError(getString(R.string.upi_id_required));
                isValid = false;
            }
        } else if (selectedPaymentId == R.id.rb_credit_card) {
            EditText etCardNumber = findViewById(R.id.et_card_number);
            EditText etExpiryDate = findViewById(R.id.et_expiry_date);
            EditText etCvv = findViewById(R.id.et_cvv);
            
            // Modified validation to accept any 16-digit number
            if (TextUtils.isEmpty(etCardNumber.getText()) || etCardNumber.getText().length() != 16) {
                etCardNumber.setError(getString(R.string.valid_card_required));
                isValid = false;
            }
            
            if (TextUtils.isEmpty(etExpiryDate.getText())) {
                etExpiryDate.setError(getString(R.string.expiry_required));
                isValid = false;
            }
            
            if (TextUtils.isEmpty(etCvv.getText()) || etCvv.getText().length() < 3) {
                etCvv.setError(getString(R.string.cvv_required));
                isValid = false;
            }
        } else if (selectedPaymentId == R.id.rb_net_banking) {
            if (spinnerBank.getSelectedItemPosition() == 0) {
                Toast.makeText(this, getString(R.string.select_bank_required), Toast.LENGTH_SHORT).show();
                isValid = false;
            }
            
            EditText etAccountNumber = findViewById(R.id.et_account_number);
            EditText etIfscCode = findViewById(R.id.et_ifsc_code);
            
            if (TextUtils.isEmpty(etAccountNumber.getText())) {
                etAccountNumber.setError(getString(R.string.account_number_required));
                isValid = false;
            }
            
            if (TextUtils.isEmpty(etIfscCode.getText())) {
                etIfscCode.setError(getString(R.string.ifsc_required));
                isValid = false;
            }
        }

        return isValid;
    }

    private void prefillUserInfo() {
        SessionManager sessionManager = SessionManager.getInstance(this);
        if (sessionManager.isLoggedIn()) {
            User user = sessionManager.getUser();
            if (user != null) {
                etFullName.setText(user.getUsername());
                etPhone.setText(user.getMobileNo());
                
                // Pre-fill address if available
                String userAddress = sessionManager.getUserAddress();
                if (!TextUtils.isEmpty(userAddress)) {
                    etAddress.setText(userAddress);
                }
            }
        }
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            // Use BottomNavManager to set up bottom navigation
            BottomNavManager.setupBottomNavigation(
                    this, bottomNavigationView, R.id.nav_cart);
        }
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        
        // Update badges
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (bottomNavigationView != null) {
            BottomNavManager.updateBadges(bottomNavigationView);
        }
    }

    private void setupPaymentButton() {
        btnProceedToPay.setOnClickListener(v -> {
            if (validateInputs()) {
                try {
                    // Calculate total amount (including delivery charge)
                    double totalWithDelivery = calculateTotalAmount();
                    Log.d("CheckoutActivity", "Proceeding to payment with total: " + totalWithDelivery);
                    
                    // Get selected payment method
                    String paymentMethod = getSelectedPaymentMethod();
                    
                    // Process payment
                    Intent intent = new Intent(CheckoutActivity.this, PaymentSuccessfulActivity.class);
                    
                    // Pass relevant information to PaymentSuccessfulActivity
                    intent.putExtra("total_amount", totalWithDelivery);
                    intent.putExtra("book_title", bookTitle);
                    intent.putExtra("payment_method", paymentMethod);
                    if (book != null) {
                        intent.putExtra("book_id", book.getId());
                    }
                    
                    startActivity(intent);
                    finish();
                } catch (Exception e) {
                    Log.e("CheckoutActivity", "Error processing payment: " + e.getMessage(), e);
                    Toast.makeText(CheckoutActivity.this, 
                        "Error processing payment. Please try again.", 
                        Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.d("CheckoutActivity", "Input validation failed");
            }
        });
    }
    
    private String getSelectedPaymentMethod() {
        int selectedId = rgPaymentMethod.getCheckedRadioButtonId();
        if (selectedId == R.id.rb_cash_on_delivery) {
            return "cod";
        } else if (selectedId == R.id.rb_upi) {
            return "upi";
        } else if (selectedId == R.id.rb_net_banking) {
            return "netbanking";
        } else if (selectedId == R.id.rb_credit_card) {
            return "card";
        }
        return "cod"; // Default to COD
    }
} 