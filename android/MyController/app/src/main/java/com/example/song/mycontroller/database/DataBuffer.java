package com.example.song.mycontroller.database;

import android.util.Log;

import com.example.song.mycontroller.bluetooth.Protocol;

import org.json.JSONObject;

import java.text.DecimalFormat;

/**
 * Created by song on 15/3/29.
 */
public class DataBuffer {
    private OnAction onAction;
    private Long addPointTimes=0l;
    private int _TIME_OUT=30; // second
    private int BufferSize=60;
    private boolean newRow=true;
    private float[] tempBuffer=new float[BufferSize];
    private int cursor=0;
    public int addPoint(float speed) {
        tempBuffer[cursor]=speed;
        cursor++;
        if(checkStop(tempBuffer)==2){//isStop    need New Row
            String[] str=toStringArray(tempBuffer,cursor-2);
            launch(str);
        }else if(checkAddPointTimeOut()){//long time disconnect  need New Row
            String[] str=toStringArray(tempBuffer,cursor-1);
            launch(str);
        }else if(cursor==BufferSize){
            onAction.onBufferReady(toStringArray(tempBuffer,cursor));
            tempBuffer=new float[BufferSize];
            cursor=0;
        }
        return 1;
    }

    private void launch(String[] str){
        Log.e("---------",str.length+"");
        int MIN_INSERT_SIZE=0;
        if(newRow){
            MIN_INSERT_SIZE=20;
        }else{
            MIN_INSERT_SIZE=2;
        }
        if(str.length>MIN_INSERT_SIZE){
            onAction.onBufferReady(str);
            newRow=false;
        }
        if(!newRow){
            onAction.onNewData();
            newRow=true;
        }
        cursor=0;
    }
    private String[] toStringArray(float[] b,int length){
        String[] s=new String[length];
        for(int i=0;i<length;i++){
            String a = new DecimalFormat("######.#").format(b[i]);
            s[i]=a;
        }
        return s;
    }
    private int checkStop(float[] b){
        int tempCounter=0;
        for(int i=0;i<cursor;i++){
           if(b[i]==0){
              tempCounter++;
           }else{
               tempCounter=0;
           }
        }
        return tempCounter;
    }

    public boolean checkAddPointTimeOut(){
        Long tsLong = System.currentTimeMillis()/1000;
        if(addPointTimes==0l){
            addPointTimes=tsLong;
        }
        Long tempTime=tsLong-addPointTimes;
        addPointTimes=tsLong;
        if(tempTime>_TIME_OUT){
            Log.e("DdatBuffer","maybe reconnect;");
            return true;
        };
        return  false;
    }

    public void OnActionListener(OnAction _onAction){
        onAction=_onAction;
    }

    public static class OnAction{
        protected void onBufferReady(String[] f){  }
        protected void onNewData(){

        }
    }
}
