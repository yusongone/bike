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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.Timestamp;
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
                Log.e("MyDatabase", "bufferReady");
            }

            @Override
            protected void onNewData() {
                super.onNewData();
                Log.e("MyDatabase", "new row");
            }
        });
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void startRecord(SQLiteDatabase db){
        table_record=new Record(db);
    }
    public String search(){
        return table_record.getALLRecord();
    }

    public void addPoint(Point p){
        table_record.newRecordRow(p);
    };



    private class Record{
        private long nowAliveRow;
        private SQLiteDatabase db;
        public Record(SQLiteDatabase _db){
            //create record table if it dose not existed;
            db=_db;
            //String drop_sql="drop table if exists record";
            //db.execSQL(drop_sql);
            //String sql="create table if not exists record(recordId integer primary key autoincrement ,startTime varchar(20),recordPoint text,status integer,other varchar(50))";
            String sql="create table if not exists record(recordId integer primary key autoincrement ,speed int(4),pressure int(8),shake int(6),temp int(2),lap int(2),createtime int(8))";
            db.execSQL(sql);
        }

        public void newRecordRow(Point p){
            ContentValues cv=new ContentValues();
            cv.put("speed",p.getSpeed());
            cv.put("pressure",p.getPressure());
            cv.put("shake",p.getShake());
            cv.put("temp",p.getTemp());
            cv.put("lap",p.getLap());
            cv.put("createtime",System.currentTimeMillis()/1000);
            nowAliveRow=db.insert("record",null,cv);
            Log.e("*******************************************MYDATAbase",""+nowAliveRow);
            getRecordPoint();
        }

        public void updateRecordPoint(String str){
            ContentValues cv=new ContentValues();
            cv.put("recordPoint",str);
            db.update("record", cv, "recordId=" + nowAliveRow, null);
        }

        public String getALLRecord(){
            String sql="select * from record ORDER BY recordId";
            Cursor cursor=db.rawQuery(sql, null);
            Point p=new Point();
            Log.e("count",""+cursor.getCount());
            String s="";
            while (cursor.moveToNext()){
                int speed= cursor.getInt(1);
                int pressure = cursor.getInt(2);
                int shake = cursor.getInt(3);
                int temp = cursor.getInt(4);
                int lap = cursor.getInt(5);
                String time=cursor.getString(6);

                p.setSpeed(speed);
                p.setPressure(pressure);
                p.setShake(shake);
                p.setTemp(temp);
                p.setLap(lap);
                p.setTimestamp(time);
                //Log.e("System.out", speed + "|" + pressure + "|" + shake + "|" + temp + "|" + lap + "|" +time);
                String d = speed + "|" + pressure + "|" + shake + "|" + temp + "|" + lap + "|" +time +",";
                s+=d;
            }
            return s;
        }
        public Point getRecordPoint(){
            String sql="select * from record ORDER BY recordId DESC LIMIT 1";
            Cursor cursor=db.rawQuery(sql, null);
            Point p=new Point();
            while (cursor.moveToNext()){
                int speed= cursor.getInt(1);
                int pressure = cursor.getInt(2);
                int shake = cursor.getInt(3);
                int temp = cursor.getInt(4);
                int lap = cursor.getInt(5);
                String time=cursor.getString(6);
                p.setSpeed(speed);
                p.setPressure(pressure);
                p.setShake(shake);
                p.setTemp(temp);
                p.setLap(lap);
                p.setTimestamp(time);
                Log.e("System.out", speed + "|" + pressure + "|" + shake + "|" + temp + "|" + lap + "|" +time);
            }
            return p;
        }
    }

    public static class Point{
        private int speed;
        private int pressure;
        private int shake;
        private int temp;
        private int lap;
        private String timestamp;

        public void setSpeed(int _speed){
           this.speed=_speed;
        }
        public float getSpeed(){
            return speed;
        }
        public void setPressure(int _pressure){
            this.pressure=_pressure;
        }
        public int getPressure() {
            return pressure;
        }
        public void setShake(int shake) {
            this.shake = shake;
        }
        public int getShake() {
            return shake;
        }
        public void setTemp(int temp) {
            this.temp = temp;
        }
        public int getTemp() {
            return temp;
        }
        public void setLap(int lap) {
            this.lap = lap;
        }
        public int getLap() {
            return lap;
        }
        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }
        public String getTimestamp() {
            return timestamp;
        }
    }
}

