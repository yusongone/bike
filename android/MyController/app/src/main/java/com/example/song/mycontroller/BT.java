package com.example.song.mycontroller;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.song.mycontroller.server.BT_Server;


public class BT extends Activity {
    private TextView tv;
    private BT_Server bt_server=null;
    private ServiceConnection conn;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bt_server!=null){
           bt_server.AlertContented();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt);

        handler=new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                bt_server=(BT_Server)msg.obj;
                bt_server.onDataChange(new BT_Server.OnDataChange() {
                    @Override
                    protected void change(String str) {
                        super.change(str);
                        final String _str=str;
                        tv.post(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(_str);
                            }
                        });
                    }
                });
            }
        };

        tv=(TextView)findViewById(R.id.dm);
        tv.setText("---");
/*
        conn=new ServiceConnection(){
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                bt_server=((BT_Server.BT_binder)service).getBT_service();
                bt_server.onDataChange(new BT_Server.OnDataChange(){
                    @Override
                    public void change(String str) {
                        super.change(str);
                        final String _str=str;
                        tv.post(new Runnable() {
                            @Override
                            public void run() {
                                tv.setText(_str);
                            }
                        });
                    }
                });
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                bt_server=null;
            }
        };
        */

        //
    }
    public static Handler handler;
    @Override
    protected void onResume() {
        super.onResume();

        Intent intent = new Intent(BT.this,BT_Server.class);
        startService(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unbindService(conn);
    }
}
