package com.freegeek.android.sheet.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by rtugeek on 15-11-29.
 * 软件提示管理
 */
public class TipUtil {
    private String TIP_FIRST_LOCATION = "KEY_FIRST_LOCATION";
    private static TipUtil instance;
    private SharedPreferences sp;
    private SharedPreferences.Editor spe;
    public TipUtil(){

    }

    public TipUtil(Context context){
        sp = context.getSharedPreferences(APP.STRING.SHARED_NAME,Context.MODE_PRIVATE);
        spe = sp.edit();
    }

    public static TipUtil getInstance(Context context){
        if(instance == null) instance = new TipUtil(context);
        return instance;
    }

//    public void get
}
