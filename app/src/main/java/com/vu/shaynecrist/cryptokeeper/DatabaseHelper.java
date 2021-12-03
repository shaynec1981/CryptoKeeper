package com.vu.shaynecrist.cryptokeeper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Altcoins.db";
    public static final String COIN_TABLE_NAME = "coins";
    public static final String COIN_COLUMN_SYM = "sym";
    public static final String COIN_COLUMN_DATE = "date";
    public static final String COIN_COLUMN_AMOUNT = "amount";
    public static final String COIN_COLUMN_RATE = "rate";

    public ArrayList<String> symbolList = new ArrayList<>();

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL("create table coins " + "(id integer primary key, sym text, date text, amount text, rate text)");
        db.execSQL("PRAGMA auto_vacuum = FULL");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS coins");
        onCreate(db);
    }

    public boolean addData (String sym, String date, String amount, String rate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("sym", sym);
        contentValues.put("date", date);
        contentValues.put("amount", amount);
        contentValues.put("rate", rate);
        db.insert("coins", null, contentValues);
        return true;
    }

    public Cursor getData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from coins", null); // where sym=" + sym + "", null);
        return res;
    }

    public int numberOfRows(){
        SQLiteDatabase db = this.getReadableDatabase();
        int numRows = (int) DatabaseUtils.queryNumEntries(db, COIN_TABLE_NAME);
        return numRows;
    }

    public Integer deleteEntry (String sym) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(COIN_TABLE_NAME,"sym = ? ", new String[] { sym });
    }

    public void deleteAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(COIN_TABLE_NAME, null, null);
    }

    public List returnSymbols() {
        SQLiteDatabase db = this.getReadableDatabase();
        for(int i = 0; i < numberOfRows(); i++) {
            Cursor res = db.query(COIN_TABLE_NAME, null, null, null, null, null, null);
            res.moveToNext();
            symbolList.add(res.getString(1));
            res.close();
        }
        return symbolList;
    }
}
