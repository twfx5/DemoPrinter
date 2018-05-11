package com.jc.printer;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gengcon.www.jcprinter.JCPrinter;
import com.jc.printer.utils.ClsUtils;
import com.jc.printer.utils.FastDialogUtils2;
import com.jc.printer.utils.ImageUtil;
import com.jc.printer.utils.SPUtils;
import com.jc.printer.utils.ToastUtils;
import com.jc.printer.utils.Tools;

import java.util.ArrayList;
import java.util.Set;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1001;

    private LocalBroadcastReceiver localBroadcastReceiver;

    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    public static String EXTRA_DEVICE_NAME = "device_name";

    String regex_B11 = "^B\\d{2}[a-zA-Z]?" + "-\\d{8,12}$";//B11-70206834
    String regex_S11 = "^S\\d{1}[a-zA-Z]?" + "-\\d{8,12}$";
    String regex_B3 = "^B[3]?" + "_\\d{4}[L]?$";//B3_7785
    String regex_M90 = "\"^[AST]\\\\d\" + \"-\\\\d{8,12}$\"";//T7 M90

    private ArrayList<Bluetooth> devicesList;
    private BluetoothDeviceAdapter mDevicesArrayAdapter;
    private BluetoothAdapter mBtAdapter;
    private ListView listView;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initData();
        initView();
        initBluetooth();
        doDiscovery();
    }

    /**
     * Start device discover with the BluetoothAdapter
     * 由本地蓝牙设备扫描远程蓝牙设备
     */
    private void doDiscovery() {
        if (mBtAdapter.isDiscovering()) {
            mBtAdapter.cancelDiscovery();
        }
        showProgress("刷新设备中...");
        // startDiscovery 是一个异步方法，调用后立即返回。
        // 该方法会进行蓝牙设备的搜索，持续12秒。
        mBtAdapter.startDiscovery();
    }

    private void initView() {
        btn = findViewById(R.id.btn);
        listView = findViewById(R.id.listView);
        mDevicesArrayAdapter = new BluetoothDeviceAdapter(this, devicesList);
        listView.setAdapter(mDevicesArrayAdapter);
        listView.setOnItemClickListener(pairedClickListener);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                devicesList.clear();
                doDiscovery();
            }
        });
    }

    private void initData() {
        //判断系统版本
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Tools.checkPermission(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
//        }

        // 注册广播
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//蓝牙开启关闭
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);//设备已连接
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);//已断开连接
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//蓝牙状态改变
        this.registerReceiver(mReceiver, filter);

        localBroadcastReceiver = new LocalBroadcastReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(localBroadcastReceiver, new IntentFilter(Constant.BLUETOOTH_STATUS));

        devicesList = new ArrayList<>();
    }

    private void initBluetooth() {
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBtAdapter != null) {
            if (!mBtAdapter.isEnabled()) {
                mBtAdapter.enable();
            } else
                getBlueInfo();

        } else {
            ToastUtils.show(this, "该手机不支持蓝牙");
            Log.i(TAG, "该手机不支持蓝牙");
        }
    }

    private void getBlueInfo() {
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                String name = device.getName();
                if (Pattern.compile(regex_B11, Pattern.CASE_INSENSITIVE).matcher(name).matches()
                        || Pattern.compile(regex_S11, Pattern.CASE_INSENSITIVE).matcher(name).matches()
                        || Pattern.compile(regex_B3, Pattern.CASE_INSENSITIVE).matcher(name).matches()) {
                    String deviceAddress = device.getAddress();
                    boolean isConnected = ClsUtils.isConnect(device);
                    String deviceState = isConnected ? "已连接" : "已配对";
                    if (isConnected)
                        devicesList.add(new Bluetooth(name, deviceState, R.drawable.bluetooth, deviceAddress));
                }
            }
            mDevicesArrayAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION://
                setPermission(permissions, grantResults, PERMISSION_REQUEST_COARSE_LOCATION);
                break;
            default:
                break;
        }
    }

    /**
     * 去系统设置界面开启权限
     *
     * @param permissions
     * @param grantResults
     * @param requestCode
     */
    private void setPermission(String[] permissions, int[] grantResults, final int requestCode) {
        //判断权限的结果，如果有被拒绝，就return
        if (!Tools.checkPermissionAllGranted(this, permissions)) {
            if (!Tools.checkPermissionRationale(this, permissions)) {//判断是否勾选了不再询问
                FastDialogUtils2.getInstance().createCustomDialog3(this, "提示", Tools.getPermissionAllGrantedStr(this, permissions, true), "取消", "确定", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, requestCode);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                });
            } else {
                finish();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != Activity.RESULT_OK) {
            boolean isGranted;
            if (requestCode == PERMISSION_REQUEST_COARSE_LOCATION) {//权限手动申请返回
                String[] permission = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
                //检查扫描相关权限是否设置成功
                isGranted = Tools.checkPermissionAllGranted(this, permission);
                if (!isGranted) {//设置失败
                    FastDialogUtils2.getInstance().createSingleButtonDialog2(this, "", Tools.getPermissionAllGrantedStr(this, permission, false), "确定", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });
                } else {//设置成功
                    initBluetooth();
                }
                return;
            }
        }
    }

    private AdapterView.OnItemClickListener pairedClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            Bluetooth mBluetooth = devicesList.get(i);
            doClickItem(mBluetooth);
        }
    };

    private void doClickItem(final Bluetooth mBluetooth) {
        if (mBluetooth.getDevicesAddress() == null || mBluetooth.getDevicesAddress().isEmpty() || !BluetoothAdapter.checkBluetoothAddress(mBluetooth.getDevicesAddress())) {
            //检查是否是有效的蓝牙地址
            return;
        }
        BluetoothDevice currentDevice = mBtAdapter.getRemoteDevice(mBluetooth.getDevicesAddress());
        try {
            Log.d(TAG, "doClickItem: " + mBluetooth.isConnectState());

            //设备已连接，则直接打印
            if (mBluetooth.isConnectState().equals("已连接")) {
//                ToastUtils.show(this, "该设备已连接，直接打印标签 " + mBluetooth.getDevicesName());
                print(mBluetooth);
                return;
            }

            //设备已配对-未连接，则先连接，再打印
            if (mBluetooth.isConnectState().equals("已配对-未连接") || mBluetooth.isConnectState().equals("已配对")) {
//                ToastUtils.show(this, "该设备已配对，连接蓝牙" + mBluetooth.getDevicesName());
                showProgress("蓝牙连接中...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doConnect(mBluetooth);
                    }
                }, 2000);
                return;
            }

            //设备未配对，点击配对
