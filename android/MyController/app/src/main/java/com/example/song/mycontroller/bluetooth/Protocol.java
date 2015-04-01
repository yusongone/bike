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

    private String headString;
    private int dataLength=-1;
    private int startData=0;
    private int subIndex=0;
    private int msgId;

    private byte[] buf=new byte[15];


    public void parseCMD(byte[] bytes){
        for(int i=0;i<bytes.length;i++){
            byte tempByte=bytes[i];
            if(startData==1){//get dataLength;
                buf[subIndex++]=tempByte;
                dataLength=(int)tempByte;
                startData++;
            }else if(startData==2){//get msgId
                buf[subIndex++]=tempByte;
                startData++;
            }else if(startData>2){
                if(dataLength==-1){
                    //if(checkSum()){
                        switchCMD(buf);
                    //};
                    startData=0;
                    subIndex=0;
                }else{
                    buf[subIndex++]=tempByte;
                    dataLength--;
                }
            }
            if(tempByte=='$'||tempByte=='B'||tempByte=='>'||tempByte=='<'){
                headString+= (char)tempByte;
                if(headString.equals("$B>")||headString.equals("$B<")){
                    startData=1;
                }
            }else{
                headString="";
            }
        }
    }
    public void switchCMD(byte[] bytes){
         switch(bytes[1]&0xff){
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

    private void got_speed(byte[] bytes){
        int d=(bytes[2]<<0&0xff)+(bytes[3]<<8);
        onAction.speedChange((float)d/10);
    }

    private void got_trip_dist(byte[] bytes){
        int d=(bytes[2]<<0&0xff)+(bytes[3]<<8);
        onAction.tripDistChange(d);
    }

    private void got_total_dist(byte[] bytes){
        int d=(bytes[2]<<0&0xff)+(bytes[3]<<8&0xff)+(bytes[4]<<16&0xff);

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

