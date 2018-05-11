package com.jc.printer.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;


/**
 * Created by stack on 2018/1/4.
 * Toast工具类，防止重复显示
 */

public class ToastUtils {

    private static Handler HANDLER = new Handler(Looper.getMainLooper());

    private static Toast sToast;

    public static void show(final Context context, final String message) {
        HANDLER.post(new Runnable() {
            @Override
            public void run() {
                if (sToast == null) {
                    sToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
                }else {
                    sToast.setText(message);
                }
                sToast.show();
            }
        });
    }

    /**
     * 取消吐司显示
     */
    public static void cancel() {
        if (sToast != null) {
            sToast.cancel();
            sToast = null;
        }
    }
}
