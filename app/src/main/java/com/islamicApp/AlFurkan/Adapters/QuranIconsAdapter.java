package com.islamicApp.AlFurkan.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.islamicApp.AlFurkan.Modules.QuranIcons;
import com.islamicApp.AlFurkan.R;

import java.util.List;

public class QuranIconsAdapter extends RecyclerView.Adapter<QuranIconsAdapter.viewHolder> {


    List<QuranIcons>list;
    OnSuraClicked onClicked;

    public QuranIconsAdapter(List<QuranIcons> list) {
        this.list = list;
    }

    public void setOnClicked(QuranIconsAdapter.OnSuraClicked onClicked) {
        this.onClicked = onClicked;
    }

    @NonNull
    @Override
    public viewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.quran_icons,parent,false);
        return new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull viewHolder holder, final int position) {
        final QuranIcons quranIcons = list.get(position);
        holder.suraName.setText(quranIcons.getSuraName());
        if(onClicked!=null) {
            holder.itemView.setOnClickListener(v -> onClicked.onItemClick(quranIcons.getSuraPosition(), quranIcons));
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }
    static class viewHolder extends RecyclerView.ViewHolder{
        TextView suraName, pageNum;
        public viewHolder(@NonNull View itemView) {
            super(itemView);
            suraName=itemView.findViewById(R.id.suraname_icon);
            pageNum=itemView.findViewById(R.id.page_number);
        }
    }
    public interface OnSuraClicked {
        void onItemClick(int postion, QuranIcons suraName);
    }
}
