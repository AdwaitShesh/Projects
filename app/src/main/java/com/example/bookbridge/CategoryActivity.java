package com.example.bookbridge;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookbridge.adapters.BookAdapter;
import com.example.bookbridge.models.Book;
import com.example.bookbridge.utils.BookManager;

import java.util.List;

public class CategoryActivity extends AppCompatActivity {

    private static final String EXTRA_CATEGORY = "category";
    
    private RecyclerView rvBooks;
    private TextView tvEmptyCategory;
    private BookAdapter bookAdapter;
    private String category;

    public static void start(Context context, String category) {
        Intent intent = new Intent(context, CategoryActivity.class);
        intent.putExtra(EXTRA_CATEGORY, category);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        // Get category from intent
        if (getIntent().hasExtra(EXTRA_CATEGORY)) {
            category = getIntent().getStringExtra(EXTRA_CATEGORY);
        } else {
            category = BookManager.CATEGORY_ALL;
        }

        // Initialize views
        initViews();

        // Set up toolbar with category name
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(category + " Books");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Load books by category
        loadBooks();
    }

    private void initViews() {
        rvBooks = findViewById(R.id.rv_category_books);
        tvEmptyCategory = findViewById(R.id.tv_empty_category);
        
        // Set up RecyclerView with grid layout (2 columns)
        rvBooks.setLayoutManager(new GridLayoutManager(this, 2));
        bookAdapter = new BookAdapter(this);
        rvBooks.setAdapter(bookAdapter);
    }

    private void loadBooks() {
        // Get books by category
        List<Book> books = BookManager.getBooksByCategory(category);
        
        // Update UI
        if (books.isEmpty()) {
            tvEmptyCategory.setVisibility(View.VISIBLE);
            rvBooks.setVisibility(View.GONE);
            tvEmptyCategory.setText("No books found in " + category + " category");
        } else {
            tvEmptyCategory.setVisibility(View.GONE);
            rvBooks.setVisibility(View.VISIBLE);
            bookAdapter.setBooks(books);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Reload books when returning to activity to reflect any changes
        loadBooks();
        
        // Sync wishlist state
        if (bookAdapter != null) {
            bookAdapter.syncWishlistState();
        }
    }
} 