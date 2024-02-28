package com.example.petshopp.Admin.Order.Fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopp.Admin.Order.Fragment.Adapter.OrderDaXacNhanAdminAdapter;
import com.example.petshopp.Domain.Order;
import com.example.petshopp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class DaXacNhanFragment extends Fragment {
    RecyclerView rv_daxacnhan;
    private DatabaseReference ordersRef;
    private ArrayList<Order> mOrders;
    private OrderDaXacNhanAdminAdapter adapter;
    public String phoneNumber="";
    String orderId="";


    public DaXacNhanFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_da_xac_nhan, container, false);
        rv_daxacnhan = view.findViewById(R.id.rv_daxacnhan);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        rv_daxacnhan.setLayoutManager(layoutManager);
        ordersRef = FirebaseDatabase.getInstance().getReference().child("Orders");
        setUpFirebaseAdapter();
        return view;
    }
    private void setUpFirebaseAdapter() {
        mOrders = new ArrayList<>();
        ordersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot phoneSnapshot : dataSnapshot.getChildren()) {
                        phoneNumber = phoneSnapshot.getKey();
                        Log.d("PhoneNumber", phoneNumber);

                        for (DataSnapshot orderSnapshot : phoneSnapshot.getChildren()) {
                            orderId = orderSnapshot.getKey();
                            Order order = orderSnapshot.getValue(Order.class);

                            if (order != null && "Đã xác nhận đơn hàng".equals(order.getTinhTrang())) {
                                mOrders.add(order);
                            }
                            adapter = new OrderDaXacNhanAdminAdapter(getContext(), mOrders);
                            rv_daxacnhan.setAdapter(adapter);
                        }
                    }
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