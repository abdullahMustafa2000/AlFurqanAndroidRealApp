package com.islamicApp.AlFurkan.Classes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.islamicApp.AlFurkan.Modules.QuranListenSuggestionsModel;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class QuranListenSuggestionsDbOpener extends SQLiteAssetHelper {
    private static final String  DATABASE_NAME = "quransuggestions.db";
    private static final int  DATABASE_VERSION = 1;

    public QuranListenSuggestionsDbOpener(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public List<QuranListenSuggestionsModel> getSuggestionsList() {
        String tableName = "suras";
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] columns = {"itemId", "suraName", "qareeName", "suraLink"};
        qb.setTables(tableName);
        Cursor cursor = db.query(tableName, columns, null, null, null, null, null, null);
        List<QuranListenSuggestionsModel> suggestionsList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                QuranListenSuggestionsModel listenSuggestions = new QuranListenSuggestionsModel();
                listenSuggestions.setItemID(cursor.getInt(cursor.getColumnIndex("itemId")));
                listenSuggestions.setSuraName(cursor.getString(cursor.getColumnIndex("suraName")));
                listenSuggestions.setQareeName(cursor.getString(cursor.getColumnIndex("qareeName")));
                listenSuggestions.setSuraLink(cursor.getString(cursor.getColumnIndex("suraLink")));
                suggestionsList.add(listenSuggestions);
            }while (cursor.moveToNext());
        }
        return suggestionsList;
    }
}
