package com.example.petshopp.Admin.ViewHolder;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopp.Interface.ItemClickListener;
import com.example.petshopp.R;

public class ProductAdminViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

        public ImageView Image;
        public TextView title;
        public TextView price;
        public Button suaproduct;
        public Button xoapproduct;

        private ItemClickListener itemClickListener;
        public ProductAdminViewHolder(@NonNull View itemView) {
            super(itemView);
            Image = itemView.findViewById(R.id.imageproduct);
            title = itemView.findViewById(R.id.nameproduct);
            suaproduct = itemView.findViewById(R.id.suaproduct);
            xoapproduct = itemView.findViewById(R.id.xoaproduct);
            price = itemView.findViewById(R.id.priceproduct);
            title.setSelected(true);
            title.setSingleLine(true);
            title.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            title.setMarqueeRepeatLimit(-1);
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



