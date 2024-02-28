package com.example.petshopp.Domain.fragment.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopp.Interface.ItemClickListener;
import com.example.petshopp.R;

public class BuyViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

    public ImageView imagebuy;
    public TextView pricebuy, namebuy, soluongbuy;

    private ItemClickListener itemClickListener;

    public BuyViewHolder(@NonNull View itemView) {
        super(itemView);
        imagebuy = itemView.findViewById(R.id.imagebuy);
        pricebuy = itemView.findViewById(R.id.buyprice);
        namebuy = itemView.findViewById(R.id.buyname);
        soluongbuy = itemView.findViewById(R.id.buysoluong);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onlick(v,getAdapterPosition(),false);
    }
}
