package com.example.petshopp.Domain.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.example.petshopp.Activity.AllDonHangActivity;
import com.example.petshopp.Activity.EditUserActivity;
import com.example.petshopp.Activity.ShoppingCartActivity;
import com.example.petshopp.Activity.SignInActivity;
import com.example.petshopp.Domain.User;
import com.example.petshopp.R;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class UserFragment extends Fragment {

    String phone;
    Button logout, thietlaptaikhoan, donhang1;
    TextView NameUser,donhang,shopingv;
    ImageView imageUser,shopingc;
    FirebaseUser users;
    DatabaseReference reference;

    private SharedPreferences sharedPreferences;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user,container,false);

        logout = view.findViewById(R.id.button3);
        NameUser = view.findViewById(R.id.NameUser);
        imageUser = view.findViewById(R.id.imageUser);
        thietlaptaikhoan = view.findViewById(R.id.thietlaptaikhoan);
        donhang = view.findViewById(R.id.donhangv1);
        donhang1 = view.findViewById(R.id.donhang);
        shopingc = view.findViewById(R.id.imageshopers);
        shopingv = view.findViewById(R.id.shoppinguser);
        updateCartBadge();
        updateCartBadge1();

        donhang1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), AllDonHangActivity.class);
                startActivity(intent);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        shopingc.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShoppingCartActivity.class );
                startActivity(intent);
            }
        });




        sharedPreferences = requireActivity().getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", "");
        reference = FirebaseDatabase.getInstance().getReference().child("User");
        thietlaptaikhoan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(requireContext(), EditUserActivity.class);
                intent.putExtra("phone", phone); // Truyền số điện thoại
                Log.d("TAG", "ID" + phone);
                startActivity(intent);
            }
        });
        reference.child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                Glide.with(imageUser.getContext())
                        .load(userProfile.getProfileImageUrl())
                        .into(imageUser);

                NameUser.setText(userProfile.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();


        // Điều hướng người dùng về màn hình đăng nhập sau khi đăng xuất
        Intent intent = new Intent(requireActivity(), SignInActivity.class);
        startActivity(intent);
    }

    private void updateCartBadge() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", "");

        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(phone);
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cartItemCount = (int) dataSnapshot.getChildrenCount();
                FragmentActivity activity = getActivity();

                donhang.setText(String.valueOf(cartItemCount));
                donhang.setVisibility(cartItemCount > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to retrieve cart information", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void updateCartBadge1() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", "");

        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(phone);
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cartItemCount = (int) dataSnapshot.getChildrenCount();
                FragmentActivity activity = getActivity();



                shopingc.setContentDescription(String.valueOf(cartItemCount));


                shopingv.setText(String.valueOf(cartItemCount));

                shopingv.setVisibility(cartItemCount > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to retrieve cart information", Toast.LENGTH_SHORT).show();
            }
        });
    }




}
