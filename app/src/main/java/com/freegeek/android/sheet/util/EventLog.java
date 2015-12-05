package com.freegeek.android.sheet.util;

import android.app.Activity;

import com.freegeek.android.sheet.R;
import com.orhanobut.logger.Logger;
import com.rey.material.widget.SnackBar;


/**
 * Created by Administrator on 2014/11/8.
 */
public class EventLog {
    public static boolean debug = true;
    
    public static void BmobToastError(int code,Activity activity){
        BmobToastError(code,"",activity);
    }

    public static void BmobToastError(int code,String content,Activity activity){
        Logger.i("BmobError:   " + code +"-Content" + content);
        SnackBar snackBar = SnackBar.make(activity).applyStyle(R.style.SnackBarMultiLine);
        switch (code){
            case 205:
                snackBar.text(activity.getString(R.string.this_mail_not_be_used));
                break;
            case 206://ContentUser cannot be altered without sessionToken Error.
                break;
            case 301:
                snackBar.text(activity.getString(R.string.error_null_username_or_password));
                break;
            case 302://Request redirected
                snackBar.text(activity.getString(R.string.error_data));
                break;
            case 9013://BmobObject（数据表名称）格式不正确
                break;
            case 9015:
                snackBar.text(activity.getString(R.string.error_data));
                break;
            case 9016:
                snackBar.text(activity.getString(R.string.error_no_network));
                break;
            case 202:
                snackBar.text(activity.getString(R.string.error_username_used));
                break;
            case 9010:
                snackBar.text(activity.getString(R.string.error_network_timeout));
                break;
            case 9008:
                snackBar.text(activity.getString(R.string.file_not_existed));
                break;
            case 101:
                snackBar.text(activity.getString(R.string.error_username_password_error));
                break;
        }
        if(snackBar != null)snackBar.show(activity);
    }

}
