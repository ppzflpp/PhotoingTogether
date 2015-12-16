package com.freegeek.android.sheet;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.freegeek.android.sheet.service.UserService;
import com.freegeek.android.sheet.util.APP;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.socialize.common.SocializeConstants;

import cn.bmob.v3.Bmob;

/**
 * Created by rtugeek@gmail.com on 2015/11/3.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Logger
                .init(getString(R.string.app_name))                 // default PRETTYLOGGER or use just init()
                .hideThreadInfo()               // default shown
                .logLevel(LogLevel.FULL);        // default LogLevel.FULL

        //友盟数据统计
        AnalyticsConfig.setAppkey(this, APP.STRING.APP_KEY_UMENG);
        SocializeConstants.APPKEY = APP.STRING.APP_KEY_UMENG;

        //使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
        Bmob.initialize(this, APP.STRING.APPLICATION_ID_BMOB);
        UserService.initialize(this);

        //百度地图
        SDKInitializer.initialize(getApplicationContext());
    }


}
