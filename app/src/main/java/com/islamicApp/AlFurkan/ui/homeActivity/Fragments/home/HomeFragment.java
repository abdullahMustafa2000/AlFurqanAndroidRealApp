package com.islamicApp.AlFurkan.ui.homeActivity.Fragments.home;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.islamicApp.AlFurkan.ui.ayatActivity.AyatActivity;
import com.islamicApp.AlFurkan.ui.azkarActivity.AzkarActivity;
import com.islamicApp.AlFurkan.ui.azkarListenActivity.ui.AzkarMediaPlayerUI;
import com.islamicApp.AlFurkan.ui.swarListActivity.SwarListActivity;
import com.islamicApp.AlFurkan.ui.quranSuggestions.ui.QuranSuggetionsMPActivity;
import com.islamicApp.AlFurkan.ui.searchActivity.ui.SearchActivity;
import com.islamicApp.AlFurkan.ui.homeActivity.Fragments.home.suggestedAzkar.AkarIconsModelAdapter;
import com.islamicApp.AlFurkan.ui.homeActivity.Fragments.home.suggestedListenAzkar.AzkarListenHomeAdapter;
import com.islamicApp.AlFurkan.ui.homeActivity.Fragments.home.suggestedListenQuran.QuranHomeListenAdapter;
import com.islamicApp.AlFurkan.ui.homeActivity.Fragments.home.suggestedQuran.QuranIconsModelAdapter;
import com.islamicApp.AlFurkan.ui.azkarListenActivity.mpClass.AzkarMediaPlayer;
import com.islamicApp.AlFurkan.Classes.InternetConnection;
import com.islamicApp.AlFurkan.Classes.QuranListenSuggestionsDbOpener;
import com.islamicApp.AlFurkan.ui.quranSuggestions.mpClass.SuggestionsMP;
import com.islamicApp.AlFurkan.Classes.StaticStrings;
import com.islamicApp.AlFurkan.Modules.AzkarListenModel;
import com.islamicApp.AlFurkan.R;
import com.islamicApp.AlFurkan.ui.azkarListenActivity.service.AzkarListenService;
import com.islamicApp.AlFurkan.ui.quranListenActivity.service.QuranService;
import com.islamicApp.AlFurkan.ui.quranSuggestions.service.QuranSuggestionsService;

import java.util.ArrayList;
import java.util.List;

import maes.tech.intentanim.CustomIntent;

public class HomeFragment extends Fragment implements View.OnClickListener {

    protected TextView searchBarView;
    protected TextView moreAzkar;
    protected RecyclerView azkarReadRv;
    protected TextView moreQuran;
    protected RecyclerView quranReadRv;
    protected RecyclerView quranLiteningRv;
    protected RecyclerView azkarLiteningRv;
    protected ProgressBar homeLoadingProgressBar;

