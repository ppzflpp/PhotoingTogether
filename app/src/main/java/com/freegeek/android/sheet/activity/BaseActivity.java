package com.freegeek.android.sheet.activity;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;


import com.freegeek.android.sheet.R;
import com.freegeek.android.sheet.bean.Event;
import com.freegeek.android.sheet.bean.User;
import com.freegeek.android.sheet.service.LocationService;
import com.freegeek.android.sheet.service.UserService;
import com.freegeek.android.sheet.ui.dialog.LoadingDialog;
import com.freegeek.android.sheet.ui.dialog.LoginDialog;
import com.freegeek.android.sheet.util.APP;
import com.orhanobut.logger.Logger;
import com.rey.material.widget.SnackBar;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMServiceFactory;
import com.umeng.socialize.controller.UMSocialService;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.sso.EmailHandler;
import com.umeng.socialize.sso.QZoneSsoHandler;
import com.umeng.socialize.sso.SmsHandler;
import com.umeng.socialize.sso.UMQQSsoHandler;
import com.umeng.socialize.weixin.controller.UMWXHandler;

import cn.bmob.v3.BmobUser;
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
        Logger.i("eventCode:" + event.getEventCode());
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

    protected void startActivity(Class<?> activity){
        startActivity(new Intent(this,activity));
    }

    protected void startService(Class<?> service){
        startService(new Intent(this, service));
    }

    /**
     * 登陆提醒
     * @param activity
     */
    public static void showLoginTip(final BaseActivity activity){
        SnackBar mSnackBar = SnackBar
                .make(activity)
                .applyStyle(R.style.SnackBarMultiLine)
                .text(activity.getString(R.string.tip_to_login))
                .actionText(activity.getString(R.string.login))
                .actionClickListener(new SnackBar.OnActionClickListener() {
                    @Override
                    public void onActionClick(SnackBar sb, int actionId) {
                        new LoginDialog(activity).show();
                    }
                })
                .duration(5000);
        mSnackBar.show(activity);
    }

    public User getCurrentUser(){
        return UserService.user;
    }

    public void share(String content,String url){
        // 首先在您的Activity中添加如下成员变量
        final UMSocialService mController = UMServiceFactory.getUMSocialService("com.umeng.share");
        // 设置分享内容
        mController.setShareContent(TextUtils.isEmpty(content) ? "  ":content);
        // 设置分享图片, 参数2为图片的url地址
        mController.setShareMedia(new UMImage(getActivity(),url));
        mController.getConfig().removePlatform( SHARE_MEDIA.WEIXIN, SHARE_MEDIA.WEIXIN_CIRCLE,SHARE_MEDIA.SINA,SHARE_MEDIA.TENCENT);
//        // 添加微信平台
//        String appID = "wx967daebe835fbeac";
//        String appSecret = "5fa9e68ca3970e87a1f83e563c8dcbce";
//        UMWXHandler wxHandler = new UMWXHandler(getActivity(),appID,appSecret);
//        wxHandler.addToSocialSDK();
//        // 添加微信朋友圈
//        UMWXHandler wxCircleHandler = new UMWXHandler(getActivity(),appID,appSecret);
//        wxCircleHandler.setToCircle(true);
//        wxCircleHandler.addToSocialSDK();
//参数1为当前Activity，参数2为开发者在QQ互联申请的APP ID，参数3为开发者在QQ互联申请的APP kEY.
        //添加QQ
        UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(getActivity(), "100424468",
                "c7394704798a158208a74ab60104f0ba");
        qqSsoHandler.addToSocialSDK();
        //添加QQ空间
        QZoneSsoHandler qZoneSsoHandler = new QZoneSsoHandler(getActivity(), "100424468",
                "c7394704798a158208a74ab60104f0ba");
        qZoneSsoHandler.addToSocialSDK();
        //添加短信
        SmsHandler smsHandler = new SmsHandler();
        smsHandler.setTargetUrl(null);
        smsHandler.addToSocialSDK();
        // 添加email
        EmailHandler emailHandler = new EmailHandler();
        emailHandler.addToSocialSDK();

        mController.openShare(this,false);

    }

}
