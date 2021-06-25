package com.islamicApp.AlFurkan.Classes;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.islamicApp.AlFurkan.Modules.RecitersItem;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecitersDataBaseOpener extends SQLiteAssetHelper {

    public static final String AR_DB = "reciters.db";
    public static final int DATABASE_VER = 1;

    public RecitersDataBaseOpener(Context context) {
        super(context, AR_DB, null, DATABASE_VER);
    }
    public List<RecitersItem> getAllReciters() {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] selected = {"raw_id", "Server", "letter", "name", "count", "suras", "rewaya", "id"};
        qb.setTables("Reciters");
        Cursor cursor = db.query("Reciters", selected, null, null, null, null, null, null);
        List<RecitersItem> recitersItemList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                RecitersItem recitersItem = new RecitersItem();
                recitersItem.setRaw_id(cursor.getInt(cursor.getColumnIndex("raw_id")));
                recitersItem.setServer(cursor.getString(cursor.getColumnIndex("Server")));
                recitersItem.setLetter(cursor.getString(cursor.getColumnIndex("letter")));
                recitersItem.setName(cursor.getString(cursor.getColumnIndex("name")));
                recitersItem.setCount(cursor.getString(cursor.getColumnIndex("count")));
                recitersItem.setSuras(cursor.getString(cursor.getColumnIndex("suras")));
                recitersItem.setRewaya(cursor.getString(cursor.getColumnIndex("rewaya")));
                recitersItem.setId(cursor.getString(cursor.getColumnIndex("id")));
                recitersItemList.add(recitersItem);
            }while (cursor.moveToNext());
        }
        return recitersItemList;
    }

    public List<RecitersItem> getRecitersLike(String name) {
        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String[] selected = {"raw_id", "Server", "letter", "name", "count", "suras", "rewaya", "id"};
        qb.setTables("Reciters");
        Cursor cursor = db.query("Reciters", selected, "name LIKE ?",
                new String[] {"%" + name + "%"}, null, null, null, null);
        List<RecitersItem> recitersItemList = new ArrayList<>();
        if (cursor.moveToFirst()) {
            do {
                RecitersItem recitersItem = new RecitersItem();
                recitersItem.setRaw_id(cursor.getInt(cursor.getColumnIndex("raw_id")));
                recitersItem.setServer(cursor.getString(cursor.getColumnIndex("Server")));
                recitersItem.setLetter(cursor.getString(cursor.getColumnIndex("letter")));
                recitersItem.setName(cursor.getString(cursor.getColumnIndex("name")));
                recitersItem.setCount(cursor.getString(cursor.getColumnIndex("count")));
                recitersItem.setSuras(cursor.getString(cursor.getColumnIndex("suras")));
                recitersItem.setRewaya(cursor.getString(cursor.getColumnIndex("rewaya")));
                recitersItem.setId(cursor.getString(cursor.getColumnIndex("id")));
                recitersItemList.add(recitersItem);
            }while (cursor.moveToNext());
        }
        return recitersItemList;
    }
}
