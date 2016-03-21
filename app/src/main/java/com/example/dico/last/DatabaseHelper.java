package com.example.dico.last;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by Dico on 16.03.16.
 */
public class DatabaseHelper extends SQLiteOpenHelper implements BaseColumns {

    public DatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory,
                          int version) {
        super(context, name, factory, version);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DatabaseAction.DATABASE_CREATE_SCRIPT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF IT EXISTS " + DatabaseAction.DATABASE_TABLE);
        onCreate(db);
    }
}
