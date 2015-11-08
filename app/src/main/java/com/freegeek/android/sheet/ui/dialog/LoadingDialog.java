package com.freegeek.android.sheet.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.freegeek.android.sheet.R;

/**
 * Created by rtugeek@gmail.com on 2015/11/7.
 */
public class LoadingDialog extends Dialog {

    public LoadingDialog(Context context) {
        super(context);
        setContentView(R.layout.dialog_loading);

    }

    public LoadingDialog(Context context, int themeResId) {
        super(context, themeResId);
        setContentView(R.layout.dialog_loading);
    }
}
