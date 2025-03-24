package com.example.bookbridge;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookbridge.adapters.RecentBooksAdapter;
import com.example.bookbridge.models.Book;
import com.example.bookbridge.utils.BookManager;
import com.example.bookbridge.utils.SearchUtils;

import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageButton btnClear;
    private RecyclerView rvSearchResults;
    private TextView tvNoResults;
    private RecentBooksAdapter searchResultsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Initialize views
        initViews();

        // Set up search functionality
        setupSearch();
    }

    private void initViews() {
        // Set up toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");

        etSearch = findViewById(R.id.et_search);
        btnClear = findViewById(R.id.btn_clear);
        rvSearchResults = findViewById(R.id.rv_search_results);
        tvNoResults = findViewById(R.id.tv_no_results);

        // Set up RecyclerView
        rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        searchResultsAdapter = new RecentBooksAdapter(this, null);
        rvSearchResults.setAdapter(searchResultsAdapter);
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString();
                performSearch(query);
                btnClear.setVisibility(query.isEmpty() ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnClear.setOnClickListener(v -> {
            etSearch.setText("");
            btnClear.setVisibility(View.GONE);
        });
    }

    private void performSearch(String query) {
        // Get all books from BookManager
        List<Book> allBooks = BookManager.getAllBooks();
        
        // Perform search using SearchUtils
        List<Book> searchResults = SearchUtils.searchBooks(query, allBooks);
        
        // Update UI
        if (searchResults.isEmpty()) {
            tvNoResults.setVisibility(View.VISIBLE);
            rvSearchResults.setVisibility(View.GONE);
        } else {
            tvNoResults.setVisibility(View.GONE);
            rvSearchResults.setVisibility(View.VISIBLE);
            searchResultsAdapter.updateBooks(searchResults);
        }
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