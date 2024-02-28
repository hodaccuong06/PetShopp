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

public class CategoryAdminViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
    public ImageView Image;
    public TextView title;
    public Button suacatgory;
    public Button xoacategory;

    private ItemClickListener itemClickListener;
    public CategoryAdminViewHolder(@NonNull View itemView) {
        super(itemView);
        Image = itemView.findViewById(R.id.imagecategory);
        title = itemView.findViewById(R.id.namecategory);
        suacatgory = itemView.findViewById(R.id.sua);
        xoacategory = itemView.findViewById(R.id.xoa);
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


