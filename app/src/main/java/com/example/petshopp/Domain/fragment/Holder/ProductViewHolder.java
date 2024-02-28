package com.example.petshopp.Domain.fragment.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopp.Interface.ItemClickListener;
import com.example.petshopp.R;

public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public ImageView imagepro1;
        public TextView namepro1, pricepro1;
        private ItemClickListener itemClickListener;
        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            imagepro1 = itemView.findViewById(R.id.product);
            namepro1 = itemView.findViewById(R.id.name10);
            pricepro1 = itemView.findViewById(R.id.pricecart);
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



