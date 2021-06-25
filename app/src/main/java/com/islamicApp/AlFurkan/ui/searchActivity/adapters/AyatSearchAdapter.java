package com.islamicApp.AlFurkan.ui.searchActivity.adapters;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.islamicApp.AlFurkan.db.tables.SqliteAyaModel;
import com.islamicApp.AlFurkan.R;

import java.util.List;

public class AyatSearchAdapter extends RecyclerView.Adapter<AyatSearchAdapter.SearchViewHolder> {

    Context context;
    List<SqliteAyaModel> ayaModelList;
    String searchWord;
    OnSearchItemClick onSearchItemClick;

    public AyatSearchAdapter(Context context, List<SqliteAyaModel> ayaModelList) {
        this.context = context;
        this.ayaModelList = ayaModelList;
    }

    public void setOnSearchItemClick(OnSearchItemClick onSearchItemClick) {
        this.onSearchItemClick = onSearchItemClick;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SearchViewHolder(LayoutInflater.from(context)
                .inflate(R.layout.search_itemview, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, final int position) {
        final SqliteAyaModel ayaModel = ayaModelList.get(position);
        String ayaNum = String.format("%d", ayaModel.getAyaNum());
        String pageNum = String.format("%d", ayaModel.getPageNum());
        holder.ayaNumTv.setText(ayaNum);
        holder.suraNameTv.setText(qrnName[ayaModel.getSuraNum() - 1]);
        holder.pageNum.setText(pageNum);

        String ayaNormalText = ayaModel.getAyaNormalText();

        if (searchWord != null && !searchWord.isEmpty()) {
            int startPos = ayaNormalText.indexOf(searchWord);
            int endPos = startPos + searchWord.length();
            if (startPos != -1) {
                Spannable spannable = new SpannableString(ayaNormalText);
                ColorStateList stateList = new ColorStateList(new int[][]{new int[]{}}, new int[]{Color.BLACK});
                TextAppearanceSpan appearanceSpan = new TextAppearanceSpan(null, Typeface.BOLD, -1, stateList, null);
                spannable.setSpan(appearanceSpan, startPos, endPos, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                holder.searchAya.setText(spannable);
            } else {
                holder.searchAya.setText(ayaNormalText);
            }
        } else {
            holder.searchAya.setText(ayaNormalText);
        }

        if (onSearchItemClick != null) {
            holder.itemView.setOnClickListener(view -> onSearchItemClick.onSearchItemClick(ayaModel, qrnName[ayaModel.getSuraNum() - 1]));
        }
    }

    public void notifyList(List<SqliteAyaModel> ayaModelList) {
        this.ayaModelList = ayaModelList;
        notifyDataSetChanged();
    }

    public void notifySearchWord(String searchWord) {
        this.searchWord = searchWord;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return ayaModelList.size();
    }

    public static class SearchViewHolder extends RecyclerView.ViewHolder {
        protected TextView searchAya;
        protected TextView ayaNumTv;
        protected TextView suraNameTv;
        protected TextView pageNum;
        private void initView(View rootView) {
            searchAya = (TextView) rootView.findViewById(R.id.search_aya);
            ayaNumTv = (TextView) rootView.findViewById(R.id.aya_num_tv);
            suraNameTv = (TextView) rootView.findViewById(R.id.sura_name_tv);
            pageNum = (TextView) rootView.findViewById(R.id.search_pageNum);
        }

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            initView(itemView);
        }
    }

    String[] qrnName = {"الفاتحه", "البقرة", "آل عمران", "النساء", "المائدة", "الأنعام", "الأعراف", "الأنفال", "التوبة", "يونس", "هود"
            , "يوسف", "الرعد", "إبراهيم", "الحجر", "النحل", "الإسراء", "الكهف", "مريم", "طه", "الأنبياء", "الحج", "المؤمنون"
            , "النّور", "الفرقان", "الشعراء", "النّمل", "القصص", "العنكبوت", "الرّوم", "لقمان", "السجدة", "الأحزاب", "سبأ"
            , "فاطر", "يس", "الصافات", "ص", "الزمر", "غافر", "فصّلت", "الشورى", "الزخرف", "الدّخان", "الجاثية", "الأحقاف"
            , "محمد", "الفتح", "الحجرات", "ق", "الذاريات", "الطور", "النجم", "القمر", "الرحمن", "الواقعة", "الحديد", "المجادلة"
            , "الحشر", "الممتحنة", "الصف", "الجمعة", "المنافقون", "التغابن", "الطلاق", "التحريم", "الملك", "القلم", "الحاقة", "المعارج"
            , "نوح", "الجن", "المزّمّل", "المدّثر", "القيامة", "الإنسان", "المرسلات", "النبأ", "النازعات", "عبس", "التكوير", "الإنفطار"
            , "المطفّفين", "الإنشقاق", "البروج", "الطارق", "الأعلى", "الغاشية", "الفجر", "البلد", "الشمس", "الليل", "الضحى", "الشرح"
            , "التين", "العلق", "القدر", "البينة", "الزلزلة", "العاديات", "القارعة", "التكاثر", "العصر",
            "الهمزة", "الفيل", "قريش", "الماعون", "الكوثر", "الكافرون", "النصر", "المسد", "الإخلاص", "الفلق", "الناس"};

    public interface OnSearchItemClick {
        void onSearchItemClick(SqliteAyaModel sqliteAyaModel, String suraName);
    }
}
