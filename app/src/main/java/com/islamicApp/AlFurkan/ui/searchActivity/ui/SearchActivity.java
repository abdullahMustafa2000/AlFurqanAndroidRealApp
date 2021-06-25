package com.islamicApp.AlFurkan.ui.searchActivity.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.islamicApp.AlFurkan.ui.ayatActivity.AyatActivity;
import com.islamicApp.AlFurkan.ui.homeActivity.activity.HomePage;
import com.islamicApp.AlFurkan.ui.searchActivity.adapters.AyatSearchAdapter;
import com.islamicApp.AlFurkan.ui.searchActivity.adapters.RecitersSearchAdapter;
import com.islamicApp.AlFurkan.Adapters.QuranIconsAdapter;
import com.islamicApp.AlFurkan.Classes.StaticStrings;
import com.islamicApp.AlFurkan.R;

public class SearchActivity extends AppCompatActivity {

    protected EditText searchEt;
    protected RecyclerView searchedSwar;
    protected RecyclerView searchedAyat;
    protected RecyclerView searchedReciters;
    protected TextView ayatSizeTv, swarSizeTv, recitersSizeTv;
    SearchViewModel viewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_app_search_u_i);
        ViewModelFactory factory = new ViewModelFactory(getApplication());
        viewModel = new ViewModelProvider(this, factory).get(SearchViewModel.class);
        initView();
        startSearch();
    }

    private void startSearch() {
        searchEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterSwar(s.toString());
                filterAyat(s.toString());
                filterReciters(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void filterReciters(final String searchText) {
        viewModel.getRecitersByName(searchText);
        viewModel.recitersListMutableLiveData.observe(this, recitersItems -> {
            recitersSizeTv.setText( String.valueOf(recitersItems.size()) );
            RecitersSearchAdapter recitersAdapter =
                    new RecitersSearchAdapter(recitersItems);
            recitersAdapter.notifyDataSetChanged();
            searchedReciters.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
            searchedReciters.setAdapter(recitersAdapter);

            //set no of reciters
            recitersSizeTv.setText(String.valueOf(recitersItems.size()));
            recitersAdapter.setOnItemClicked((position, recitersItem) -> {
                Intent intent = new Intent(this, HomePage.class);
                intent.putExtra("From", "openLF");
                intent.putExtra("qareeLink", recitersItem.getServer());
                startActivity(intent);
            });
        });

    }

    private void filterAyat(String searchText) {
        viewModel.getAyatByInput(searchText);
        viewModel.ayatListMutableLiveData.observe(this, sqliteAyaModels -> {
            AyatSearchAdapter ayatSearchAdapter = new AyatSearchAdapter(SearchActivity.this, sqliteAyaModels);
            ayatSearchAdapter.notifySearchWord(searchText);
            searchedAyat.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
            searchedAyat.setAdapter(ayatSearchAdapter);

            //set ayat no
            ayatSizeTv.setText( String.valueOf(sqliteAyaModels.size()) );
            ayatSearchAdapter.setOnSearchItemClick((sqliteAyaModel, suraName) -> {
                Intent intent = new Intent(SearchActivity.this, AyatActivity.class);
                intent.putExtra(StaticStrings.INTENT_SURA_NAME, suraName);
                intent.putExtra(StaticStrings.INTENT_SURA_POSITION, sqliteAyaModel.getSuraNum() - 1);
                intent.putExtra(StaticStrings.INTENT_AYA_INDEX, sqliteAyaModel.getAyaNum() - 1);
                startActivity(intent);
            });
        });

    }

    private void filterSwar(String searchText) {
        viewModel.getSwarByInput(searchText);
        viewModel.swarListMutableLiveData.observe(this, quranIcons -> {
            QuranIconsAdapter iconsAdapter = new QuranIconsAdapter(quranIcons);
            searchedSwar.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
            searchedSwar.setAdapter(iconsAdapter);

            //set no of swar
            swarSizeTv.setText( (String.valueOf(quranIcons.size())) );
            iconsAdapter.setOnClicked((postion, suraName) -> {
                Intent intent = new Intent(SearchActivity.this, AyatActivity.class);
                intent.putExtra(StaticStrings.INTENT_SURA_NAME, suraName.getSuraName());
                intent.putExtra(StaticStrings.INTENT_SURA_POSITION, postion);
                startActivity(intent);
            });
        });

    }

    private void initView() {
        searchEt = findViewById(R.id.search_et);
        searchedSwar = findViewById(R.id.searched_swar);
        searchedAyat = findViewById(R.id.searched_ayat);
        searchedReciters = findViewById(R.id.searched_reciters);
        ayatSizeTv = findViewById(R.id.ayat_size_tv);
        swarSizeTv = findViewById(R.id.swar_size_tv);
        recitersSizeTv = findViewById(R.id.reciters_size);
    }

}