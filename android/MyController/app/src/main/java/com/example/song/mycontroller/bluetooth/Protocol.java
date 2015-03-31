package com.example.song.mycontroller.bluetooth;

import android.util.Log;

import com.example.song.mycontroller.bluetooth.MyBuf;

/**
 * Created by song on 14/12/21.
 */
public class Protocol{
    private final int GET_SPEED=201;
    private final int GET_TRIP_DIST=202;
    private final int GET_TOTAL_DIST=203;
    private OnAction onAction;
    public Protocol(){
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
             case GET_TRIP_DIST:
                 got_trip_dist(bytes);
                 break;
         }
     }
    }

    private void got_speed(byte[] bytes){
        int d=(bytes[5]<<0&0xff)+(bytes[6]<<8);
        onAction.speedChange((float)d/10);
    }

    private void got_trip_dist(byte[] bytes){
        int d=(bytes[5]<<0&0xff)+(bytes[6]<<8);
        onAction.tripDistChange(d);
    }

    private void got_total_dist(byte[] bytes){
        int d=(bytes[5]<<0&0xff)+(bytes[6]<<8&0xff)+(bytes[7]<<16&0xff);
        onAction.totalDistChange(d);
    }

    public byte[] requestTotalDist(){
        MyBuf mb=new MyBuf(2);
        mb.writeUInt8(0,0);//data length
        mb.writeUInt8(GET_TOTAL_DIST,1); // mesId according to multiwii serial protocol   http://www.multiwii.com/wiki/index.php?title=Multiwii_Serial_Protocol
        return mb.merge();
    }

    public byte[] requestTripDist(){
        MyBuf mb=new MyBuf(2);
        mb.writeUInt8(0,0);//data length
        mb.writeUInt8(GET_TRIP_DIST,1); // mesId according to multiwii serial protocol   http://www.multiwii.com/wiki/index.php?title=Multiwii_Serial_Protocol
        return mb.merge();
    }
    public byte[] requestSpeed(){
        MyBuf mb=new MyBuf(2);
        mb.writeUInt8(0,0);//data length
        mb.writeUInt8(GET_SPEED,1); // mesId according to multiwii serial protocol   http://www.multiwii.com/wiki/index.php?title=Multiwii_Serial_Protocol
        return mb.merge();
    }



    public void OnActionListener(OnAction _onAction){
        onAction=_onAction;
    }

    public static class OnAction{
        protected void speedChange(float num){ }
        protected void totalDistChange(int num){ }
        protected void tripDistChange(float num){ }
        protected void airChange(float num){ }
    }
}

