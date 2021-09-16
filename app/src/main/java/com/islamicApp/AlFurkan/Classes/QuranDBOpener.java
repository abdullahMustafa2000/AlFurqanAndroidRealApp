package com.islamicApp.AlFurkan.Classes;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.util.Log;

import com.islamicApp.AlFurkan.db.tables.SqliteAyaModel;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class QuranDBOpener extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "qurandb.db"; // must have file extension
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "ayat";

    private final String ID_COL = "ID";
    private final String SORA_NO_COL = "soraano";
    private final String PAGE_NO_COL = "pageno";
    private final String AYAH_NO_COL = "ayaano";
    //AYAH TASHKEEL
    private final String AYAH_TASH_COL = "ayaa";
    //AYAH WITHOUT TASHKEEL
    private final String NOR_AYAH_COL = "AyaaIndex";
    private final String CHAPTER_NO_COL = "chapterNO";
    private final String LAST_AYA_ON_PAGE_COL = "isLastAya";
    private final String IS_MARKED = "isMarked";

    String[] columnsArr = {ID_COL, SORA_NO_COL, PAGE_NO_COL, AYAH_NO_COL, AYAH_TASH_COL, NOR_AYAH_COL, CHAPTER_NO_COL, LAST_AYA_ON_PAGE_COL, IS_MARKED};

    public QuranDBOpener(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        //updateSomeValues();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
    }

    public List<SqliteAyaModel> getAllMarkedAyat() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] selected = columnsArr;

        qb.setTables(TABLE_NAME);

        Cursor cursor = db
                .query(TABLE_NAME, selected, IS_MARKED + " = 1", null, null, null, null, null);

        List<SqliteAyaModel> ayaModelList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                SqliteAyaModel ayaModel = new SqliteAyaModel();
                ayaModel.setAyaId(cursor.getInt(cursor.getColumnIndex(ID_COL)));
                ayaModel.setSuraNum(cursor.getInt(cursor.getColumnIndex(SORA_NO_COL)));
                ayaModel.setPageNum(cursor.getInt(cursor.getColumnIndex(PAGE_NO_COL)));
                ayaModel.setAyaNum(cursor.getInt(cursor.getColumnIndex(AYAH_NO_COL)));
                ayaModel.setAyaTashkelText(cursor.getString(cursor.getColumnIndex(AYAH_TASH_COL)));
                ayaModel.setAyaNormalText(cursor.getString(cursor.getColumnIndex(NOR_AYAH_COL)));
                ayaModel.setChapterNum(cursor.getInt(cursor.getColumnIndex(CHAPTER_NO_COL)));
                ayaModel.setIsLastAyaOnPage(cursor.getInt(cursor.getColumnIndex(LAST_AYA_ON_PAGE_COL)));
                ayaModel.setIsMarked(cursor.getInt(cursor.getColumnIndex(IS_MARKED)));
                ayaModelList.add(ayaModel);
            }while (cursor.moveToNext());
        }
        return ayaModelList;
    }

    public List<SqliteAyaModel> getSuchAya(String name) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] selected = columnsArr;
        qb.setTables(TABLE_NAME);
        Cursor cursor = db.query(TABLE_NAME, selected, NOR_AYAH_COL + " LIKE ?",
                new String[] {"%" + name + "%"}, null, null, null, null);
        List<SqliteAyaModel> ayaModelList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                SqliteAyaModel ayaModel = new SqliteAyaModel();
                ayaModel.setAyaId(cursor.getInt(cursor.getColumnIndex(ID_COL)));
                ayaModel.setSuraNum(cursor.getInt(cursor.getColumnIndex(SORA_NO_COL)));
                ayaModel.setPageNum(cursor.getInt(cursor.getColumnIndex(PAGE_NO_COL)));
                ayaModel.setAyaNum(cursor.getInt(cursor.getColumnIndex(AYAH_NO_COL)));
                ayaModel.setAyaTashkelText(cursor.getString(cursor.getColumnIndex(AYAH_TASH_COL)));
                ayaModel.setAyaNormalText(cursor.getString(cursor.getColumnIndex(NOR_AYAH_COL)));
                ayaModel.setChapterNum(cursor.getInt(cursor.getColumnIndex(CHAPTER_NO_COL)));
                ayaModel.setIsLastAyaOnPage(cursor.getInt(cursor.getColumnIndex(LAST_AYA_ON_PAGE_COL)));
                ayaModel.setIsMarked(cursor.getInt(cursor.getColumnIndex(IS_MARKED)));
                ayaModelList.add(ayaModel);
            }while (cursor.moveToNext());
        }
        return ayaModelList;
    }

    public List<SqliteAyaModel> getAyatBySuraIndex(int suraIndex) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] selected = columnsArr;
        qb.setTables(TABLE_NAME);
        Cursor cursor = db.query(TABLE_NAME, selected, SORA_NO_COL + " = " + suraIndex, null, null, null, null, null);
        List<SqliteAyaModel> ayaModelList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                    SqliteAyaModel ayaModel = new SqliteAyaModel();
                    ayaModel.setAyaId(cursor.getInt(cursor.getColumnIndex(ID_COL)));
                    ayaModel.setSuraNum(cursor.getInt(cursor.getColumnIndex(SORA_NO_COL)));
                    ayaModel.setPageNum(cursor.getInt(cursor.getColumnIndex(PAGE_NO_COL)));
                    ayaModel.setAyaNum(cursor.getInt(cursor.getColumnIndex(AYAH_NO_COL)));
                    ayaModel.setAyaTashkelText(cursor.getString(cursor.getColumnIndex(AYAH_TASH_COL)));
                    ayaModel.setAyaNormalText(cursor.getString(cursor.getColumnIndex(NOR_AYAH_COL)));
                    ayaModel.setChapterNum(cursor.getInt(cursor.getColumnIndex(CHAPTER_NO_COL)));
                    ayaModel.setIsLastAyaOnPage(cursor.getInt(cursor.getColumnIndex(LAST_AYA_ON_PAGE_COL)));
                    ayaModel.setIsMarked(cursor.getInt(cursor.getColumnIndex(IS_MARKED)));
                    ayaModelList.add(ayaModel);
            }while (cursor.moveToNext());
        }
        return ayaModelList;
    }

    public boolean setIsMarked(int rowId, int newValue) {
        SQLiteDatabase db = getReadableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(IS_MARKED, newValue);
        int updated = db.update(TABLE_NAME, cv, ID_COL + " =? ", new String[] {String.valueOf(rowId)});
        return updated > 0;
    }
}
