package com.example.petshopp.Domain.fragment.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopp.Interface.ItemClickListener;
import com.example.petshopp.R;

public class TopViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public ImageView imagepro2;
    public TextView namepro2, pricepro2;
    private ItemClickListener itemClickListener;
    public TopViewHolder(@NonNull View itemView) {

        super(itemView);

        imagepro2 = itemView.findViewById(R.id.product2);
        namepro2 = itemView.findViewById(R.id.name2);
        pricepro2 = itemView.findViewById(R.id.price2);
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
