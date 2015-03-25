package com.example.song.mycontroller.multiwii;

import android.util.Log;

import com.example.song.mycontroller.MyBluetooth;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;

/**
 * Created by song on 14/12/21.
 */
public class Protocol{
    private final int GET_SPEED=201;
    private final int GET_TOTAL_DIST=203;
    public Protocol(){
    }
    public byte[] getSpeed(){
        MyBuf mb=new MyBuf(2);
        mb.writeUInt8(0,0);//data length
        mb.writeUInt8(GET_TOTAL_DIST,1); // mesId according to multiwii serial protocol   http://www.multiwii.com/wiki/index.php?title=Multiwii_Serial_Protocol
        return mb.merge();
    }


    public void switchCMD(byte[] bytes){
     if(bytes[0]==0x24&&bytes[1]==0x42&&bytes[2]==0x3c){
         switch(bytes[4]&0xff){
             case GET_SPEED:
                 got_speed(bytes);
                 break;
             case GET_TOTAL_DIST:
                 got_total_dist(bytes);
                 break;
         }
     }
    }
    private void got_speed(byte[] bytes){
        Log.e("sped","dd");
    }
    private void got_total_dist(byte[] bytes){
        int d=(bytes[5]<<0&0xff)+(bytes[6]<<8)+(bytes[7]<<16);
        Log.e("total1",((bytes[5]<<0&0xff)+""));
        Log.e("total1",((bytes[6]<<8)+""));
        Log.e("total1",((bytes[7]<<16)+""));
        Log.e("total",d+"f");
    }
}

