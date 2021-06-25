package com.islamicApp.AlFurkan.ui.swarListActivity.fragments.markedAyat;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.islamicApp.AlFurkan.R;
import com.islamicApp.AlFurkan.db.tables.SqliteAyaModel;
import com.islamicApp.AlFurkan.ui.markedAyatActivity.MarkedAyatViewModel;

import java.util.List;

public class MarkedAyatFragment extends Fragment {

    private MarkedAyatFragmentViewModel viewModel;
    private RecyclerView markedAyatRv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_marked_ayat, container, false);
        initView(v);
        initViewModel();
        getAyatList();
        return v;
    }

    private void initViewModel() {
        MarkedAyatVMFactory factory = new MarkedAyatVMFactory(getActivity().getApplication());
        viewModel = new ViewModelProvider(this, factory).get(MarkedAyatFragmentViewModel.class);
    }

    public void getAyatList() {
        markedAyatRv.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        viewModel.getAyaLike(1).observe(this.getActivity(), sqliteAyaModels -> {
            MarkedAyatAdapter ayatAdapter = new MarkedAyatAdapter(sqliteAyaModels, this.getActivity());
            markedAyatRv.setAdapter(ayatAdapter);
            ayatAdapter.notifyDataSetChanged();
        });

    }

    private void initView(View v) {
        markedAyatRv = v.findViewById(R.id.marked_ayat_list_rv);
    }
}