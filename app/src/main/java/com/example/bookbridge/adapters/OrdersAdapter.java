package com.example.bookbridge.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.bookbridge.R;
import com.example.bookbridge.TrackOrderActivity;
import com.example.bookbridge.models.Book;
import com.example.bookbridge.models.Order;
import com.example.bookbridge.utils.OrderManager;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrderViewHolder> {

    private final Context context;
    private final List<Order> orderList;
    private final SimpleDateFormat dateFormat;

    public OrdersAdapter(Context context, List<Order> orderList) {
        this.context = context;
        this.orderList = orderList;
        this.dateFormat = new SimpleDateFormat("dd/M/yyyy", Locale.getDefault());
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orderList.get(position);
        
        // Set order ID and date
        holder.tvOrderId.setText("Order #" + order.getOrderId());
        holder.tvOrderDate.setText(dateFormat.format(order.getOrderDate()));
        
        // Set status and payment method
        holder.tvOrderStatus.setText("Status: " + order.getStatus());
        holder.tvPaymentMethod.setText("Payment: " + order.getPaymentMethod());
        
        // Get books for this order
        List<Book> booksInOrder = OrderManager.getBooksForOrder(order.getOrderId());
        
        // Display book information if available
        if (!booksInOrder.isEmpty()) {
            // Display the first book in the order
            Book book = booksInOrder.get(0);
            
            // Set book title and author
            if (holder.tvBookTitle != null) {
                holder.tvBookTitle.setText(book.getTitle());
            }
            
            if (holder.tvAuthor != null) {
                holder.tvAuthor.setText(book.getAuthor());
            }
            
            // Set book price
            if (holder.tvPrice != null) {
                holder.tvPrice.setText(String.format(Locale.getDefault(), "₹%.2f", book.getPrice() * 0.8)); // 20% discount
            }
            
            // Load book cover image using Glide
            if (holder.ivBookCover != null && book.getImageUrl() != null) {
                Glide.with(context)
                        .load(book.getImageUrl())
                        .placeholder(R.drawable.book_placeholder)
                        .error(R.drawable.book_placeholder)
                        .into(holder.ivBookCover);
            }
            
            // If there are multiple books, indicate that
            if (booksInOrder.size() > 1 && holder.tvBookTitle != null) {
                holder.tvBookTitle.setText(book.getTitle() + " + " + (booksInOrder.size() - 1) + " more");
            }
        }
        
        // Set track order button click listener
        holder.btnTrackOrder.setOnClickListener(v -> {
            // Launch TrackOrderActivity with the order ID
            TrackOrderActivity.start(context, order.getOrderId());
        });
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId;
        TextView tvOrderDate;
        TextView tvOrderStatus;
        TextView tvPaymentMethod;
        Button btnTrackOrder;
        
        // Book details views
        ImageView ivBookCover;
        TextView tvBookTitle;
        TextView tvAuthor;
        TextView tvPrice;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tv_order_id);
            tvOrderDate = itemView.findViewById(R.id.tv_order_date);
            tvOrderStatus = itemView.findViewById(R.id.tv_order_status);
            tvPaymentMethod = itemView.findViewById(R.id.tv_payment_method);
            btnTrackOrder = itemView.findViewById(R.id.btn_track_order);
            
            // Initialize book details views
            ivBookCover = itemView.findViewById(R.id.iv_book_cover);
            tvBookTitle = itemView.findViewById(R.id.tv_book_title);
            tvAuthor = itemView.findViewById(R.id.tv_author);
            tvPrice = itemView.findViewById(R.id.tv_price);
        }
    }
} 