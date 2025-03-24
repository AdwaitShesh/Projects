package com.example.bookbridge;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.example.bookbridge.models.Book;
import com.example.bookbridge.utils.BookManager;
import com.example.bookbridge.utils.SessionManager;
import com.example.bookbridge.data.User;

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

        // Get data from intent
        int bookId = getIntent().getIntExtra("book_id", -1);
        bookPrice = getIntent().getDoubleExtra("book_price", 0.0);
        bookTitle = getIntent().getStringExtra("book_title");

        // Get book from BookManager if available
        if (bookId != -1) {
            book = BookManager.getBookById(bookId);
        }

        // Set up toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.checkout));
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize views
        initViews();
        
        // Pre-fill user information from SessionManager
        prefillUserInfo();
        
        // Setup bank spinner
        setupSpinner();
        
        // Setup radio button listeners
        setupPaymentMethodListeners();
        
        // Cash on Delivery is checked by default in XML
        
        // Set order summary
        displayOrderSummary();

        // Set click listener for Proceed to Pay button
        btnProceedToPay.setOnClickListener(v -> {
            if (validateInputs()) {
                // Get selected payment method
                int selectedPaymentId = rgPaymentMethod.getCheckedRadioButtonId();
                RadioButton selectedPayment = findViewById(selectedPaymentId);
                String paymentMethod = selectedPayment.getText().toString();

                // Navigate to payment successful screen
                Intent intent = new Intent(CheckoutActivity.this, PaymentSuccessfulActivity.class);
                intent.putExtra("book_id", bookId);
                intent.putExtra("book_title", bookTitle);
                intent.putExtra("total_amount", calculateTotalAmount());
                intent.putExtra("payment_method", paymentMethod);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, getString(R.string.enter_valid_details), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void initViews() {
        etFullName = findViewById(R.id.et_full_name);
        etAddress = findViewById(R.id.et_address);
        etCity = findViewById(R.id.et_city);
        etState = findViewById(R.id.et_state);
        etPincode = findViewById(R.id.et_pincode);
        etPhone = findViewById(R.id.et_phone);
        
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
            
            if (TextUtils.isEmpty(etCardNumber.getText()) || etCardNumber.getText().length() < 16) {
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
} 