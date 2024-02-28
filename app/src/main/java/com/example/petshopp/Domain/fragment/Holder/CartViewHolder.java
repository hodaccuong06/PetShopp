package com.example.petshopp.Domain.fragment.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopp.Interface.ItemClickListener;
import com.example.petshopp.R;

public class CartViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

    public ImageView imagecart1;
    public TextView txtprice1, txtcart, len, xuong, soluong, txtxoa;

    private ItemClickListener itemClickListener;

    public CartViewHolder(@NonNull View itemView) {
        super(itemView);
        imagecart1 = itemView.findViewById(R.id.imagecart1);
        txtprice1 = itemView.findViewById(R.id.txtprice1);
        txtcart = itemView.findViewById(R.id.txtcart);
        len = itemView.findViewById(R.id.len);
        xuong = itemView.findViewById(R.id.xuong);
        soluong = itemView.findViewById(R.id.soluong);
        itemView.setOnClickListener(this);
        len.setOnClickListener(this);
        xuong.setOnClickListener(this);
        txtxoa = itemView.findViewById(R.id.txtxoa);


    }



    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onlick(v,getAdapterPosition(),false);
    }
    }

