package com.jc.printer.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jc.printer.CustomDialog;
import com.jc.printer.R;
import com.jc.printer.RippleView;


/*
* 文件名：FastDialogUtils2
* 描    述：对话框快速搭建
* 作    者：杨兴
* 时    间：2018.03.15
* 版    权：武汉精臣智慧标识科技有限公司
*/
public class FastDialogUtils2 implements DialogInterface.OnDismissListener {

    /**
     * FastDialogUtils的实例。
     */
    private static FastDialogUtils2 mFastDialogUtils;

    /**
     * 获取FastDialogUtils的实例。
     *
     * @return FastDialogUtils的实例。
     */
    public static FastDialogUtils2 getInstance() {
        if (mFastDialogUtils == null) {
            mFastDialogUtils = new FastDialogUtils2();
        }
        return mFastDialogUtils;
    }

    private PopupWindow mHeadPopupWindow;

    /**
     * @return void
     * @Description 通用对话框（取消+确认）
     */
    public void createCustomDialog(Context context, String dialogContent,
                                   final OnClickListener positiveCallBack) {
        createCustomDialog(context, null, dialogContent, null, null,
                positiveCallBack);
    }

    /**
     * @return void
     * @Description 通用对话框（取消+确认）
     */
    public void createCustomDialog(Context context, String title,
                                   String dialogContent, String nagetive, String positive,
                                   final OnClickListener positiveCallBack) {
        final CustomDialog dialog = new CustomDialog(context,
                R.style.custom_dialog_style,
                R.layout.custom_common_dialog_layout);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        dialog.show();

        if (!TextUtils.isEmpty(title)) {
            ((TextView) dialog.findViewById(R.id.tv_warn_title)).setText(title);
        }

        if (!TextUtils.isEmpty(dialogContent)) {
            ((TextView) dialog.findViewById(R.id.tv_dialog_content))
                    .setText(dialogContent);
        }

        if (!TextUtils.isEmpty(nagetive)) {
            ((TextView) dialog.findViewById(R.id.tv_cancel)).setText(nagetive);
        }

        if (!TextUtils.isEmpty(positive)) {
            ((TextView) dialog.findViewById(R.id.tv_ok)).setText(positive);
        }

        dialog.findViewById(R.id.rv_ok).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (positiveCallBack != null) {
                                    positiveCallBack.onClick(null);
                                }
                                dialog.dismiss();
                            }
                        }, RippleView.DURATION);

                    }
                });
        dialog.findViewById(R.id.rv_cancel).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                            }
                        }, RippleView.DURATION);
                    }
                });
    }

    /**
     * @return void
     * @author 杨兴
     * @Description 通用对话框（单个button）
     * @date 2015年7月17日 上午9:56:14
     */
    public void createSingleButtonDialog2(Context context, String title,
                                          String dialogContent, String positive, final OnClickListener onclickListener) {
        final CustomDialog dialog = new CustomDialog(context,
                R.style.custom_dialog_style,
                R.layout.custom_single_button_dialog_layout2);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();

        if (!TextUtils.isEmpty(title)) {
            ((TextView) dialog.findViewById(R.id.tv_warn_title)).setText(title);
        }

        if (!TextUtils.isEmpty(dialogContent)) {
            ((TextView) dialog.findViewById(R.id.tv_dialog_content))
                    .setText(dialogContent);
        }

        if (!TextUtils.isEmpty(positive)) {
            ((TextView) dialog.findViewById(R.id.tv_ok)).setText(positive);
        }

        dialog.findViewById(R.id.rv_ok).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (onclickListener != null) {
                                    onclickListener.onClick(dialog.findViewById(R.id.rv_ok));
                                }
                                dialog.dismiss();
                            }
                        }, RippleView.DURATION);
                    }
                });
    }

    /**
     * @return void
     * @Description 通用对话框（取消+确认）
     */
    public void createCustomDialog3(Context context, String title,
                                    String dialogContent, String nagetive, String positive,
                                    final OnClickListener positiveCallBack,
                                    final OnClickListener nagetiveCallBack) {
        final CustomDialog dialog = new CustomDialog(context,
                R.style.custom_dialog_style,
                R.layout.custom_common_dialog_layout3);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(true);
        dialog.show();

        if (!TextUtils.isEmpty(title)) {
            ((TextView) dialog.findViewById(R.id.tv_warn_title)).setText(title);
        }

        if (!TextUtils.isEmpty(dialogContent)) {
            ((TextView) dialog.findViewById(R.id.tv_dialog_content))
                    .setText(dialogContent);
            ((TextView) dialog.findViewById(R.id.tv_dialog_content2))
                    .setText(dialogContent);
            if ("版本更新".equals(title)) {
                ((TextView) dialog.findViewById(R.id.tv_dialog_content)).setVisibility(View.GONE);
                ((TextView) dialog.findViewById(R.id.tv_dialog_content2)).setVisibility(View.VISIBLE);
            }
        }

        if (!TextUtils.isEmpty(nagetive)) {
            ((TextView) dialog.findViewById(R.id.tv_cancel)).setText(nagetive);
        }

        if (!TextUtils.isEmpty(positive)) {
            ((TextView) dialog.findViewById(R.id.tv_ok)).setText(positive);
        }

        dialog.findViewById(R.id.rv_ok).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                dialog.dismiss();
                                if (positiveCallBack != null) {
                                    positiveCallBack.onClick(null);
                                }
                            }
                        }, RippleView.DURATION);

                    }
                });
        dialog.findViewById(R.id.rv_cancel).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (nagetiveCallBack != null) {
                                    nagetiveCallBack.onClick(null);
                                }
                                dialog.dismiss();
                            }
                        }, RippleView.DURATION);
                    }
                });
    }


    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        if (null != onDialogDismissListener)
            onDialogDismissListener.onDialogDismiss(isFinish);
    }

    /**
     * 自定义Dialog监听器
     */
    public interface OnDialogDismissListener {

        void onDialogDismiss(boolean isFinish);
    }

    private OnDialogDismissListener onDialogDismissListener;
    private boolean isFinish = true;
}
