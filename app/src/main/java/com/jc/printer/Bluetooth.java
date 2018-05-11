package com.jc.printer;

import java.io.Serializable;

/**
 * Created by Administrator on 2018/3/7.
 */

public class Bluetooth implements Serializable{
    private String mDevicesName;
    private String mConnectState;

    private String mDevicesAddress;

    private boolean isTested;// 已被测试

    public String getDevicesAddress() {
        return mDevicesAddress;
    }

    private int mImageResources;

    public Bluetooth(String mDevicesName, String mConnectState, int mImageResources, String mDevicesAddress) {
        this.mDevicesName = mDevicesName;
        this.mConnectState = mConnectState;
        this.mImageResources = mImageResources;
        this.mDevicesAddress = mDevicesAddress;
    }

    public String getDevicesName() {
        return mDevicesName;
    }


    public String isConnectState() {
        return mConnectState;
    }

    public void setConnectState(String mConnectState) {
        this.mConnectState = mConnectState;
    }

    public int getImageResources() {
        return mImageResources;
    }

    public boolean isTested() {
        return isTested;
    }

    public void setTested(boolean tested) {
        isTested = tested;
    }
}
