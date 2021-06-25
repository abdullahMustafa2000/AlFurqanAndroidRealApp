package com.islamicApp.AlFurkan.ui.swarListActivity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.islamicApp.AlFurkan.Classes.StaticStrings;
import com.islamicApp.AlFurkan.ui.ayatActivity.AyatActivity;
import com.islamicApp.AlFurkan.Adapters.QuranIconsAdapter;
import com.islamicApp.AlFurkan.Modules.QuranIcons;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.islamicApp.AlFurkan.R;
import com.islamicApp.AlFurkan.ui.swarListActivity.adapters.QuranViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import maes.tech.intentanim.CustomIntent;

public class SwarListActivity extends AppCompatActivity {

    protected ViewPager2 quranViewPager;
    protected TabLayout mTabLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_quran_read_swar);
        initView();
        getBannerAdMob();
        initViewPager();
    }

    private void initViewPager() {
        QuranViewPagerAdapter adapter = new QuranViewPagerAdapter(this);
        quranViewPager.setAdapter(adapter);
        quranViewPager.setCurrentItem(adapter.getItemCount());
        //mTabLayout.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        new TabLayoutMediator(mTabLayout, quranViewPager, (tab, position) -> {
            if (position == 1) {
                tab.setText(R.string.quran);
            } else {
                tab.setText(R.string.marked_text);
            }
        }).attach();
    }


    private void initView() {
        quranViewPager = findViewById(R.id.quran_viewpager);
        mTabLayout = findViewById(R.id.fragments_tablayout);
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
            public void onAdFailedToLoad(LoadAdError errorCode) {
                // Code to be executed when an ad request fails.
                //Toast.makeText(HomePage.this, "internet connection lost", Toast.LENGTH_SHORT).show();
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
            public void onAdImpression() {
                super.onAdImpression();
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the user is about to return
                // to the app after tapping on an ad.
            }
        });
    }
}