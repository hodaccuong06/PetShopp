package com.example.petshopp.Domain.fragment.Holder;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopp.Interface.ItemClickListener;
import com.example.petshopp.R;

    public class CategoryViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        public ImageView Image;
        public TextView title;

        private ItemClickListener itemClickListener;
        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            Image = itemView.findViewById(R.id.categoryPic);
            title = itemView.findViewById(R.id.categoryTi);
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


