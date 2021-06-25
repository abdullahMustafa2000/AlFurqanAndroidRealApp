package com.islamicApp.AlFurkan.ui.homeActivity.Fragments.home.suggestedListenQuran;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.islamicApp.AlFurkan.Modules.QuranListenSuggestionsModel;
import com.islamicApp.AlFurkan.R;

import java.util.List;

public class QuranHomeListenAdapter extends RecyclerView.Adapter<QuranHomeListenAdapter.QuranListenVH> {

    List<QuranListenSuggestionsModel> modelList;
    OnSuggestionClick onSuggestionClick;

    public QuranHomeListenAdapter(List<QuranListenSuggestionsModel> newList) {
        this.modelList = newList;
        notifyDataSetChanged();
    }

    public void setOnSuggestionClick(OnSuggestionClick onSuggestionClick) {
        this.onSuggestionClick = onSuggestionClick;
    }

    @NonNull
    @Override
    public QuranListenVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new QuranListenVH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quran_sugg_litening_shape, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull QuranListenVH holder, final int position) {
        final QuranListenSuggestionsModel homeModel = modelList.get(position);
        holder.suraName_tv.setText(homeModel.getSuraName());
        holder.reciterName_tv.setText(homeModel.getQareeName());
        if (onSuggestionClick!=null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onSuggestionClick.onItemClicked(homeModel, position);
                }
            });
        }
    }

    public void setList(List<QuranListenSuggestionsModel> modelList) {
        this.modelList = modelList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return 10;
    }

    public static class QuranListenVH extends RecyclerView.ViewHolder {
        TextView suraName_tv, reciterName_tv;
        public QuranListenVH(@NonNull View itemView) {
            super(itemView);
            suraName_tv = itemView.findViewById(R.id.listen_suraName);
            reciterName_tv = itemView.findViewById(R.id.listen_reciterName);
        }
    }

    public interface OnSuggestionClick {
        void onItemClicked(QuranListenSuggestionsModel homeModel, int position);
    }
}
