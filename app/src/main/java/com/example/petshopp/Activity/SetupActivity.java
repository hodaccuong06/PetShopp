package com.example.petshopp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopp.Domain.Address;
import com.example.petshopp.Domain.fragment.Holder.AddressViewHolder;
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

public class SetupActivity extends AppCompatActivity {
    LinearLayout themdiachi;
    private DatabaseReference mDatabaseRef;
    RecyclerView rv_setup;
    ImageView back;




    FirebaseRecyclerAdapter<Address, AddressViewHolder> adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        themdiachi = findViewById(R.id.themdiachi);
        rv_setup = findViewById(R.id.rv_setup);
        rv_setup.setLayoutManager(new LinearLayoutManager(this));
        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), BuyActivity.class);
                startActivity(intent);
            }
        });
        themdiachi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), AddressActivity.class);
                startActivity(intent);
            }
        });
        SharedPreferences sharedPreferences = getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", "");

        FirebaseRecyclerOptions<Address> options = new FirebaseRecyclerOptions.Builder<Address>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Addresses").child(phone), Address.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Address, AddressViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull AddressViewHolder holder, int position, @NonNull Address model) {

                holder.nameaddress.setText(model.getName());
                holder.phoneaddres.setText(model.getPhoneNumber());
                holder.strestaddress.setText(model.getStreet());

                holder.editxoa.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String addressId = adapter.getRef(position).getKey();
                        deleteAddress(addressId, phone); // Xóa địa chỉ trong cơ sở dữ liệu

                        // Cập nhật giao diện sau khi xóa
                        adapter.notifyItemRemoved(position);
                    }
                });


                holder.editaddress.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(SetupActivity.this, AddressActivity.class);
                        intent.putExtra("AddressesId",adapter.getRef(position).getKey());
                        startActivity(intent);

                    }
                });

                holder.laydulieu.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(SetupActivity.this, BuyActivity.class);
                        intent.putExtra("AddressesId",adapter.getRef(position).getKey());
                        startActivity(intent);
                    }
                });
            }

            @NonNull
            @Override
            public AddressViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.setuplayout, parent, false);
                return new AddressViewHolder(view);
            }
        };

        Log.d("TAG",""+adapter.getItemCount());
        Log.d("TAG", "Kiểm tra phân loại " +phone);
        rv_setup.setAdapter(adapter);
        adapter.startListening();

    }
    private void deleteAddress(String addressId, String phone) {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("Addresses").child(phone).child(addressId);
        categoryRef.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Xóa thành công, cập nhật lại ID cho các mục còn lại
                updateRemainingIds(phone);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Xử lý khi xóa thất bại (nếu cần)
            }
        });
    }

    private void updateRemainingIds(String phone) {
        DatabaseReference categoryRef = FirebaseDatabase.getInstance().getReference().child("Addresses").child(phone);
        categoryRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int updatedId = 1;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String addressId = snapshot.getKey();
                    if (addressId != null) {
                        DatabaseReference currentAddressRef = categoryRef.child(addressId);
                        currentAddressRef.removeValue(); // Xóa node hiện tại

                        String newAddressKey = "Address" + updatedId;
                        categoryRef.child(newAddressKey).setValue(snapshot.getValue());
                        updatedId++;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Xử lý khi có lỗi (nếu cần)
            }
        });
    }

}