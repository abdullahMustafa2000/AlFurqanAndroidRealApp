package com.islamicApp.AlFurkan.ui.searchActivity.ui;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.islamicApp.AlFurkan.Modules.RecitersItem;
import com.islamicApp.AlFurkan.Classes.RecitersDataBaseOpener;
import com.islamicApp.AlFurkan.Classes.QuranDBOpener;
import com.islamicApp.AlFurkan.Modules.QuranIcons;
import com.islamicApp.AlFurkan.db.tables.SqliteAyaModel;
import com.islamicApp.AlFurkan.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.ObservableOnSubscribe;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchViewModel extends AndroidViewModel {

    QuranDBOpener quranDBOpener;
    RecitersDataBaseOpener reciters;
    String[] swarNames;
    MutableLiveData<List<RecitersItem>> recitersListMutableLiveData = new MutableLiveData<>();
    MutableLiveData<List<SqliteAyaModel>> ayatListMutableLiveData = new MutableLiveData<>();
    MutableLiveData<List<QuranIcons>> swarListMutableLiveData = new MutableLiveData<>();

    CompositeDisposable disposable = new CompositeDisposable();
    
    public SearchViewModel(@NonNull Application application) {
        super(application);
        quranDBOpener = new QuranDBOpener(application);
        reciters = new RecitersDataBaseOpener(application);
        swarNames = application.getResources().getStringArray(R.array.swar_names);
    }

    public void getRecitersByName(String name) {
       Observable<List<RecitersItem>> observable = Observable.create(emitter -> {
           if (name != null || name.length() > 0)
               emitter.onNext(name);
       }).map(o -> reciters.getRecitersLike((String) o))
               .subscribeOn(Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread());
       disposable.add(observable.subscribe(recitersItems -> recitersListMutableLiveData.setValue(recitersItems)));
    }

    public void getAyatByInput(String input) {
        Observable<List<SqliteAyaModel>> observable = Observable.create(emitter -> {
            if (input.length() > 0)
                emitter.onNext(input);
        }).map(o-> quranDBOpener.getSuchAya(input)).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
        disposable.add(observable
                .subscribe(ayat-> ayatListMutableLiveData.setValue(ayat)));
    }

    public void getSwarByInput(String input) {
        Observable.create((ObservableOnSubscribe<String>) emitter -> emitter.onNext(input)).map(new Function<String, List<QuranIcons>>() {
            @Override
            public List<QuranIcons> apply(String s) {
                List<QuranIcons> filteredList = new ArrayList<>();
                for (int i=0; i< swarNames.length; i++) {
                    if (swarNames[i].contains(s))
                        filteredList.add(new QuranIcons(swarNames[i], i));
                }
                return filteredList;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
        .subscribe(o-> swarListMutableLiveData.setValue(o));

    }

    @Override
    protected void onCleared() {
        disposable.clear();
        super.onCleared();
    }
}
