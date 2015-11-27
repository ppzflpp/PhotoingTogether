package com.freegeek.android.sheet.bean;

/**
 * Created by rtugeek@gmail.com on 2015/11/3.
 */
public class Event {
    private Object tag;
    private int eventCode;
    public static final int EVENT_USER_SIGN_IN = 1;//登录
    public static final int EVENT_USER_SIGN_UP = 2;//注册
    public static final int EVENT_USER_SIGN_OUT = 2;//注销
    public static final int EVENT_USER_PROFILE_UPDATE = 4;//注销
    public static final int EVENT_GET_CAMERA_SHEET_PHOTO = 5;//通过相机发布图片
    public static final int EVENT_PUSH_SHEET = 6;//发布了一张
    public static final int EVENT_GET_LOCATION = 7;//获取到地理位置
    public Event(int eventCode) {
        this.eventCode = eventCode;
    }

    public Event(Object tag, int eventCode) {
        this.tag = tag;
        this.eventCode = eventCode;
    }

    public Object getTag() {
        return tag;
    }

    public void setTag(Object tag) {
        this.tag = tag;
    }

    public int getEventCode() {
        return eventCode;
    }

    public void setEventCode(int eventCode) {
        this.eventCode = eventCode;
    }

}
