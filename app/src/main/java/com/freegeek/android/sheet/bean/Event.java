package com.freegeek.android.sheet.bean;

/**
 * Created by rtugeek@gmail.com on 2015/11/3.
 */
public class Event {
    private Object tag;
    private int eventCode;
    /**
     * 登陆
     */
    public static final int EVENT_USER_SIGN_IN = 1;
    /**
     * 注册
     */
    public static final int EVENT_USER_SIGN_UP = 2;
    /**
     * 注销
     */
    public static final int EVENT_USER_SIGN_OUT = 2;
    /**
     *用户更新资料
     */
    public static final int EVENT_USER_PROFILE_UPDATE = 4;
    /**
     *通过相机发布图片
     */
    public static final int EVENT_GET_CAMERA_SHEET_PHOTO = 5;
    /**
     *发布了一张新照片
     */
    public static final int EVENT_PUSH_SHEET = 6;
    /**
     *删除了一张新照片
     */
    public static final int EVENT_DELETE_SHEET = 7;
    /**
     *获取到地理位置
     */
    public static final int EVENT_GET_LOCATION = 8;
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
