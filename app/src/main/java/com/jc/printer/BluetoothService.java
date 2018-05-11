package com.jc.printer;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.gengcon.www.jcprinter.JCPrinter;

/**
 * Created by Simon on 2018/5/9.
 * 蓝牙连接Service
 */

public class BluetoothService extends IntentService {

    private LocalBroadcastManager mLocalBroadcastManager;

    private int status;

    public BluetoothService() {
        super("BluetoothService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (null == intent) return;
        Bluetooth mBluetooth = (Bluetooth) intent.getSerializableExtra("Bluetooth");
        if (null != mBluetooth) {
            if (JCPrinter.isSupported(mBluetooth.getDevicesName()) != 0) {
                if (JCPrinter.openPrinter(mBluetooth.getDevicesName(), mBluetooth.getDevicesAddress(), this)) {
                    // 成功
                    status = 1;
                } else {
                    // 失败
                    status = 0;
                }
                sendServiceStatus(status, mBluetooth);
            }
        }
    }

    // 发送服务状态信息
    private void sendServiceStatus(int status, Bluetooth mBluetooth) {
        Intent intent = new Intent();
        intent.setAction(Constant.BLUETOOTH_STATUS);
        intent.putExtra("status", status);
        intent.putExtra("Bluetooth", mBluetooth);
        mLocalBroadcastManager.sendBroadcast(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
