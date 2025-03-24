package com.example.bookbridge;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookbridge.adapters.ReviewAdapter;
import com.example.bookbridge.adapters.SuggestedBooksAdapter;
import com.example.bookbridge.models.Book;
import com.example.bookbridge.models.Review;
import com.example.bookbridge.utils.BookManager;
import com.example.bookbridge.utils.CartManager;
import com.example.bookbridge.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BookDetailsActivity extends AppCompatActivity {

    private static final String BOOK_ID = "book_id";

    private ImageView ivBookCover;
    private TextView tvBookTitle, tvBookAuthor, tvBookPrice, tvBookDescription, tvDiscountedPrice, tvDiscount;
    private TextView tvSellerName, tvSellerLocation, tvCondition, tvCategory;
    private Button btnAddToCart, btnBuyNow, btnContactSeller, btnSubmitReview;
    private ImageView ivWishlist, ivShare;
    private RecyclerView rvSuggestedBooks, rvReviews;
    private RatingBar ratingBar;
    private EditText etReviewText;

    private Book book;
    private SuggestedBooksAdapter suggestedBooksAdapter;
    private ReviewAdapter reviewAdapter;
    private List<Review> reviews = new ArrayList<>();

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
        
        setContentView(R.layout.activity_book_details);

        // Get the book ID from intent
        int bookId = getIntent().getIntExtra(BOOK_ID, -1);
        if (bookId == -1) {
            Toast.makeText(this, "Error: Book not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Find the book
        book = BookManager.getBookById(bookId);
        if (book == null) {
            Toast.makeText(this, "Error: Book not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Set up Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // Initialize views
        initViews();

        // Load data
        displayBookDetails();
        loadSuggestedBooks();
        loadReviews();

        // Set up listeners
        setupListeners();
    }

    private void initViews() {
        ivBookCover = findViewById(R.id.iv_book_cover);
        tvBookTitle = findViewById(R.id.tv_book_title);
        tvBookAuthor = findViewById(R.id.tv_book_author);
        tvBookPrice = findViewById(R.id.tv_book_price);
        tvDiscountedPrice = findViewById(R.id.tv_discounted_price);
        tvDiscount = findViewById(R.id.tv_discount);
        tvBookDescription = findViewById(R.id.tv_book_description);
        tvSellerName = findViewById(R.id.tv_seller_name);
        tvSellerLocation = findViewById(R.id.tv_seller_location);
        tvCondition = findViewById(R.id.tv_condition);
        tvCategory = findViewById(R.id.tv_category);
        btnAddToCart = findViewById(R.id.btn_add_to_cart);
        btnBuyNow = findViewById(R.id.btn_buy_now);
        btnContactSeller = findViewById(R.id.btn_contact_seller);
        ivWishlist = findViewById(R.id.iv_book_wishlist);
        ivShare = findViewById(R.id.iv_book_share);
        rvSuggestedBooks = findViewById(R.id.rv_suggested_books);
        rvReviews = findViewById(R.id.rv_reviews);
        ratingBar = findViewById(R.id.rating_bar);
        etReviewText = findViewById(R.id.et_review_text);
        btnSubmitReview = findViewById(R.id.btn_submit_review);

        // Set up RecyclerViews
        rvSuggestedBooks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvReviews.setLayoutManager(new LinearLayoutManager(this));
    }

    private void displayBookDetails() {
        // Set book details
        ivBookCover.setImageResource(book.getImageResource());
        tvBookTitle.setText(book.getTitle());
        tvBookAuthor.setText("by " + book.getAuthor());
        
        // Extract details from description
        String[] parts = book.getDescription().split(", ");
        String sellerName = "Seller";
        String location = "Location";
        String condition = "Condition";
        String category = "Category"; 
        StringBuilder description = new StringBuilder();
        
        for (String part : parts) {
            if (part.startsWith("Location: ")) {
                location = part.substring("Location: ".length());
                tvSellerLocation.setText(location);
            } else if (part.startsWith("Seller: ")) {
                sellerName = part.substring("Seller: ".length());
                tvSellerName.setText(sellerName);
            } else if (part.startsWith("Condition: ")) {
                condition = part.substring("Condition: ".length());
                tvCondition.setText(condition);
            } else if (part.startsWith("Category: ")) {
                category = part.substring("Category: ".length());
                tvCategory.setText(category);
            } else {
                description.append(part).append("\n");
            }
        }
        
        // Set description text
        if (description.length() > 0) {
            tvBookDescription.setText(description.toString().trim());
        } else {
            tvBookDescription.setText("No description available");
        }
        
        // Format price
        tvBookPrice.setText(String.format(Locale.getDefault(), "₹%.2f", book.getPrice()));
        // Apply strike-through to original price
        tvBookPrice.setPaintFlags(tvBookPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        
        // Set up discount (20% off) based on the screenshot
        double discountedPrice = book.getPrice() * 0.8; // 20% off
        tvDiscountedPrice.setText(String.format(Locale.getDefault(), "₹%.2f", discountedPrice));
        tvDiscount.setText("20% OFF");
        
        // Set wishlist icon
        if (book.isWishlisted()) {
            ivWishlist.setImageResource(R.drawable.ic_favorite);
        } else {
            ivWishlist.setImageResource(R.drawable.ic_favorite_border);
        }
    }

    private void loadSuggestedBooks() {
        // Get some books from the BookManager (excluding the current book)
        List<Book> allBooks = BookManager.getAllBooks();
        List<Book> suggestedBooks = new ArrayList<>();
        
        // Add up to 5 books that are not the current book
        for (Book b : allBooks) {
            if (b.getId() != book.getId()) {
                suggestedBooks.add(b);
                if (suggestedBooks.size() >= 5) {
                    break;
                }
            }
        }
        
        suggestedBooksAdapter = new SuggestedBooksAdapter(this, suggestedBooks);
        rvSuggestedBooks.setAdapter(suggestedBooksAdapter);
    }

    private void loadReviews() {
        // For demo, add some sample reviews
        if (reviews.isEmpty()) {
            reviews.add(new Review("John Doe", "Great book for beginners! Highly recommended.", 4.5f, "2023-03-15"));
            reviews.add(new Review("Jane Smith", "Content is good but book condition could be better.", 3.0f, "2023-03-10"));
            reviews.add(new Review("Mike Johnson", "Exactly what I needed for my course. Seller was prompt with delivery.", 5.0f, "2023-03-05"));
        }
        
        reviewAdapter = new ReviewAdapter(this, reviews);
        rvReviews.setAdapter(reviewAdapter);
    }

    private void setupListeners() {
        // Set up wishlist button
        ivWishlist.setOnClickListener(v -> {
            book.setWishlisted(!book.isWishlisted());
            // Update the same book in BookManager
            Book bookInManager = BookManager.getBookById(book.getId());
            if (bookInManager != null) {
                bookInManager.setWishlisted(book.isWishlisted());
            }
            // Update UI
            if (book.isWishlisted()) {
                ivWishlist.setImageResource(R.drawable.ic_favorite);
                Toast.makeText(this, "Added to wishlist", Toast.LENGTH_SHORT).show();
            } else {
                ivWishlist.setImageResource(R.drawable.ic_favorite_border);
                Toast.makeText(this, "Removed from wishlist", Toast.LENGTH_SHORT).show();
            }
            // Update wishlist badge in MainActivity
            if (getParent() instanceof MainActivity) {
                ((MainActivity) getParent()).onWishlistUpdated();
            }
        });

        // Set up share button
        ivShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this book on BookBridge!");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out " + book.getTitle() + " by " + book.getAuthor() + " on BookBridge!");
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        });

        // Add to Cart button
        btnAddToCart.setOnClickListener(v -> {
            com.example.bookbridge.utils.CartManager.addToCart(book.getId());
            Toast.makeText(this, "Added to cart", Toast.LENGTH_SHORT).show();
        });

        // Buy Now button
        btnBuyNow.setOnClickListener(v -> {
            Intent intent = new Intent(BookDetailsActivity.this, CheckoutActivity.class);
            intent.putExtra("book_id", book.getId());
            intent.putExtra("book_title", book.getTitle());
            // Calculate 20% discount
            double discountedPrice = book.getPrice() * 0.8; // 20% off
            intent.putExtra("book_price", discountedPrice);
            startActivity(intent);
        });

        // Contact Seller button
        btnContactSeller.setOnClickListener(v -> {
            Toast.makeText(this, "Contacting seller...", Toast.LENGTH_SHORT).show();
        });

        // Submit Review button
        btnSubmitReview.setOnClickListener(v -> {
            String reviewText = etReviewText.getText().toString().trim();
            if (reviewText.isEmpty()) {
                Toast.makeText(this, "Please enter a review", Toast.LENGTH_SHORT).show();
                return;
            }
            
            float rating = ratingBar.getRating();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            String date = sdf.format(new Date());
            
            // Add the review
            Review newReview = new Review("You", reviewText, rating, date);
            reviews.add(0, newReview);
            reviewAdapter.notifyItemInserted(0);
            rvReviews.scrollToPosition(0);
            
            // Clear the input
            etReviewText.setText("");
            ratingBar.setRating(0);
            
            Toast.makeText(this, "Review submitted!", Toast.LENGTH_SHORT).show();
        });
    }

    // Static method to start this activity
    public static void start(AppCompatActivity activity, int bookId) {
        Intent intent = new Intent(activity, BookDetailsActivity.class);
        intent.putExtra(BOOK_ID, bookId);
        activity.startActivity(intent);
    }
} 