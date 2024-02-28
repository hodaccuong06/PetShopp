package com.example.petshopp.Admin.Order.Fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopp.Admin.Order.Fragment.Adapter.OrderChuaXacNhanAdminAdapter;
import com.example.petshopp.Domain.Order;
import com.example.petshopp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChuaXacNhanFragment extends Fragment{
    RecyclerView rv_chuaxacnhan;
    private DatabaseReference ordersRef;
    private ArrayList<Order> mOrders;
    private OrderChuaXacNhanAdminAdapter adapter;
    public String phoneNumber="";
    String orderId="";

    public ChuaXacNhanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chua_xac_nhan, container, false);

        rv_chuaxacnhan = view.findViewById(R.id.rv_chuaxacnhan);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv_chuaxacnhan.setLayoutManager(layoutManager);
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        mOrders = new ArrayList<>();


        setUpFirebaseAdapter();
        return view;
    }


    private void setUpFirebaseAdapter() {
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot phoneSnapshot : dataSnapshot.getChildren()) {
                        phoneNumber = phoneSnapshot.getKey();
                        Log.d("PhoneNumber", phoneNumber);

                        for (DataSnapshot orderSnapshot : phoneSnapshot.getChildren()) {
                            orderId = orderSnapshot.getKey();
                            Order order = orderSnapshot.getValue(Order.class);

                            if (order != null && "Chưa xác nhận đơn hàng".equals(order.getTinhTrang())) {
                                mOrders.add(order);
                            }



                        }
                    }
                        adapter = new OrderChuaXacNhanAdminAdapter(getContext(), mOrders);
                        rv_chuaxacnhan.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                } else {
                    Log.d("Orders", "No orders found");
                }
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Error", "Error fetching orders: " + databaseError.getMessage());
            }

        });

    }



}


