package com.example.petshopp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petshopp.Domain.Product;
import com.example.petshopp.Domain.fragment.Holder.ProductViewHolder;
import com.example.petshopp.Interface.ItemClickListener;
import com.example.petshopp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

public class CategoryProductActivity extends AppCompatActivity {

    RecyclerView rv_categoryproduct;
    String categoryId ="";
    String categoryName ="";
    FirebaseRecyclerAdapter<Product,ProductViewHolder> adapter;
    ImageView back, shoppingc;
    TextView searchbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_product);

        rv_categoryproduct = findViewById(R.id.rv_shoppingcart);
        searchbar = findViewById(R.id.searchbar);
        back = findViewById(R.id.back1);
        shoppingc = findViewById(R.id.shoppingc);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        shoppingc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CategoryProductActivity.this, ShoppingCartActivity.class );
                startActivity(intent);
            }
        });



        if(getIntent() !=null)
            categoryId = getIntent().getStringExtra("CategoryId");
            categoryName = getIntent().getStringExtra("CategoryName");
        if(!categoryId.isEmpty() && categoryId !=null){
            loadListProduct(categoryId,categoryName);
        }
        updateCartBadge();

    }

    private void updateCartBadge() {
        SharedPreferences sharedPreferences = getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", "");

        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(phone);
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cartItemCount = (int) dataSnapshot.getChildrenCount();

                ImageView cartImage = findViewById(R.id.shoppingc);

                cartImage.setContentDescription(String.valueOf(cartItemCount));

                TextView cartBadge = findViewById(R.id.shoppingv2);
                cartBadge.setText(String.valueOf(cartItemCount));

                cartBadge.setVisibility(cartItemCount > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CategoryProductActivity.this, "Failed to retrieve cart information", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadListProduct(String categoryId,String categoryName) {
        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Product").orderByChild("MenuId").equalTo(categoryId), Product.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {
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
                holder.namepro1.setText(model.getName());
                holder.pricepro1.setText("đ"+formattedPrice);
                searchbar.setText(categoryName);
                Glide.with(holder.imagepro1.getContext())
                        .load(model.getImage())
                        .into(holder.imagepro1);
                final Product local = model;
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onlick(View view, int position, boolean isLongClick) {
                        Intent productdetails = new Intent(CategoryProductActivity.this, DetailActivity.class);
                        productdetails.putExtra("ProductId", adapter.getRef(position).getKey());

                        startActivity(productdetails);
                    }
                });
            }

            @NonNull
            @Override
            public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_product, parent, false);
                return new ProductViewHolder(view);
            }
        };
        Log.d("TAG",""+adapter.getItemCount());
        Log.d("TAG", "Kiểm tra phân loại " +categoryId);
        rv_categoryproduct.setAdapter(adapter);
        adapter.startListening();
    }

}