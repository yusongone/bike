package com.example.song.mycontroller.multiwii;

import android.util.Log;

import com.example.song.mycontroller.MyBluetooth;

import java.lang.reflect.Array;
import java.nio.ByteBuffer;

/**
 * Created by song on 14/12/21.
 */
public class Protocol{
    private int Range_min;
    private int Range_max;
    private int Range;
    private int PITCH;
    private int ROLL;
    private int YAW;
    private int THR;
    private int AUX1;
    private int AUX2;
    private int AUX3;
    private int AUX4;
    private MyBluetooth mbt;
    private Thread sendDataThread;
    public Protocol(int min,int max,MyBluetooth _mbt){
        Range_min=min;
        Range_max=max;
        Range=max-min;
        AUX1=Range_min;
        AUX2=Range_min;
        AUX3=Range_min;
        AUX4=Range_min;
        mbt=_mbt;
        setRawData((float) 0.5, (float) 0.5, (float) 0.5, (float) 0.5, -1, -1, -1, -1);
    }
    public byte[] setVersion(){
        MyBuf mb=new MyBuf(2);
        mb.writeUInt8(0,0);
        mb.writeUInt8(100,1);
        return mb.merge();
    }
    public byte[] getRAWRC(){
        // ROLL/PITCH/YAW/THROTTLE/AUX1/AUX2/AUX3AUX4
        MyBuf mb=new MyBuf(18);
        mb.writeUInt8(16,0);//data length
        mb.writeUInt8(200,1); // mesId according to multiwii serial protocol   http://www.multiwii.com/wiki/index.php?title=Multiwii_Serial_Protocol
        mb.writeUInt16(ROLL,2);
        mb.writeUInt16(PITCH,4);
        mb.writeUInt16(YAW,6);
        mb.writeUInt16(THR,8);
        mb.writeUInt16(AUX1,10);
        mb.writeUInt16(AUX2,12);
        mb.writeUInt16(AUX3,14);
        mb.writeUInt16(AUX4,16);
        return mb.merge();
    }
    public void setRawData(float _ROLL,float _PITCH,float _YAW,float _THR,float _AUX1,float _AUX2,float _AUX3,float _AUX4){
       PITCH=_PITCH!=-1?computer(_PITCH):PITCH;
       ROLL=_ROLL!=-1?computer(_ROLL):ROLL;
       YAW=_YAW!=-1?computer(_YAW):YAW;
       THR=_THR!=-1?computer(_THR):THR;
       AUX1=_AUX1!=-1?computer(_AUX1):AUX1;
       AUX2=_AUX2!=-1?computer(_AUX2):AUX2;
       AUX3=_AUX1!=-1?computer(_AUX1):AUX3;
       AUX4=_AUX2!=-1?computer(_AUX2):AUX4;
    }
    public void stopSendDataToMultiwii() {
        sendDataThread.stop();
    }
    public void keepSendDataToMultiwii(){
        sendDataThread=new Thread(new Runnable() {
            @Override
            public void run() {
                while(!Thread.currentThread().isInterrupted()) {
                    final byte[] b=getRAWRC();
                    mbt.sendData(b);
                    Log.e("----",""+b[3]);
                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        sendDataThread.start();
    }

    private int computer(float bl){
     return (int)(Range_min+Range*bl);
    }


    class MyBuf{
        private byte[] buf;
        private byte[] mergeBuf;
        public MyBuf(int index){
            buf=new byte[index];
        }
        public void writeUInt16(int num,int index){
            buf[index]=(byte)((num>>0)&0xff);
            buf[index+1]=(byte)((num>>8)&0xff);
        }
        public void writeUInt8(int num,int index){
            buf[index]=(byte)(num&0xFF);
        }
        public byte[] merge(){
            mergeBuf=new byte[buf.length+4];// 3 from sendHeader 1 from sum
            mergeBuf[0]=0x24;
            mergeBuf[1]=0x4D;
            mergeBuf[2]=0x3C;
            for(int i=0;i<buf.length;i++){
                mergeBuf[i+3]=buf[i];
            }
            mergeBuf[buf.length+3]=checkSum(buf);
            return mergeBuf;
        }
        private byte checkSum(byte[] buf){
            if(buf[0]==0){
                return buf[1];
            }else{
                byte z=buf[0];
                for(int i=1;i<buf.length;i++){
                    z=(byte)(z^(buf[i]&0xff));
                }
                return z;
            }
        }
    }
}

