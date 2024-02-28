package com.example.petshopp.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petshopp.Domain.CartItem;
import com.example.petshopp.Domain.Product;
import com.example.petshopp.Domain.fragment.Holder.TopViewHolder;
import com.example.petshopp.Interface.ItemClickListener;
import com.example.petshopp.R;
import com.example.petshopp.Domain.fragment.HomeFragment;
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

public class DetailActivity extends AppCompatActivity {

    TextView txtname, txtprice, txtdescription, txtshort , btn_xem;
    ImageView imagepro,back1;

    String productId = "";
    String productName = "";
    String image = "";
    String price1="";


    FirebaseDatabase database;
    DatabaseReference product;
    RecyclerView rv_top;
    private View popupView;
    private TextView tvQuantity;
    private Button btnIncreaseQuantity;
    private Button btnDecreaseQuantity;
    private TextView addCart, buydeals;

    final int[] quantity = {1};
    private TextView Tolal;

    private double price = 0;
    FirebaseRecyclerAdapter<Product , TopViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);



        database  = FirebaseDatabase.getInstance();
        product  = database.getReference().child("Product");

        ConstraintLayout contraint2 = findViewById(R.id.constrant2);
        ScrollView deails = findViewById(R.id.details);
        buydeals =findViewById(R.id.buydetail);
        back1 = findViewById(R.id.back1);
        txtname = findViewById(R.id.txtname);
        txtprice = findViewById(R.id.txtprice);
        txtdescription = findViewById(R.id.txtdecris);
        imagepro = findViewById(R.id.imagepro);
        txtshort = findViewById(R.id.txtshort);
        rv_top = findViewById(R.id.rv_top);
        btn_xem = findViewById(R.id.btn_xem);
        rv_top.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));

        back1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                updateCartBadge();

                // Khi quay lại từ DetailActivity, cập nhật dữ liệu trên HomeFragment
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    Fragment fragment = getSupportFragmentManager().getFragments().get(getSupportFragmentManager().getBackStackEntryCount() - 1);
                    if (fragment instanceof HomeFragment) {
                        ((HomeFragment) fragment).updateData();
                    }
                }

            }
        });

        deails.setOnScrollChangeListener(new View.OnScrollChangeListener() {
            @Override
            public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if (scrollY > 100) {
                    contraint2.setBackgroundColor(Color.parseColor("#FFFFFF")); // đổi màu nền thành màu tắng
                } else {
                    contraint2.setBackgroundColor(Color.argb(0, 255, 255, 255));// đổi màu nền về màu trắng ban đầu
                }
            }
        });



        btn_xem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtdescription.getMaxLines() == 2) {
                    txtdescription.setMaxLines(Integer.MAX_VALUE);
                    btn_xem.setText("Thu gọn \u028C");
                } else {
                    txtdescription.setMaxLines(2);
                    btn_xem.setText("Xem thêm V");
                }
            }
        });



        if(getIntent() != null)
            productId = getIntent().getStringExtra("ProductId");
        if(!productId.isEmpty()){
            getDetailProduct(productId);
        }
        Log.d("TAG", "ID"+productId);
        loadTop();
        updateCartBadge();





    }
    private void updateCartBadge() {
        SharedPreferences sharedPreferences = getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", "");

        DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(phone);
        cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int cartItemCount = (int) dataSnapshot.getChildrenCount();

                ImageView cartImage = findViewById(R.id.shoppingc);

                cartImage.setContentDescription(String.valueOf(cartItemCount));
                cartImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(DetailActivity.this, ShoppingCartActivity.class );
                        startActivity(intent);
                    }
                });


                TextView cartBadge = findViewById(R.id.shoppingv);
                cartBadge.setText(String.valueOf(cartItemCount));

                cartBadge.setVisibility(cartItemCount > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(DetailActivity.this, "Failed to retrieve cart information", Toast.LENGTH_SHORT).show();
            }
        });
    }






    public void buttonPopupwindow(View view){
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        // Tạo popup window

        popupView = LayoutInflater.from(DetailActivity.this).inflate(R.layout.popup_cart, null);
        PopupWindow popupWindow = new PopupWindow(DetailActivity.this);
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);



        View overlay = new View(this);
        overlay.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        overlay.setBackgroundColor(Color.parseColor("#80000000"));
        ((ViewGroup) findViewById(android.R.id.content)).addView(overlay);

        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                ((ViewGroup) findViewById(android.R.id.content)).removeView(overlay);
            }
        });


        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popupWindow.setOutsideTouchable(true);


        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 1000, 0);
        translateAnimation.setDuration(500);

        popupView.startAnimation(translateAnimation);

        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);




        FirebaseDatabase.getInstance().getReference().child("Product").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    price = Double.parseDouble(snapshot.child("Price").getValue(String.class));
                    updateQuantityAndTotal();
                    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                    symbols.setDecimalSeparator('.');
                    symbols.setGroupingSeparator('.');
                    DecimalFormat decimalFormat = new DecimalFormat("#,###.##", symbols);

                    String formattedPrice;
                    try {
                        Number priceNumber = decimalFormat.parse(String.valueOf(price));
                        formattedPrice = decimalFormat.format(priceNumber);
                    } catch (ParseException e) {
                        formattedPrice = "N/A";
                    }
                    // gán giá trị cho các thành phần trên layout của Popup
                    TextView pricecart = popupView.findViewById(R.id.pricecart);
                    pricecart.setText("đ"+formattedPrice);
                    ImageView imageView = popupView.findViewById(R.id.imagecart);
                    String imageUrl = snapshot.child("Image").getValue(String.class);
                    Glide.with(DetailActivity.this)
                            .load(imageUrl)
                            .into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Tolal = popupView.findViewById(R.id.pricecart);





        tvQuantity = popupView.findViewById(R.id.txtamount);
        btnIncreaseQuantity = popupView.findViewById(R.id.buttonup);
        btnDecreaseQuantity = popupView.findViewById(R.id.buttondown);

        btnIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity[0]++;
                updateQuantityAndTotal();
            }
        });

        btnDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity[0] > 1) {
                    quantity[0]--;
                    updateQuantityAndTotal();
                }
            }
        });
                addCart = popupView.findViewById(R.id.addcart);
        SharedPreferences sharedPreferences = getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", "");
        addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(phone);
                cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean found = false;
                        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                            CartItem existingCartItem = itemSnapshot.getValue(CartItem.class);
                            if (existingCartItem != null && existingCartItem.getProductId().equals(productId)) {
                                // If the product already exists in the cart, update the quantity
                                int currentQuantity = Integer.parseInt(existingCartItem.getQuantity());
                                existingCartItem.setQuantity(String.valueOf(currentQuantity + quantity[0]));
                                itemSnapshot.getRef().setValue(existingCartItem);
                                found = true;
                                break;
                            }
                        }
                        updateCartBadge();

                        if (!found) {
                            // If the product does not exist in the cart, create a new cart item
                            CartItem cartItem = new CartItem(productId, productName, price1, String.valueOf(quantity[0]), image);
                            int cartCounter = (int) dataSnapshot.getChildrenCount();
                            String cartCounterKey = "Cart" + (cartCounter + 1);
                            cartRef.child(cartCounterKey).setValue(cartItem);
                        }

                        Toast.makeText(DetailActivity.this, "Added to cart", Toast.LENGTH_SHORT).show();
                        popupWindow.dismiss();
                        ((ViewGroup) findViewById(android.R.id.content)).removeView(overlay);
                        updateCartBadge();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(DetailActivity.this, "Failed to retrieve cart information", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


    private void updateQuantityAndTotal() {
        double total = quantity[0] * price;

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##", symbols);
        String formattedTotal = decimalFormat.format(total);

        tvQuantity.setText(String.valueOf(quantity[0]));
        Tolal.setText("đ"+formattedTotal);



    }


    public void buyNowClicked(View view) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
        // Tạo popup window

        popupView = LayoutInflater.from(DetailActivity.this).inflate(R.layout.popup_cart, null);
        PopupWindow popupWindow = new PopupWindow(DetailActivity.this);
        popupWindow.setContentView(popupView);
        popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);



        View overlay = new View(this);
        overlay.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        overlay.setBackgroundColor(Color.parseColor("#80000000"));
        ((ViewGroup) findViewById(android.R.id.content)).addView(overlay);

        overlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                ((ViewGroup) findViewById(android.R.id.content)).removeView(overlay);
            }
        });


        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popupWindow.setOutsideTouchable(true);


        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 1000, 0);
        translateAnimation.setDuration(500);

        popupView.startAnimation(translateAnimation);

        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);




        FirebaseDatabase.getInstance().getReference().child("Product").child(productId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    price = Double.parseDouble(snapshot.child("Price").getValue(String.class));
                    updateQuantityAndTotal();
                    DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                    symbols.setDecimalSeparator('.');
                    symbols.setGroupingSeparator('.');
                    DecimalFormat decimalFormat = new DecimalFormat("#,###.##", symbols);

                    String formattedPrice;
                    try {
                        Number priceNumber = decimalFormat.parse(String.valueOf(price));
                        formattedPrice = decimalFormat.format(priceNumber);
                    } catch (ParseException e) {
                        formattedPrice = "N/A";
                    }
                    // gán giá trị cho các thành phần trên layout của Popup
                    TextView pricecart = popupView.findViewById(R.id.pricecart);
                    pricecart.setText("đ"+formattedPrice);
                    ImageView imageView = popupView.findViewById(R.id.imagecart);
                    String imageUrl = snapshot.child("Image").getValue(String.class);
                    Glide.with(DetailActivity.this)
                            .load(imageUrl)
                            .into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        Tolal = popupView.findViewById(R.id.pricecart);





        tvQuantity = popupView.findViewById(R.id.txtamount);
        btnIncreaseQuantity = popupView.findViewById(R.id.buttonup);
        btnDecreaseQuantity = popupView.findViewById(R.id.buttondown);

        btnIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity[0]++;
                updateQuantityAndTotal();
            }
        });

        btnDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(quantity[0] > 1) {
                    quantity[0]--;
                    updateQuantityAndTotal();
                }
            }
        });
        addCart = popupView.findViewById(R.id.addcart);
        addCart.setText("Mua Ngay");
        SharedPreferences sharedPreferences = getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", "");
        addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference cartRef = FirebaseDatabase.getInstance().getReference().child("Cart").child(phone);
                cartRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean found = false;
                        for (DataSnapshot itemSnapshot : dataSnapshot.getChildren()) {
                            CartItem existingCartItem = itemSnapshot.getValue(CartItem.class);
                            if (existingCartItem != null && existingCartItem.getProductId().equals(productId)) {
                                int currentQuantity = Integer.parseInt(existingCartItem.getQuantity());
                                existingCartItem.setQuantity(String.valueOf(currentQuantity + quantity[0]));
                                itemSnapshot.getRef().setValue(existingCartItem);
                                found = true;
                                break;
                            }
                        }
                        updateCartBadge();

                        if (!found) {
                            // If the product does not exist in the cart, create a new cart item
                            CartItem cartItem = new CartItem(productId, productName, price1, String.valueOf(quantity[0]), image);
                            int cartCounter = (int) dataSnapshot.getChildrenCount();
                            String cartCounterKey = "Cart" + (cartCounter + 1);
                            cartRef.child(cartCounterKey).setValue(cartItem);
                        }
                        Intent intent = new Intent(DetailActivity.this,ShoppingCartActivity.class);
                        startActivity(intent);
                        popupWindow.dismiss();
                        ((ViewGroup) findViewById(android.R.id.content)).removeView(overlay);
                        updateCartBadge();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(DetailActivity.this, "Failed to retrieve cart information", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }



    private void loadTop() {
        DatabaseReference productRef = FirebaseDatabase.getInstance().getReference().child("Product");
        FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>()
                .setQuery(productRef.limitToFirst(10), Product.class)
                .build();
        adapter = new FirebaseRecyclerAdapter<Product, TopViewHolder>(options) {

            @NonNull
            @Override
            public TopViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.top, parent, false);
                return new TopViewHolder(view);
            }

            @Override
            protected void onBindViewHolder(@NonNull TopViewHolder holder, int position, @NonNull Product model) {
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
                holder.namepro2.setText(model.getName());

                holder.pricepro2.setText("đ"+formattedPrice);
                Glide.with(holder.imagepro2.getContext())
                        .load(model.getImage())
                        .into(holder.imagepro2);
                holder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onlick(View view, int position, boolean isLongClick) {
                        Intent productdetails = new Intent(DetailActivity.this, DetailActivity.class);
                       productdetails.putExtra("ProductId", adapter.getRef(position).getKey());
                        startActivity(productdetails);
                    }
                });

            }
        };
        rv_top.setAdapter(adapter);
        adapter.startListening();
    }




    private void getDetailProduct(String productId) {
        product.child(productId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DecimalFormatSymbols symbols = new DecimalFormatSymbols();
                symbols.setDecimalSeparator('.');
                symbols.setGroupingSeparator('.');
                DecimalFormat decimalFormat = new DecimalFormat("#,###.##", symbols);

                String formattedPrice;
                try {
                    Number priceNumber = decimalFormat.parse(snapshot.child("Price").getValue().toString());
                    formattedPrice = decimalFormat.format(priceNumber);
                } catch (ParseException e) {

                    formattedPrice = "N/A";
                }
                Product product = snapshot.getValue(Product.class);
                String imageUrl = product.getImage();
                Glide.with(imagepro.getContext())
                        .load(imageUrl)
                        .into(imagepro);

                txtname.setText(product.getName());
                productName = txtname.getText().toString();
                txtprice.setText("đ"+formattedPrice);
                image = imageUrl.toString();
                price1 = snapshot.child("Price").getValue().toString();
                txtdescription.setText(product.getFulldescription());
                txtshort.setText(product.getShortdes());

                String description = product.getFulldescription();
                String[] lines = description.split("\\\\n");
                StringBuilder sb = new StringBuilder();
                for (String line : lines) {
                    sb.append(line).append("\n");
                }
                txtdescription.setText(sb.toString());

                String shortdes = product.getShortdes();
                String[] lines1 = shortdes.split("\\\\n");
                StringBuilder sbb = new StringBuilder();
                for (String line1 : lines1) {
                    sbb.append(line1).append("\n");
                }
                txtshort.setText(sbb.toString());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    @Override
    public void onBackPressed() {
        updateCartBadge();
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            // Fragment is reattached, perform reloading logic here
        } else {
            super.onBackPressed();
        }
    }

}