package com.islamicApp.AlFurkan.ui.searchActivity.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.islamicApp.AlFurkan.Modules.RecitersItem;
import com.islamicApp.AlFurkan.R;

import java.util.List;

public class RecitersSearchAdapter extends RecyclerView.Adapter<RecitersSearchAdapter.RecitersVH> {

    List<RecitersItem> recitersItemList;

    public RecitersSearchAdapter(List<RecitersItem> recitersItemList) {
        this.recitersItemList = recitersItemList;
        notifyDataSetChanged();
    }

    OnReciterClicked onItemClicked;

    public void setOnItemClicked(OnReciterClicked onItemClicked) {
        this.onItemClicked = onItemClicked;
    }

    @NonNull
    @Override
    public RecitersVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new RecitersVH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reciter_search, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecitersVH holder, final int position) {
        final RecitersItem item = recitersItemList.get(position);
        holder.reciterName_tv.setText(item.getName());
        holder.RewayaName_tv.setText(item.getRewaya());
        if (onItemClicked!=null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClicked.onReciterClicked(position, item);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return recitersItemList.size();
    }

    public static class RecitersVH extends RecyclerView.ViewHolder {
        TextView reciterName_tv, RewayaName_tv;
        public RecitersVH(@NonNull View itemView) {
            super(itemView);
            reciterName_tv = itemView.findViewById(R.id.reciter_name_tv);
            RewayaName_tv = itemView.findViewById(R.id.rewaya_name_tv);
        }
    }

    public interface OnReciterClicked {
        void onReciterClicked(int position, RecitersItem recitersItem);
    }
}
