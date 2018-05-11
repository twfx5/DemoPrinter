package com.jc.printer;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

/*
* 文件名：CustomDialog
* 描    述：通用的自定义布局的dialog
* 作    者：杨兴
* 时    间：2018.03.7
* 版    权：武汉精臣智慧标识科技有限公司
*/
public class CustomDialog extends Dialog {
    private int mLayoutId = 0;

    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int theme, int layoutId) {
        super(context, theme);
        mLayoutId = layoutId;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(mLayoutId);
    }

}
