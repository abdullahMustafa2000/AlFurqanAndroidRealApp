package com.islamicApp.AlFurkan.db.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.islamicApp.AlFurkan.db.tables.SqliteAyaModel;

import java.util.List;

import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface MarkedAyaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void addAya(SqliteAyaModel ayaModel);

    @Query("DELETE FROM marked_aya WHERE ayaId=:ayaId")
    void removeAya(int ayaId);

    @Query("SELECT * FROM marked_aya")
    LiveData<List<SqliteAyaModel>> getAll();

    @Query("SELECT ayaId FROM marked_aya")
    LiveData<List<Integer>> getMarkedIds();

    @Query("SELECT EXISTS (SELECT * FROM marked_aya WHERE ayaId LIKE :query)")
    boolean getAyaLike(int query);

    @Query("SELECT * FROM marked_aya WHERE ayaNum = :ayanum")
    LiveData<List<SqliteAyaModel>> getSuraMarkedAyat(int ayanum);
}
