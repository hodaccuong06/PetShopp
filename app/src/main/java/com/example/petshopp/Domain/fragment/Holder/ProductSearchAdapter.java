package com.example.petshopp.Domain.fragment.Holder;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petshopp.Activity.DetailActivity;
import com.example.petshopp.Domain.Product;
import com.example.petshopp.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class ProductSearchAdapter extends RecyclerView.Adapter<ProductSearchAdapter.MyViewHolder> {
    private List<Product> productList;
    private Context context;

    public ProductSearchAdapter(ArrayList<Product> productList, Context context) {

        this.productList = productList;
        this.context = context;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.categories_product, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Product product = productList.get(position);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##", symbols);

        String formattedPrice;
        try {
            Number priceNumber = decimalFormat.parse(product.getPrice());
            formattedPrice = decimalFormat.format(priceNumber);
        } catch (ParseException e) {
            formattedPrice = "N/A";
        }

        holder.namepro1.setText(product.getName());
        holder.pricepro1.setText("đ" + formattedPrice);
        Glide.with(context)
                .load(product.getImage())
                .into(holder.imagepro1);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productDetailsIntent = new Intent(context, DetailActivity.class);
                productDetailsIntent.putExtra("ProductId", product.getMenuId());
                context.startActivity(productDetailsIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }
    public  void searchProductList(ArrayList<Product>searchList){
        productList = searchList;
        notifyDataSetChanged();
    }



    static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView imagepro1;
        TextView namepro1, pricepro1;

        MyViewHolder(View itemView) {
            super(itemView);
            imagepro1 = itemView.findViewById(R.id.product);
            namepro1 = itemView.findViewById(R.id.name10);
            pricepro1 = itemView.findViewById(R.id.pricecart);
        }
    }
}
