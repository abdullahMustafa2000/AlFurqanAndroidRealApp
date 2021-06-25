package com.islamicApp.AlFurkan.ui.azkarListActivity;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.islamicApp.AlFurkan.Modules.HadeethIcon;
import com.islamicApp.AlFurkan.R;

import java.util.List;

public class AzkarIconsAdapter extends RecyclerView.Adapter<AzkarIconsAdapter.AzkarIconsViewHolder> {

    List<HadeethIcon> icons;
    onClick onClick;

    public void setOnClick(AzkarIconsAdapter.onClick onClick) {
        this.onClick = onClick;
    }

    public AzkarIconsAdapter(List<HadeethIcon> icons) {
        this.icons = icons;
    }

    @NonNull
    @Override
    public AzkarIconsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AzkarIconsViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.zekr_icon, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final AzkarIconsViewHolder holder, final int position) {

      final HadeethIcon hadeeth = icons.get(position);

      holder.hadeethName.setText(hadeeth.getHadeethName());
      if(onClick!=null){
          holder.itemView.setOnClickListener(v -> onClick.onClick(position,hadeeth, holder.itemView, holder.hadeethName));
      }
    }

    @Override
    public int getItemCount() {
        return icons.size();
    }

    public static class AzkarIconsViewHolder extends RecyclerView.ViewHolder {
        TextView hadeethName;
        public AzkarIconsViewHolder(@NonNull View itemView) {
            super(itemView);
            hadeethName= itemView.findViewById(R.id.hadeeth_name);
        }
    }
    public interface onClick{
        void onClick(int pos, HadeethIcon hadeethIcon, View view, View textView);
    }
}