    SharedPreferences sharedPref;
    private InterstitialAd interstitialAd;
    private AzkarListenHomeAdapter listenHomeAdapter;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quran, container, false);
        initView(view);
        homeLoadingProgressBar.setVisibility(View.GONE);
        sharedPref = getActivity().getSharedPreferences("homeFragmentPref", Context.MODE_PRIVATE);
        showAzkarRecyclerview();
        showQuranRecyclerview();
        showQuranListenRecyclerview();
        showAzkarListenRecyclerview();
        getInterstitinlaAd();
        return view;
    }

    private void showAzkarListenRecyclerview() {
        azkarLiteningRv.setLayoutManager(new LinearLayoutManager(this.getActivity(), LinearLayoutManager.HORIZONTAL, true));
        List<AzkarListenModel> modelList = new ArrayList<>();
        for (int i = 0; i < namesArray.length; i++) {
            AzkarListenModel listenModel = new AzkarListenModel(namesArray[i], getLink(i));
            modelList.add(listenModel);
            listenHomeAdapter = new AzkarListenHomeAdapter(modelList, getActivity());
            listenHomeAdapter.notifyDataSetChanged();
            azkarLiteningRv.setAdapter(listenHomeAdapter);
            listenHomeAdapter.setOnZekrListenClicked((listenModel1, position) -> {
                if (new InternetConnection(getActivity()).internetIsConnected()) {
                    stopOthersButAzkar();
                    if (!AzkarMediaPlayer.isInitialized() || sharedPref.getInt("playingPos", -1) != position)
                        startNewAzkarSound(position);
                    else
                        getActivity().startActivity(new Intent(getActivity(), AzkarMediaPlayerUI.class).putExtra("zekr_pos", position));

                    sharedPref.edit().putInt("playingPos", position).apply();
                } else {
                    Toast.makeText(getActivity(), "يرجى الاتصال بالانترنت", Toast.LENGTH_SHORT).show();
                }

            });
        }
    }

    public void stopOthersButAzkar() {
        getActivity().startService(new Intent(getActivity(),
                QuranSuggestionsService.class).putExtra("actionToken", "stop"));

        getActivity().startService(new Intent(getActivity(),
                QuranService.class).putExtra("action_token", "stop"));
    }

    public void stopOthersButSugQuran() {
        getActivity().startService(new Intent(getActivity(),
                AzkarListenService.class).putExtra("azkar_action", "stop"));

        getActivity().startService(new Intent(getActivity(),
                QuranService.class).putExtra("action_token", "stop"));
    }

    public void startNewAzkarSound(int position) {
        homeLoadingProgressBar.setVisibility(View.VISIBLE);
        Intent intent = new Intent(getActivity(), AzkarListenService.class);
        intent.putExtra("azkar_action", "start_azkar");
        intent.putExtra("position", position);
        getActivity().startService(intent);
    }

    private void showQuranListenRecyclerview() {
        QuranListenSuggestionsDbOpener suggestionsModel = new QuranListenSuggestionsDbOpener(this.getActivity());
        QuranHomeListenAdapter listenAdapter = new QuranHomeListenAdapter(suggestionsModel.getSuggestionsList());
        quranLiteningRv.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, true));
        quranLiteningRv.setAdapter(listenAdapter);
        listenAdapter.setOnSuggestionClick((homeModel, position) -> {
            if (AzkarMediaPlayer.isPlaying()) {
                getActivity().startService(new Intent(getActivity(),
                        AzkarListenService.class).putExtra("azkar_action", "stop"));
            }
            startNewQuranSound(position, homeModel.getSuraLink());
        });
    }

    private void startNewQuranSound(int position, String link) {
        if (new InternetConnection(getActivity()).internetIsConnected()) {
            stopOthersButSugQuran();
            getActivity().startActivity(new Intent(this.getActivity(), QuranSuggetionsMPActivity.class).putExtra("currentPos", position));
            if (!SuggestionsMP.isInitialized() ||!sharedPref.getString("quranListenLink", "").equals(link)) {
                this.getActivity().startService(new Intent(getActivity()
                        , QuranSuggestionsService.class).putExtra("actionToken", "startNewSura").putExtra("selectedPos", position));
            }
            sharedPref.edit().putString("quranListenLink", link).apply();
        } else {
            Toast.makeText(getActivity(), "يرجى الاتصال بالانترنت", Toast.LENGTH_SHORT).show();
        }
    }

    private void showQuranRecyclerview() {
        QuranIconsModelAdapter quranReadAdapter = new QuranIconsModelAdapter(this.getActivity());
        quranReadRv.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        quranReadRv.setAdapter(quranReadAdapter);
        quranReadAdapter.setOnQuranIconClick((position, title) -> {
            Intent intent = new Intent(HomeFragment.this.getActivity(),
                    AyatActivity.class);
            intent.putExtra(StaticStrings.INTENT_SURA_NAME, title);
            intent.putExtra(StaticStrings.INTENT_SURA_POSITION, position);
            startActivity(intent);
            CustomIntent.customType(getActivity(), "left-to-right");
        });
    }

    private void showAzkarRecyclerview() {
        AkarIconsModelAdapter azkarReadAdapter = new AkarIconsModelAdapter();
        azkarReadRv.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        azkarReadRv.setAdapter(azkarReadAdapter);
        azkarReadAdapter.setOnModelClick((position, title) -> {
            Intent intent = new Intent(HomeFragment.this.getActivity(), AzkarActivity.class);
            intent.putExtra("hadeethName", title);
            intent.putExtra("hadeethpos", position);
            startActivity(intent);
            CustomIntent.customType(getActivity(), "right-to-left");
        });
    }

    public String getLink(int position) {
        String[] links = {
                "https://firebasestorage.googleapis.com/v0/b/alfurkan-d22c3.appspot.com/o/%D8%A3%D8%B0%D9%83%D8%A7%D8%B1%20%D8%A7%D9%84%D8%B5%D8%A8%D8%A7%D8%AD.mp3?alt=media&token=dee02ada-885f-4df2-895b-3144d6d7e59e",
                "https://firebasestorage.googleapis.com/v0/b/alfurkan-d22c3.appspot.com/o/%D8%A3%D8%B0%D9%83%D8%A7%D8%B1%20%D8%A7%D9%84%D9%85%D8%B3%D8%A7%D8%A1.mp3?alt=media&token=8d705241-36da-4bf7-8e14-1b27ae763ba6",
                "https://firebasestorage.googleapis.com/v0/b/alfurkan-d22c3.appspot.com/o/%D8%A3%D8%B0%D9%83%D8%A7%D8%B1%20%D8%A7%D9%84%D8%A5%D8%B3%D8%AA%D9%8A%D9%82%D8%A7%D8%B8%20%D9%85%D9%86%20%D8%A7%D9%84%D9%86%D9%88%D9%85.mp3?alt=media&token=f80aba97-6638-4f88-a588-8fc08e2bee28",
                "https://firebasestorage.googleapis.com/v0/b/alfurkan-d22c3.appspot.com/o/%D8%A3%D8%B0%D9%83%D8%A7%D8%B1%20%D8%A7%D9%84%D9%86%D9%88%D9%85.mp3?alt=media&token=fd578cae-8f44-49b5-823f-d9009d55a66b",
                "https://firebasestorage.googleapis.com/v0/b/alfurkan-d22c3.appspot.com/o/%D8%A3%D8%B0%D9%83%D8%A7%D8%B1%20%D9%85%D8%A7%20%D8%A8%D8%B9%D8%AF%20%D8%A7%D9%84%D8%B5%D9%84%D8%A7%D8%A9.mp3?alt=media&token=8722e2c6-f963-4161-8a94-c70c324c2f4e",
                "https://firebasestorage.googleapis.com/v0/b/alfurkan-d22c3.appspot.com/o/%D8%A7%D9%84%D8%AD%D8%AC%20%D9%88%D8%A7%D9%84%D8%B9%D9%85%D8%B1%D8%A9.mp3?alt=media&token=15fa5e04-478c-4d6a-a523-f00215454ce8",
                "https://firebasestorage.googleapis.com/v0/b/alfurkan-d22c3.appspot.com/o/%D8%A7%D9%84%D8%B1%D9%8F%D9%91%D9%82%D9%8A%D8%A9%20%D8%A7%D9%84%D8%B4%D8%B1%D8%B9%D9%8A%D8%A9.mp3?alt=media&token=bdb7576a-6d77-42bc-a2be-256855cc590a",
                "https://firebasestorage.googleapis.com/v0/b/alfurkan-d22c3.appspot.com/o/%D8%A7%D9%84%D9%83%D8%B3%D9%88%D9%81%20%D9%88%D8%A7%D9%84%D8%AE%D8%B3%D9%88%D9%81.mp3?alt=media&token=d075b7d4-508e-4f16-82b1-18bf6b622aa2"
        };
        return links[position];
    }

    public String[] namesArray = {"أذكار الصباح", "أذكار المساء", "أذكار الإستيقاظ من النوم",
            "أذكار النوم", "أذكار ما بعد الصلاة", "الحج والعمرة",
            "الرُّقية الشرعية", "الكسوف والخسوف"};
    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.more_azkar) {
            startActivity(new Intent(this.getActivity(), com.islamicApp.AlFurkan.ui.azkarListActivity.AzkarListActivity.class));
        } else if (view.getId() == R.id.more_quran) {
            startActivity(new Intent(this.getActivity(), SwarListActivity.class));
        } else if (view.getId() == R.id.search_bar_view) {
            if (interstitialAd.isLoaded())
                interstitialAd.show();
            else
                startSearchActivityIntent();
        }
    }

    public void startSearchActivityIntent() {
        Intent intent = new Intent(this.getActivity(), SearchActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), searchBarView, ViewCompat.getTransitionName(searchBarView));
        startActivity(intent, options.toBundle());
    }

    @Override
    public void onPause() {
        homeLoadingProgressBar.setVisibility(View.GONE);
        if (listenHomeAdapter != null)
        listenHomeAdapter.setPosition(-1);
        super.onPause();
    }

    private void initView(View rootView) {
        searchBarView = (TextView) rootView.findViewById(R.id.search_bar_view);
        searchBarView.setOnClickListener(HomeFragment.this);
        moreAzkar = (TextView) rootView.findViewById(R.id.more_azkar);
        moreAzkar.setOnClickListener(HomeFragment.this);
        azkarReadRv = (RecyclerView) rootView.findViewById(R.id.azkar_rv);
        moreQuran = (TextView) rootView.findViewById(R.id.more_quran);
        moreQuran.setOnClickListener(HomeFragment.this);
        quranReadRv = (RecyclerView) rootView.findViewById(R.id.quran_rv);
        quranLiteningRv = (RecyclerView) rootView.findViewById(R.id.quranLitening_rv);
        azkarLiteningRv = (RecyclerView) rootView.findViewById(R.id.azkarLitening_rv);
        homeLoadingProgressBar = (ProgressBar) rootView.findViewById(R.id.home_loading_progressBar);
    }

    public void getInterstitinlaAd(){
        interstitialAd = new InterstitialAd(this.getActivity());
        interstitialAd.setAdUnitId("ca-app-pub-5492091545098636/5829839811");
        interstitialAd.loadAd(new AdRequest.Builder().build());

        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);
                interstitialAd.loadAd(new AdRequest.Builder().build());
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
                //Toast.makeText(HomePage.this, "ad clicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                startSearchActivityIntent();
                interstitialAd.loadAd(new AdRequest.Builder().build());            }
        });
    }

}