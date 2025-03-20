package com.example.bookbridge;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.bookbridge.models.Book;

public class BookDetailsActivity extends AppCompatActivity {

    public static final String EXTRA_BOOK = "extra_book";
    
    private ImageView ivBookCover;
    private TextView tvTitle;
    private TextView tvAuthor;
    private TextView tvPrice;
    private TextView tvCategory;
    private TextView tvCondition;
    private TextView tvDescription;
    private Button btnAddToCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_details);
        
        // Setup toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Book Details");
        }
        
        // Initialize views
        ivBookCover = findViewById(R.id.ivBookCover);
        tvTitle = findViewById(R.id.tvTitle);
        tvAuthor = findViewById(R.id.tvAuthor);
        tvPrice = findViewById(R.id.tvPrice);
        tvCategory = findViewById(R.id.tvCategory);
        tvCondition = findViewById(R.id.tvCondition);
        tvDescription = findViewById(R.id.tvDescription);
        btnAddToCart = findViewById(R.id.btnAddToCart);
        
        // Get book data from intent using Serializable
        if (getIntent() != null && getIntent().hasExtra(EXTRA_BOOK)) {
            Book book = (Book) getIntent().getSerializableExtra(EXTRA_BOOK);
            
            if (book != null) {
                displayBookDetails(book);
                
                btnAddToCart.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(BookDetailsActivity.this, 
                                book.getTitle() + " added to cart!", 
                                Toast.LENGTH_SHORT).show();
                        // TODO: Implement cart functionality
                    }
                });
            }
        }
    }
    
    private void displayBookDetails(Book book) {
        if (book == null) return;
        
        // Set book details to views
        ivBookCover.setImageResource(book.getCoverResourceId());
        tvTitle.setText(book.getTitle());
        tvAuthor.setText("By " + book.getAuthor());
        tvPrice.setText(String.format("₹%.2f", book.getPrice()));
        tvCategory.setText(book.getCategory());
        tvCondition.setText(book.getCondition());
        tvDescription.setText(book.getDescription());
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
} 