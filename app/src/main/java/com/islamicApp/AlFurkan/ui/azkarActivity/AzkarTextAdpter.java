package com.islamicApp.AlFurkan.ui.azkarActivity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.islamicApp.AlFurkan.Modules.ZekrModel;
import com.islamicApp.AlFurkan.R;

import java.util.ArrayList;

public class AzkarTextAdpter extends RecyclerView.Adapter<AzkarTextAdpter.AzkarViewHolder>{

    ArrayList<ZekrModel> azkarLines;
    Context context;

    public AzkarTextAdpter(ArrayList<ZekrModel> azkarLines, Context context){
        this.azkarLines = azkarLines;
        this.context = context;
    }

    @NonNull
    @Override
    public AzkarViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AzkarViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.zekr_line, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final AzkarViewHolder holder, final int position) {
        final ZekrModel zekrModel = azkarLines.get(position);
        holder.zekrtxt.setText(zekrModel.getZekrLine());
    }

    @Override
    public int getItemCount() {
        return azkarLines.size();
    }

    public static class AzkarViewHolder extends RecyclerView.ViewHolder{
        TextView zekrtxt;

        public AzkarViewHolder(@NonNull View itemView) {
            super(itemView);
            zekrtxt = itemView.findViewById(R.id.zekr_line);
        }
    }

}
