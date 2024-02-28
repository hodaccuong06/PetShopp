package com.example.petshopp.Domain.fragment.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopp.Interface.ItemClickListener;
import com.example.petshopp.R;

public class PopularViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
    public ImageView imagepro;
    public TextView namepro, pricepro, len, xuong , so;

    private ItemClickListener itemClickListener;

    public PopularViewHolder(@NonNull View itemView) {
        super(itemView);
        imagepro = itemView.findViewById(R.id.product);
        namepro = itemView.findViewById(R.id.name10);
        pricepro = itemView.findViewById(R.id.pricecart);
        len = itemView.findViewById(R.id.len);
        xuong = itemView.findViewById(R.id.xuong);
        so = itemView.findViewById(R.id.soluong);

        itemView.setOnClickListener(this);
    }
    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        itemClickListener.onlick(v,getAdapterPosition(),false);

    }
}
