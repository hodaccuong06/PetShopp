package com.example.petshopp.Domain.fragment;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.example.petshopp.Activity.CategoryProductActivity;
import com.example.petshopp.Activity.ChatActivity;
import com.example.petshopp.Activity.DetailActivity;
import com.example.petshopp.Activity.SearchActivity;
import com.example.petshopp.Activity.ShoppingCartActivity;
import com.example.petshopp.Domain.Category;
import com.example.petshopp.Domain.Product;
import com.example.petshopp.Domain.fragment.Holder.CategoryViewHolder;
import com.example.petshopp.Domain.fragment.Holder.PopularViewHolder;
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
import java.util.ArrayList;
import java.util.List;


public class HomeFragment extends Fragment {

    ImageSlider imageSlider;
    RecyclerView rv_category, rv_popular;
    FirebaseRecyclerAdapter<Category, CategoryViewHolder> adapter;
    FirebaseRecyclerAdapter<Product, PopularViewHolder> adapter1;
    TextView searchbar;
    ImageView cartView,chatView;
    TextView cartBadge;


    public HomeFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        imageSlider = view.findViewById(R.id.image_slider);
        rv_category = view.findViewById(R.id.rv_category);
        rv_popular = view.findViewById(R.id.rv_popular);
        searchbar = view.findViewById(R.id.searchbar);
        ScrollView scrollview1 = view.findViewById(R.id.scollview1);
        ConstraintLayout constraint1 = view.findViewById(R.id.contrain1);
        cartView = view.findViewById(R.id.shoppingc1);
        chatView=view.findViewById(R.id.chat);
        cartBadge = view.findViewById(R.id.shoppingv1);
        rv_popular.setHasFixedSize(true);

        rv_popular.setLayoutManager(new GridLayoutManager(getContext(), 2));

        rv_category.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));



        scrollview1.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {

                if (scrollY > 100) {
                    constraint1.setBackgroundColor(Color.parseColor("#FFFFFF")); // đổi màu nền thành màu tắng
                } else {
                    constraint1.setBackgroundColor(Color.argb(0, 255, 255, 255));// đổi màu nền về màu trắng ban đầu
                }
            }
        });

        searchbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

            }
        });
        cartView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ShoppingCartActivity.class );
                startActivity(intent);
            }
        });
        chatView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ChatActivity.class );
                startActivity(intent);
            }
        });
        loadPopular();
        loadCategory();
        updateCartBadge();




        final List<SlideModel> remoteimages = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("Slider")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot data : snapshot.getChildren()) {
                            remoteimages.add(new SlideModel(data.child("url").getValue().toString(), ScaleTypes.FIT));
                        }
                        imageSlider.setImageList(remoteimages);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        updateData();
    }
    public void updateData() {
        // Đặt logic cập nhật dữ liệu tại đây
        updateCartBadge();
        // Các phần cập nhật khác nếu cần
    }

    private void loadCategory() {
        FirebaseRecyclerOptions<Category> options1 = new FirebaseRecyclerOptions.Builder<Category>()
                .setQuery(FirebaseDatabase.getInstance().getReference().child("Category"), Category.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Category, CategoryViewHolder>(options1) {

            @NonNull
            @Override
            public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categori_layout, parent, false);
                return new CategoryViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull CategoryViewHolder holder, int position, @NonNull Category model) {
                holder.title.setText(model.getTitle());
                Glide.with(holder.Image.getContext())
                        .load(model.getImage())
                        .into(holder.Image);
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onlick(View view, int position, boolean isLongClick) {
                        Intent intent = new Intent(getContext(), CategoryProductActivity.class);
                        intent.putExtra("CategoryId",adapter.getRef(position).getKey());
                        intent.putExtra("CategoryName", model.getTitle());
                        startActivity(intent);
                    }
                });

            }
        };
        rv_category.setAdapter(adapter);
        adapter.startListening();

    }
    private void updateCartBadge() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", "");

        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(phone);
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cartItemCount = (int) dataSnapshot.getChildrenCount();
                FragmentActivity activity = getActivity();



                cartView.setContentDescription(String.valueOf(cartItemCount));


                cartBadge.setText(String.valueOf(cartItemCount));

                cartBadge.setVisibility(cartItemCount > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "Failed to retrieve cart information", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void loadPopular() {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Product");
        FirebaseRecyclerOptions<Product> options2 = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(productRef.limitToFirst(10), Product.class)
                .build();
        adapter1 = new FirebaseRecyclerAdapter<Product, PopularViewHolder>(options2) {

            @NonNull
            @Override
            public PopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_product, parent, false);
                return new PopularViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull PopularViewHolder holder, int position, @NonNull Product model) {
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
                holder.namepro.setText(model.getName());
                holder.pricepro.setText("đ" + formattedPrice);
                Glide.with(holder.imagepro.getContext())
                        .load(model.getImage())
                        .into(holder.imagepro);
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onlick(View view, int position, boolean isLongClick) {
                        Intent productdetails = new Intent(view.getContext(), DetailActivity.class);
                        productdetails.putExtra("ProductId", adapter.getRef(position).getKey());
                        startActivity(productdetails);

                    }
                });

            }
        };
        rv_popular.setAdapter(adapter1);
        adapter1.startListening();
    }


}