package com.islamicApp.AlFurkan.ui.homeActivity.Fragments.home.suggestedQuran;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.islamicApp.AlFurkan.R;

public class QuranIconsModelAdapter extends RecyclerView.Adapter<QuranIconsModelAdapter.QuranIconsVH> {

    OnQuranIconClick onQuranIconClick;
    Context context;
    String[] qrnName;
    public QuranIconsModelAdapter(Context context) {
        this.context = context;
        qrnName = context.getResources().getStringArray(R.array.swar_names);
    }

    public void setOnQuranIconClick(OnQuranIconClick onQuranIconClick) {
        this.onQuranIconClick = onQuranIconClick;
    }

    @NonNull
    @Override
    public QuranIconsVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QuranIconsVH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_azkar_quran_read, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QuranIconsVH holder, final int position) {
        final String title = qrnName[position];
        holder.title.setText(title);
        if (onQuranIconClick!=null) {
            holder.itemView.setOnClickListener(v -> onQuranIconClick.onIconClick(position, title));
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    public static class QuranIconsVH extends RecyclerView.ViewHolder {
        TextView title;
        public QuranIconsVH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
        }
    }

    public interface OnQuranIconClick {
        void onIconClick(int position, String title);
    }
}
