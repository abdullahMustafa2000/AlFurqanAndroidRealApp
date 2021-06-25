package com.islamicApp.AlFurkan.ui.homeActivity.Fragments.home.suggestedListenAzkar;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.islamicApp.AlFurkan.Classes.InternetConnection;
import com.islamicApp.AlFurkan.Modules.AzkarListenModel;
import com.islamicApp.AlFurkan.R;

import java.util.List;

public class AzkarListenHomeAdapter extends RecyclerView.Adapter<AzkarListenHomeAdapter.AzkarHomeListenVH> {

    List<AzkarListenModel> listenModels;
    OnZekrListenClicked onZekrListenClicked;
    Context context;
    int position = -1;
    public AzkarListenHomeAdapter(List<AzkarListenModel> listenModels, Context context) {
        this.listenModels = listenModels;
        this.context = context;
    }

    public void setOnZekrListenClicked(OnZekrListenClicked onZekrListenClicked) {
        this.onZekrListenClicked = onZekrListenClicked;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AzkarHomeListenVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AzkarHomeListenVH(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.listening_azkar_shape, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull AzkarHomeListenVH holder, final int position) {
        final AzkarListenModel listenModel = listenModels.get(position);
        holder.zekrName_tv.setText(listenModel.getZekrName());
        if (onZekrListenClicked!=null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onZekrListenClicked.onZekrClick(listenModel, position);
                    if (new InternetConnection(context).internetIsConnected())
                    setPosition(position);
                    notifyDataSetChanged();
                }
            });
        }
        if (position == getPosition())
            holder.itemView.setBackgroundColor(Color.parseColor("#585858"));
        else holder.itemView.setBackgroundResource(R.drawable.azkar_listen_shape);
    }

    @Override
    public int getItemCount() {
        return listenModels.size();
    }

    public static class AzkarHomeListenVH extends RecyclerView.ViewHolder {
        TextView zekrName_tv;
        public AzkarHomeListenVH(@NonNull View itemView) {
            super(itemView);
            zekrName_tv = itemView.findViewById(R.id.zekr_name);
        }
    }

    public interface OnZekrListenClicked {
        void onZekrClick(AzkarListenModel listenModel, int position);
    }
}
