package com.example.petshopp.Admin.Product;

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

public class AddProductActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    FirebaseDatabase database;
    DatabaseReference product;
    ImageView themimageproduct;
    EditText themnameproduct, thempriceproduct, themsortdes, themmota, themidcategory;

    Button chonanhproduct;
    Button luuproduct;
    Button quaylaiproduct;
    DatabaseReference productIdsRef;
    String image = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        database  = FirebaseDatabase.getInstance();
        product  = database.getReference().child("Product");
        productIdsRef = database.getReference().child("ProductID");

        themimageproduct = findViewById(R.id.luuimageproduct);
        themnameproduct = findViewById(R.id.luunameproduct);
        thempriceproduct = findViewById(R.id.luugiasanpham);
        themsortdes = findViewById(R.id.luumotangan);
        themmota = findViewById(R.id.luumota);
        themidcategory = findViewById(R.id.luuiddanhmuc);
        chonanhproduct = findViewById(R.id.chonanhproduct);
        luuproduct = findViewById(R.id.luuproduct);
        quaylaiproduct = findViewById(R.id.quaylaiproduct);

        chonanhproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mở cửa sổ chọn ảnh từ thư viện
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
            }
        });

        luuproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewProduct();
                Intent intent = new Intent(AddProductActivity.this, ProductActivity.class);
                startActivity(intent);
            }
        });

        quaylaiproduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddProductActivity.this, ProductActivity.class);
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
                    .into(themimageproduct);

            // Lưu đường dẫn của ảnh
            image = imageUri.toString();
        }
    }


    private void addNewProduct() {
        String newTitle = themnameproduct.getText().toString();
        String newPrice = thempriceproduct.getText().toString();
        String newShortdest = themsortdes.getText().toString();
        String newFullDes = themmota.getText().toString();
        String newMenuId = themidcategory.getText().toString();
        if (!newTitle.isEmpty() && !image.isEmpty()) {
            product.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int numberOfCategories = (int) snapshot.getChildrenCount();
                    String productId = String.format("%02d", numberOfCategories + 1);

                    if (!snapshot.hasChild(productId)) {
                        // Tạo một đối tượng mới chứa thông tin của danh mục
                        DatabaseReference newCategoryRef = product.child(productId);
                        newCategoryRef.child("Name").setValue(newTitle);
                        newCategoryRef.child("Price").setValue(newPrice);
                        newCategoryRef.child("Shortdes").setValue(newShortdest);
                        newCategoryRef.child("Fulldescription").setValue(newFullDes);
                        newCategoryRef.child("MenuId").setValue(newMenuId);

                        // Gọi hàm uploadImageToStorage và truyền categoryId vào đây
                        uploadImageToStorage(image, productId);

                        // Hiển thị thông báo thành công
                        Toast.makeText(AddProductActivity.this, "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddProductActivity.this, "Số ID đã tồn tại", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Xử lý khi có lỗi
                }
            });

        } else {
            Toast.makeText(AddProductActivity.this, "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
        }
    }





    private void uploadImageToStorage(String imageUri, String categoryId) {
        // Tạo một tham chiếu đến thư mục trong Firebase Storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("product_images");

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
                    Toast.makeText(AddProductActivity.this, "Lỗi khi tải lên ảnh", Toast.LENGTH_SHORT).show();
                });
    }


    private void saveImageURLToDatabase(String imageURL, String categoryId) {
        // Tạo một đối tượng HashMap để lưu thông tin URL vào Firebase Database
        HashMap<String, Object> updateData = new HashMap<>();
        updateData.put("Image", imageURL);

        // Cập nhật thông tin URL vào Firebase Database
        product.child(categoryId).updateChildren(updateData)
                .addOnSuccessListener(aVoid -> {
                    // Xử lý khi cập nhật thành công
                    Toast.makeText(AddProductActivity.this, "URL ảnh đã được lưu", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Xử lý khi cập nhật thất bại
                    Toast.makeText(AddProductActivity.this, "Lỗi khi lưu URL ảnh", Toast.LENGTH_SHORT).show();
                });
    }
}