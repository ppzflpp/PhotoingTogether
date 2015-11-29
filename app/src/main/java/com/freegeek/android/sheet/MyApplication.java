package com.freegeek.android.sheet;

import android.app.Application;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.freegeek.android.sheet.bean.User;
import com.freegeek.android.sheet.util.APP;
import com.freegeek.android.sheet.util.EventLog;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.HashCodeFileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.decode.BaseImageDecoder;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;
import com.orhanobut.logger.LogLevel;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.AnalyticsConfig;

import java.io.File;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by rtugeek@gmail.com on 2015/11/3.
 */
public class MyApplication extends Application {
    public static BDLocation location;
    public static User currentUser;
    public static MyApplication instance;
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        Logger
                .init(getString(R.string.app_name))                 // default PRETTYLOGGER or use just init()
                .hideThreadInfo()               // default shown
                .logLevel(LogLevel.FULL);        // default LogLevel.FULL

        //友盟数据统计
        AnalyticsConfig.setAppkey(this, APP.STRING.APP_KEY_UMENG);

        //使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
        Bmob.initialize(this, APP.STRING.APPLICATION_ID_BMOB);

        //初始化Android-Universal-Image-Loader
        File cacheDir = StorageUtils.getCacheDirectory(this);
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
                .memoryCacheExtraOptions(480, 800) // default = device screen dimensions
                .diskCacheExtraOptions(480, 800, null)
                .threadPoolSize(3) // default
                .threadPriority(Thread.NORM_PRIORITY - 2) // default
                .tasksProcessingOrder(QueueProcessingType.FIFO) // default
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
                .memoryCacheSize(2 * 1024 * 1024)
                .memoryCacheSizePercentage(13) // default
                .diskCache(new UnlimitedDiskCache(cacheDir)) // default
                .diskCacheSize(50 * 1024 * 1024)
                .diskCacheFileCount(100)
                .diskCacheFileNameGenerator(new HashCodeFileNameGenerator()) // default
                .imageDownloader(new BaseImageDownloader(this)) // default
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple()) // default
                .writeDebugLogs()
                .build();
        ImageLoader.getInstance().init(config);

        //百度地图
        SDKInitializer.initialize(getApplicationContext());

        currentUser = BmobUser.getCurrentUser(this,User.class);
    }

    /**
     * 更新用户地理位置
     */
    public static void updateUserLocation(){
        if(currentUser !=null && location != null){
            BmobGeoPoint bmobGeoPoint = new BmobGeoPoint();
            bmobGeoPoint.setLongitude(location.getLongitude());
            bmobGeoPoint.setLatitude(location.getLatitude());
            currentUser.setLocation(bmobGeoPoint);
            currentUser.update(instance, currentUser.getObjectId(), new UpdateListener() {
                @Override
                public void onSuccess() {
                    Logger.i("UPDATE USER LOCATION SUCCEED!");
                }

                @Override
                public void onFailure(int i, String s) {
                    Logger.i(s);
                }
            });
        }
    }
}
