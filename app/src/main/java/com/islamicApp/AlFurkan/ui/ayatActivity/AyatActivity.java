package com.islamicApp.AlFurkan.ui.ayatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.islamicApp.AlFurkan.Classes.QuranDBOpener;
import com.islamicApp.AlFurkan.Classes.StaticStrings;
import com.islamicApp.AlFurkan.db.tables.SqliteAyaModel;
import com.islamicApp.AlFurkan.R;

import java.util.List;

import maes.tech.intentanim.CustomIntent;

public class AyatActivity extends AppCompatActivity {

    protected TextView suraNameView;
    protected RecyclerView ayatRecyclerview;
    protected TextView basmalaTv;

    //to get HomeFragment intent data
    int suraPos;
    String suraname;
    //to set recyclerView data
    AyatAdapter quranAyaAdapter;
    Animation downAnim;

    AyatViewModel ayatViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_ayat_view);
        initView();
        initViewModel();
        //get el sura Data from QuranFragment
        setSuraData();
        suraBasmala();
    }

    private void suraBasmala() {
        if (suraPos == 0 || suraPos == 8) {
            basmalaTv.setVisibility(View.GONE);
        } else {
            basmalaTv.setVisibility(View.VISIBLE);
        }
    }

    private void initViewModel() {
        ayatViewModel = new AyatViewModel(getApplication());
    }

    private void setSuraData() {
        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        suraname = bundle.getString(StaticStrings.INTENT_SURA_NAME);
        suraNameView.setText(suraname);
        suraPos = bundle.getInt(StaticStrings.INTENT_SURA_POSITION, 0);
        int ayaPos = bundle.getInt(StaticStrings.INTENT_AYA_INDEX, -1);
        setAyatRecyclerview(ayaPos, suraPos);
    }


    public void setAyatRecyclerview(int ayaPos, int suraPos) {
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        quranAyaAdapter = new AyatAdapter(this,
                suraPos, ayatViewModel);
        ayatViewModel.getMarkedAyat().observe(this, sqliteAyaModels -> {
            if (sqliteAyaModels != null) {
                quranAyaAdapter.setMarkedAyat(sqliteAyaModels);
                quranAyaAdapter.notifyDataSetChanged();
            }
        });
        ayatViewModel.addedMarkedAyaMutableLiveData.observe(this, added -> {
            if (added)
                Toast.makeText(this, getString(R.string.aya_marked), Toast.LENGTH_SHORT).show();
        });
        quranAyaAdapter.setSuraName(suraname);
        quranAyaAdapter.setAyaPos(ayaPos);

        ayatRecyclerview.setLayoutManager(layoutManager);
        ayatRecyclerview.setAdapter(quranAyaAdapter);
        ayatRecyclerview.scrollToPosition(ayaPos);

        if (basmalaTv.getVisibility() != View.GONE) {
            setOnScrollAnimation(layoutManager);
        }
    }


    private void setOnScrollAnimation(LinearLayoutManager layoutManager) {
        ayatRecyclerview.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(ayatRecyclerview, newState);
                int firstVisible = layoutManager.findFirstVisibleItemPosition();
                downAnim = AnimationUtils.loadAnimation(AyatActivity.this, R.anim.basmala_anim);
                if (firstVisible > 10) {
                    basmalaTv.setVisibility(View.GONE);
                    suraNameView.setVisibility(View.GONE);
                } else if (firstVisible == 0) {
                    basmalaTv.setAnimation(downAnim);
                    basmalaTv.setVisibility(View.VISIBLE);
                    suraNameView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initView() {
        suraNameView = findViewById(R.id.sura_name_view);
        ayatRecyclerview = findViewById(R.id.ayat_recyclerview);
        basmalaTv = findViewById(R.id.basmala_tv);
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "right-to-left");
    }
}