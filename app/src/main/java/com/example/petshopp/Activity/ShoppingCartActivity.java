package com.example.petshopp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petshopp.Domain.CartItem;
import com.example.petshopp.Domain.fragment.Holder.CartViewHolder;
import com.example.petshopp.Interface.ItemClickListener;
import com.example.petshopp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

public class ShoppingCartActivity extends AppCompatActivity {
    RecyclerView rv_shoppingcart;
    TextView totalprice, mua;
    ImageView back;
    String phone = "";

    FirebaseRecyclerAdapter<CartItem, CartViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_cart);
        rv_shoppingcart = findViewById(R.id.rv_shoppingcart);
        rv_shoppingcart.setLayoutManager(new LinearLayoutManager(this));
        totalprice = findViewById(R.id.totalprice);
        mua = findViewById(R.id.mua);
        back = findViewById(R.id.back1);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        mua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ShoppingCartActivity.this, BuyActivity.class);
                startActivity(intent);
            }
        });

        SharedPreferences sharedPreferences = getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", "");
        Query cartQuery = FirebaseDatabase.getInstance().getReference()
                .child("Cart")
                .child(phone);
        FirebaseRecyclerOptions<CartItem> options =
                new FirebaseRecyclerOptions.Builder<CartItem>()
                        .setQuery(cartQuery, CartItem.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<CartItem, CartViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull CartViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull CartItem model) {
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setDecimalSeparator('.');
                symbols.setGroupingSeparator('.');
                DecimalFormat decimalFormat = new DecimalFormat("#,###.##", symbols);

                String formattedPrice;
                try {
                    Number priceNumber = decimalFormat.parse(model.getPrice());
                    formattedPrice = decimalFormat.format(priceNumber);
                } catch (ParseException e) {
                    formattedPrice = "N/A";
                }
                holder.txtcart.setText(model.getProductName());
                holder.txtprice1.setText("đ" + formattedPrice);
                Glide.with(holder.imagecart1.getContext())
                        .load(model.getImage())
                        .into(holder.imagecart1);
                holder.soluong.setText(model.getQuantity());

                holder.len.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int updatedQuantity = Integer.parseInt(model.getQuantity()) + 1;
                        DatabaseReference cartItemRef = getRef(position);
                        cartItemRef.child("quantity").setValue(String.valueOf(updatedQuantity));
                        updateTotalPrice(); // Cập nhật tổng tiền sau khi thay đổi số lượng
                    }
                });

                holder.xuong.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int updatedQuantity = Integer.parseInt(model.getQuantity()) - 1;
                        if (updatedQuantity >= 0) {
                            DatabaseReference cartItemRef = getRef(position);
                            if (updatedQuantity > 0) {
                                cartItemRef.child("quantity").setValue(String.valueOf(updatedQuantity));
                            } else {
                                cartItemRef.removeValue();
                                updateRemainingIds(phone);
                            }
                            updateTotalPrice();
                        }
                    }
                });


                holder.txtxoa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String CartID = adapter.getRef(position).getKey();
                        deleteCart(CartID, phone);
                        adapter.notifyItemRemoved(position);
                    }
                });

                final CartItem local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onlick(View view, int position, boolean isLongClick) {
                        DatabaseReference cartRef = getRef(position);
                        String cartKey = cartRef.getKey();
                        String productId = local.getProductId();
                        Intent intent = new Intent(ShoppingCartActivity.this, DetailActivity.class);
                        intent.putExtra("ProductId", productId);
                        startActivity(intent);
                        Log.d("TG", "Product: " + productId);
                        Log.d("TG", "Cart: " + cartKey);
                    }
                });
            }

            @NonNull
            @Override
            public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.shopping_cart, parent, false);
                return new CartViewHolder(view);
            }
        };

        rv_shoppingcart.setAdapter(adapter);
        adapter.startListening();
        updateTotalPrice();
    }

    private void updateTotalPrice() {
        final double[] totalPrice = {0};

        DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference()
                .child("Cart")
                .child(phone);

        cartReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot cartItemSnapshot : dataSnapshot.getChildren()) {
                    CartItem cartItem = cartItemSnapshot.getValue(CartItem.class);
                    if (cartItem != null) {
                        double price = Double.parseDouble(cartItem.getPrice().replace(".", ""));
                        int quantity = Integer.parseInt(cartItem.getQuantity());
                        double productTotalPrice = price * quantity;
                        totalPrice[0] += productTotalPrice;
                    }
                }

                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setDecimalSeparator('.');
                symbols.setGroupingSeparator('.');
                DecimalFormat decimalFormat = new DecimalFormat("#,###.##", symbols);
                String formattedTotalPrice = decimalFormat.format(totalPrice[0]);

                totalprice.setText("Tổng giá: " + formattedTotalPrice + " đ");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi (nếu cần)
            }
        });
    }

    private void deleteCart(String CartID, String phone) {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(phone).child(CartID);
        categoryRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                updateRemainingIds(phone);
                updateTotalPrice();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle failure if needed
            }
        });
    }

    private void updateRemainingIds(String phone) {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(phone);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int updatedId = 1;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String addressId = snapshot.getKey();
                    if (addressId != null) {
                        DatabaseReference currentAddressRef = categoryRef.child(addressId);
                        currentAddressRef.removeValue();
                        String newAddressKey = "Cart" + updatedId;
                        categoryRef.child(newAddressKey).setValue(snapshot.getValue());
                        updatedId++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle error if needed
            }
        });
    }
}
