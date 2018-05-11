package com.jc.printer;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.jc.printer.utils.ClsUtils;
import com.jc.printer.utils.ToastUtils;

/**
 * Created by Simon on 2018/5/10.
 * 自动配对，不输入pin码
 * 调用ClsUtils.createBond(btDevice.getClass(), btDevice)方法来进行配对;该方法执行后，系统会收到一个请求配对的广播
 * 在源码里面有一个自动配对的方法，也就是把pin值自动设为“0000”
 */

public class PairingRequest extends BroadcastReceiver {
    String strPsw = "0000";
    final String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_PAIRING_REQUEST)) {

            final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                try {
                    //1.确认配对
                    ClsUtils.setPairingConfirmation(device.getClass(), device, true);
                    //2.中止广播
                    Log.i("order...", "isOrderedBroadcast: "+isOrderedBroadcast()+", isInitialStickyBroadcast: "+isInitialStickyBroadcast());
                    abortBroadcast();
                    //3.调用setPin方法进行配对
                    ClsUtils.setPin(device.getClass(), device, strPsw);
//                    ClsUtils.cancelPairingUserInput(device.getClass(),device);
                    // 一般情况下不要和setPin（setPasskey、setPairingConfirmation、setRemoteOutOfBandData）一起用，这几个方法都会remove掉map里面的key:value（也就是互斥的）。
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    ToastUtils.show(context,"请求连接错误 ..." + device.getName());
                }
            }
        }
    }
}
