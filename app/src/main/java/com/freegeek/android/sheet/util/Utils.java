package com.freegeek.android.sheet.util;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

/**
 * Created by rtugeek on 15-12-2.
 */
public class Utils  {
    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

//-------------------------------------获取屏幕宽高-----START-----------------------------------------------

    /**获取屏幕宽度
     * @param context
     * @return
     */
    public static int getScreenWidth(Context context){
        return  getDisplayMetrics(context).widthPixels;
    }

    public static DisplayMetrics getDisplayMetrics(Context context){
        WindowManager windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        return  displayMetrics;
    }

    /**获取屏幕高度
     * @param context
     * @return
     */
    public static int getScreenHeight(Context context){
        return  getDisplayMetrics(context).heightPixels;
    }

//-------------------------------------获取屏幕宽高-----END-----------------------------------------------

    /**
     * 判断是否是平板
     * @param context
     * @return
     */
    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 判断是否是竖屏
     * @param context
     * @return
     */
    public static boolean isPortrait(Context context) {
        if (context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
        {
            return false;
        }
        return false;
    }
}
