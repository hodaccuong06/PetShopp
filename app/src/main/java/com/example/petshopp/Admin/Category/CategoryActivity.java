package com.example.petshopp.Admin.Category;

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
import com.example.petshopp.Admin.ViewHolder.CategoryAdminViewHolder;
import com.example.petshopp.Domain.Category;
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

public class CategoryActivity extends AppCompatActivity {
    RecyclerView categoryadmin;
    Button themcategory;
    FirebaseRecyclerAdapter<Category, CategoryAdminViewHolder> adapter;

    ImageView quaylai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        categoryadmin = findViewById(R.id.admincategory);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        categoryadmin.setLayoutManager(layoutManager);
        themcategory = findViewById(R.id.themcategory);
        quaylai = findViewById(R.id.back2);

        loadCategory();


        themcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryActivity.this, AddCategoryActivity.class);
                startActivity(intent);
            }
        });

        quaylai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CategoryActivity.this, AdminActivity.class);
                startActivity(intent);
            }
        });
    }


    private void loadCategory() {
        FirebaseRecyclerOptions<Category> options = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Category"), Category.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Category, CategoryAdminViewHolder>(options) {

            @NonNull
            @Override
            public CategoryAdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categoryadmin, parent, false);
                return new CategoryAdminViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CategoryAdminViewHolder holder, int position, @NonNull Category model) {
                holder.title.setText(model.getTitle());
                Glide.with(holder.Image.getContext())
                        .load(model.getImage())
                        .into(holder.Image);
                holder.suacatgory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent categoryedit = new Intent(CategoryActivity.this, EditCategoryActivity.class);
                        categoryedit.putExtra("CategoryId", adapter.getRef(position).getKey());

                        startActivity(categoryedit);
                    }
                });
                holder.xoacategory.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String categoryId = adapter.getRef(position).getKey();
                        deleteCategory(categoryId);
                    }
                });

            }
        };
        categoryadmin.setAdapter(adapter);
        adapter.startListening();


    }
    private void deleteCategory(String categoryId) {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("Category").child(categoryId);
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
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("Category");
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int updatedId = 1; // Khởi tạo ID mới từ 1
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String categoryId = snapshot.getKey();
                    if (categoryId != null) {
                        // Cập nhật lại key cho node hiện tại
                        DatabaseReference currentCategoryRef = categoryRef.child(categoryId);
                        String newKey = String.format("%02d", updatedId);
                        currentCategoryRef.getParent().child(newKey).setValue(snapshot.getValue());
                        if (!newKey.equals(categoryId)) {
                            currentCategoryRef.removeValue(); // Xóa node cũ
                        }
                        updatedId++; // Tăng ID lên 1 sau mỗi danh mục
                    }
                }
                // Sau khi cập nhật xong, gọi lại hàm loadCategory để cập nhật danh sách và reset lại trang
                loadCategory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi (nếu cần)
            }
        });
    }





}