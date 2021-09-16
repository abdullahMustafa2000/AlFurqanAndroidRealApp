package com.islamicApp.AlFurkan.ui.swarListActivity.fragments.markedAyat;

import android.app.Application;
import android.util.Log;
import android.util.Pair;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.islamicApp.AlFurkan.Classes.QuranDBOpener;
import com.islamicApp.AlFurkan.db.LocalDbClass;
import com.islamicApp.AlFurkan.db.daos.MarkedAyaDao;
import com.islamicApp.AlFurkan.db.tables.SqliteAyaModel;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableEmitter;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleEmitter;
import io.reactivex.rxjava3.core.SingleOnSubscribe;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MarkedAyatFragmentViewModel extends AndroidViewModel {

    MarkedAyaDao dao;
    QuranDBOpener db;
    MutableLiveData<List<SqliteAyaModel>> markedAyatMutableLiveData = new MutableLiveData<>();
    public MarkedAyatFragmentViewModel(@NonNull Application application) {
        super(application);
        dao = LocalDbClass.getInstance(application).getMarkedAyaDao();
        db = new QuranDBOpener(application);
    }

    public void getMarkedAyatList() {
        Observable.create((ObservableOnSubscribe<List<SqliteAyaModel>>) emitter -> emitter.onNext(db.getAllMarkedAyat())).observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
        .subscribe((o) -> markedAyatMutableLiveData.setValue(o), e-> Log.d("TAG", "getMarkedAyatList: abdo is "+ e.getMessage()));
    }

    public LiveData<List<SqliteAyaModel>> getAyaLike(int ayaNum) {
        return dao.getSuraMarkedAyat(ayaNum);
    }

    public void deleteAyaFromMarked(int ayaId) {
        Single.create((SingleOnSubscribe<Pair<Integer, Integer>>) emitter ->
                emitter.onSuccess(new Pair<>(ayaId, 0)))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(o -> db.setIsMarked(o.first, o.second));
    }

}
