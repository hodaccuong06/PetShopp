package com.example.petshopp.Admin.Category;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.petshopp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.UUID;

public class AddCategoryActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    FirebaseDatabase database;
    DatabaseReference category;
    ImageView themimagecategory;
    EditText themnamecategory;

    Button chonanhcategory2;
    Button luucategory2;
    Button quaylaicategory2;
    DatabaseReference categoryIdsRef;
    String image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);
        database  = FirebaseDatabase.getInstance();
        category  = database.getReference().child("Category");
        categoryIdsRef = database.getReference().child("CategoryID");

        themimagecategory = findViewById(R.id.luuimagecategory);
        themnamecategory = findViewById(R.id.luunamecategory);
        chonanhcategory2 = findViewById(R.id.chonanhdanhmuc2);
        luucategory2 = findViewById(R.id.luucategory2);
        quaylaicategory2 = findViewById(R.id.quaylaicategory2);

        chonanhcategory2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mở cửa sổ chọn ảnh từ thư viện
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
            }
        });

        luucategory2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewCategory();
                Intent intent = new Intent(AddCategoryActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        });

        quaylaicategory2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddCategoryActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            // Lấy đường dẫn của ảnh đã chọn
            Uri imageUri = data.getData();

            // Sử dụng Glide để hiển thị ảnh trong ImageView
            Glide.with(this)
                    .load(imageUri)
                    .into(themimagecategory);

            // Lưu đường dẫn của ảnh
            image = imageUri.toString();
        }
    }


    private void addNewCategory() {
        String newTitle = themnamecategory.getText().toString();
        if (!newTitle.isEmpty() && !image.isEmpty()) {
            category.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int numberOfCategories = (int) snapshot.getChildrenCount();
                    String categoryId = String.format("%02d", numberOfCategories + 1);

                    if (!snapshot.hasChild(categoryId)) {
                        // Tạo một đối tượng mới chứa thông tin của danh mục
                        DatabaseReference newCategoryRef = category.child(categoryId);
                        newCategoryRef.child("Title").setValue(newTitle);

                        // Gọi hàm uploadImageToStorage và truyền categoryId vào đây
                        uploadImageToStorage(image, categoryId);

                        // Hiển thị thông báo thành công
                        Toast.makeText(AddCategoryActivity.this, "Thêm danh mục thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddCategoryActivity.this, "Số ID đã tồn tại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý khi có lỗi
                }
            });

        } else {
            Toast.makeText(AddCategoryActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
    }





    private void uploadImageToStorage(String imageUri, String categoryId) {
        // Tạo một tham chiếu đến thư mục trong Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("category_images");

        // Tạo một tên duy nhất cho ảnh
        String imageName = UUID.randomUUID().toString();

        // Tạo tham chiếu cho ảnh mới
        StorageReference imageRef = storageRef.child(imageName);

        // Upload ảnh lên Firebase Storage
        imageRef.putFile(Uri.parse(imageUri))
                .addOnSuccessListener(taskSnapshot -> {
                    // Lấy URL của ảnh sau khi upload thành công
                    imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        // Lưu URL vào Firebase Database
                        String imageURL = uri.toString();
                        saveImageURLToDatabase(imageURL, categoryId);
                    });
                })
                .addOnFailureListener(exception -> {
                    // Xử lý khi upload ảnh thất bại
                    Toast.makeText(AddCategoryActivity.this, "Lỗi khi tải lên ảnh", Toast.LENGTH_SHORT).show();
                });
    }


    private void saveImageURLToDatabase(String imageURL, String categoryId) {
        // Tạo một đối tượng HashMap để lưu thông tin URL vào Firebase Database
        HashMap<String, Object> updateData = new HashMap<>();
        updateData.put("Image", imageURL);

        // Cập nhật thông tin URL vào Firebase Database
        category.child(categoryId).updateChildren(updateData)
                .addOnSuccessListener(aVoid -> {
                    // Xử lý khi cập nhật thành công
                    Toast.makeText(AddCategoryActivity.this, "URL ảnh đã được lưu", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Xử lý khi cập nhật thất bại
                    Toast.makeText(AddCategoryActivity.this, "Lỗi khi lưu URL ảnh", Toast.LENGTH_SHORT).show();
                });
    }


}