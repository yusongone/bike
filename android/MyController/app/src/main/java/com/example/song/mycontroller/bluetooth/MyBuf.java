package com.example.song.mycontroller.bluetooth;

import android.util.Log;

/**
 * Created by song on 15/3/25.
 */
public class MyBuf {
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
        buf[index]=(byte)((num>>0)&0xff);
    }
    public byte[] merge(){
        mergeBuf=new byte[buf.length+8];// 3 from sendHeader 1 from sum
        mergeBuf[0]=0x24;
        mergeBuf[1]=0x42;
        mergeBuf[2]=0x3C;
        for(int i=0;i<buf.length;i++){
            mergeBuf[i+3]=buf[i];
        }
        mergeBuf[buf.length+3]=checkSum(buf);
        mergeBuf[buf.length+4]=0x00;
        mergeBuf[buf.length+5]=0x00;
        mergeBuf[buf.length+6]=0x00;
        mergeBuf[buf.length+7]=0x00;
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
