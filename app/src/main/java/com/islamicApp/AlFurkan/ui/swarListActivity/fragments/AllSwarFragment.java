package com.islamicApp.AlFurkan.ui.swarListActivity.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.islamicApp.AlFurkan.Adapters.QuranIconsAdapter;
import com.islamicApp.AlFurkan.Classes.StaticStrings;
import com.islamicApp.AlFurkan.Modules.QuranIcons;
import com.islamicApp.AlFurkan.R;
import com.islamicApp.AlFurkan.ui.ayatActivity.AyatActivity;
import com.islamicApp.AlFurkan.ui.swarListActivity.SwarListActivity;

import java.util.ArrayList;
import java.util.List;

import maes.tech.intentanim.CustomIntent;


public class AllSwarFragment extends Fragment {

    RecyclerView swarListRv;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_all_swar, container, false);
        initView(v);
        setRecyclerView();
        return v;
    }

    private void setRecyclerView() {
        swarListRv.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        List<QuranIcons> iconsList = new ArrayList<>();
        String[] arrSwar = getResources().getStringArray(R.array.swar_names);
        for (int i=0; i<arrSwar.length; i++) {
            iconsList.add(new QuranIcons(arrSwar[i], i));
        }
        QuranIconsAdapter adapter = new QuranIconsAdapter(iconsList);
        swarListRv.setAdapter(adapter);

        adapter.setOnClicked((postion, suraName) -> startIntent(suraName, postion));
    }

    public void startIntent(QuranIcons suraName, int postion) {
        Intent intent = new Intent(this.getActivity(), AyatActivity.class);
        intent.putExtra(StaticStrings.INTENT_SURA_NAME, suraName.getSuraName());
        intent.putExtra(StaticStrings.INTENT_SURA_POSITION, postion);
        startActivity(intent);
        CustomIntent.customType(this.getActivity(), "left-to-right");
    }

    private void initView(View v) {
        swarListRv = v.findViewById(R.id.swar_list_rv);
    }


}