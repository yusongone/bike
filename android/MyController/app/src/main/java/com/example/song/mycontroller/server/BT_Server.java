package com.example.song.mycontroller.server;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import com.example.song.mycontroller.BT;
import com.example.song.mycontroller.Menu_activity;
import com.example.song.mycontroller.R;

import java.util.UUID;

public class BT_Server extends Service {
    private Notification n;
    private IBinder binder;
    private OnDataChange odc=null;
    private BluetoothAdapter mBluetoothAdapter;
    private Handler mHandler;
    private BluetoothAdapter.LeScanCallback  leScanCallback;
    private BluetoothManager bluetoothManager;
    private BluetoothGattCallback bluetoothGattCallback;
    private NotificationManager nm;
    private boolean contented=false;
    private boolean mScanning=false;

    private UUID MY_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    private BluetoothGattCharacteristic serialChara=null;
    private BluetoothGatt serialGatt=null;
    private Message msg;



    public BT_Server() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        //initBluetooth();
        return binder;
    }

    public void onDataChange(OnDataChange cdc){
        odc=cdc;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        binder=new BT_binder();
        mHandler=new Handler();
        Log.e("create","---------------------");
        initBluetooth();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        msg=new Message();
        msg.obj=BT_Server.this;
        BT.handler.sendMessage(msg);
        if((!mScanning)&&(!contented)){
            Log.e("-----------------------","i will scan");
            if(serialGatt!=null){
                serialGatt.close();
            };
            scanLeDevice(true);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //stopForeground(false);
        if(serialGatt!=null){
           //serialGatt.disconnect();
        }
    }

    public void initBluetooth(){
        Log.e("--------","initBluetooth");
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e("Error","system has no feature of BLE");
            return;
        }

        bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Log.e("Error","need open bluetooth");
            return;
        }


        leScanCallback=new BluetoothAdapter.LeScanCallback(){
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                String name=device.getName();
                if(name.equals("HMSoft")){
                    scanLeDevice(false);

                    device.connectGatt(BT_Server.this,true,bluetoothGattCallback);
                }
            }
        };


        bluetoothGattCallback=new BluetoothGattCallback() {
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                serialGatt=gatt;
                if(newState==2){// contented;
                    contented=true;
                    Log.e("----","connect--ed");
                    gatt.discoverServices();
                }else{
                    contented=false;
                    Log.e("----","stop content");
                    AlertDiscontent();
                }
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);

                for(BluetoothGattService gatt_service : gatt.getServices()){
                    for(BluetoothGattCharacteristic gatt_chara : gatt_service.getCharacteristics()){
                        if(gatt_chara.getUuid().equals(MY_UUID)){
                            Log.e("info",gatt_chara.getUuid()+"get a ble deviceff");
                            serialChara=gatt_chara;
                            serialGatt.setCharacteristicNotification(serialChara,true);
                            KeepWriteToBlue();
                            break;
                        };
                    }
                }
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                super.onReadRemoteRssi(gatt, rssi, status);
                odc.change(rssi+"");
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                Log.e("serial","----------------------");
                byte[] b=characteristic.getValue();
                Log.e("[[[[[[[[[[[[",b[0]+":"+b[1]+":"+b.length);
            }
        };
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // 经过预定扫描期后停止扫描
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.stopLeScan(leScanCallback);
                }
            }, 15000);
            mScanning = true;
            mBluetoothAdapter.startLeScan(leScanCallback);
        } else {
            Log.e("stop","stop scan");
            mScanning = false;
            mBluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    public void KeepWriteToBlue(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(contented){
                    //serialChara.setValue("fefe");
                    if(serialGatt!=null){
                        serialGatt.readRemoteRssi();
                    }
                    //serialGatt.writeCharacteristic(serialChara);
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }
    public void AlertDiscontent(){
        Intent intent = new Intent(this, Menu_activity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        n  = new Notification.Builder(this)
                .setContentTitle("连接断开")
                .setContentText("已经和 BLE 设备断开")
                .setSmallIcon(R.drawable.spider)
                .setLights(250,250,250)
                .setAutoCancel(true)
                .setContentIntent(pIntent).build();
        n.defaults |= Notification.DEFAULT_ALL;
        nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        //startForeground(0,n);
        stopForeground(true);
        //stopSelf();
        nm.notify(0, n);
    };
    public void AlertContented(){
        Intent intent = new Intent(this, BT.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, 0);
        n  = new Notification.Builder(this)
                .setContentTitle("成功连接")
                .setContentText("已经连接到 BLE 设备")
                .setSmallIcon(R.drawable.spider)
                .setLights(250,250,250)
                .setContentIntent(pIntent).build();
        //n.defaults |= Notification.DEFAULT_VIBRATE;
        nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        //nm.notify(0,n);
        startForeground(1, n);
    }

    /*
    * Binder
    * */
    public class BT_binder extends Binder {

        public BT_Server getBT_service(){

            return BT_Server.this;
        }
    }

    /*
    * Service event
    * */
    public static class OnDataChange{
       protected  void change(String str){

       }
    }
};
