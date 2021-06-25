package com.islamicApp.AlFurkan.ui.azkarActivity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.islamicApp.AlFurkan.Classes.StaticStrings;
import com.islamicApp.AlFurkan.Modules.ZekrModel;
import com.islamicApp.AlFurkan.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import maes.tech.intentanim.CustomIntent;

public class AzkarActivity extends AppCompatActivity {

    protected TextView zekrName;
    protected RecyclerView zekrRecyclerView;

    int hadeethPos;
    AzkarTextAdpter azkarTextAdpter;
    ArrayList<ZekrModel> zekr_line;
    String hadeethName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_azkar);
        initView();

        // get zekr position and name from azkar list
        getZekrData();
        //read zekr text from assets file
        readTxt();
        // set zekr name and azkar lines
        setAzkarData();
    }

    private void getZekrData() {
        Bundle bundle = getIntent().getExtras();
        hadeethName = bundle.getString(StaticStrings.INTENT_HDEETH_NAME);
        hadeethPos = bundle.getInt(StaticStrings.INTENT_HDEETH_POSITION);
    }

    private void setAzkarData() {
        zekrName.setText(hadeethName);
        setZekrRecyclerViewData();
    }

    private void setZekrRecyclerViewData() {
        azkarTextAdpter = new AzkarTextAdpter(zekr_line, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        zekrRecyclerView.setLayoutManager(layoutManager);
        zekrRecyclerView.setAdapter(azkarTextAdpter);
    }

    private void initView() {
        zekrName = findViewById(R.id.hdeeth_name);
        zekrRecyclerView = findViewById(R.id.zekr_recycler_view);
    }


    private void readTxt() {
        BufferedReader reader;
        zekr_line = new ArrayList<>();
        try {
            final InputStream file = getAssets().open("h"+(hadeethPos + 1) + ".txt");
            reader = new BufferedReader(new InputStreamReader(file));
            String line = reader.readLine();
            while (line != null) {
                zekr_line.add(new ZekrModel(line.replaceAll("\\[", "\"").replaceAll("\\]", "\"")));
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void finish() {
        super.finish();
        CustomIntent.customType(this, "left-to-right");
    }
}