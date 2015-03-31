package com.example.song.mycontroller.bluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.util.Log;

import com.example.song.mycontroller.server.Main_server;

import java.util.List;
import java.util.UUID;

/**
 * Created by song on 15/3/28.
 */
public class BT_connection extends BluetoothGattCallback{
    private OnAction onAction;
    private Protocol protocol;
    private boolean contented=false;
    private BluetoothGattCharacteristic serialChara=null;
    private BluetoothGatt serialGatt=null;
    private UUID MY_UUID = UUID.fromString("0000ffe1-0000-1000-8000-00805f9b34fb");
    private BluetoothManager bluetoothManager;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGattCallback myBluetoothGattCallback;
    private Context context;

    private boolean myScaning=false;
    private BluetoothAdapter.LeScanCallback leScanCallback;

    public BT_connection(BluetoothManager _bluetoothManager,Context _context){
       bluetoothManager=_bluetoothManager;
        leScanCallback=new BluetoothAdapter.LeScanCallback(){
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                String name=device.getName();
                Log.e("scan","fefeffefefe"+name);
                if(name.equals("HMSoft")){
                    scanLeDevice(false);
                    device.connectGatt(context, true, getGattCallback());
                }
            }

        };
       context=_context;
       getGattCallback();
    }


    public boolean checkSupportBle(){
        bluetoothAdapter=bluetoothManager.getAdapter();
        if (!context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Log.e("Error","system has no feature of BLE");
            return false;
        }
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Log.e("Error","need open bluetooth");
            return false;
        }
        return true;
    }

    // scan BLE drive
    public void scanLeDevice(boolean enable) {
        if (enable) {
            if(!myScaning){
                myScaning=true;
                if(serialGatt!=null){
                    serialGatt.close();
                }

                Log.e("scan","i will scan");
                bluetoothAdapter.startLeScan(leScanCallback);
                // 经过预定扫描期后停止扫描
                new Handler().postDelayed(new Runnable() {
                  @Override
                    public void run() {
                        Log.e("stop","auto stop scan");
                        myScaning=false;
                        bluetoothAdapter.stopLeScan(leScanCallback);
                    }
                }, 15000);
            }else{
               Log.e("BT_connection","scan exits");
            }
        } else {
            Log.e("stop","stop scan");
            myScaning=false;
            bluetoothAdapter.stopLeScan(leScanCallback);
        }
    }

    public BluetoothGattCallback getGattCallback(){
        return new  BluetoothGattCallback(){

            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                super.onConnectionStateChange(gatt, status, newState);
                if(newState==2){// contented;
                    contented=true;
                    protocol=new Protocol();//init a Protocol ;
                    serialGatt=gatt;
                    gatt.discoverServices();
                    Log.e("BT_connection","device connected");
                }else{
                    contented=false;
                    Log.e("BT_connection","device disconnected");
                }
                onAction.connectStateChange(newState);
            }

            @Override
            public void onServicesDiscovered(BluetoothGatt gatt, int status) {
                super.onServicesDiscovered(gatt, status);

                for(BluetoothGattService gatt_service : gatt.getServices()){
                    for(BluetoothGattCharacteristic gatt_chara : gatt_service.getCharacteristics()){
                        if(gatt_chara.getUuid().equals(MY_UUID)){
                            Log.e("BT_connection","get a ble device, now you can send data to ble device");
                            scanLeDevice(false);
                            serialChara=gatt_chara;
                            serialGatt.setCharacteristicNotification(serialChara,true);//open characteristic
                            onAction.getSerialChara();
                            break;
                        };
                    }
                }
            }

            @Override
            public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
                super.onReadRemoteRssi(gatt, rssi, status);
                //onAction.change(rssi+"");
            }

            @Override
            public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
                super.onCharacteristicChanged(gatt, characteristic);
                byte[] b=characteristic.getValue();
                Log.e("BT_connect","yffou have a bt goods");
                onAction.haveGoods(b);
            }
        };
    }
    public void writeCharacteristic(byte[] b){
        if(serialChara!=null){
            serialChara.setValue(b);
            for(int i=0;i<b.length;i++){
                Log.e("send","---------------"+b[i]);
            }
            serialGatt.writeCharacteristic(serialChara);
        }
    }

    public byte[] mergeCMD(byte[] a,byte[] b){
        byte[] newCMD=new byte[a.length+b.length];
        System.arraycopy(a, 0, newCMD, 0, a.length);
        System.arraycopy(b, 0, newCMD, a.length, b.length);
        return newCMD;
    }

    public void OnActionListener(OnAction _onAction){
        onAction=_onAction;
    };

    public static class OnAction{
        protected void connectStateChange(int state){ }
        protected void haveGoods(byte[] b){  }
        protected void getSerialChara(){}
    }
}
