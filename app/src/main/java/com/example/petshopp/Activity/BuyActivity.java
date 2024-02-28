package com.example.petshopp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petshopp.Domain.Address;
import com.example.petshopp.Domain.CartItem;
import com.example.petshopp.Domain.Order;
import com.example.petshopp.Domain.fragment.Holder.BuyViewHolder;
import com.example.petshopp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class BuyActivity extends AppCompatActivity {
    RecyclerView rv_buy;
    LinearLayout setup;
    ImageView back;
    TextView nameaddress, phoneaddress, giveaddress, totalprice, tienship, tientong1, tienhang, phuongthuc,dathang;

    String addressesId = "";
    FirebaseDatabase database;
    DatabaseReference address;
    String phone = "";
    FirebaseRecyclerAdapter<CartItem, BuyViewHolder> adapter;
    private List<CartItem> selectedProductsList = new ArrayList<>();
    private Address selectedAddress;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy);

        rv_buy = findViewById(R.id.rv_buy);
        rv_buy.setLayoutManager(new LinearLayoutManager(this));
        setup = findViewById(R.id.setup);
        back = findViewById(R.id.back1);
        nameaddress = findViewById(R.id.nameaddres);
        phoneaddress = findViewById(R.id.phoneaddress);
        giveaddress = findViewById(R.id.giveaddress);
        totalprice = findViewById(R.id.tientong);
        tienship = findViewById(R.id.tienship);
        tientong1 = findViewById(R.id.tientong1);
        tienhang = findViewById(R.id.tienhang);
        phuongthuc = findViewById(R.id.phuongthuc);
        dathang = findViewById(R.id.dathang);

        SharedPreferences sharedPreferences = getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        phone = sharedPreferences.getString("phone", "");
        database = FirebaseDatabase.getInstance();
        address  = database.getReference().child("Addresses").child(phone);

        if(getIntent() !=null)
            addressesId = getIntent().getStringExtra("AddressesId");
        if (addressesId != null && !addressesId.isEmpty()) {
            loadAddresses(addressesId);
        } else {
            loadFirstAddress();

        }

        dathang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Lấy danh sách sản phẩm từ adapter
                selectedProductsList.clear();
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    CartItem cartItem = adapter.getItem(i);
                    if (cartItem != null) {
                        selectedProductsList.add(cartItem);
                    }
                }

                // Lưu địa chỉ đã chọn
                selectedAddress = new Address();
                selectedAddress.setName(nameaddress.getText().toString());
                selectedAddress.setPhoneNumber(phoneaddress.getText().toString());
                selectedAddress.setStreet(giveaddress.getText().toString());




                saveOrderToDatabase();
            }
        });








        setup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SetupActivity.class);
                startActivity(intent);
            }
        });


        Query cartQuery = FirebaseDatabase.getInstance().getReference()
                .child("Cart")
                .child(phone);
        FirebaseRecyclerOptions<CartItem> options =
                new FirebaseRecyclerOptions.Builder<CartItem>()
                        .setQuery(cartQuery, CartItem.class)
                        .build();
        adapter = new FirebaseRecyclerAdapter<CartItem, BuyViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull BuyViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull CartItem model) {
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
                holder.namebuy.setText(model.getProductName());
                holder.pricebuy.setText("đ" + formattedPrice);
                Glide.with(holder.imagebuy.getContext())
                        .load(model.getImage())
                        .into(holder.imagebuy);
                holder.soluongbuy.setText(model.getQuantity());


            }

            @NonNull
            @Override
            public BuyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buy_layout, parent, false);
                return new BuyViewHolder(view);
            }
        };

        rv_buy.setAdapter(adapter);
        adapter.startListening();
        updateTotalPrice();

    }
    private void loadFirstAddress() {
        address.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists() && snapshot.getChildrenCount() > 0) {
                    for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                        Address loadedAddress = childSnapshot.getValue(Address.class);

                        if (loadedAddress != null) {
                            nameaddress.setText(loadedAddress.getName());
                            phoneaddress.setText(loadedAddress.getPhoneNumber());
                            giveaddress.setText(loadedAddress.getStreet());
                            break;
                        }
                    }
                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database query cancellation or errors
            }
        });
    }


    private void loadAddresses(String addressId) {
        DatabaseReference specificAddress = address.child(addressId);
        specificAddress.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Address loadedAddress = snapshot.getValue(Address.class);

                if (loadedAddress != null) {
                    nameaddress.setText(loadedAddress.getName());
                    phoneaddress.setText(loadedAddress.getPhoneNumber());
                    giveaddress.setText(loadedAddress.getStreet());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý khi có lỗi xảy ra trong quá trình truy vấn cơ sở dữ liệu
            }
        });
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
                        String priceString = cartItem.getPrice();
                        if (priceString != null) {
                            double price = Double.parseDouble(priceString.replace(".", "").replace(",", ""));
                            int quantity = Integer.parseInt(cartItem.getQuantity());
                            double productTotalPrice = price * quantity;
                            totalPrice[0] += productTotalPrice;
                        }
                    }
                }

                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setDecimalSeparator('.');
                symbols.setGroupingSeparator('.');
                DecimalFormat decimalFormat = new DecimalFormat("#,###.##", symbols);
                String formattedTotalPrice = decimalFormat.format(totalPrice[0]);
                tienhang.setText(formattedTotalPrice + " đ");
                // Tính tiền ship
                double shippingFee = 15000; // Tiền ship = 15.000 đ
                String formattedShippingFee = decimalFormat.format(shippingFee);
                tienship.setText(formattedShippingFee + " đ");

                // Tính tổng tiền
                double totalPayment = totalPrice[0] + shippingFee;
                String formattedTotalPayment = decimalFormat.format(totalPayment);
                tientong1.setText(formattedTotalPayment + " đ");
                String totalPriceText = formattedTotalPayment + " đ";


                Spannable spannable = new SpannableString(totalPriceText);


                spannable.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.red)), 0, totalPriceText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                spannable.setSpan(new RelativeSizeSpan(1.25f), 0, totalPriceText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                spannable.setSpan(new StyleSpan(Typeface.BOLD), 0, totalPriceText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


                totalprice.setText(TextUtils.concat("Tổng thanh toán\n", spannable));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi (nếu cần)
            }
        });
    }
    private void saveOrderToDatabase() {
        if (selectedAddress != null && !selectedProductsList.isEmpty()) {
            String diaChi = selectedAddress.getStreet();
            String name = selectedAddress.getName();
            String phoneNum = selectedAddress.getPhoneNumber();
            double tongTienSanPham = calculateTotalPrice(selectedProductsList);
            double tienShipper = 15000;
            double tongTien = tongTienSanPham + tienShipper;

            DatabaseReference ordersReference = FirebaseDatabase.getInstance().getReference().child("Orders").child(phone);
            ordersReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String tinhTrang = "Chưa xác nhận đơn hàng";
                    int cartCounter = (int) snapshot.getChildrenCount();
                    String cartCounterKey = "Order" + (cartCounter + 1);
                    Order newOrder = new Order(phone,cartCounterKey,name, phoneNum, diaChi, tongTienSanPham, tienShipper, tongTien, selectedProductsList, System.currentTimeMillis(),tinhTrang);

                        ordersReference.child(cartCounterKey).setValue(newOrder);
                        DatabaseReference cartReference = FirebaseDatabase.getInstance().getReference().child("Cart")
                                .child(phone);
                        cartReference.removeValue();
                    Intent orderIntent = new Intent(BuyActivity.this, OrderActivity.class);
                    orderIntent.putExtra("orderId", cartCounterKey);
                    startActivity(orderIntent);
                    Log.d("Order", "orderId " + cartCounterKey);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


        }
    }

    private double calculateTotalPrice(List<CartItem> products) {
        double totalPrice = 0;
        for (CartItem product : products) {
            try {
                double price = Double.parseDouble(product.getPrice().replace(".", "").replace(",", ""));
                int quantity = Integer.parseInt(product.getQuantity());
                double productTotalPrice = price * quantity;
                totalPrice += productTotalPrice;
            } catch (NumberFormatException e) {
                // Xử lý nếu có lỗi chuyển đổi số
            }
        }
        return totalPrice;
    }



}