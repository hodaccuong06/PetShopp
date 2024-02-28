package com.example.petshopp.Admin.Product;

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
import com.example.petshopp.Domain.Product;
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

public class EditProductActivity extends AppCompatActivity {

    String productId = "";
    private static final int PICK_IMAGE_REQUEST_CODE = 1;
    String image = "";
    String ProductName = "";
    String ProductPrice = "";
    String ProductShort = "";
    String ProductFull = "";
    String ProductIDCate = "";
    FirebaseDatabase database;
    DatabaseReference product;
    ImageView themimageproduct2;
    EditText themnameproduct2, thempriceproduct2, themsortdes2, themmota2, themidcategory2;

    Button chonanhproduct2;
    Button luuproduct2;
    Button quaylaiproduct2;
    DatabaseReference productIdsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_product);

        database  = FirebaseDatabase.getInstance();
        product  = database.getReference().child("Product");
        productIdsRef = database.getReference().child("ProductId");

        themimageproduct2 = findViewById(R.id.luuimageproduct2);
        themnameproduct2 = findViewById(R.id.luunameproduct2);
        thempriceproduct2 = findViewById(R.id.luugiasanpham2);
        themsortdes2 = findViewById(R.id.luumotangan2);
        themmota2 = findViewById(R.id.luumota2);
        themidcategory2 = findViewById(R.id.luuiddanhmuc2);
        chonanhproduct2 = findViewById(R.id.chonanhproduct2);
        luuproduct2 = findViewById(R.id.luuproduct2);
        quaylaiproduct2 = findViewById(R.id.quaylaiproduct2);

        if (getIntent() != null) {
            productId = getIntent().getStringExtra("ProductId");
            if (productId == null || productId.isEmpty()) {

            } else {
                // Lấy chi tiết danh mục nếu categoryId tồn tại
                getDetailCategory(productId);
            }
        }
        Log.d("TAG", "ID" + productId);

        chonanhproduct2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Mở cửa sổ chọn ảnh từ thư viện
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(intent, PICK_IMAGE_REQUEST_CODE);
            }
        });

        quaylaiproduct2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(EditProductActivity.this, ProductActivity.class);
                startActivity(intent);
            }
        });

        luuproduct2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveProduct();

                Intent intent = new Intent(EditProductActivity.this, ProductActivity.class);
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
                    .into(themimageproduct2);

            // Lưu đường dẫn của ảnh
            image = imageUri.toString();
        }
    }

    private void getDetailCategory(String categoryId) {
        product.child(categoryId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Product product = snapshot.getValue(Product.class);

                themnameproduct2.setText(product.getName());
                thempriceproduct2.setText(product.getPrice());
                themsortdes2.setText(product.getShortdes());
                themmota2.setText(product.getFulldescription());
                themidcategory2.setText(product.getMenuId());
                ProductName = themnameproduct2.getText().toString();
                ProductPrice = thempriceproduct2.getText().toString();
                ProductShort = themsortdes2.getText().toString();
                ProductFull = themmota2.getText().toString();
                ProductIDCate = themidcategory2.getText().toString();
                String imageUrl = product.getImage();
                Glide.with(themimageproduct2.getContext())
                        .load(imageUrl)
                        .into(themimageproduct2);
                image = imageUrl.toString();
            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void saveProduct() {
        // Lấy thông tin mới từ các trường EditText
        String newTitle = themnameproduct2.getText().toString();
        String newPrice = thempriceproduct2.getText().toString();
        String newShortdest = themsortdes2.getText().toString();
        String newFullDes = themmota2.getText().toString();
        String newMenuId = themidcategory2.getText().toString();

        // Tạo một đối tượng Category mới với thông tin mới
        DatabaseReference newCategoryRef = product.child(productId);
        newCategoryRef.child("Name").setValue(newTitle);
        newCategoryRef.child("Price").setValue(newPrice);
        newCategoryRef.child("Shortdes").setValue(newShortdest);
        newCategoryRef.child("Fulldescription").setValue(newFullDes);
        newCategoryRef.child("MenuId").setValue(newMenuId);


        // Lưu ảnh lên Firebase Storage và lấy URL
        if (image != null && !image.isEmpty()) {
            newCategoryRef.child("Image").setValue(image); // Lưu đường dẫn ảnh vào cơ sở dữ liệu
            uploadImageToStorage(image); // Tải ảnh lên Firebase Storage
        }

        // Thông báo cho người dùng rằng thông tin đã được lưu
        Toast.makeText(EditProductActivity.this, "Thông tin đã được lưu", Toast.LENGTH_SHORT).show();
    }

    private void uploadImageToStorage(String imageUri) {
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
                        saveImageURLToDatabase(imageURL);
                    });
                })
                .addOnFailureListener(exception -> {
                    // Xử lý khi upload ảnh thất bại
                    Toast.makeText(EditProductActivity.this, "Lỗi khi tải lên ảnh", Toast.LENGTH_SHORT).show();
                });
    }


    private void saveImageURLToDatabase(String imageURL) {
        // Tạo một đối tượng HashMap để lưu thông tin URL vào Firebase Database
        HashMap<String, Object> updateData = new HashMap<>();
        updateData.put("Image", imageURL);

        // Cập nhật thông tin URL vào Firebase Database
        product.child(productId).updateChildren(updateData)
                .addOnSuccessListener(aVoid -> {
                    // Xử lý khi cập nhật thành công
                    Toast.makeText(EditProductActivity.this, "URL ảnh đã được lưu", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Xử lý khi cập nhật thất bại
                    Toast.makeText(EditProductActivity.this, "Lỗi khi lưu URL ảnh", Toast.LENGTH_SHORT).show();
                });
    }
}