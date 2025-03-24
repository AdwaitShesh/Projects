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
import com.example.bookbridge.R;
import com.example.bookbridge.models.Book;

import java.util.List;
import java.util.Locale;

public class FeaturedBooksAdapter extends RecyclerView.Adapter<FeaturedBooksAdapter.FeaturedBookViewHolder> {

    private Context context;
    private List<Book> books;

    public FeaturedBooksAdapter(Context context, List<Book> books) {
        this.context = context;
        this.books = books;
    }

    @NonNull
    @Override
    public FeaturedBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_featured_book, parent, false);
        return new FeaturedBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeaturedBookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.tvBookTitle.setText(book.getTitle());
        holder.tvBookAuthor.setText(book.getAuthor());
        holder.tvBookPrice.setText(String.format(Locale.getDefault(), "₹%.2f", book.getPrice()));
        holder.ivBookCover.setImageResource(book.getImageResource());
        
        if (book.isWishlisted()) {
            holder.ivWishlist.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.ivWishlist.setImageResource(R.drawable.ic_favorite_border);
        }

        holder.cardBook.setOnClickListener(v -> {
            // Navigate to book details
            BookDetailsActivity.start((AppCompatActivity) context, book.getId());
        });

        holder.ivWishlist.setOnClickListener(v -> {
            book.setWishlisted(!book.isWishlisted());
            notifyItemChanged(position);
            if (book.isWishlisted()) {
                Toast.makeText(context, "Added to wishlist", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Removed from wishlist", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class FeaturedBookViewHolder extends RecyclerView.ViewHolder {
        CardView cardBook;
        ImageView ivBookCover;
        ImageView ivWishlist;
        TextView tvBookTitle;
        TextView tvBookAuthor;
        TextView tvBookPrice;

        public FeaturedBookViewHolder(@NonNull View itemView) {
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