package com.example.petshopp.Admin.Order.Fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petshopp.Domain.CartItem;
import com.example.petshopp.Interface.ItemClickListener;
import com.example.petshopp.R;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;

public class OrderProductAdminAdapter extends RecyclerView.Adapter<OrderProductAdminAdapter.HolderOrderDetail> {
    private Context mContext;
    private ArrayList<CartItem> mCartItems;

    public OrderProductAdminAdapter(Context mContext, ArrayList<CartItem> mCartItems) {
        this.mContext = mContext;
        this.mCartItems = mCartItems;
    }

    @NonNull
    @Override
    public HolderOrderDetail onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.buy_layout, parent, false);
        return new HolderOrderDetail(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderProductAdminAdapter.HolderOrderDetail holder, int position) {
        CartItem model = mCartItems.get(position);
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        symbols.setGroupingSeparator('.');
        DecimalFormat decimalFormat = new DecimalFormat("#,###.##", symbols);

        String formattedPrice;
        try {
            Number priceNumber = decimalFormat.parse(model.getPrice());
            formattedPrice = decimalFormat.format(priceNumber);
        } catch (ParseException e) {
            formattedPrice = "N/A";
        }
        String image = model.getImage();
        String productName = model.getProductName();
        holder.namebuy1.setText(model.getProductName());
        holder.pricebuy1.setText("đ" + formattedPrice);
        Glide.with(holder.imagebuy1.getContext())
                .load(image)
                .into(holder.imagebuy1);
        holder.soluongbuy1.setText(model.getQuantity());
    }


    @Override
    public int getItemCount() {
        return mCartItems.size();
    }

    class HolderOrderDetail extends RecyclerView.ViewHolder {
        private ImageView imagebuy1;
        private TextView pricebuy1, namebuy1, soluongbuy1;

        private ItemClickListener itemClickListener;

        public HolderOrderDetail(@NonNull View itemView) {
            super(itemView);
            imagebuy1 = itemView.findViewById(R.id.imagebuy);
            pricebuy1 = itemView.findViewById(R.id.buyprice);
            namebuy1 = itemView.findViewById(R.id.buyname);
            soluongbuy1 = itemView.findViewById(R.id.buysoluong);
        }
    }
}