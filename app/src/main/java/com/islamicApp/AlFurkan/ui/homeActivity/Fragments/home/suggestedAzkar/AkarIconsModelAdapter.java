package com.islamicApp.AlFurkan.ui.homeActivity.Fragments.home.suggestedAzkar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.islamicApp.AlFurkan.R;

public class AkarIconsModelAdapter extends RecyclerView.Adapter<AkarIconsModelAdapter.IconsModelVH> {

    OnModelClick onModelClick;

    public void setOnModelClick(OnModelClick onModelClick) {
        this.onModelClick = onModelClick;
    }

    @NonNull
    @Override
    public IconsModelVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new IconsModelVH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.home_azkar_quran_read, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull IconsModelVH holder, final int position) {
        String title = array[position];
        if (title.length()> 13)
            title = title.substring(0, 13) + "...";
        holder.title.setText(title);
        if (onModelClick != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onModelClick.onClick(position, array[position]);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return array.length;
    }
    public static class IconsModelVH extends RecyclerView.ViewHolder {
        TextView title;
        public IconsModelVH(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.item_title);
        }
    }

    public interface OnModelClick {
        void onClick(int position, String title);
    }

    String[] array ={"أذكار الصباح","أذكار المساء","أذكار بعد السلام من الصلاة"
            ,"أذكار النوم","أذكار من قلق في فراشه ولم ينم"};
}
