package com.islamicApp.AlFurkan.ui.ayatActivity;


import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.util.Pair;
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

import org.w3c.dom.Attr;

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
    MutableLiveData<Boolean> addedMarkedAyaMutableLiveData = new MutableLiveData<>();

    public AyatViewModel(Application context) {
        super(context);
        db = new QuranDBOpener(context);
        ayaDao = LocalDbClass.getInstance(context).getMarkedAyaDao();
    }

    public List<SqliteAyaModel> getSuraAyat(int suraIndex) {
        /*Observable.create((ObservableOnSubscribe<Integer>) emitter -> {
            emitter.onNext(suraIndex);
        }).map(integer -> db.getAyatBySuraIndex(suraIndex + 1))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(sqliteAyaModels -> suraMutableLiveData.setValue(sqliteAyaModels));*/
        return db.getAyatBySuraIndex(suraIndex + 1);

    }

    public void addAyaToMarked(SqliteAyaModel ayaModel) {
        addedMarkedAyaMutableLiveData.setValue(db.setIsMarked(ayaModel.getAyaId(), 1));
    }

    /*public void removeAyaFromDb(SqliteAyaModel ayaModel) {
        Single.create(emitter -> emitter.onSuccess(ayaModel.getAyaId())).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(o ->


                {
                    ayaDao.removeAya((Integer) o);
                    Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();
                });
    }*/

}

