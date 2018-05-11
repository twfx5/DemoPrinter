package com.jc.printer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.jc.printer.utils.SPUtils;

import java.util.ArrayList;

/**
 * Created by Administrator on 2018/3/7.
 */

public class BluetoothDeviceAdapter extends ArrayAdapter<Bluetooth> {

    private Context mContext;
    public BluetoothDeviceAdapter(@NonNull Context context, ArrayList<Bluetooth> bluetooth) {
        super(context, 0, bluetooth);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listView = convertView;
        if (listView == null) {
            listView = LayoutInflater.from(getContext()).inflate(R.layout.bluetooth_devices_list, null);
        }


        Bluetooth bluetooth = getItem(position);

        TextView devicesName = listView.findViewById(R.id.devices_name);
        devicesName.setText(bluetooth.getDevicesName());


        TextView devicesState = listView.findViewById(R.id.devices_state);
        devicesState.setText(bluetooth.isConnectState());
        String result = SPUtils.getString(mContext,"Bluetooth",null);
        if (!TextUtils.isEmpty(result) && result.contains(devicesName.getText())) {
            devicesState.setTextColor(mContext.getResources().getColor(R.color.green));
            devicesName.setTextColor(mContext.getResources().getColor(R.color.green));
        } else {
            devicesState.setTextColor(mContext.getResources().getColor(R.color.black));
            devicesName.setTextColor(mContext.getResources().getColor(R.color.black));
        }

//        ImageView bluetoothIcon = listView.findViewById(R.id.bluetooth_icon);
//        bluetoothIcon.setImageResource(R.drawable.bluetooth);

        return listView;
    }
}
