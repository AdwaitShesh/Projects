package com.example.bookbridge.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.util.Log;
import android.widget.Toast;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.example.bookbridge.BookDetailsActivity;
import com.example.bookbridge.MainActivity;
import com.example.bookbridge.R;
import com.example.bookbridge.models.Book;
import com.example.bookbridge.utils.BookManager;
import com.example.bookbridge.utils.CartManager;
import com.example.bookbridge.WishlistActivity;
import com.example.bookbridge.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class WishlistAdapter extends RecyclerView.Adapter<WishlistAdapter.WishlistViewHolder> {

    private final Context context;
    private List<Book> books;

    public WishlistAdapter(Context context) {
        this.context = context;
        this.books = new ArrayList<>();
    }

    @NonNull
    @Override
    public WishlistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_wishlist_book, parent, false);
        return new WishlistViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WishlistViewHolder holder, int position) {
        Book book = books.get(position);

        // Set book details
        holder.tvBookTitle.setText(book.getTitle());
        holder.tvBookAuthor.setText(book.getAuthor());
        
        // Load image using our utility class
        ImageUtils.loadBookCover(context, holder.ivBookCover, book);

        // Set prices
        double originalPrice = book.getPrice();
        double discountedPrice = originalPrice * 0.8; // 20% discount
        holder.tvOriginalPrice.setText(String.format(Locale.getDefault(), "₹%.2f", originalPrice));
        holder.tvOriginalPrice.setPaintFlags(holder.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tvDiscountedPrice.setText(String.format(Locale.getDefault(), "₹%.2f", discountedPrice));

        // Handle click on the whole item
        holder.itemView.setOnClickListener(v -> {
            BookDetailsActivity.start((AppCompatActivity) context, book.getId());
        });

        // Handle add to cart button click
        holder.btnAddToCart.setOnClickListener(v -> {
            CartManager.addToCart(book.getId());
            notifyItemChanged(position);
        });

        // Handle remove from wishlist button click
        holder.btnRemoveWishlist.setOnClickListener(v -> {
            book.setWishlisted(false);
            // Update the same book in BookManager
            Book bookInManager = BookManager.getBookById(book.getId());
            if (bookInManager != null) {
                bookInManager.setWishlisted(false);
            }
            
            // Show toast message when removed from wishlist
            Toast.makeText(context, "Removed from wishlist", Toast.LENGTH_SHORT).show();
            
            books.remove(position);
            notifyItemRemoved(position);
            if (books.isEmpty()) {
                // Show empty state if no books left
                ((AppCompatActivity) context).findViewById(R.id.tv_empty_wishlist).setVisibility(View.VISIBLE);
                ((AppCompatActivity) context).findViewById(R.id.rv_wishlist).setVisibility(View.GONE);
            }
            
            // Notify WishlistActivity about the removal
            if (context instanceof WishlistActivity) {
                ((WishlistActivity) context).onBookRemovedFromWishlist();
            }
            
            // Update wishlist badge in MainActivity
            if (context instanceof MainActivity) {
                ((MainActivity) context).onWishlistUpdated();
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public void setBooks(List<Book> books) {
        this.books = books;
        notifyDataSetChanged();
    }

    static class WishlistViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBookCover;
        TextView tvBookTitle;
        TextView tvBookAuthor;
        TextView tvOriginalPrice;
        TextView tvDiscountedPrice;
        Button btnAddToCart;
        ImageView btnRemoveWishlist;

        public WishlistViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookCover = itemView.findViewById(R.id.iv_book_cover);
            tvBookTitle = itemView.findViewById(R.id.tv_book_title);
            tvBookAuthor = itemView.findViewById(R.id.tv_book_author);
            tvOriginalPrice = itemView.findViewById(R.id.tv_original_price);
            tvDiscountedPrice = itemView.findViewById(R.id.tv_discounted_price);
            btnAddToCart = itemView.findViewById(R.id.btn_add_to_cart);
            btnRemoveWishlist = itemView.findViewById(R.id.btn_remove_wishlist);
        }
    }
} 