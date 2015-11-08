package com.freegeek.android.sheet.ui.dialog;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.freegeek.android.sheet.R;
import com.freegeek.android.sheet.activity.BaseActivity;
import com.freegeek.android.sheet.util.APP;
import com.rey.material.app.Dialog;

/**
 * Created by rtugeek@gmail.com on 2015/11/7.
 */
public class BaseDialog extends Dialog
{


    private BaseActivity context;
    protected SharedPreferences sp;
    protected SharedPreferences.Editor spe;
    public BaseDialog(BaseActivity context) {
        super(context, R.style.Material_App_Dialog_Light);
        this.context = context;
        sp = context.getSharedPreferences(APP.STRING.SHARED_NAME,0);
        spe = sp.edit();
    }

    protected String getString(int id){
        return getContext().getString(id);
    }

    public void showLoading(){
        context.showLoading();
    };

    public void dismissLoading(){
        context.dismissLoading();
    };

    public void showToast(int id){
        context.showToast(id);
    }

    public void showToast(String content){
        context.showToast(content);
    }

    public BaseActivity getActivity(){
        return context;
    }
}
