package com.islamicApp.AlFurkan.ui.ayatActivity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.islamicApp.AlFurkan.db.tables.SqliteAyaModel;
import com.islamicApp.AlFurkan.R;

import java.lang.reflect.Method;
import java.util.List;


public class AyatAdapter extends RecyclerView.Adapter<AyatAdapter.AyaViewHolder> {
    
    Activity context;
    List<SqliteAyaModel> suraAyat;
    List<SqliteAyaModel> allMushaf;
    List<Integer> markedAyat;
    AyatViewModel ayatViewModel;

    public AyatAdapter(AppCompatActivity context, int suraIndex, AyatViewModel ayatViewModel) {
        this.context = context;
        this.ayatViewModel = ayatViewModel;
        ayatViewModel.getSuraAyat(suraIndex);
        ayatViewModel.suraMutableLiveData.observe(context, sqliteAyaModels -> {
            suraAyat = sqliteAyaModels;
            notifyDataSetChanged();
        });
        ayatViewModel.getAyatFromSQl();
        ayatViewModel.allMushafMutableLiveData.observe(context, sqliteAyaModels -> {
            allMushaf = sqliteAyaModels;
            notifyDataSetChanged();
        });
    }

    public void setMarkedAyat(List<Integer> markedAyat) {
        this.markedAyat = markedAyat;
        notifyDataSetChanged();
    }

    String suraName;
    int ayaPos;
    boolean firstOpen = true;
    public String getSuraName() {
        return suraName;
    }

    public void setSuraName(String suraName) {
        this.suraName = suraName;
        notifyDataSetChanged();
    }

    private int getAyaPos() {
        return ayaPos;
    }

    public void setAyaPos(int ayaPos) {
        this.ayaPos = ayaPos;
    }

    @NonNull
    @Override
    public AyaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new AyaViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.one_aya_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final AyaViewHolder holder, final int position) {
        final SqliteAyaModel ayaModel = suraAyat.get(position);

        holder.ayaTv.setText(ayaModel.getAyaTashkelText());

        final String ayaNum = String.format("%d", ayaModel.getAyaNum());
        holder.ayaNumTv.setText(ayaNum);

        final String pageNum = String.format("%d", ayaModel.getPageNum());
        int ayaIndex = ayaModel.getAyaId();
        setPageNumber(ayaIndex, holder, pageNum);

        holder.itemView.setOnLongClickListener(v -> {
            showPup(holder, ayaModel.getAyaNum(), ayaModel, ayatViewModel, context);
            return true;
        });

        if (ayaModel.getAyaNum() == getAyaPos() + 1 && firstOpen) {
            startAnimation(context, holder, true, ayaModel);
            firstOpen = false;
        } else {
            startAnimation(context, holder, false, ayaModel);
        }

        // marked ayat
        findMarkedAyat(ayaModel, holder);
    }

    private void findMarkedAyat(SqliteAyaModel ayaModel, AyaViewHolder holder) {
        if (markedAyat != null) {
            if (markedAyat.contains(ayaModel.getAyaId())) {
                holder.itemView.setBackgroundColor(Color.parseColor("#805CD38C"));
            } else
                holder.itemView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }
    }

    @Override
    public int getItemCount() {
        if (suraAyat != null)
        return suraAyat.size();
        return 0;
    }

    public static class AyaViewHolder extends RecyclerView.ViewHolder {
        private final View pageNumView;
        TextView ayaTv, pageNum, ayaNumTv;
        public AyaViewHolder(@NonNull View itemView) {
            super(itemView);
            ayaTv =itemView.findViewById(R.id.aya_txt);
            pageNumView = itemView.findViewById(R.id.pageNumView);
            pageNum = itemView.findViewById(R.id.pageNum);
            ayaNumTv = itemView.findViewById(R.id.ayaNum_tv);
        }
    }

    private void setPageNumber(int ayaIndex, AyaViewHolder holder, String pageNum) {
        if (allMushaf != null && ayaIndex <= allMushaf.size() - 1) {
            if (allMushaf.get(ayaIndex - 1).getPageNum() != allMushaf.get(ayaIndex).getPageNum()) {
                holder.pageNumView.setVisibility(View.VISIBLE);
                holder.pageNum.setText(pageNum);
            } else {
                holder.pageNumView.setVisibility(View.GONE);
                holder.pageNum.setText("");
            }
        } else {
            holder.pageNumView.setVisibility(View.VISIBLE);
            holder.pageNum.setText(pageNum);
        }
    }

    private void showPup(AyaViewHolder holder, int ayaNum, SqliteAyaModel ayaModel, AyatViewModel ayatViewModel, Activity context) {
        PopupMenu popup = new PopupMenu(context, holder.ayaTv);

        try {
            Method method = popup.getMenu().getClass().getDeclaredMethod("setOptionalIconsVisible", boolean.class);
            method.setAccessible(true);
            method.invoke(popup.getMenu(), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //inflating menu from xml resource
        popup.inflate(R.menu.aya_options);

        //adding click listener
        popup.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.copy_aya_txt) {
                copyAya(suraName, ayaNum, ayaModel);
                return true;

            } else if (item.getItemId() == R.id.mark_aya) {
                addedMarkedAya(ayaModel, ayatViewModel, context);
                return true;
            }
            return false;
        });
        //displaying the popup
        popup.show();
    }

    private void addedMarkedAya(SqliteAyaModel ayaModel, AyatViewModel ayatViewModel, Activity context) {
        ayatViewModel.addAyaToDb(ayaModel);
    }

    private void copyAya(String suraName, int ayaNum, SqliteAyaModel ayaModel) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("aya_txt", "\"" + ayaModel.getAyaTashkelText() + "\""
                + "\n" + "(" + " ايه " + ayaNum + " , من سورة " + suraName + " )");
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(context, R.string.copy_aya_tost, Toast.LENGTH_SHORT).show();
    }

    private void startAnimation(Context context, final AyaViewHolder holder, final boolean doWhen, SqliteAyaModel ayaModel) {
        int colorFrom = context.getResources().getColor(R.color.heavy_gray_bg);
        int colorTo;
        if (markedAyat!=null && markedAyat.contains(ayaModel.getAyaId()))
            colorTo = context.getResources().getColor(R.color.lightBrown);
        else
            colorTo = context.getResources().getColor(R.color.white);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(3000); // milliseconds
        colorAnimation.addUpdateListener(animator -> {
            if (doWhen)
                holder.itemView.setBackgroundColor((int) animator.getAnimatedValue());
        });
        colorAnimation.start();
    }

}
