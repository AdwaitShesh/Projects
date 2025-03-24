package com.example.bookbridge;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
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

import com.example.bookbridge.models.Book;
import com.example.bookbridge.utils.BookManager;

import java.io.IOException;
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
    private RadioButton rbEngineering, rbComputerScience, rbElectronics;
    private Button btnListBook;

    // Selected image
    private Uri imageUri;
    private boolean hasUploadedImage = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        rbLikeNew = findViewById(R.id.rb_like_new);
        rbGood = findViewById(R.id.rb_good);
        rbFair = findViewById(R.id.rb_fair);
        rbEngineering = findViewById(R.id.rb_engineering);
        rbComputerScience = findViewById(R.id.rb_computer_science);
        rbElectronics = findViewById(R.id.rb_electronics);
        btnListBook = findViewById(R.id.btn_list_book);
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
                ivBookPhoto.setImageBitmap(bitmap);
                ivBookPhoto.setVisibility(View.VISIBLE);
                layoutUploadImage.setVisibility(View.GONE);
                hasUploadedImage = true;
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void validateAndListBook() {
        // Validate all fields
        if (!validateInputs()) {
            return;
        }

        // Get condition
        String condition;
        int checkedConditionId = rgCondition.getCheckedRadioButtonId();
        if (checkedConditionId == R.id.rb_like_new) {
            condition = "Like New";
        } else if (checkedConditionId == R.id.rb_good) {
            condition = "Good";
        } else {
            condition = "Fair";
        }

        // Get category
        String category;
        int checkedCategoryId = rgCategory.getCheckedRadioButtonId();
        if (checkedCategoryId == R.id.rb_engineering) {
            category = "Engineering";
        } else if (checkedCategoryId == R.id.rb_computer_science) {
            category = "Computer Science";
        } else {
            category = "Electronics";
        }

        // Get resource ID for a random book image if no image was uploaded
        int resourceId;
        if (!hasUploadedImage) {
            int[] bookImages = {
                R.drawable.book_dsa,
                R.drawable.book_networks,
                R.drawable.book_digital_logic,
                R.drawable.book_os,
                R.drawable.book_microprocessor,
                R.drawable.book_toc
            };
            resourceId = bookImages[new Random().nextInt(bookImages.length)];
        } else {
            // In a real app, you would save the image to storage and get its path
            // For this example, we'll just use a predefined resource
            resourceId = R.drawable.book_toc;
        }

        // Create a new book
        String title = etBookTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        double price = Double.parseDouble(etPrice.getText().toString().trim());
        String description = "Location: " + etLocation.getText().toString().trim() +
                ", Seller: " + etSellerName.getText().toString().trim() +
                ", Condition: " + condition +
                ", Category: " + category;

        // Create a new Book object with a temporary ID (BookManager will assign the final ID)
        Book newBook = new Book(0, title, author, price, description, resourceId, false);
        
        // Add the book to the BookManager
        BookManager.addBook(newBook);

        // In a real app, this would be added to a database
        // For this demo, we'll just show a success message and go back
        Toast.makeText(this, "Book listed successfully!", Toast.LENGTH_SHORT).show();
        
        // Finish the activity and return to MainActivity
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