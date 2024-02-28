package com.example.petshopp.Admin.Order;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopp.Admin.Order.Fragment.OrderProductAdminAdapter;
import com.example.petshopp.Domain.CartItem;
import com.example.petshopp.Domain.Order;
import com.example.petshopp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;

public class OrderAdminDetailActivity extends AppCompatActivity {
    private OrderProductAdminAdapter adapter;
    private ArrayList<CartItem> mCartItems;
    String phone="";
    String orderId="";
    FirebaseDatabase database;
    DatabaseReference orderReference;
    TextView nameaddress, phoneaddress, giveaddress, tienship, tientong1, tienhang;
    ImageView back;
    RecyclerView rv_orderdetail;
    Button xacnhanadmindeteail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_admin_detail);
        orderId = getIntent().getStringExtra("orderId");
        Log.d("TAG", "Kiểm tra phân loại " +orderId);
        phone = getIntent().getStringExtra("phone");
        Log.d("TAG", "Kiểm tra phân loại " +phone);
        rv_orderdetail = findViewById(R.id.rv_orderdetail1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        rv_orderdetail.setLayoutManager(layoutManager);
        back = findViewById(R.id.backdeatail1);
        nameaddress = findViewById(R.id.nameaddresdetail1);
        phoneaddress = findViewById(R.id.phoneaddressdetaill1);
        giveaddress = findViewById(R.id.giveaddressdetail1);
        tienship = findViewById(R.id.tienshipdetail1);
        tientong1 = findViewById(R.id.tientongdetail1);
        tienhang = findViewById(R.id.tienhangdetail1);
        xacnhanadmindeteail = findViewById(R.id.xacnhandetailchinh);
        database = FirebaseDatabase.getInstance();

        loadOrderDetails();
        LoadInfor();
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        xacnhanadmindeteail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Order orders = new Order();
                    String xacnhanadmin = "Đã xác nhận đơn hàng";
                    orders.setTinhTrang(xacnhanadmin);
                    DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(phone).child(orderId);
                    orderRef.child("tinhTrang").setValue(xacnhanadmin);
                    Intent intent = new Intent(OrderAdminDetailActivity.this, OrderActivity.class);
                    intent.putExtra("navigateToUserFragment", true);
                    startActivity(intent);
                    finish();
            }
        });

    }
    private void LoadInfor(){
        orderReference = database.getReference().child("Orders").child(phone).child(orderId);
        orderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Order order = snapshot.getValue(Order.class);
                    if (order != null) {
                        nameaddress.setText(order.getName());
                        phoneaddress.setText(order.getPhonenumber());
                        giveaddress.setText(order.getAddress());
                        double totalPrice = order.getTotalAmount();
                        double shippingFee = order.getShipper();
                        double totalPayment = totalPrice + shippingFee;
                        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                        symbols.setDecimalSeparator('.');
                        symbols.setGroupingSeparator('.');
                        DecimalFormat decimalFormat = new DecimalFormat("#,###.##", symbols);

                        String formattedTotalPrice = decimalFormat.format(totalPrice);
                        String formattedShippingFee = decimalFormat.format(shippingFee);
                        String formattedTotalPayment = decimalFormat.format(totalPayment);
                        tienhang.setText(formattedTotalPrice + " đ");
                        tienship.setText(formattedShippingFee + " đ");
                        tientong1.setText(formattedTotalPayment + " đ");

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadOrderDetails() {
        mCartItems = new ArrayList<>();
        orderReference = database.getReference().child("Orders").child(phone).child(orderId).child("products");
        orderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mCartItems.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    CartItem cartItem = dataSnapshot.getValue(CartItem.class);
                    mCartItems.add(cartItem);
                }
                adapter = new OrderProductAdminAdapter(OrderAdminDetailActivity.this, mCartItems);
                rv_orderdetail.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}