package com.example.petshopp.Admin.Category;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.petshopp.Domain.Category;
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

public class EditCategoryActivity extends AppCompatActivity {
    String categoryId = "";
    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    String image = "";
    String CategoryName = "";
    FirebaseDatabase database;
    DatabaseReference category;
    ImageView editimagecategory;
    EditText editnamecategory;

    Button chonanhcategory;
    Button luucategory;
    Button quaylaicategory;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        database  = FirebaseDatabase.getInstance();
        category  = database.getReference().child("Category");


        if (getIntent() != null) {
            categoryId = getIntent().getStringExtra("CategoryId");
            if (categoryId == null || categoryId.isEmpty()) {

            } else {
                // Lấy chi tiết danh mục nếu categoryId tồn tại
                getDetailCategory(categoryId);
            }
        }
        Log.d("TAG", "ID" + categoryId);



        editimagecategory = findViewById(R.id.editimagecategory);
        editnamecategory = findViewById(R.id.editnamecategory);
        chonanhcategory = findViewById(R.id.chonanhdanhmuc);
        luucategory = findViewById(R.id.luucategory);
        quaylaicategory = findViewById(R.id.quaylaicategory);



        chonanhcategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mở cửa sổ chọn ảnh từ thư viện
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
            }
        });

        luucategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    saveCategoryInfo();

                Intent intent = new Intent(EditCategoryActivity.this, CategoryActivity.class);
                startActivity(intent);
            }
        });

        quaylaicategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditCategoryActivity.this, CategoryActivity.class);
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
                    .into(editimagecategory);

            // Lưu đường dẫn của ảnh
            image = imageUri.toString();
        }
    }

    private void getDetailCategory(String categoryId) {
        category.child(categoryId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Category category = snapshot.getValue(Category.class);

                editnamecategory.setText(category.getTitle());
                CategoryName = editnamecategory.getText().toString();
                String imageUrl = category.getImage();
                Glide.with(editimagecategory.getContext())
                        .load(imageUrl)
                        .into(editimagecategory);
                image = imageUrl.toString();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void saveCategoryInfo() {
        // Lấy thông tin mới từ các trường EditText
        String newTitle = editnamecategory.getText().toString();

        // Tạo một đối tượng Category mới với thông tin mới
        DatabaseReference newCategoryRef = category.child(categoryId);
        newCategoryRef.child("Title").setValue(newTitle);


        // Lưu ảnh lên Firebase Storage và lấy URL
        if (image != null && !image.isEmpty()) {
            newCategoryRef.child("Image").setValue(image); // Lưu đường dẫn ảnh vào cơ sở dữ liệu
            uploadImageToStorage(image); // Tải ảnh lên Firebase Storage
        }

        // Thông báo cho người dùng rằng thông tin đã được lưu
        Toast.makeText(EditCategoryActivity.this, "Thông tin đã được lưu", Toast.LENGTH_SHORT).show();
    }

    private void uploadImageToStorage(String imageUri) {
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
                        saveImageURLToDatabase(imageURL);
                    });
                })
                .addOnFailureListener(exception -> {
                    // Xử lý khi upload ảnh thất bại
                    Toast.makeText(EditCategoryActivity.this, "Lỗi khi tải lên ảnh", Toast.LENGTH_SHORT).show();
                });
    }


    private void saveImageURLToDatabase(String imageURL) {
        // Tạo một đối tượng HashMap để lưu thông tin URL vào Firebase Database
        HashMap<String, Object> updateData = new HashMap<>();
        updateData.put("Image", imageURL);

        // Cập nhật thông tin URL vào Firebase Database
        category.child(categoryId).updateChildren(updateData)
                .addOnSuccessListener(aVoid -> {
                    // Xử lý khi cập nhật thành công
                    Toast.makeText(EditCategoryActivity.this, "URL ảnh đã được lưu", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Xử lý khi cập nhật thất bại
                    Toast.makeText(EditCategoryActivity.this, "Lỗi khi lưu URL ảnh", Toast.LENGTH_SHORT).show();
                });
    }
}