package com.islamicApp.AlFurkan.ui.homeActivity.Fragments.listening.onlineQuran;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.islamicApp.AlFurkan.Modules.OnlineSwarListen;
import com.islamicApp.AlFurkan.R;

import java.util.ArrayList;
import java.util.List;

public class ListeningSwarAdapter extends RecyclerView.Adapter<ListeningSwarAdapter.ViewHolder> {

    List<OnlineSwarListen> mList;
    OnClicked onClicked;
    String suraLink;

    public String getSuraLink() {
        return suraLink;
    }

    public void setSuraLink(String suraLink) {
        this.suraLink = suraLink;
    }

    public void setOnClicked(OnClicked onClicked) {
        this.onClicked = onClicked;
    }
    Context context;
    public ListeningSwarAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.quran_sound_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        final OnlineSwarListen suraModel = mList.get(position);
        holder.suraName.setText("سورة "+ mList.get(position).getSuraName()+ " ");
        if (onClicked != null){
            holder.audioItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onClicked.onsuraClick(position, suraModel);
                }
            });
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onClicked.onSuraLongClick(position, suraModel);
                    return false;
                }
            });
        }

        if (suraModel.getSuraLink().equals(getSuraLink())) {
            holder.suraName.setTextColor(Color.parseColor("#FFFFFF"));
            holder.itemView.setBackground(ResourcesCompat.getDrawable(context.getResources(),
                    R.drawable.playing_background_shape, null));
            holder.playing_img.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.listening_icon, null));
        } else {
            holder.suraName.setTextColor(Color.parseColor("#707070"));
            holder.itemView.setBackground(null);
            holder.playing_img.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.sound_off_icon, null));
        }

    }

    public void notifyChanges(ArrayList<OnlineSwarListen> suras){
        this.mList = suras;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        View audioItem;
        TextView suraName;
        ImageView playing_img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            suraName = itemView.findViewById(R.id.listening_sura_name);
            audioItem = itemView.findViewById(R.id.audio_item);
            playing_img = itemView.findViewById(R.id.play_img);
        }
    }

    public interface OnClicked{
        void onsuraClick(int position, OnlineSwarListen onlineSwarListen);
        void onSuraLongClick(int position, OnlineSwarListen onlineSwarListen);
    }

}
