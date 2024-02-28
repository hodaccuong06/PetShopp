package com.example.petshopp.Domain.fragment.Holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopp.Interface.ItemClickListener;
import com.example.petshopp.R;

public class AddressViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{

    public TextView nameaddress, phoneaddres, strestaddress, editaddress, editxoa;
    public RadioButton radio;
    public LinearLayout laydulieu;

    private ItemClickListener itemClickListener;
    private boolean isSelected = false;

    public AddressViewHolder(@NonNull View itemView) {
        super(itemView);
        nameaddress = itemView.findViewById(R.id.nameaddres);
        phoneaddres = itemView.findViewById(R.id.phoneaddress);
        strestaddress = itemView.findViewById(R.id.strestaddress);
        editaddress = itemView.findViewById(R.id.editadress);
        editxoa = itemView.findViewById(R.id.editxoa);
        radio = itemView.findViewById(R.id.radioButton);
        laydulieu = itemView.findViewById(R.id.laydulieu);
        itemView.setOnClickListener(this);
    }

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onClick(View v) {
        if (itemClickListener != null) {
            itemClickListener.onlick(v, getAdapterPosition(), false);
        }
    }

}
