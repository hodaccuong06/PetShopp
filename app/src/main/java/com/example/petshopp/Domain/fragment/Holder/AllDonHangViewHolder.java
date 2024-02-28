package com.example.petshopp.Domain.fragment.Holder;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopp.Interface.ItemClickListener;
import com.example.petshopp.R;

public class AllDonHangViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
    public ImageView imagedonhangall;
    public TextView nameproorderdonhangall, priceproorderdonhangall, soluongdonhangall, sosanphamdonhangall, priceall, tinhtrang;
    public LinearLayout xemthem;

    private ItemClickListener itemClickListener;
    public AllDonHangViewHolder(@NonNull View itemView) {
        super(itemView);
        imagedonhangall = itemView.findViewById(R.id.imagedonhangall);
        nameproorderdonhangall = itemView.findViewById(R.id.buynamedonhangall);
        priceproorderdonhangall = itemView.findViewById(R.id.buypricedonhangall);
        soluongdonhangall = itemView.findViewById(R.id.buysoluongdonhangall);
        sosanphamdonhangall = itemView.findViewById(R.id.sosanpham);
        xemthem = itemView.findViewById(R.id.xemthem);
        priceall = itemView.findViewById(R.id.priceall);
        tinhtrang = itemView.findViewById(R.id.tinhtrangdonhang);

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
