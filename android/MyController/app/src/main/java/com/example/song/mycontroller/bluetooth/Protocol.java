package com.example.song.mycontroller.bluetooth;

import android.util.Log;

import com.example.song.mycontroller.bluetooth.MyBuf;

/**
 * Created by song on 14/12/21.
 */
public class Protocol{
    public static int LAP=5;
    private final int GET_SPEED=201;
    private final int GET_TRIP_DIST=202;
    private final int GET_TOTAL_DIST=203;
    private final int GET_SPEED_PRESSURE_SHAKE=205;
    private OnAction onAction;
    public Protocol(){
    }

    private String headString="";
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
                if(dataLength==0){
                    buf[subIndex++]=tempByte;
                    if(checkSum(buf)){
                        switchCMD(buf);
                    };
                    startData=0;
                    subIndex=0;
                }else{
                    buf[subIndex++]=tempByte;
                    dataLength--;
                }
            }else if(tempByte=='$'||tempByte=='B'||tempByte=='>'||tempByte=='<'){
                headString+= (char)tempByte;
                if(headString.equals("$B>")||headString.equals("$B<")){
                    startData=1;
                    headString="";
                }
            }else{
                headString="";
            }
        }
    }

    private boolean checkSum(byte[] buf){
        byte c=buf[0];//datalength;
        for(int i=1;i<=subIndex-2;i++){
            c=(byte)(c^(buf[i]&0xff));
        };
        if(c==buf[subIndex-1]){
            return true;
        }
        return false;
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
             case GET_SPEED_PRESSURE_SHAKE:
                 got_speed_pressure_shake(bytes);
                 break;
         }
    }

    private void got_speed(byte[] bytes){

        int d=((bytes[2]&0xff)<<0)+((bytes[3]&0xff)<<8);
        onAction.speedChange((float)d/10);
    }

    private void got_speed_pressure_shake(byte[] bytes){
        int speed=((buf[2]&0xff)<<0|(buf[3]&0xff)<<8);
        int pressure=((buf[4]&0xff)<<0|(buf[5]&0xff)<<8|(buf[6]&0xff)<<16);
        int shake=((buf[7]&0xff)<<0|(buf[8]&0xff)<<8);
        int temp=((buf[9]&0xff)<<0|(buf[10]&0xff)<<8);
        int lap=((buf[11]&0xff)<<0);
        onAction.dataChange(speed,pressure,shake,temp,lap);
    }

    private void got_trip_dist(byte[] bytes){
        int d=((bytes[2]&0xff)<<0)+((bytes[3]&0xff)<<8);
        onAction.tripDistChange(d);
    }

    private void got_total_dist(byte[] bytes){
        long d=((bytes[2]&0xff)<<0)+((bytes[3]&0xff)<<8)+((bytes[4]&0xff)<<16);
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
        protected void totalDistChange(long num){ }
        protected void tripDistChange(float num){ }
        protected void airChange(float num){ }
        protected void dataChange(int speed,int pressure,int shake,int temp,int lap){ }
    }
}

