package com.example.petshopp.Admin.Order.Fragment.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.petshopp.Admin.Order.Fragment.OrderAdminDaXacNhanActivity;
import com.example.petshopp.Admin.Order.OrderAdminDetailActivity;
import com.example.petshopp.Domain.CartItem;
import com.example.petshopp.Domain.Order;
import com.example.petshopp.Interface.ItemClickListener;
import com.example.petshopp.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;

public class OrderDaXacNhanAdminAdapter extends RecyclerView.Adapter<OrderDaXacNhanAdminAdapter.MyOrderDetailViewHolder>{
    private Context context;
    private ArrayList<Order> mOrderList;
    private ItemClickListener itemClickListener;

    public void setItemClickListener(ItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }



    public OrderDaXacNhanAdminAdapter(Context context, ArrayList<Order> mOrderList) {
        this.context = context;
        this.mOrderList = mOrderList;
    }

    @NonNull
    @Override
    public OrderDaXacNhanAdminAdapter.MyOrderDetailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.admindonhang, parent, false);
        return new OrderDaXacNhanAdminAdapter.MyOrderDetailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderDaXacNhanAdminAdapter.MyOrderDetailViewHolder holder, int position) {
        Order model = mOrderList.get(position);
        List<CartItem> products = model.getProducts();
        if (products != null && !products.isEmpty()) {
            CartItem firstProduct = products.get(0);
            DecimalFormatSymbols symbols = new DecimalFormatSymbols();
            symbols.setDecimalSeparator('.');
            symbols.setGroupingSeparator('.');
            DecimalFormat decimalFormat = new DecimalFormat("#,###.##", symbols);

            String formattedPrice = "N/A"; // Set a default value

            try {
                double sumTotalAmount = model.getSumtotalAmount();
                formattedPrice = decimalFormat.format(sumTotalAmount);
            } catch (NumberFormatException e) {
                Log.e(FragmentManager.TAG, "Error parsing sumTotalAmount: " + e.getMessage());
            }
            int productCount = model.getProducts().size();
            if(productCount > 1){
                holder.xemthemadmin.setVisibility(View.VISIBLE);
            }
            holder.priceproorderadmindonhangall.setText("đ" + formattedPrice);
            String productPrice = firstProduct.getPrice();
            try {
                double price = Double.parseDouble(productPrice); // Chuyển đổi thành số
                holder.pricealladmin.setText("đ" + decimalFormat.format(price));
            } catch (NumberFormatException e) {
                holder.pricealladmin.setText("đ" + productPrice);

            }
            holder.tinhtrangadmin.setText(model.getTinhTrang());
            holder.tinhtrangadmin.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.oran));

            String image = firstProduct.getImage();
            String productName = firstProduct.getProductName();
            String soluong = firstProduct.getQuantity();
            holder.soluongadmindonhangall.setText("x" + soluong);
            holder.sosanphamadmindonhangall.setText(model.getProducts().size() + " sản phẩm");

            holder.nameproorderadmindonhangall.setText(productName);
            Glide.with(holder.imageadmindonhangall.getContext())
                    .load(image)
                    .into(holder.imageadmindonhangall);

            Log.d("Product Name", productName);
            Log.d("Image URL", image);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onlick(v, position, false);
                    }
                }
            });
            holder.xacnhanadmin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String xacnhanadmin = "Đã giao";
                    model.setTinhTrang(xacnhanadmin);
                    DatabaseReference orderRef = FirebaseDatabase.getInstance().getReference().child("Orders").child(model.getPhone()).child(model.getOrderId());
                    orderRef.child("tinhTrang").setValue(xacnhanadmin)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    notifyDataSetChanged();
                                    Log.d("Firebase", "TinhTrang updated successfully in Firebase");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Firebase", "Error updating TinhTrang: " + e.getMessage());
                                }
                            });
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Order selecteTask = mOrderList.get(position);
                    String orderId = selecteTask.getOrderId();
                    String phone = selecteTask.getPhone();
                    Intent intent = new Intent(context, OrderAdminDaXacNhanActivity.class);
                    intent.putExtra("orderId", orderId);
                    intent.putExtra("phone", phone);
                    Log.d("ChuaXacNhanFragment", "Number of OrderId: " + orderId);
                    context.startActivity(intent);
                }
            });


        }

    }

    @Override
    public int getItemCount() {
        return mOrderList.size();
    }



    class MyOrderDetailViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView imageadmindonhangall;
        private TextView nameproorderadmindonhangall,priceproorderadmindonhangall,soluongadmindonhangall,sosanphamadmindonhangall,pricealladmin,tinhtrangadmin;
        private LinearLayout xemthemadmin;
        private Button xacnhanadmin;
        private ItemClickListener itemClickListener;

        public MyOrderDetailViewHolder(@NonNull View itemView) {
            super(itemView);
            imageadmindonhangall=itemView.findViewById(R.id.imageadmindonhangall);
            nameproorderadmindonhangall=itemView.findViewById(R.id.buynameadmindonhangall);
            priceproorderadmindonhangall=itemView.findViewById(R.id.buypricedonhangalladmin);
            soluongadmindonhangall=itemView.findViewById(R.id.buysoluongadmindonhangall);
            sosanphamadmindonhangall=itemView.findViewById(R.id.sosanphamadmin);
            xemthemadmin=itemView.findViewById(R.id.xemthemadmin);
            pricealladmin=itemView.findViewById(R.id.pricealladmin);
            tinhtrangadmin=itemView.findViewById(R.id.tinhtrangdonhangadmin);
            xacnhanadmin = itemView.findViewById(R.id.btn_xacnhan);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onlick(v, getAdapterPosition(), false);
                    }
                }
            });
        }
        public void setItemClickListener(ItemClickListener itemClickListener) {
            this.itemClickListener = itemClickListener;
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onlick(v,getAdapterPosition(),false);

        }

    }
}

