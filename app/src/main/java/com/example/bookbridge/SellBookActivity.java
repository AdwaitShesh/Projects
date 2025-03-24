package com.example.bookbridge;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.bookbridge.models.Book;
import com.example.bookbridge.utils.BookManager;
import com.example.bookbridge.utils.SessionManager;
import com.example.bookbridge.data.User;
import com.example.bookbridge.utils.BookUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class SellBookActivity extends AppCompatActivity {

    // Request code for image picking
    private static final int PICK_IMAGE_REQUEST = 1;

    // UI components
    private LinearLayout layoutUploadImage;
    private ImageView ivBookPhoto;
    private EditText etBookTitle, etAuthor, etPrice, etLocation, etSellerName;
    private RadioGroup rgCondition, rgCategory;
    private RadioButton rbLikeNew, rbGood, rbFair;
    private Button btnListBook;

    // Selected image
    private Uri imageUri;
    private boolean hasUploadedImage = false;
    private String imageUrl; // Store the URL of the uploaded image

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
        
        setContentView(R.layout.activity_sell_book);

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize UI components
        initViews();

        // Set up click listeners
        setupListeners();
    }

    private void initViews() {
        layoutUploadImage = findViewById(R.id.layout_upload_image);
        ivBookPhoto = findViewById(R.id.iv_book_photo);
        etBookTitle = findViewById(R.id.et_book_title);
        etAuthor = findViewById(R.id.et_author);
        etPrice = findViewById(R.id.et_price);
        etLocation = findViewById(R.id.et_location);
        etSellerName = findViewById(R.id.et_seller_name);
        rgCondition = findViewById(R.id.rg_condition);
        rgCategory = findViewById(R.id.rg_category);
        
        // Update radio buttons to match our category constants
        rbLikeNew = findViewById(R.id.rb_like_new);
        rbGood = findViewById(R.id.rb_good);
        rbFair = findViewById(R.id.rb_fair);
        
        // Set up category radio buttons dynamically using BookManager categories
        // (Skip the "All" category which is only for filtering)
        rgCategory.removeAllViews();
        List<String> categories = BookManager.getAllCategories();
        
        Log.d("SellBookActivity", "Setting up category radio buttons: " + categories.size() + " categories");
        
        for (int i = 1; i < categories.size(); i++) { // Start from 1 to skip "All"
            String category = categories.get(i);
            RadioButton rb = new RadioButton(this);
            rb.setId(View.generateViewId());
            rb.setText(category);
            rb.setTag(category); // Store category name as tag for retrieval
            
            Log.d("SellBookActivity", "Created radio button for category: " + category + " with tag: " + rb.getTag());
            
            // Apply the same style as other radio buttons
            rb.setBackground(getResources().getDrawable(R.drawable.radio_selector));
            rb.setTextColor(getResources().getColorStateList(R.drawable.radio_text_selector));
            rb.setButtonDrawable(null);
            rb.setGravity(Gravity.CENTER);
            rb.setPadding(36, 36, 36, 36);
            
            // Set the first category as checked by default
            if (i == 1) {
                rb.setChecked(true);
                Log.d("SellBookActivity", "Setting default selected category: " + category);
            }
            
            // Set layout parameters
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(
                    0, RadioGroup.LayoutParams.WRAP_CONTENT, 1.0f);
            params.setMargins(i > 1 ? 16 : 0, 0, 0, 0);
            rb.setLayoutParams(params);
            
            rgCategory.addView(rb);
        }
        
        btnListBook = findViewById(R.id.btn_list_book);

        // Pre-fill seller name with logged-in user's name
        SessionManager sessionManager = SessionManager.getInstance(this);
        if (sessionManager.isLoggedIn()) {
            User user = sessionManager.getUser();
            if (user != null) {
                etSellerName.setText(user.getUsername());
            }
        }
    }

    private void setupListeners() {
        // Set up image upload section click listener
        layoutUploadImage.setOnClickListener(v -> openImagePicker());

        // Set up list book button click listener
        btnListBook.setOnClickListener(v -> validateAndListBook());
    }

    private void openImagePicker() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Book Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                // Display the selected image
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                
                // Save the image to local app storage to create a URL
                imageUrl = saveImageToStorage(bitmap);
                
                // Display the image using Glide with improved configuration
                RequestOptions requestOptions = new RequestOptions()
                    .placeholder(R.drawable.book_placeholder)
                    .error(R.drawable.book_placeholder)
                    .centerCrop();
                    
                try {
                    Glide.with(this)
                        .load(bitmap)
                        .apply(requestOptions)
                        .transition(DrawableTransitionOptions.withCrossFade())
                        .into(ivBookPhoto);
                        
                    ivBookPhoto.setVisibility(View.VISIBLE);
                    layoutUploadImage.setVisibility(View.GONE);
                    hasUploadedImage = true;
                } catch (Exception e) {
                    e.printStackTrace();
                    ivBookPhoto.setImageResource(R.drawable.book_placeholder);
                    Toast.makeText(this, "Failed to display image, but it was saved", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    // Save the bitmap image to local storage and return its path as a URL
    private String saveImageToStorage(Bitmap bitmap) throws IOException {
        // Create a unique filename using timestamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "BOOK_" + timeStamp + ".jpg";
        
        // Get the app's private directory
        File storageDir = new File(getFilesDir(), "book_images");
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }
        
        File imageFile = new File(storageDir, fileName);
        
        // Save the bitmap to the file
        FileOutputStream fos = new FileOutputStream(imageFile);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
        fos.close();
        
        // Return the path as a file:// URL
        return "file://" + imageFile.getAbsolutePath();
    }

    private void validateAndListBook() {
        // Get input values
        String title = etBookTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        
        // Validate input
        if (title.isEmpty() || author.isEmpty() || priceStr.isEmpty() || location.isEmpty()) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }
        
        double price = Double.parseDouble(priceStr);
        
        // Get selected category
        int selectedId = rgCategory.getCheckedRadioButtonId();
        String category = "Fiction"; // Default category
        
        if (selectedId != -1) {
            RadioButton radioButton = findViewById(selectedId);
            if (radioButton != null && radioButton.getTag() != null) {
                category = radioButton.getTag().toString();
                Log.d("SellBookActivity", "Selected category from radio button: " + category);
            } else {
                Log.w("SellBookActivity", "Radio button tag is null, using default category: " + category);
            }
        } else {
            Log.w("SellBookActivity", "No category radio button selected, using default: " + category);
        }
        
        // Create new book
        Book book;
        
        if (imageUri != null) {
            // Create book with selected image URI
            String imageUrl = imageUri.toString();
            String description = "Location: " + location +
                    ", Seller: " + etSellerName.getText().toString().trim();
            book = new Book(0, title, author, price, description, imageUrl, false, category);
            Log.d("SellBookActivity", "Creating book with image URL: " + imageUrl);
        } else {
            // Create book with random book cover
            int randomDrawableResId = BookUtils.getRandomBookCoverResourceId(this);
            String description = "Location: " + location +
                    ", Seller: " + etSellerName.getText().toString().trim();
            book = new Book(0, title, author, price, description, randomDrawableResId, false, category);
            Log.d("SellBookActivity", "Creating book with random resource ID: " + randomDrawableResId);
        }
        
        // Add book to BookManager
        BookManager.addBook(book);
        Log.d("SellBookActivity", "Book added successfully: " + title + " by " + author + " in category " + category);
        
        // Show success message and finish activity
        Toast.makeText(this, "Book listed successfully!", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean validateInputs() {
        boolean isValid = true;

        // Validate Book Title
        if (TextUtils.isEmpty(etBookTitle.getText())) {
            etBookTitle.setError("Book title is required");
            isValid = false;
        }

        // Validate Author
        if (TextUtils.isEmpty(etAuthor.getText())) {
            etAuthor.setError("Author name is required");
            isValid = false;
        }

        // Validate Price
        if (TextUtils.isEmpty(etPrice.getText())) {
            etPrice.setError("Price is required");
            isValid = false;
        } else {
            try {
                double price = Double.parseDouble(etPrice.getText().toString().trim());
                if (price <= 0) {
                    etPrice.setError("Price must be greater than 0");
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                etPrice.setError("Invalid price format");
                isValid = false;
            }
        }

        // Validate Location
        if (TextUtils.isEmpty(etLocation.getText())) {
            etLocation.setError("Location is required");
            isValid = false;
        }

        // Validate Seller Name
        if (TextUtils.isEmpty(etSellerName.getText())) {
            etSellerName.setError("Seller name is required");
            isValid = false;
        }

        // Validate Image (optional, as we can use a default image)
        if (!hasUploadedImage) {
            Toast.makeText(this, "No image selected, a default image will be used", Toast.LENGTH_SHORT).show();
        }

        return isValid;
    }
} 