package com.example.petshopp.Admin;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.petshopp.Admin.Category.CategoryActivity;
import com.example.petshopp.Admin.Chat.ChatAdminActivity;
import com.example.petshopp.Admin.Order.OrderActivity;
import com.example.petshopp.Admin.Product.ProductActivity;
import com.example.petshopp.R;


public class AdminActivity extends AppCompatActivity {
    ConstraintLayout categoryadmin;
    ConstraintLayout productadmin;
    ConstraintLayout cartadmin;
    ConstraintLayout chatadmin;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        categoryadmin = findViewById(R.id.categoryadmin);
        productadmin = findViewById(R.id.productadmin);
        cartadmin = findViewById(R.id.cartadmin);
        chatadmin = findViewById(R.id.chatadmin);


        categoryadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this , CategoryActivity.class);
                startActivity(intent);
            }
        });

        productadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this , ProductActivity.class);
                startActivity(intent);
            }
        });
        cartadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this , OrderActivity.class);
                startActivity(intent);
            }
        });
        chatadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AdminActivity.this, ChatAdminActivity.class);
                startActivity(intent);
            }
        });

    }


    }
