package com.example.song.mycontroller.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorJoiner;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.Log;

import java.util.Arrays;

/**
 * Created by song on 15/3/26.
 */
public class MyDatabase extends SQLiteOpenHelper {
    private Record table_record;
    private DataBuffer dataBuffer;
    public MyDatabase(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        dataBuffer=new DataBuffer();
        dataBuffer.OnActionListener(new DataBuffer.OnAction(){
            @Override
            protected void onBufferReady(String[] f) {
                super.onBufferReady(f);
                Log.e("MyDatabase","bufferReady");
                parsePoint(f);
                table_record.getRecordPoint();
            }

            @Override
            protected void onNewData() {
                super.onNewData();
                Log.e("MyDatabase","new row");
                table_record.newRecordRow();
            }
        });
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void parsePoint(String[] f){
        String newStr="";
        for(int i=0;i<f.length;i++){
            newStr+=f[i]+",";
        }
        String str=table_record.getRecordPoint();
        table_record.updateRecordPoint(str+newStr);
    }

    public void startRecord(SQLiteDatabase db){
        table_record=new Record(db);
        table_record.newRecordRow();
    }

    public void addPoint(float speed){
       dataBuffer.addPoint(speed);
    };



    private class Record{
        private long nowAliveRow;
        private SQLiteDatabase db;
        public Record(SQLiteDatabase _db){
            //create record table if it dose not existed;
            db=_db;
            String drop_sql="drop table record";
            db.execSQL(drop_sql);
            String sql="create table if not exists record(recordId integer primary key autoincrement ,startTime varchar(20),recordPoint text,status integer,other varchar(50))";
            db.execSQL(sql);
        }

        public void newRecordRow(){
            Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
            t.setToNow(); // 取得系统时间。
            int year = t.year;
            int month = t.month;
            int date = t.monthDay;
            int hour = t.hour; // 0-23
            int minute = t.minute;
            int second = t.second;

            ContentValues cv=new ContentValues();
            cv.put("startTime",year+"-"+month+"-"+date+" "+hour+":"+minute+":"+second);
            cv.put("recordPoint","");
            nowAliveRow=db.insert("record",null,cv);
            Log.e("*******************************************MYDATAbase",""+nowAliveRow);
        }

        public void updateRecordPoint(String str){
            ContentValues cv=new ContentValues();
            cv.put("recordPoint",str);
            db.update("record", cv, "recordId=" + nowAliveRow, null);
        }

        public String getRecordPoint(){
            String sql="select * from record ORDER BY recordId DESC LIMIT 1";
            String recordPoint="";
            Cursor cursor=db.rawQuery(sql, null);
            while (cursor.moveToNext()){
                String startTime = cursor.getString(1);
                recordPoint= cursor.getString(2);
                Log.e("System.out", startTime+ "|" + recordPoint);
            }
            return recordPoint;
        }
    }
}

