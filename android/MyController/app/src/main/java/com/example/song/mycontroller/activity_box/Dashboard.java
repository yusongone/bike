package com.example.song.mycontroller.activity_box;

import android.app.Activity;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.example.song.mycontroller.R;
import com.example.song.mycontroller.server.Main_server;
import com.example.song.mycontroller.bluetooth.Protocol;

import java.text.DecimalFormat;


/*
* this activity is UI enter;
* */
public class Dashboard extends Activity {
    public static Handler handler;
    private Main_server mainserver;
    private TextView blueStatus;
    private TextView speedText;
    private Protocol protocol;

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        blueStatus=(TextView)findViewById(R.id.blueProgress);
        speedText=(TextView)findViewById(R.id.speedText);
        Intent intent = new Intent(Dashboard.this,Main_server.class);
        startService(intent);
        Log.e("create", "create");
    }

    @Override
    protected void onResume() {
        super.onResume();
        catchBT_server();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unbindService(conn);
    }


    /**
     *this method use to catch a BT_server by handler;
     */
    private void catchBT_server(){
        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                mainserver =(Main_server)msg.obj;
                mainserver.OnActionListener(new Main_server.OnAction(){
                    @Override
                    protected void blueStatusChange(int status) {
                        final String show;
                        if(status== BluetoothProfile.STATE_CONNECTED){
                            Log.e("dashboard", "" + status);
                            show="已连接";
                        }else{
                            show="已断开";
                        }
                        blueStatus.post(new Runnable() {
                            @Override
                            public void run() {
                                blueStatus.setText(show);
                            }
                        });
                    }

                    @Override
                    protected void speedChange(float _num) {
                        super.speedChange(_num);
                        final float num=_num;
                        blueStatus.post(new Runnable() {
                            @Override
                            public void run() {
                                String a = new DecimalFormat("######.#").format(num);
                                speedText.setText(a);
                            }
                        });

                    }
                });
                mainserver.getAllStatus();
            }
        };
    }
}
