package com.islamicApp.AlFurkan.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.islamicApp.AlFurkan.db.daos.MarkedAyaDao;
import com.islamicApp.AlFurkan.db.tables.SqliteAyaModel;

@Database(entities = {SqliteAyaModel.class}, version = 1, exportSchema = false)
public abstract class LocalDbClass extends RoomDatabase {

    public abstract MarkedAyaDao getMarkedAyaDao();

    public static LocalDbClass Instance;

    public static synchronized LocalDbClass getInstance(Context context) {
        if (Instance == null) {
            Instance = Room
                    .databaseBuilder(context, LocalDbClass.class, "alfurkan.db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return Instance;
    }

}
