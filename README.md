# DemoPrinter
1.使用原生的方式扫描蓝牙（搜索时，发广播）
2.自动配对和连接：(配对时，调用createBond方法，该方法执行后，系统会收到一个请求配对的广播)
a.确认配对
ClsUtils.setPairingConfirmation(device.getClass(), device, true);
b.中止广播
 abortBroadcast();
c.调用setPin方法进行配对
ClsUtils.setPin(device.getClass(), device, strPsw);
3.调用JCPrint打印标签

=======================================================================================================================================

程序运行流程：https://blog.csdn.net/qq_25827845/article/details/52400782

1、点击按钮，判断蓝牙是否打开，执行bluetoothAdapter.startDiscovery();

由本地蓝牙设备扫描远程蓝牙设备，startDiscovery（）方法是一个异步方法，调用后立即返回。

该方法会进行蓝牙设备的搜索，持续12秒。

2、搜索时，系统会发送3个广播，分别为：

ACTION_DISCOVERY_START:开始搜索 、

ACTION_DISCOVERY_FINISHED:搜索结束、 

ACTION_FOUND:找到设备，该Intent中包含两个extra fields;         

3、在广播接收类中BluetoothReceiver.java中，当设备找到之后会执行其onReceive方法。

4、String action = intent.getAction(); //得到action，

第一次action的值为BluetoothDevice.ACTION_FOUND，

当找到的设备是我们目标蓝牙设备时，调用createBond方法来进行配对。ClsUtils.createBond(btDevice.getClass(), btDevice);

该方法执行后，系统会收到一个请求配对的广播，即android.bluetooth.device.action.PAIRING_REQUEST。

最后进行自动配对操作。

5、配对操作借助工具类ClsUtils.java得到了Android蓝牙API中隐藏的方法，实现自动配对，不弹出配对框的功能。
