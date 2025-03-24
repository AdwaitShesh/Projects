package com.example.bookbridge.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.bookbridge.BookDetailsActivity;
import com.example.bookbridge.MainActivity;
import com.example.bookbridge.R;
import com.example.bookbridge.models.Book;
import com.example.bookbridge.utils.BookManager;
import com.example.bookbridge.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecentBooksAdapter extends RecyclerView.Adapter<RecentBooksAdapter.RecentBookViewHolder> {

    private Context context;
    private List<Book> books;

    public RecentBooksAdapter(Context context, List<Book> books) {
        this.context = context;
        this.books = new ArrayList<>(books != null ? books : new ArrayList<>());
    }

    @NonNull
    @Override
    public RecentBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recent_book, parent, false);
        return new RecentBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentBookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.tvBookTitle.setText(book.getTitle());
        holder.tvBookAuthor.setText(book.getAuthor());
        holder.tvBookPrice.setText(String.format(Locale.getDefault(), "₹%.2f", book.getPrice()));
        
        // Load image using our utility class
        ImageUtils.loadBookCover(context, holder.ivBookCover, book);
        
        // Get fresh state from BookManager
        Book bookInManager = BookManager.getBookById(book.getId());
        if (bookInManager != null) {
            book.setWishlisted(bookInManager.isWishlisted());
        }
        
        // Update wishlist icon state
        updateWishlistIcon(holder.ivWishlist, book.isWishlisted());

        holder.cardBook.setOnClickListener(v -> {
            if (context instanceof AppCompatActivity) {
                BookDetailsActivity.start((AppCompatActivity) context, book.getId());
            } else {
                Toast.makeText(context, "Book details coming soon!", Toast.LENGTH_SHORT).show();
            }
        });

        holder.ivWishlist.setOnClickListener(v -> {
            // Toggle wishlist state
            book.setWishlisted(!book.isWishlisted());
            
            // Update icon
            updateWishlistIcon(holder.ivWishlist, book.isWishlisted());
            
            // Update the book in the manager too
            Book managerBook = BookManager.getBookById(book.getId());
            if (managerBook != null) {
                managerBook.setWishlisted(book.isWishlisted());
            }
            
            // Show toast
            Toast.makeText(context, book.isWishlisted() ? 
                    "Added to wishlist" : "Removed from wishlist", Toast.LENGTH_SHORT).show();
        });
    }

    private void updateWishlistIcon(ImageView ivWishlist, boolean isWishlisted) {
        ivWishlist.setImageResource(isWishlisted ? 
                R.drawable.ic_favorite : R.drawable.ic_favorite_border);
    }

    @Override
    public int getItemCount() {
        return books.size();
    }
    
    public void updateBooks(List<Book> newBooks) {
        this.books = new ArrayList<>(newBooks != null ? newBooks : new ArrayList<>());
        notifyDataSetChanged();
    }

    static class RecentBookViewHolder extends RecyclerView.ViewHolder {
        CardView cardBook;
        ImageView ivBookCover;
        ImageView ivWishlist;
        TextView tvBookTitle;
        TextView tvBookAuthor;
        TextView tvBookPrice;

        public RecentBookViewHolder(@NonNull View itemView) {
            super(itemView);
            cardBook = itemView.findViewById(R.id.card_book);
            ivBookCover = itemView.findViewById(R.id.iv_book_cover);
            ivWishlist = itemView.findViewById(R.id.iv_wishlist);
            tvBookTitle = itemView.findViewById(R.id.tv_book_title);
            tvBookAuthor = itemView.findViewById(R.id.tv_book_author);
            tvBookPrice = itemView.findViewById(R.id.tv_book_price);
        }
    }
} 