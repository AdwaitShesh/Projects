package com.example.bookbridge.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookbridge.BookDetailsActivity;
import com.example.bookbridge.R;
import com.example.bookbridge.adapters.BookAdapter;
import com.example.bookbridge.adapters.CategoryAdapter;
import com.example.bookbridge.models.Book;
import com.example.bookbridge.utils.DataProvider;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment implements CategoryAdapter.OnCategoryClickListener, BookAdapter.OnBookClickListener {

    private RecyclerView rvCategories;
    private RecyclerView rvNewArrivals;
    private RecyclerView rvPopularBooks;
    private ImageView ivSearch;
    private TextView tvNewArrivalsViewAll;
    private TextView tvPopularViewAll;

    private CategoryAdapter categoryAdapter;
    private BookAdapter newArrivalsAdapter;
    private BookAdapter popularBooksAdapter;

    private List<String> categories;
    private List<Book> allBooks;
    private List<Book> filteredBooks;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // Initialize views
        rvCategories = view.findViewById(R.id.rvCategories);
        rvNewArrivals = view.findViewById(R.id.rvNewArrivals);
        rvPopularBooks = view.findViewById(R.id.rvPopularBooks);
        ivSearch = view.findViewById(R.id.ivSearch);
        tvNewArrivalsViewAll = view.findViewById(R.id.tvNewArrivalsViewAll);
        tvPopularViewAll = view.findViewById(R.id.tvPopularViewAll);

        // Set up RecyclerViews
        setupCategoryRecyclerView();
        setupBookRecyclerViews();
        setupClickListeners();

        return view;
    }

    private void setupClickListeners() {
        ivSearch.setOnClickListener(v -> {
            Toast.makeText(getContext(), "Search functionality coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        tvNewArrivalsViewAll.setOnClickListener(v -> {
            Toast.makeText(getContext(), "View all new arrivals coming soon!", Toast.LENGTH_SHORT).show();
        });
        
        tvPopularViewAll.setOnClickListener(v -> {
            Toast.makeText(getContext(), "View all popular books coming soon!", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupCategoryRecyclerView() {
        categories = DataProvider.getBookCategories();
        
        // Configure RecyclerView
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        
        // Create and set adapter
        categoryAdapter = new CategoryAdapter(getContext(), categories, this);
        rvCategories.setAdapter(categoryAdapter);
    }

    private void setupBookRecyclerViews() {
        allBooks = DataProvider.getSampleBooks();
        filteredBooks = new ArrayList<>(allBooks);
        
        // Configure New Arrivals RecyclerView
        rvNewArrivals.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        newArrivalsAdapter = new BookAdapter(getContext(), filteredBooks, this);
        rvNewArrivals.setAdapter(newArrivalsAdapter);
        
        // Configure Popular Books RecyclerView
        rvPopularBooks.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        popularBooksAdapter = new BookAdapter(getContext(), filteredBooks, this);
        rvPopularBooks.setAdapter(popularBooksAdapter);
    }

    @Override
    public void onCategoryClick(String category, int position) {
        if (category.equals("All")) {
            filteredBooks = new ArrayList<>(allBooks);
        } else {
            filteredBooks = new ArrayList<>();
            for (Book book : allBooks) {
                if (book.getCategory().equals(category)) {
                    filteredBooks.add(book);
                }
            }
        }
        
        // Update adapters with filtered books
        newArrivalsAdapter.updateData(filteredBooks);
        popularBooksAdapter.updateData(filteredBooks);
    }

    @Override
    public void onBookClick(Book book, int position) {
        if (book != null && getActivity() != null) {
            // Navigate to book details screen
            Intent intent = new Intent(getActivity(), BookDetailsActivity.class);
            intent.putExtra(BookDetailsActivity.EXTRA_BOOK, book);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Unable to open book details", Toast.LENGTH_SHORT).show();
        }
    }
} 