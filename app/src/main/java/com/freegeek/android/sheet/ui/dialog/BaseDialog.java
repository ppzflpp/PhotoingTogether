package com.freegeek.android.sheet.ui.dialog;

import android.content.Intent;
import android.content.SharedPreferences;

import com.freegeek.android.sheet.R;
import com.freegeek.android.sheet.activity.BaseActivity;
import com.freegeek.android.sheet.bean.User;
import com.freegeek.android.sheet.util.APP;
import com.rey.material.app.Dialog;

import cn.bmob.v3.BmobUser;

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

    protected User getCurrentUser(){
        return BmobUser.getCurrentUser(getActivity(),User.class);
    }

    protected void startActivity(Class<?> activity){
        context.startActivity(new Intent(context,activity));
    }
}
