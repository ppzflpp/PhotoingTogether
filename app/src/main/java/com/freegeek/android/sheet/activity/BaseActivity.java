package com.freegeek.android.sheet.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;


import com.freegeek.android.sheet.R;
import com.freegeek.android.sheet.bean.Event;
import com.freegeek.android.sheet.ui.dialog.LoadingDialog;
import com.freegeek.android.sheet.util.APP;
import com.orhanobut.logger.Logger;
import com.rey.material.widget.SnackBar;

import de.greenrobot.event.EventBus;

/**
 * Created by rtugeek@gmail.com on 2015/11/3.
 */
public class BaseActivity extends AppCompatActivity {
    protected SnackBar mSnackBar;
    protected SharedPreferences sp;
    protected SharedPreferences.Editor spe;
    protected LoadingDialog mLoadingDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        sp = getSharedPreferences(APP.STRING.APP_NAME, 0);
        spe = sp.edit();
        mSnackBar = SnackBar.make(this).applyStyle(R.style.SnackBarMultiLine);
        mLoadingDialog = new LoadingDialog(this,R.style.LoadingDialog);
    }

    public void onEvent(Event event){
        Logger.i("eventCode:" + event.getEventCode()+"-eventTag" + event.getTag().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if(mLoadingDialog.isShowing()) mLoadingDialog.dismiss();
    }

    protected Activity getActivity(){
        return this;
    }



    public void showToast(int id){
        mSnackBar.text(getString(id)).show(this);
    }

    public void showToast(String content){
        mSnackBar.text(content).show(this);
    }


    public void showLoading(){
        if(!mLoadingDialog.isShowing()) mLoadingDialog.show();
    }

    public void dismissLoading() {
        if(mLoadingDialog.isShowing()) mLoadingDialog.dismiss();
    }

    public FragmentTransaction getFragmentTransaction(){
        return getSupportFragmentManager().beginTransaction();
    }
}
