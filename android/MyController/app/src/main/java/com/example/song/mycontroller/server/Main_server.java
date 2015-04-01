package com.example.song.mycontroller.server;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.song.mycontroller.activity_box.Dashboard;
import com.example.song.mycontroller.R;
import com.example.song.mycontroller.bluetooth.BT_connection;
import com.example.song.mycontroller.bluetooth.Protocol;
import com.example.song.mycontroller.database.MyDatabase;

import java.text.DecimalFormat;
import java.util.List;


public class Main_server extends Service {
    private NotificationManager nm;
    private Notification n;
    private IBinder binder;
    private BT_connection bt_connection;
    private OnAction onAction;
    private Protocol protocol;
    private MyDatabase myDatabase;

    private int bluetoothStates=0;
    private float speed=0;

    private Message msg;

    private int cmdStatus;
    private MyDatabase.Point pointModel;


    @Override
    public void onCreate() {
        super.onCreate();
        bt_connection=new BT_connection((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE) ,this);
        protocol=new Protocol();
        pointModel=new MyDatabase.Point();
        myDatabase=new MyDatabase(this,"df",null,1);
        SQLiteDatabase db=myDatabase.getWritableDatabase();
        myDatabase.startRecord(db);

        initProtocolAction();
        bindActionToBT();
        runForeground();

    }

    private void initProtocolAction(){
        protocol.OnActionListener(new Protocol.OnAction(){
            @Override
            protected void speedChange(float num) {
                super.speedChange(num);
                pointModel.set_speed(num);
                myDatabase.addPoint(num);
                speed=num;
                onAction.speedChange(num);
                Log.e("*****","speed change"+num);
            }

            @Override
            protected void tripDistChange(float num) {
                super.tripDistChange(num);
                Log.e("*****","trip dist change"+num);
            }

            @Override
            protected void totalDistChange(int num) {
                super.totalDistChange(num);
                Log.e("*****","total dist change"+num);
            }

            @Override
            protected void airChange(float num) {
                super.airChange(num);
                pointModel.set_air(num);
            }
        });
    }

    private void keepRequestBTData(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("Main_server","start request data from bt drive");
                while (bluetoothStates==2){
                    bt_connection.sendCMDToQueue(protocol.requestTripDist());
                    bt_connection.sendCMDToQueue(protocol.requestTotalDist());
                    bt_connection.sendCMDToQueue(protocol.requestSpeed());
                    try {
                        Thread.sleep(8*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    public void getAllStatus(){
        onAction.blueStatusChange(bluetoothStates);
        onAction.speedChange(speed);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sendMetoActivity();
        if(bluetoothStates!= 2){
            if(bt_connection.checkSupportBle()){
                bt_connection.scanLeDevice(true);
            };
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void bindActionToBT(){
        bt_connection.OnActionListener(new BT_connection.OnAction(){
            @Override
            protected void connectStateChange(int state) {
                super.connectStateChange(state);
                bluetoothStates=state;
                onAction.blueStatusChange(state);
                if(bluetoothStates!=BluetoothProfile.STATE_CONNECTED){
                    alertDisconnect();
                }
            }

            @Override
            protected void haveGoods(byte[] b) {
                super.haveGoods(b);
                protocol.parseCMD(b);
            }
            @Override
            protected void getSerialChara() {
                super.getSerialChara();
                keepRequestBTData();
            }
        });
    }

    private void sendMetoActivity(){
        msg=new Message();
        msg.obj=Main_server.this;
        Dashboard.handler.sendMessage(msg);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        //initBluetooth();
        return binder;
    }

    public void runForeground(){
        Intent intent = new Intent(this, Dashboard.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        n  = new Notification.Builder(this)
                .setContentTitle("Bike 蓝牙服务")
                .setContentText("123")
                .setSmallIcon(R.drawable.logo_482)
                .setLights(250,250,250)
                .setAutoCancel(true)
                .setContentIntent(pIntent).build();
        startForeground(1, n);

    };

    public void alertDisconnect(){
        Intent intent = new Intent(this, Dashboard.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        n  = new Notification.Builder(this)
                .setContentTitle("Bike 蓝牙已经断开")
                .setContentText("请点击重新连接，或者设置为自动连接。")
                .setSmallIcon(R.drawable.logo_482)
                .setLights(250,250,250)
                .setAutoCancel(true)
                .setContentIntent(pIntent).build();
        n.defaults=Notification.DEFAULT_ALL;
        nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        nm.notify(0,n);
    }
    public void OnActionListener(OnAction _onAction){
       onAction=_onAction;
    }

    public static class OnAction{
        protected void blueStatusChange(int status){  }
        protected void speedChange(float num){  }
    }
};
