package com.islamicApp.AlFurkan.ui.swarListActivity.fragments.markedAyat;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.islamicApp.AlFurkan.Classes.StaticStrings;
import com.islamicApp.AlFurkan.R;
import com.islamicApp.AlFurkan.db.tables.SqliteAyaModel;
import com.islamicApp.AlFurkan.ui.ayatActivity.AyatActivity;

import java.util.List;

public class MarkedAyatAdapter extends RecyclerView.Adapter<MarkedAyatAdapter.MarkedAyatViewHolder> {

    private List<SqliteAyaModel> ayatList;
    private Context context;
    private String[] swarNames;

    public MarkedAyatAdapter(List<SqliteAyaModel> ayatList, Context context) {
        this.ayatList = ayatList;
        this.context = context;
        swarNames = context.getResources().getStringArray(R.array.swar_names);
    }

    @NonNull
    @Override
    public MarkedAyatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MarkedAyatViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.item_marked_aya, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MarkedAyatViewHolder holder, int position) {
        SqliteAyaModel ayaModel = ayatList.get(position);
        String suraName = swarNames[ayaModel.getSuraNum() - 1];
        holder.markedSuraTv.setText(suraName);
        String aya = ayaModel.getAyaTashkelText();
        if (ayaModel.getAyaNormalText().length() > 30)
            aya = ayaModel.getAyaTashkelText().substring(0, 30) + "...";

        holder.markedAyaTv.setText(aya);

        holder.itemView.setOnClickListener(v -> context.startActivity(new Intent(context, AyatActivity.class)
                .putExtra(StaticStrings.INTENT_SURA_POSITION, ayaModel.getSuraNum() - 1)
                .putExtra(StaticStrings.INTENT_SURA_NAME, suraName)
                .putExtra(StaticStrings.INTENT_AYA_INDEX, ayaModel.getAyaNum() - 1)));
    }

    @Override
    public int getItemCount() {
        return ayatList.size();
    }

    public static class MarkedAyatViewHolder extends RecyclerView.ViewHolder {
        TextView markedAyaTv, markedSuraTv;
        public MarkedAyatViewHolder(@NonNull View itemView) {
            super(itemView);
            markedAyaTv = itemView.findViewById(R.id.marked_ayah_tv);
            markedSuraTv = itemView.findViewById(R.id.marked_surah_name_tv);
        }
    }
}
