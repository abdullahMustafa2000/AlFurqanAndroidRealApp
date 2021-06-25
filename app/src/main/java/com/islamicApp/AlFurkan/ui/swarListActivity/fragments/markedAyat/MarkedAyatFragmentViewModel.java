package com.islamicApp.AlFurkan.ui.swarListActivity.fragments.markedAyat;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.islamicApp.AlFurkan.db.LocalDbClass;
import com.islamicApp.AlFurkan.db.daos.MarkedAyaDao;
import com.islamicApp.AlFurkan.db.tables.SqliteAyaModel;

import java.util.List;

public class MarkedAyatFragmentViewModel extends AndroidViewModel {

    MarkedAyaDao dao;
    public MarkedAyatFragmentViewModel(@NonNull Application application) {
        super(application);
        dao = LocalDbClass.getInstance(application).getMarkedAyaDao();
    }

    public LiveData<List<SqliteAyaModel>> getMarkedAyatList() {
        return dao.getAll();
    }

    public LiveData<List<SqliteAyaModel>> getAyaLike(int ayaNum) {
        return dao.getSuraMarkedAyat(ayaNum);
    }

}
