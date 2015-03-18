package com.example.song.mycontroller.common;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import com.example.song.mycontroller.Single_RC;

/**
 * Created by song on 15/1/20.
 */
public class MySensor{
    Sensor Sen_ACC;
    Sensor Sen_GYRO;
    SensorManager sm;
    long oldTime=0l;
    SensorEventListener SEL;
    SensorEventListener SEL2;
    SensorData gy_data;
    SensorData acc_data;
    float finalAngle_Pitch;
    float finalAngle_ROLL;

    private OnDataChange osd=null;
    Kalman k_Pitch;
    Kalman k_Roll;

    private void callback(){
        if(osd!=null&&gy_data.isFill()&&acc_data.isFill()){
            float[] a=acc_data.getData();
            float[] g=gy_data.getData();
            finalAngle_ROLL=k_Pitch.getAngle(a[0],g[1],gy_data.getDt());
            finalAngle_Pitch=k_Roll.getAngle(a[1],g[0],gy_data.getDt());
            osd.OnChange(-finalAngle_Pitch,finalAngle_ROLL);
            gy_data.clear();
            acc_data.clear();
        }
    }

    public MySensor(SensorManager _sm){
        sm=_sm;
        gy_data=new SensorData();
        acc_data=new SensorData();
        k_Pitch=new Kalman();
        k_Roll=new Kalman();

        Sen_ACC=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sen_GYRO=sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        SEL=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                float x= sensorEvent.values[0];
                float y=sensorEvent.values[1];
                float z=sensorEvent.values[2];
                if(z<0){z=-z;};
                float nowY=(float)Math.sqrt(x*x+z*z);
                float nowX=(float)Math.sqrt(y*y+z*z);
                acc_data.setData(-(float)((Math.atan(x/nowX)*57.2957786)),-(float)((Math.atan(y/nowY)*57.2957786)),0);
                callback();
            }
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
        SEL2=new SensorEventListener() {
            float NS2S = 1.0f / 1000000000.0f;
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                if(oldTime==0){oldTime=sensorEvent.timestamp;};
                double d=(double)(Math.round(sensorEvent.timestamp-oldTime));
                float x= sensorEvent.values[0];
                float y=sensorEvent.values[1];
                float z=sensorEvent.values[2];
                gy_data.setDt((float)d*NS2S);
                gy_data.setData(-(float)(x*57.2957786),(float)(y*57.2957786),0);
                callback();
                oldTime=sensorEvent.timestamp;
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };

    }
    public void addListener(OnDataChange _odc){
        osd=_odc;
    };

    public void start(){
        oldTime=0l;
        sm.registerListener(SEL2, Sen_GYRO, SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(SEL, Sen_ACC, SensorManager.SENSOR_DELAY_GAME);
    }

    public void stop(){
        sm.unregisterListener(SEL);
        sm.unregisterListener(SEL2);
    }


    public static class OnDataChange{
        public void OnChange(float Pitch,float Roll){
        }
    }


    public static class SensorData{
        private boolean fill=false;
        private float[] Data=new float[3];
        private float dt;
        public void setData(float _data_x,float _data_y,float _data_z){
            Data[0]=_data_x;
            Data[1]=_data_y;
            Data[2]=_data_z;
            fill=true;
        }
        public void setDt(float _dt){
            dt=0.0f;
            dt=_dt;
        }
        public float getDt(){
            return  dt;
        }
        public float[] getData(){
            return Data;
        }
        public boolean isFill(){
            return fill;
        }
        public void clear(){
            fill=false;
            Data[0]= Data[1]= Data[2]=0;
        }
    }
}
