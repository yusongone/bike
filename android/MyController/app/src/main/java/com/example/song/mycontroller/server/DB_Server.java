package com.example.song.mycontroller.server;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by song on 15/3/25.
 */
public class DB_Server extends SQLiteOpenHelper {
    public DB_Server(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void test(){

    };

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.e("DB_SERVER","created a db");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
