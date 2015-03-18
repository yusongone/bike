package com.example.song.mycontroller;

import android.app.Activity;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import com.example.song.mycontroller.common.MySensor.SensorData;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.song.mycontroller.common.MySensor;


public class Single_RC extends Activity {
    private MySensor ms;
    private Rocker r_single;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single__rc);

        r_single=(Rocker)findViewById(R.id.single_RC);

        r_single.starAnimate();
        ms=new MySensor((SensorManager)getSystemService(Context.SENSOR_SERVICE));
        ms.addListener(new MySensor.OnDataChange() {
            @Override
            public void OnChange(final float Pitch, final float ROLL) {
                super.OnChange(Pitch, ROLL);
                r_single.post(new Runnable() {
                    @Override
                    public void run() {
                        r_single.setPointPosition(4*ROLL,4*Pitch);
                    }
                });
            }
        });
    }

    protected void onResume(){
        super.onResume();
        ms.start();
    }

    protected void onPause(){
        super.onPause();
        ms.stop();
    }
}
