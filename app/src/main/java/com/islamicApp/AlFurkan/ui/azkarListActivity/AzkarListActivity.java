package com.islamicApp.AlFurkan.ui.azkarListActivity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.islamicApp.AlFurkan.Classes.StaticStrings;
import com.islamicApp.AlFurkan.Modules.HadeethIcon;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.islamicApp.AlFurkan.R;
import com.islamicApp.AlFurkan.ui.azkarActivity.AzkarActivity;

import java.util.ArrayList;
import java.util.List;

import maes.tech.intentanim.CustomIntent;

public class AzkarListActivity extends AppCompatActivity {

    protected RecyclerView azkarRecyclerview;
    AzkarIconsAdapter azkarAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_azkar_read_list_u_i);
        initView();
        getBannerAdMob();
        showRecyclerview();
    }

    private void showRecyclerview() {
        azkarAdapter = new AzkarIconsAdapter(setData());
        azkarRecyclerview.setLayoutManager(new LinearLayoutManager(this));
        azkarRecyclerview.setAdapter(azkarAdapter);

        azkarAdapter.setOnClick((pos, hadeethIcon, view, textView) -> {
            Intent intent = new Intent(AzkarListActivity.this, AzkarActivity.class);
            intent.putExtra(StaticStrings.INTENT_HDEETH_NAME, hadeethIcon.getHadeethName());
            intent.putExtra(StaticStrings.INTENT_HDEETH_POSITION, pos);
            startActivity(intent);
            CustomIntent.customType(AzkarListActivity.this, "right-to-left");
        });
    }

    private List<HadeethIcon> setData() {
        ArrayList<HadeethIcon> icons = new ArrayList<>();
        String[] azkarNames = getResources().getStringArray(R.array.Azkar_names);
        for (String azkarName : azkarNames) {
            icons.add(new HadeethIcon(azkarName));
        }
        return icons;
    }

    private void initView() {
        azkarRecyclerview = findViewById(R.id.azkar_recyclerview);
    }

    public void getBannerAdMob(){
        AdView mAdView;
        MobileAds.initialize(this);
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when an ad opens an overlay that
                // covers the screen.
                //Toast.makeText(HomePage.this, "adMob clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
    }

}