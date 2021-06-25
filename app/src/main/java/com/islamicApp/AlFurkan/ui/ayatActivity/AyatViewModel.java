package com.islamicApp.AlFurkan.ui.ayatActivity;


import android.app.Application;
import android.content.Context;
import android.widget.Toast;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.islamicApp.AlFurkan.BuildConfig;
import com.islamicApp.AlFurkan.Classes.QuranDBOpener;
import com.islamicApp.AlFurkan.R;
import com.islamicApp.AlFurkan.db.LocalDbClass;
import com.islamicApp.AlFurkan.db.daos.MarkedAyaDao;
import com.islamicApp.AlFurkan.db.tables.SqliteAyaModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class AyatViewModel extends AndroidViewModel {

    QuranDBOpener db;
    MarkedAyaDao ayaDao;
    MutableLiveData<List<SqliteAyaModel>> suraMutableLiveData = new MutableLiveData<>();
    MutableLiveData<List<SqliteAyaModel>> allMushafMutableLiveData = new MutableLiveData<>();
    MutableLiveData<Boolean> addedMarkedAyaMutableLiveData = new MutableLiveData<>();
    public AyatViewModel(Application context) {
        super(context);
        db = new QuranDBOpener(context);
        ayaDao = LocalDbClass.getInstance(context).getMarkedAyaDao();
    }

    public void getAyatFromSQl() {
        Observable.create((ObservableOnSubscribe<Void>) emitter -> {

        }).map(aVoid -> db.getAllAyat())
        .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(sqliteAyaModels -> allMushafMutableLiveData.setValue(sqliteAyaModels));

    }

    public void getSuraAyat(int suraIndex) {
        Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            emitter.onNext(suraIndex);
        }).map(integer -> db.getAyatBySuraIndex(suraIndex + 1))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sqliteAyaModels -> suraMutableLiveData.setValue(sqliteAyaModels));

    }

    public void addAyaToDb(SqliteAyaModel ayaModel) {
        Single.create((SingleOnSubscribe<Boolean>) emitter ->
                ayaDao.addAya(ayaModel)
        )
                .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread()).subscribe((aBoolean, throwable) -> {
            if (aBoolean)
                addedMarkedAyaMutableLiveData.setValue(aBoolean);
            else
                addedMarkedAyaMutableLiveData.setValue(false);
        });
    }

    /*public void removeAyaFromDb(SqliteAyaModel ayaModel) {
        Single.create(emitter -> emitter.onSuccess(ayaModel.getAyaId())).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(o ->


                {
                    ayaDao.removeAya((Integer) o);
                    Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();
                });
    }*/

    public LiveData<List<Integer>> getMarkedAyat() {
        return ayaDao.getMarkedIds();
    }
}

