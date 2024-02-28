package com.example.petshopp.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopp.R;

public class OrderActivity extends AppCompatActivity {
    RecyclerView rv_order;
    TextView homeorder, order;
    String orderId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);
        order= findViewById(R.id.buyoder);
        homeorder = findViewById(R.id.homeoder);

        orderId = getIntent().getStringExtra("orderId");

        order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailOrderIntent = new Intent(OrderActivity.this, DetailOrderActivity.class);
                detailOrderIntent.putExtra("orderId", orderId);
                startActivity(detailOrderIntent);
                Log.d("TAG", "Kiểm tra phân loại " +orderId);
            }
        });
        homeorder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent detailOrderIntent = new Intent(OrderActivity.this, MainActivity.class);
                startActivity(detailOrderIntent);
            }
        });

    }
}