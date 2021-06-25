package com.islamicApp.AlFurkan.ui.markedAyatActivity;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.ViewModelProvider;

public class MarkedAyatViewModelFactory extends ViewModelProvider.AndroidViewModelFactory {
    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     * @param application an application to pass in {@link AndroidViewModel}
     */
    public MarkedAyatViewModelFactory(@NonNull Application application) {
        super(application);
        new MarkedAyatViewModel(application);
    }
}
