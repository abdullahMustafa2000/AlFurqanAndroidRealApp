package com.islamicApp.AlFurkan.ui.markedAyatActivity;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.islamicApp.AlFurkan.ui.searchActivity.adapters.AyatSearchAdapter;
import com.islamicApp.AlFurkan.R;

public class MarkedAyatActivity extends AppCompatActivity {

    RecyclerView markedRv;
    MarkedAyatViewModel viewModel;
    protected AyatSearchAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marked_ayat);
        initView();
        initViewModel();
        prepareRv();
    }

    private void initViewModel() {
        MarkedAyatViewModelFactory factory = new MarkedAyatViewModelFactory(getApplication());
        viewModel = new ViewModelProvider(this, factory).get(MarkedAyatViewModel.class);
    }

    private void prepareRv() {
        markedRv.setLayoutManager(new LinearLayoutManager(this));
        viewModel.getAllMarked();
        viewModel.getAllMarked().observe(this, sqliteAyaModels -> {
            adapter = new AyatSearchAdapter(this, sqliteAyaModels);
            markedRv.setAdapter(adapter);
        });

    }

    private void initView() {
        markedRv = findViewById(R.id.marked_ayat_rv);
    }
}
