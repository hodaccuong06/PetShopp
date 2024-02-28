package com.example.petshopp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.petshopp.Domain.Address;
import com.example.petshopp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddressActivity extends AppCompatActivity {
        TextView menuset;
        EditText hovaten, sodienthoai,tinhthanhpho,tenduong;
        Button button4;
        String Street,Name,PhoneNumber;
        ImageView back;
        String addressesId = "";
        FirebaseDatabase database;
        DatabaseReference address;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address);
        menuset = findViewById(R.id.menuset);
        hovaten = findViewById(R.id.hovaten);
        sodienthoai = findViewById(R.id.sodienthoai);
        tinhthanhpho = findViewById(R.id.tinhthanhpho);
        tenduong = findViewById(R.id.tenduong);
        button4 = findViewById(R.id.button4);
        back =findViewById(R.id.back);
        database  = FirebaseDatabase.getInstance();
        SharedPreferences sharedPreferences = getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", "");
        address  = database.getReference().child("Addresses").child(phone);
        if(getIntent() !=null)
            addressesId = getIntent().getStringExtra("AddressesId");
        if(addressesId != null && !addressesId.isEmpty()){
            loadAddresses(addressesId);
        }
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Name = hovaten.getText().toString();
                PhoneNumber = sodienthoai.getText().toString();

                Street =  tenduong.getText().toString()+ "."+"\n" +tinhthanhpho.getText().toString() + ".";

                if (hovaten.getText().toString().isEmpty()) {
                    hovaten.setError("Vui lòng nhập họ và tên");
                    hovaten.requestFocus();
                } else if (sodienthoai.getText().toString().isEmpty()) {
                    sodienthoai.setError("Vui lòng nhập số điện thoại");
                    sodienthoai.requestFocus();

                } else if (tinhthanhpho.getText().toString().isEmpty()) {
                    tinhthanhpho.setError("Vui lòng nhập tỉnh/thành phố");
                    tinhthanhpho.requestFocus();

                } else if (tenduong.getText().toString().isEmpty()) {
                    tenduong.setError("Vui lòng nhập tên đường/tổ");
                    tenduong.requestFocus();
                } else{

                    SharedPreferences sharedPreferences = getSharedPreferences("TaiKhoan", MODE_PRIVATE);
                    String phone = sharedPreferences.getString("phone", "");
                    DatabaseReference addressesRef = FirebaseDatabase.getInstance().getReference().child("Addresses").child(phone);
                    addressesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (addressesId != null && !addressesId.isEmpty()) {
                                Address updatedAddress = new Address(Name, PhoneNumber, Street);
                                addressesRef.child(addressesId).setValue(updatedAddress);
                                Toast.makeText(AddressActivity.this, "Địa chỉ đã được cập nhật", Toast.LENGTH_SHORT).show();
                                finish(); // Đóng Activity sau khi cập nhật địa chỉ
                            } else {
                                // Xử lý thêm địa chỉ mới (như trong phần code ban đầu của bạn)
                                Address address = new Address(Name, PhoneNumber, Street);
                                int cartCounter = (int) snapshot.getChildrenCount();
                                String cartCounterKey = "Address" + (cartCounter + 1);
                                addressesRef.child(cartCounterKey).setValue(address);
                                Toast.makeText(AddressActivity.this, "Địa chỉ đã được lưu", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(AddressActivity.this, SetupActivity.class);
                                startActivity(intent);
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });


                }



            }
        });






    }

    private void loadAddresses(String addressesId) {

        address.child(addressesId).addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Address loadedAddress = snapshot.getValue(Address.class);
                Log.d("TAG", "Kiểm tra phân loại " + loadedAddress);

                if (loadedAddress != null) {
                    hovaten.setText(loadedAddress.getName());
                    sodienthoai.setText(loadedAddress.getPhoneNumber());
                    String street = loadedAddress.getStreet();
                    String[] streetComponents = street.split("\n");
                    if (streetComponents.length >= 2) {
                        tenduong.setText(streetComponents[0]);
                        tinhthanhpho.setText(streetComponents[1]);
                    }
                } else {
                    // Xử lý khi loadedAddress là null, ví dụ: hiển thị thông báo hoặc thực hiện các hành động khác
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });
    }

}