//            mBtAdapter.cancelDiscovery();
            if (ClsUtils.createBond(currentDevice.getClass(), currentDevice)) {
                ToastUtils.show(this, "已配对成功！！！");
                mDevicesArrayAdapter.notifyDataSetChanged();
                // 成功后连接蓝牙
                showProgress("蓝牙连接中...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        doConnect(mBluetooth);
                    }
                }, 2000);
            } else {
                ToastUtils.show(this, "配对失败！！！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Dialog dialog = null;

    private void showProgress(String tips) {
        if (null == dialog) {
            dialog = new Dialog(this, R.style.Theme_AppCompat_Dialog);
            dialog.setCanceledOnTouchOutside(false);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            View view = LayoutInflater.from(this).inflate(R.layout.dialog_progress_loading, null);
            TextView tipsTv = view.findViewById(R.id.tv_tips);
            dialog.setContentView(view);
            tipsTv.setText(tips);
        }
        if (null != dialog && !dialog.isShowing()) {
            dialog.show();
        }
    }

    private void dismiss() {
        if (null != dialog) {
            if (dialog.isShowing()) {
                dialog.cancel();
                dialog.dismiss();
            }
            dialog = null;
        }
    }


    private void print(Bluetooth bluetooth) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.text);
        String flag = JCPrinter.printerResources(60, 35,
                0, ImageUtil.compressImage(bitmap), 1, MainActivity.this);
        Log.d(TAG, "doClickItem: print = " + flag);
        String result = SPUtils.getString(this, "Bluetooth", null);
        // 把打印成功的设备缓存下来
        if (flag.equals("SUCCESS")) {
            if (TextUtils.isEmpty(result)) {
                SPUtils.putString(this, "Bluetooth", bluetooth.getDevicesName());
            } else {
                SPUtils.putString(this, "Bluetooth", result + "," + bluetooth.getDevicesName());
            }
        }
    }

    public void doConnect(final Bluetooth mBluetooth) {
        Intent intent = new Intent(this, BluetoothService.class);
        intent.putExtra("Bluetooth", mBluetooth);
        startService(intent);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.d(TAG, "action:" + action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {  //发现新设备
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //需要精简，打印部分代码，无需判断是否支持，不支持设备无法搜索到
                try {
                    if (Pattern.compile(regex_B11, Pattern.CASE_INSENSITIVE).matcher(device.getName()).matches()
                            || Pattern.compile(regex_S11, Pattern.CASE_INSENSITIVE).matcher(device.getName()).matches()
                            || Pattern.compile(regex_B3, Pattern.CASE_INSENSITIVE).matcher(device.getName()).matches() || Pattern.compile(regex_M90, Pattern.CASE_INSENSITIVE).matcher(device.getName()).matches()) {
                        if (device.getBondState() != BluetoothDevice.BOND_BONDED && device.getBluetoothClass().getDeviceClass() == 1664) {
                            String name = device.getName();
                            String deviceAddress = device.getAddress();
                            String deviceState = device.getBondState() == BluetoothDevice.BOND_BONDED ? "已配对-未连接" : "未配对";
                            boolean flag = false;
                            for (Bluetooth blue : devicesList) {
                                if (blue.getDevicesAddress().equals(deviceAddress)) {
                                    flag = true;
                                }
                            }
                            if (!flag) {
                                devicesList.add(new Bluetooth(name, deviceState, R.drawable.bluetooth, deviceAddress));
                                mDevicesArrayAdapter.notifyDataSetChanged();
                            }
                        } else {
                            String name = device.getName();
                            String deviceAddress = device.getAddress();
                            String deviceState = device.getBondState() == BluetoothDevice.BOND_BONDED ? "已配对-未连接" : "未配对";
                            boolean flag = false;
                            for (Bluetooth blue : devicesList) {
                                if (blue.getDevicesAddress().equals(deviceAddress)) {
                                    flag = true;
                                }
                            }
                            if (!flag) {
                                devicesList.add(new Bluetooth(name, deviceState, R.drawable.bluetooth, deviceAddress));
                                mDevicesArrayAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) { //搜索结束
                if (mDevicesArrayAdapter.getCount() == 0) {
                    String noDevices = "未找到新设备";
                    ToastUtils.show(MainActivity.this, noDevices);
//                    mDevicesArrayAdapter.add(new Bluetooth(noDevices, "", R.drawable.bluetooth, "",false));
                } else {
                    ToastUtils.show(MainActivity.this, "查找设备结束");
                }
                dismiss();
            } else if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blueState) {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        break;
                    case BluetoothAdapter.STATE_ON:
//                        switchs.setChecked(true);
                        getBlueInfo();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_OFF:
//                        switchs.setChecked(false);
                        devicesList.clear();
//                        connected_devices_name.setText("无");
                        mDevicesArrayAdapter.notifyDataSetChanged();
                        break;
                }
            }
            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String name = device.getName();
                //bug 存在name 等于null的问题
                if (Pattern.compile(regex_B11, Pattern.CASE_INSENSITIVE).matcher(name).matches()
                        || Pattern.compile(regex_S11, Pattern.CASE_INSENSITIVE).matcher(name).matches()
                        || Pattern.compile(regex_B3, Pattern.CASE_INSENSITIVE).matcher(name).matches()) {
                    String deviceAddress = device.getAddress();
                    String deviceState = "已配对";
                    for (int i = devicesList.size() - 1; i >= 0; i--) {
                        Bluetooth bluetooth = devicesList.get(i);
                        if (bluetooth.getDevicesAddress().equals(deviceAddress)) {
                            devicesList.remove(i);
                            break;
                        }
                    }
                    Bluetooth bluetooth = new Bluetooth(name, deviceState, R.drawable.bluetooth, deviceAddress);
                    devicesList.add(0, bluetooth);
                    mDevicesArrayAdapter.notifyDataSetChanged();
                }
//                mBtAdapter.cancelDiscovery();
//                intent.putExtra(EXTRA_DEVICE_NAME, name);
//                intent.putExtra(EXTRA_DEVICE_ADDRESS, deviceAddress);
//                DeviceListActivity.this.setResult(Activity.RESULT_OK, intent);
//                finish();
            } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {

                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String deviceName = device.getName();
                String deviceState = device.getBondState() == BluetoothDevice.BOND_BONDED ? "已配对" : "未配对";

                for (Bluetooth bluetooth : devicesList) {
                    if (bluetooth.getDevicesName().equals(deviceName)) {
                        bluetooth.setConnectState(deviceState);
                        break;
                    }
                }
                mDevicesArrayAdapter.notifyDataSetChanged();
//                showShortToast(device.getName() + "断开");

            }
//
//
//            else if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
//                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
//                String name = device.getName();
//                String deviceAddress = device.getAddress();
//                String deviceState = device.getBondState() == BluetoothDevice.BOND_BONDED ? "已配对-未连接" : "未配对";
//                int state = device.getBondState();
//                switch (state) {
//                    case BluetoothDevice.BOND_NONE:
//                        mView.setVisibility(View.GONE);
//                        Log.v("测试1", device.getBondState() + "");
//                        break;
//                    case BluetoothDevice.BOND_BONDING:
//                        Log.v("测试2", device.getBondState() + "");
//                        break;
//                    case BluetoothDevice.BOND_BONDED:
//                        mView.setVisibility(View.GONE);
//                        Log.v("测试3", device.getBondState() + "");
//                        mDevicesList.remove(new Bluetooth(name, "未配对", R.drawable.bluetooth, deviceAddress));
//                        mDevicesList.toString();
//                        mBluetoothDeviceAdapter.notifyDataSetChanged();
//                        break;
//                }
//
//
//            }
        }
    };

    // 广播 蓝牙的连接状态
    private class LocalBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            dismiss();
            if (intent == null) {
                Log.d(TAG, "onReceive: LocalBroadcastReceiver intent is null");
                return;
            }
            String action = intent.getAction();
            if (null != action && action.equals(Constant.BLUETOOTH_STATUS)) {
                progressBiz(intent);
            }
        }
    }

    private void progressBiz(Intent intent) {
        mDevicesArrayAdapter.notifyDataSetChanged();
        int status = intent.getIntExtra("status", 0);
        if (status == 0) {
            ToastUtils.show(MainActivity.this, "连接失败！！！");
        } else {
            if (null != intent.getSerializableExtra("Bluetooth")) {
                Bluetooth mBluetooth = (Bluetooth) intent.getSerializableExtra("Bluetooth");
                Bluetooth temp = new Bluetooth(mBluetooth.getDevicesName(), "已连接", R.drawable.bluetooth, mBluetooth.getDevicesAddress());

                for (int i = devicesList.size() - 1; i >= 0; i--) {
                    Bluetooth bluetooth = devicesList.get(i);
                    if (bluetooth.getDevicesAddress().equals(mBluetooth.getDevicesAddress())) {
                        devicesList.remove(i);
                        break;
                    }
                }
                devicesList.add(0, temp);
                mDevicesArrayAdapter.notifyDataSetChanged();

                String address = temp.getDevicesAddress();
                String name = temp.getDevicesName();
                Intent i = new Intent();
                i.putExtra(EXTRA_DEVICE_NAME, name);
                i.putExtra(EXTRA_DEVICE_ADDRESS, address);
                setResult(Activity.RESULT_OK, i);
                ToastUtils.show(MainActivity.this, "连接成功");
                print(temp);// 打印
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mBtAdapter != null) {
            mBtAdapter.cancelDiscovery();
        }
        this.unregisterReceiver(mReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(localBroadcastReceiver);
    }

}
