package com.example.song.mycontroller.database;

import android.util.Log;

import com.example.song.mycontroller.bluetooth.Protocol;

import java.text.DecimalFormat;

/**
 * Created by song on 15/3/29.
 */
public class DataBuffer {
    private int BufferSize=10;
    private String[] buffer=new String[BufferSize];
    private int cursor=0;
    private int stopCount=0;
    private OnAction onAction;
    public void addPoint(float speed){
        String a = new DecimalFormat("###,###,###.##").format(speed);
        Log.e("DataBuffer", "-------------add point" + a);
        buffer[cursor]=a;
        cursor++;
        if(speed==0){
            stopCount++;
        }else{
            stopCount=0;
        }
        if(stopCount==2||cursor==BufferSize){// bike stop ;
            onAction.onBufferReady(buffer,cursor);
            cursor=0;
        }
    }
    public void OnActionListener(OnAction _onAction){
        onAction=_onAction;
    }

    public static class OnAction{
        protected void onBufferReady(String[] f,int length){  }
    }
}
