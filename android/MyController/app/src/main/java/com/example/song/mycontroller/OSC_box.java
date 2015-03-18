package com.example.song.mycontroller;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;

import com.example.song.mycontroller.common.Kalman;
import com.example.song.mycontroller.common.MySensor;


public class OSC_box extends Activity {
    Sensor Sen_ACC;
    Sensor Sen_GYRO;
    Sensor Sen_Step;
    SensorManager sm;
    MySensor ms;
    SensorEventListener SEL;
    SensorEventListener SEL2;
    OSC.Line line;
    OSC.Line line2;
    OSC.Line line3;
    OSC.Line line4;
    OSC.sensorData gy_data;
    OSC.sensorData acc_data;
    OSC osd;
    Kalman k;
    float finalAngle;
    long oldTime=0l;
    private void callback(){
        if(false&&gy_data.isFill()&&acc_data.isFill()){
            line2.pushPoint((int)acc_data.getData());
            line.pushPoint((int)gy_data.getData());
            finalAngle=k.getAngle(acc_data.getData(),gy_data.getData(),gy_data.getDt());
            line3.pushPoint((int)finalAngle);
            line4.pushPoint(0);
            gy_data.clear();
            acc_data.clear();
            osd.post(new Runnable() {

                @Override
                public void run() {
                    osd.draw();
                }
            });
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osc_box);

        sm=(SensorManager)getSystemService(Context.SENSOR_SERVICE);
        Sen_ACC=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        Sen_GYRO=sm.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        //Sen_Step=sm.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR);
        //null /////Sen_Test=sm.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED);
        //Sen_Test=sm.getDefaultSensor(Sensor.TYPE_ALL);

        osd=(OSC)findViewById(R.id.osc);
        line=osd.getLine(Color.argb(250,250,0,0));
        line2=osd.getLine(Color.argb(250,0,250,0));
        line3=osd.getLine(Color.argb(250,250,250,0));
        line4=osd.getLine(Color.argb(250,250,250,250));
        line4.pushPoint(0);
        gy_data=osd.getSensorData();
        acc_data=osd.getSensorData();
        k=new Kalman();

        SEL=new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent sensorEvent) {
                final float x= sensorEvent.values[0];
                Log.e("ef",x+"");
                /*
                final float y=sensorEvent.values[1];
                float z=sensorEvent.values[2];
                acc_data.setData(-(float)((Math.atan(x/z)*57.2957786)));
                callback();
                */
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
                gy_data.setData((float)(y*57.2957786));
                oldTime=sensorEvent.timestamp;
                callback();
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }
    protected void onResume(){
        super.onResume();
        oldTime=0l;
        /*
        sm.registerListener(SEL2, Sen_GYRO, SensorManager.SENSOR_DELAY_GAME);
        sm.registerListener(SEL, Sen_Step, SensorManager.SENSOR_DELAY_GAME);
        */
        ms=new MySensor(sm);
        ms.addListener(new MySensor.OnDataChange(){
            @Override
            public void OnChange(float Pitch, float Roll) {
                super.OnChange(Pitch, Roll);
                line.pushPoint((int)Pitch);
                line2.pushPoint((int)Roll);
                osd.post(new Runnable() {
                    @Override
                    public void run() {
                        osd.draw();
                    }
                });
            }
        });

        ms.start();
    }
    protected void onPause(){
        super.onPause();
        ms.stop();
        sm.unregisterListener(SEL);
        sm.unregisterListener(SEL2);
    }

}
