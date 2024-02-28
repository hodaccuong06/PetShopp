package com.example.petshopp.Domain.fragment.Holder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.petshopp.R;
import com.example.petshopp.Domain.SearchHistory;

import java.util.List;

public class SearchHistoryAdapter extends RecyclerView.Adapter<SearchHistoryAdapter.ViewHolder> {
    private List<SearchHistory> searchHistoryList;
    private ItemClickListener itemClickListener;

    public SearchHistoryAdapter(List<SearchHistory> searchHistoryList) {
        this.searchHistoryList = searchHistoryList;
    }

    public void setOnItemClickListener(ItemClickListener listener) {
        this.itemClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SearchHistory searchHistory = searchHistoryList.get(position);
        holder.bindData(searchHistory);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClickListener != null) {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        itemClickListener.onlick(v, position, false);
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return searchHistoryList.size();
    }

    public interface ItemClickListener {

        void onlick(View v, int position, boolean b);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView txtKeyword;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtKeyword = itemView.findViewById(R.id.txt_keyword);
            itemView.setOnClickListener(this);
        }

        void bindData(SearchHistory searchHistory) {
            txtKeyword.setText(searchHistory.getKeyword());
        }

        @Override
        public void onClick(View v) {
            itemClickListener.onlick(v,getAdapterPosition(),false);
        }
    }
}
