package com.example.petshopp.Domain.fragment.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopp.Interface.ItemClickListener;
import com.example.petshopp.R;

public class OrderViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
    public ImageView imageorder;
    public TextView nameproorder, priceproorder, danhanhang;

    private ItemClickListener itemClickListener;

    public OrderViewHolder(@NonNull View itemView) {
        super(itemView);
        imageorder = itemView.findViewById(R.id.orderimage);
        nameproorder = itemView.findViewById(R.id.txtcart2);
        priceproorder = itemView.findViewById(R.id.txtprice2);
        danhanhang = itemView.findViewById(R.id.txtxoa2);

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
