package com.example.petshopp.Admin.Product;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petshopp.Admin.AdminActivity;
import com.example.petshopp.Admin.Category.AddCategoryActivity;
import com.example.petshopp.Admin.Category.CategoryActivity;
import com.example.petshopp.Admin.Category.EditCategoryActivity;
import com.example.petshopp.Admin.ViewHolder.CategoryAdminViewHolder;
import com.example.petshopp.Admin.ViewHolder.ProductAdminViewHolder;
import com.example.petshopp.Domain.Category;
import com.example.petshopp.Domain.Product;
import com.example.petshopp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

public class ProductActivity extends AppCompatActivity {

    RecyclerView productadmin;
    Button themproduct;
    FirebaseRecyclerAdapter<Product, ProductAdminViewHolder> adapter;

    ImageView quaylai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        productadmin = findViewById(R.id.adminproduct);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        productadmin.setLayoutManager(layoutManager);
        themproduct = findViewById(R.id.themproduct);
        quaylai = findViewById(R.id.back3);

        loadProduct();


        themproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });

        quaylai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProductActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        });
    }


    private void loadProduct() {
        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Product"), Product.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Product, ProductAdminViewHolder>(options) {

            @NonNull
            @Override
            public ProductAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.productadmin, parent, false);
                return new ProductAdminViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull ProductAdminViewHolder holder, @SuppressLint("RecyclerView") int position, @NonNull Product model) {
                holder.title.setText(model.getName());
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

                holder.price.setText("đ"+formattedPrice);

                    Glide.with(holder.Image.getContext())
                        .load(model.getImage())
                        .into(holder.Image);
                holder.suaproduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent productedit = new Intent(ProductActivity.this, EditProductActivity.class);
                        productedit.putExtra("ProductId", adapter.getRef(position).getKey());

                        startActivity(productedit);
                    }
                });
                holder.xoapproduct.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String productId = adapter.getRef(position).getKey();
                        deleteCategory(productId);
                    }
                });

            }
        };
        productadmin.setAdapter(adapter);
        adapter.startListening();


    }
    private void deleteCategory(String productId) {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("Product").child(productId);
        categoryRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Xóa thành công, cập nhật lại ID cho các mục còn lại
                updateRemainingIds();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Xử lý khi xóa thất bại (nếu cần)
            }
        });
    }

    private void updateRemainingIds() {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("Product");
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int updatedId = 1; // Khởi tạo ID mới từ 1
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String productId = snapshot.getKey();
                    if (productId != null) {
                        // Cập nhật lại key cho node hiện tại
                        DatabaseReference currentCategoryRef = categoryRef.child(productId);
                        String newKey = String.format("%02d", updatedId);
                        currentCategoryRef.getParent().child(newKey).setValue(snapshot.getValue());
                        if (!newKey.equals(productId)) {
                            currentCategoryRef.removeValue(); // Xóa node cũ
                        }
                        updatedId++; // Tăng ID lên 1 sau mỗi danh mục
                    }
                }
                // Sau khi cập nhật xong, gọi lại hàm loadCategory để cập nhật danh sách và reset lại trang
                loadProduct();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi (nếu cần)
            }
        });
    }

}