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
    private Thread sendDataThread=null;


    @Override
    public void onCreate() {
        super.onCreate();
        bt_connection=new BT_connection((BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE) ,this);
        protocol=new Protocol();
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
                onAction.speedChange(num);
                Log.e("*****","speed change"+num);
            }

            @Override
            protected void tripDistChange(float num) {
                super.tripDistChange(num);
                Log.e("*****","trip dist change"+num);
            }

            @Override
            protected void totalDistChange(long num) {
                super.totalDistChange(num);

                Log.e("*****","total dist change"+num);
            }


            @Override
            protected void dataChange(int speed, int pressure, int shake, int temp, int lap) {
                super.dataChange(speed, pressure, shake, temp, lap);
                MyDatabase.Point p=new MyDatabase.Point();
                p.setSpeed(speed);
                p.setPressure(pressure);
                p.setShake(shake);
                p.setTemp(temp);
                p.setLap(lap);
                myDatabase.addPoint(p);
                onAction.speedChange((float)speed/10);
                if(lap<Protocol.LAP&&speed>10){
                    onAction.speedChange(0);
                    MyDatabase.Point p0=new MyDatabase.Point();
                    p0.setSpeed(0);
                    p0.setPressure(0);
                    p0.setShake(0);
                    p0.setTemp(0);
                    p0.setLap(0);
                    myDatabase.addPoint(p0);
                }
            }
        });
    }

    private void keepRequestBTData(){
        if(sendDataThread!=null&&sendDataThread.isAlive()){
            Log.e("",sendDataThread.isAlive()+"");
            return;
        }
         sendDataThread=new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("Main_server","start request data from bt drive");
                while (bluetoothStates==2){
                    /*
                    Log.e("a","sendCMD");
                    bt_connection.sendCMDToQueue(protocol.requestTripDist());
                    bt_connection.sendCMDToQueue(protocol.requestTotalDist());
                    bt_connection.sendCMDToQueue(protocol.requestSpeed());
                    */
                    try {
                        Thread.sleep(5*1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        sendDataThread.start();
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
                //keepRequestBTData();
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
