package com.example.petshopp.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petshopp.Domain.Product;
import com.example.petshopp.Domain.SearchHistory;
import com.example.petshopp.Domain.fragment.Holder.ProductViewHolder;
import com.example.petshopp.Domain.fragment.Holder.SearchHistoryAdapter;
import com.example.petshopp.Interface.ItemClickListener;
import com.example.petshopp.R;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class SearchActivity extends AppCompatActivity {
    private DatabaseReference mDatabaseRef;
    RecyclerView rv_search,rv_listproduct;
    FirebaseRecyclerAdapter<Product, ProductViewHolder> adapter;
    FirebaseAuth auth;
    private ArrayList<SearchHistory> searchHistoryList;
    private SearchHistoryAdapter historyAdapter;
    View ln_search;
    View ln_listproduct;
    TextView tv_search;

    ImageView mSearch;
    ImageView back2;
    EditText search;
    private static final int MAX_HISTORY_ITEMS = 5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mSearch = findViewById(R.id.btn_search1);
        search = findViewById(R.id.txt_search);
        rv_listproduct = findViewById(R.id.rv_listpro);
        tv_search = findViewById(R.id.tv_search);
        ln_search = findViewById(R.id.ln_search);
        ln_listproduct = findViewById(R.id.ln_listpro);


        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        back2 = findViewById(R.id.bac2);
        rv_listproduct.setLayoutManager(new GridLayoutManager(this, 2));

        rv_search = findViewById(R.id.rv_search);
        rv_search.setLayoutManager(new LinearLayoutManager(this));
        searchHistoryList = new ArrayList<>();
        historyAdapter = new SearchHistoryAdapter(searchHistoryList);
        rv_search.setAdapter(historyAdapter);
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ln_search.setVisibility(View.VISIBLE);
                ln_listproduct.setVisibility(View.GONE);
                search.setVisibility(View.VISIBLE);
                tv_search.setVisibility(View.GONE);
                mSearch.setVisibility(View.VISIBLE);
            }
        });


        back2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        SharedPreferences sharedPreferences = getSharedPreferences("TaiKhoan", MODE_PRIVATE);
        String phone = sharedPreferences.getString("phone", "");

        mDatabaseRef.child("History").child(phone).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                String keyword = snapshot.child("keyword").getValue(String.class);
                long timestamp = snapshot.child("timestamp").getValue(Long.class);
                if (!TextUtils.isEmpty(keyword) && timestamp > 0) {
                    SearchHistory searchHistory = new SearchHistory(keyword, timestamp);
                    int existingIndex = -1;
                    for (int i = 0; i < searchHistoryList.size(); i++) {
                        if (searchHistoryList.get(i).getKeyword().equals(keyword)) {
                            existingIndex = i;
                            break;
                        }
                    }

                    if (existingIndex != -1) {

                        searchHistoryList.remove(existingIndex);
                    }

                    searchHistoryList.add(0, searchHistory);

                    Collections.sort(searchHistoryList, new Comparator<SearchHistory>() {
                        @Override
                        public int compare(SearchHistory o1, SearchHistory o2) {
                            return Long.compare(o2.getTimestamp(), o1.getTimestamp());
                        }
                    });

                    if (searchHistoryList.size() > MAX_HISTORY_ITEMS) {
                        searchHistoryList.subList(MAX_HISTORY_ITEMS, searchHistoryList.size()).clear();
                    }

                    historyAdapter.notifyDataSetChanged();
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                searchHistoryList.clear();


                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    String keyword = dataSnapshot.child("keyword").getValue(String.class);
                    if (!TextUtils.isEmpty(keyword) ) {
                        SearchHistory searchHistory = new SearchHistory(keyword);
                        searchHistoryList.add(0, searchHistory);
                    }
                }

                historyAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        historyAdapter.setOnItemClickListener(new SearchHistoryAdapter.ItemClickListener() {
            @Override
            public void onlick(View v, int position, boolean b) {
                String keyword = searchHistoryList.get(position).getKeyword();
                search.setText(keyword);
                SearchHistory selectedHistory = searchHistoryList.get(position);
                searchHistoryList.remove(position);
                searchHistoryList.add(0, selectedHistory);
                mSearch.performClick();
                historyAdapter.notifyDataSetChanged();

            }
        });




        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText searchEditText = findViewById(R.id.txt_search);
                final String keyword = searchEditText.getText().toString().trim();
                ln_search.setVisibility(View.GONE);
                mSearch.setVisibility(View.GONE);
                tv_search.setEms(20);
                ln_listproduct.setVisibility(View.VISIBLE);
                rv_listproduct.setVisibility(View.VISIBLE);
                search.setVisibility(View.GONE);
                tv_search.setVisibility(View.VISIBLE);
                tv_search.setText(keyword);

                if (!TextUtils.isEmpty(keyword)) {
                    final long timestamp = System.currentTimeMillis();
                    mDatabaseRef.child("History").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            int historyCounter = (int) snapshot.getChildrenCount();
                            String searchId = "history" + (historyCounter + 1);
                            SearchHistory searchHistory = new SearchHistory(keyword, timestamp);

                            if (TextUtils.isEmpty(phone)) {

                                Log.d("SharedPreferences", "Phone value is empty. Please handle this case.");
                            } else {
                                mDatabaseRef.child("History").child(phone).child(searchId).setValue(searchHistory);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }
                Query searchQuery = mDatabaseRef.child("Product").orderByChild("Name").startAt(keyword).endAt(keyword+"\uf8ff");
                Log.d("TAG", "Search Query: " + searchQuery.toString());
                FirebaseRecyclerOptions<Product> options = new FirebaseRecyclerOptions.Builder<Product>()
                        .setQuery(searchQuery, Product.class)
                        .build();

                adapter = new FirebaseRecyclerAdapter<Product, ProductViewHolder>(options) {
                    @Override
                    protected void onBindViewHolder(@NonNull ProductViewHolder holder, int position, @NonNull Product model) {


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
                        holder.namepro1.setText(model.getName());
                        holder.pricepro1.setText("đ"+formattedPrice);

                        Glide.with(holder.imagepro1.getContext())
                                .load(model.getImage())
                                .into(holder.imagepro1);


                        holder.setItemClickListener(new ItemClickListener() {
                            @Override
                            public void onlick(View view, int position, boolean isLongClick) {
                                Intent productdetails = new Intent(SearchActivity.this, DetailActivity.class);
                                productdetails.putExtra("ProductId", adapter.getRef(position).getKey());
                                startActivity(productdetails);
                            }
                        });
                    }

                    @NonNull
                    @Override
                    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_product, parent, false);
                        return new ProductViewHolder(view);
                    }
                };

                Log.d("TAG",""+adapter.getItemCount());
                Log.d("TAG", "Kiểm tra phân loại " +keyword);
                rv_listproduct.setAdapter(adapter);
                adapter.startListening();



            }

        });

    }
}
