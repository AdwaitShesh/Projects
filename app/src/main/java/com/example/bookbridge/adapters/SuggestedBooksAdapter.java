package com.example.bookbridge.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookbridge.BookDetailsActivity;
import com.example.bookbridge.R;
import com.example.bookbridge.models.Book;
import com.example.bookbridge.utils.ImageUtils;

import java.util.List;
import java.util.Locale;

public class SuggestedBooksAdapter extends RecyclerView.Adapter<SuggestedBooksAdapter.SuggestedBookViewHolder> {

    private Context context;
    private List<Book> books;

    public SuggestedBooksAdapter(Context context, List<Book> books) {
        this.context = context;
        this.books = books;
    }

    @NonNull
    @Override
    public SuggestedBookViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_suggested_book, parent, false);
        return new SuggestedBookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SuggestedBookViewHolder holder, int position) {
        Book book = books.get(position);
        holder.tvBookTitle.setText(book.getTitle());
        holder.tvBookAuthor.setText(book.getAuthor());
        holder.tvBookPrice.setText(String.format(Locale.getDefault(), "₹%.2f", book.getPrice()));
        
        // Load image using our utility class
        ImageUtils.loadBookCover(context, holder.ivBookCover, book);

        holder.cardBook.setOnClickListener(v -> {
            // Open book details
            BookDetailsActivity.start((AppCompatActivity) context, book.getId());
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    static class SuggestedBookViewHolder extends RecyclerView.ViewHolder {
        CardView cardBook;
        ImageView ivBookCover;
        TextView tvBookTitle;
        TextView tvBookAuthor;
        TextView tvBookPrice;

        public SuggestedBookViewHolder(@NonNull View itemView) {
            super(itemView);
            cardBook = itemView.findViewById(R.id.card_book);
            ivBookCover = itemView.findViewById(R.id.iv_book_cover);
            tvBookTitle = itemView.findViewById(R.id.tv_book_title);
            tvBookAuthor = itemView.findViewById(R.id.tv_book_author);
            tvBookPrice = itemView.findViewById(R.id.tv_book_price);
        }
    }
} 