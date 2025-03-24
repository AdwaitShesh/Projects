package com.example.bookbridge.adapters;

import android.content.Context;
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

import com.example.bookbridge.BookDetailsActivity;
import com.example.bookbridge.MainActivity;
import com.example.bookbridge.R;
import com.example.bookbridge.models.Book;
import com.example.bookbridge.utils.BookManager;

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
        holder.ivBookCover.setImageResource(book.getImageResource());
        
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
            book.setWishlisted(!book.isWishlisted());
            
            // Update the same book in BookManager
            Book updatedBook = BookManager.getBookById(book.getId());
            if (updatedBook != null) {
                updatedBook.setWishlisted(book.isWishlisted());
            }
            
            // Update wishlist icon immediately
            updateWishlistIcon(holder.ivWishlist, book.isWishlisted());
            
            // Show appropriate toast message
            if (book.isWishlisted()) {
                Toast.makeText(context, "Added to wishlist", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Removed from wishlist", Toast.LENGTH_SHORT).show();
            }
            
            // Update wishlist badge in MainActivity
            if (context instanceof MainActivity) {
                ((MainActivity) context).onWishlistUpdated();
            }
        });
    }
    
    private void updateWishlistIcon(ImageView imageView, boolean isWishlisted) {
        imageView.setImageResource(isWishlisted ? R.drawable.ic_favorite : R.drawable.ic_favorite_border);
    }

    @Override
    public int getItemCount() {
        return books != null ? books.size() : 0;
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