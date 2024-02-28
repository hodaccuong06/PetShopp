package com.example.petshopp.Activity;

import static androidx.fragment.app.FragmentManager.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petshopp.Domain.CartItem;
import com.example.petshopp.Domain.Order;
import com.example.petshopp.Domain.fragment.Holder.AllDonHangViewHolder;
import com.example.petshopp.Interface.ItemClickListener;
import com.example.petshopp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

public class AllDonHangActivity extends AppCompatActivity {
    ImageView backdonhangall;
    RecyclerView rv_donhangall;
    String phone = "";
    String orderId = "";
    FirebaseRecyclerAdapter<Order, AllDonHangViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_don_hang);
        backdonhangall = findViewById(R.id.backdonhangall);
        rv_donhangall = findViewById(R.id.rv_donhangall);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_donhangall.setLayoutManager(layoutManager);
        backdonhangall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        SharedPreferences sharedPreferences = getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", "");
        Log.d(TAG, "phone" + phone);
        Query cartQuery = FirebaseDatabase.getInstance().getReference()
                .child("Orders")
                .child(phone);
        FirebaseRecyclerOptions<Order> options =
                new FirebaseRecyclerOptions.Builder<Order>()
                        .setQuery(cartQuery, Order.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<Order, AllDonHangViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AllDonHangViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Order model) {
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setDecimalSeparator('.');
                symbols.setGroupingSeparator('.');
                DecimalFormat decimalFormat = new DecimalFormat("#,###.##", symbols);

                String formattedPrice = "N/A"; // Set a default value

                try {
                    double sumTotalAmount = model.getSumtotalAmount();
                    formattedPrice = decimalFormat.format(sumTotalAmount);
                } catch (NumberFormatException e) {
                    Log.e(TAG, "Error parsing sumTotalAmount: " + e.getMessage());
                }

                holder.priceproorderdonhangall.setText("đ" + formattedPrice);

                int productCount = model.getProducts().size();
                if(productCount > 1){
                    holder.xemthem.setVisibility(View.VISIBLE);
                }
                holder.tinhtrang.setText(model.getTinhTrang());
                if (holder.tinhtrang.getText().toString().equals("Chưa xác nhận đơn hàng")) {
                    holder.tinhtrang.setTextColor(getResources().getColor(R.color.red));
                }else if (holder.tinhtrang.getText().toString().equals("Đã xác nhận đơn hàng")){
                    holder.tinhtrang.setTextColor(getResources().getColor(R.color.oran));
                }else{
                    holder.tinhtrang.setTextColor(getResources().getColor(R.color.blue));
                }
                if (model.getProducts() != null && !model.getProducts().isEmpty()) {
                    CartItem firstProduct = model.getProducts().get(0);
                    String productPrice = firstProduct.getPrice();

                    try {
                        double price = Double.parseDouble(productPrice); // Chuyển đổi thành số
                        holder.priceall.setText("đ" + decimalFormat.format(price));
                    } catch (NumberFormatException e) {
                        holder.priceall.setText("đ" + productPrice); // Nếu không thể chuyển đổi, hiển thị chuỗi ban đầu
                    }

                }
                // Lấy tên của sản phẩm đầu tiên
                if (model.getProducts() != null && !model.getProducts().isEmpty()) {
                    CartItem firstProduct = model.getProducts().get(0);
                    String productName = firstProduct.getProductName();
                    holder.nameproorderdonhangall.setText(productName);
                }

                if (model.getProducts() != null && !model.getProducts().isEmpty()) {
                    CartItem firstProduct = model.getProducts().get(0);
                    Glide.with(holder.imagedonhangall.getContext())
                            .load(firstProduct.getImage())
                            .into(holder.imagedonhangall);
                    holder.soluongdonhangall.setText("x" + firstProduct.getQuantity());
                }
                holder.sosanphamdonhangall.setText(model.getProducts().size() + " sản phẩm");

                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onlick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(AllDonHangActivity.this, DetailOrderActivity.class);
                        intent.putExtra("orderId",adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public AllDonHangViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.all_donhang, parent, false);
                return new AllDonHangViewHolder(view);
            }
        };

        rv_donhangall.setAdapter(adapter);
        Log.d(TAG, "phone" + adapter);
        adapter.startListening();
        adapter.notifyDataSetChanged();
    }
    }
