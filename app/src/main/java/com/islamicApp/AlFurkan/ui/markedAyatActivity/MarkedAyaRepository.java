package com.islamicApp.AlFurkan.ui.markedAyatActivity;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.islamicApp.AlFurkan.R;
import com.islamicApp.AlFurkan.db.LocalDbClass;
import com.islamicApp.AlFurkan.db.daos.MarkedAyaDao;
import com.islamicApp.AlFurkan.db.tables.SqliteAyaModel;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MarkedAyaRepository {

    MarkedAyaDao mDao;
    Context context;
    private static final String TAG = "MarkedAyaRepository";
    public MarkedAyaRepository (Application app) {
        context = app;
        LocalDbClass db = LocalDbClass.getInstance(app);
        mDao = db.getMarkedAyaDao();
    }

    public LiveData<List<SqliteAyaModel>> getAll() {
        return mDao.getAll();
    }

    public void insertAya(SqliteAyaModel ayaModel) {
        Single.create((SingleOnSubscribe<SqliteAyaModel>) emitter -> {
            mDao.addAya(ayaModel);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((ayaModel1) -> Toast.makeText(context, context.getString(R.string.aya_marked), Toast.LENGTH_SHORT).show(), throwable -> Log.d(TAG, "insertAya: abdo "+ throwable));
    }

}
