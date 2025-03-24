package com.example.bookbridge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bookbridge.BookDetailsActivity;
import com.example.bookbridge.R;
import com.example.bookbridge.models.Book;
import com.example.bookbridge.utils.CartManager;
import com.example.bookbridge.utils.ImageUtils;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private Context context;
    private List<Book> books;
    private Map<Book, Integer> cartItems;
    private CartItemListener listener;

    public interface CartItemListener {
        void onQuantityChanged();
        void onItemRemoved();
    }

    public CartAdapter(Context context, List<Book> books, Map<Book, Integer> cartItems, CartItemListener listener) {
        this.context = context;
        this.books = books;
        this.cartItems = cartItems;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart_book, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Book book = books.get(position);
        int quantity = cartItems.get(book);

        holder.tvBookTitle.setText(book.getTitle());
        holder.tvBookAuthor.setText(book.getAuthor());
        
        // Apply 20% discount for display
        double discountedPrice = book.getPrice() * 0.8;
        DecimalFormat df = new DecimalFormat("0.00");
        holder.tvBookPrice.setText(String.format(Locale.getDefault(), "₹%s", df.format(discountedPrice)));
        
        // Use ImageUtils to load the book cover image
        ImageUtils.loadBookCover(context, holder.ivBookCover, book);
        holder.tvQuantity.setText(String.valueOf(quantity));

        // Set up item click
        holder.itemView.setOnClickListener(v -> {
            BookDetailsActivity.start((AppCompatActivity) context, book.getId());
        });

        // Set up quantity controls
        holder.btnIncrease.setOnClickListener(v -> {
            CartManager.addToCart(book.getId());
            int newQuantity = CartManager.getQuantity(book.getId());
            holder.tvQuantity.setText(String.valueOf(newQuantity));
            listener.onQuantityChanged();
        });

        holder.btnDecrease.setOnClickListener(v -> {
            if (quantity > 1) {
                CartManager.removeFromCart(book.getId());
                int newQuantity = CartManager.getQuantity(book.getId());
                holder.tvQuantity.setText(String.valueOf(newQuantity));
                listener.onQuantityChanged();
            } else {
                CartManager.removeAllFromCart(book.getId());
                listener.onItemRemoved();
            }
        });

        holder.btnRemove.setOnClickListener(v -> {
            CartManager.removeAllFromCart(book.getId());
            listener.onItemRemoved();
        });
    }

    @Override
    public int getItemCount() {
        return books.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBookCover, btnRemove, btnDecrease, btnIncrease;
        TextView tvBookTitle, tvBookAuthor, tvBookPrice, tvQuantity;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBookCover = itemView.findViewById(R.id.iv_book_cover);
            tvBookTitle = itemView.findViewById(R.id.tv_book_title);
            tvBookAuthor = itemView.findViewById(R.id.tv_book_author);
            tvBookPrice = itemView.findViewById(R.id.tv_book_price);
            tvQuantity = itemView.findViewById(R.id.tv_quantity);
            btnRemove = itemView.findViewById(R.id.btn_remove);
            btnDecrease = itemView.findViewById(R.id.btn_decrease);
            btnIncrease = itemView.findViewById(R.id.btn_increase);
        }
    }
} 