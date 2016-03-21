package com.example.dico.last;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

/**
 * Created by Dico on 17.03.16.
 */
public class DatabaseAction {
    public static final String DATABASE_TABLE = "geo";
    public static final String IP_COLUMN = "ip";
    public static final String COUNTRY_COLUMN = "country";
    public static final String CITY_COLUMN = "city";
    public static final String FLAG_COLUMN = "flag";
    public static final String DATABASE_CREATE_SCRIPT = "create table "
            + DATABASE_TABLE + " (" + BaseColumns._ID
            + " integer primary key autoincrement, " + IP_COLUMN
            + " text not null, " + COUNTRY_COLUMN + " text, " + CITY_COLUMN
            + " text, " + FLAG_COLUMN + " text);";
    private static final String DB_NAME = "mydb";
    private static final int DB_VERSION = 1;
    private final Context mCtx;


    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDB;

    public DatabaseAction(Context ctx) {
        mCtx = ctx;
    }

    // открыть подключение
    public void open() {
        mDBHelper = new DatabaseHelper(mCtx, DB_NAME, null, DB_VERSION);
        mDB = mDBHelper.getWritableDatabase();
    }

    // закрыть подключение
    public void close() {
        if (mDBHelper != null) mDBHelper.close();
    }

    // получить все данные из таблицы
    public Cursor getAllData() {
        return mDB.query(DATABASE_TABLE, null, null, null, null, null, null);
    }

    public Cursor getAllIp() {
        return mDB.query(DATABASE_TABLE, new String[]{IP_COLUMN}, null, null, null, null, null);
    }

    public Cursor getOldIp(String ip) {
        return mDB.query(DATABASE_TABLE, null, IP_COLUMN + "= ?", new String[]{ip}, null, null, null);
    }

    // добавить запись
    public void addRec(String ip, String country, String city, int flag) {
        ContentValues cv = new ContentValues();
        cv.put(IP_COLUMN, ip);
        cv.put(COUNTRY_COLUMN, country);
        cv.put(CITY_COLUMN, city);
        cv.put(FLAG_COLUMN, flag);
        mDB.insert("geo", null, cv);
    }

    //удалить данные
    public void deletAll() {
        mDB.delete(DATABASE_TABLE, null, null);
    }
}