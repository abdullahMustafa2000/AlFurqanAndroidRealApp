package com.islamicApp.AlFurkan.ui.homeActivity.Fragments.listening.onlineQuran;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.islamicApp.AlFurkan.Modules.RecitersItem;
import com.islamicApp.AlFurkan.R;

import java.util.List;

public class RecitersSpinnerAdapter extends BaseAdapter {

    List<RecitersItem> recitersItemList;
    Activity activity;
    LayoutInflater inflater;
    int selectedPosition;

    public RecitersSpinnerAdapter(List<RecitersItem> recitersItems, Activity activity) {
        this.recitersItemList = recitersItems;
        this.activity = activity;
        try {
            this.inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }catch (Exception e){
            //Log.e(TAG, "SpinnerAdapter: "+ e.getMessage(), new Throwable());
        }

    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public int getCount() {
        return recitersItemList.size();
    }

    @Override
    public Object getItem(int position) {
        return recitersItemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null){
            convertView = inflater.inflate(R.layout.spinner_raw_veiw, parent,false);
            viewHolder = new ViewHolder();
            viewHolder.qareeName = convertView.findViewById(R.id.qaree_name);
            viewHolder.rewayaName = convertView.findViewById(R.id.rewaya_name);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.qareeName.setText(recitersItemList.get(position).getName());
        viewHolder.rewayaName.setText(recitersItemList.get(position).getRewaya());
        return convertView;
    }

    static class ViewHolder{
        TextView qareeName, rewayaName;
    }
}
