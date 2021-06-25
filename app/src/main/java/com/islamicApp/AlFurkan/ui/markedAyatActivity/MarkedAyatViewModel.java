package com.islamicApp.AlFurkan.ui.markedAyatActivity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.islamicApp.AlFurkan.db.tables.SqliteAyaModel;

import java.util.List;

public class MarkedAyatViewModel extends AndroidViewModel {

    MarkedAyaRepository repository;
    public MarkedAyatViewModel(@NonNull Application application) {
        super(application);
        repository = new MarkedAyaRepository(application);
    }

    public LiveData<List<SqliteAyaModel>> getAllMarked() {
        return repository.getAll();
    }
}
