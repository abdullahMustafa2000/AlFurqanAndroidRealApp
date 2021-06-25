package com.islamicApp.AlFurkan.ui.swarListActivity.fragments.markedAyat;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

public class MarkedAyatVMFactory extends ViewModelProvider.AndroidViewModelFactory {
    /**
     * Creates a {@code AndroidViewModelFactory}
     *
     */
    public MarkedAyatVMFactory(@NonNull Application application) {
        super(application);
        new MarkedAyatFragmentViewModel(application);
    }
}
