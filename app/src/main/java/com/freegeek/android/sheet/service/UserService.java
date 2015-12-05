package com.freegeek.android.sheet.service;

import android.content.Context;

import com.baidu.location.BDLocation;
import com.freegeek.android.sheet.bean.Comment;
import com.freegeek.android.sheet.bean.Event;
import com.freegeek.android.sheet.bean.Sheet;
import com.freegeek.android.sheet.bean.User;
import com.orhanobut.logger.Logger;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import de.greenrobot.event.EventBus;

/**
 * Created by rtugeek on 15-12-1.
 * 用户管理服务
 */
public class UserService {
    public static User user;
    private static UserService mInstance;
    private static Context mContext;
    public UserService(Context context){
        this.mContext = context;
        user = BmobUser.getCurrentUser(context,User.class);
    }

    public static void initialize(Context context){
        mInstance = new UserService(context);
        mContext = context;
    }

    public static  UserService getInstance() {
        return mInstance;
    }

    public void refreshUser(){
        user = BmobUser.getCurrentUser(mContext,User.class);
    }

    public void updateUser(){

    }

    /**
     * 添加喜欢的图片
     * @param sheet
     */
    public void addLikeSheet(Sheet sheet,UpdateListener updateListener){
        if(!checkLogin()) return;
        if(user.addLikeSheet(sheet.getObjectId())){
            user.update(mContext,updateListener);
        }
        if(sheet.addLiker(user.getObjectId())){
            sheet.update(mContext);
        }

    }

    /**
     * 删除喜欢的图片
     * @param sheet
     */
    public void removeLikeSheet(Sheet sheet,UpdateListener updateListener){
        if(!checkLogin()) return;
        if(user.removeLikeSheet(sheet.getObjectId())){
            user.update(mContext,updateListener);
        }
        if(sheet.removeLiker(user.getObjectId())){
            sheet.update(mContext);
        }
    }

    public boolean checkLogin(){
        if(user == null){
            Logger.i("USER DOES NOT SIGN IN!");
            return false;
        }
        return true;
    }

    /**
     * 登陆
     */
    public void login(User user,final SaveListener saveListener){
        user.login(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                refreshUser();
                EventBus.getDefault().post(new Event(Event.EVENT_USER_SIGN_IN));
                MobclickAgent.onEvent(mContext, "UserLogin");
                Logger.i("user login succeed!");
                saveListener.onSuccess();
            }

            @Override
            public void onFailure(int i, String s) {
                saveListener.onFailure(i, s);
            }
        });
        if(checkLogin()){
            Logger.i("USER HAD ALREADY LOGIN!");
            return;
        }
        BmobUser.logOut(mContext);
        user = null;
        EventBus.getDefault().post(new Event(Event.EVENT_USER_SIGN_OUT));
    }


    /**
     * 注销
     */
    public void logout(){
        if(!checkLogin()){
            Logger.i("USER HAD ALREADY LOGOUT!");
            return;
        }
        BmobUser.logOut(mContext);
        user = null;
        EventBus.getDefault().post(new Event(Event.EVENT_USER_SIGN_OUT));
    }


    public void refreshLikeSheet(){
        // 查询喜欢这个帖子的所有用户，因此查询的是用户表
        BmobQuery<User> query = new BmobQuery<User>();
        Sheet sheet = new Sheet();
//likes是Post表中的字段，用来存储所有喜欢该帖子的用户
        query.addWhereRelatedTo("likes", new BmobPointer(sheet));
        query.findObjects(mContext, new FindListener<User>() {

            @Override
            public void onSuccess(List<User> object) {
                // TODO Auto-generated method stub
                object.size();
            }

            @Override
            public void onError(int code, String msg) {
            }
        });
    }


    /**
     * 获取图片评论
     * @param sheet
     * @param findListener
     */
    public void getSheetComments(Sheet sheet,FindListener<Comment> findListener){
        BmobQuery<Comment> query = new BmobQuery<>();
        query.addWhereEqualTo("sheet", sheet);
        query.order("-updatedAt");
        query.include("user");
        query.findObjects(mContext, findListener);
    }

    /**
     * 发表评论
     * @param comment
     * @param saveListener
     */
    public void postComment(final Comment comment,final MySaveListener saveListener){
        if(!checkLogin())return;
        comment.setUser(user);
        comment.save(mContext, new SaveListener() {
            @Override
            public void onSuccess() {
                saveListener.onSuccess(comment);
            }

            @Override
            public void onFailure(int i, String s) {
                saveListener.onFailure(i, s);
            }
        });
    }

    public interface MySaveListener{
        void onSuccess(Object object);
        void onFailure(int i, String s);
    }

    /**
     * 获取附近的卡片
     * @param findListener
     */
    public void getNearbySheet(FindListener<Sheet> findListener){
        BDLocation bdLocation = LocationService.location;
        if(bdLocation == null){
            LocationService.getInstance(mContext).getLocation();
            return;
        }
        BmobQuery<Sheet> bmobQuery = new BmobQuery<>();

        BmobGeoPoint bmobGeoPoint = new BmobGeoPoint(bdLocation.getLongitude(),bdLocation.getLatitude());
        bmobQuery.addWhereNear("location", bmobGeoPoint);
        bmobQuery.include("author");
        bmobQuery.setLimit(10);    //获取最接近用户地点的10条数据

        bmobQuery.findObjects(mContext,findListener);
    }

    /**
     * 更新用户地理位置
     */
    public void updateUserLocation(){
        if( user !=null && LocationService.location != null){
            BmobGeoPoint bmobGeoPoint = new BmobGeoPoint();
            bmobGeoPoint.setLongitude(LocationService.location.getLongitude());
            bmobGeoPoint.setLatitude(LocationService.location.getLatitude());
            user.setLocation(bmobGeoPoint);
            user.update(mContext, user.getObjectId(), new UpdateListener() {
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

    public void deleteSheet(final Sheet sheet,final DeleteListener deleteListener){
        getSheetComments(sheet, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {
                List<BmobObject> list1 = new ArrayList<>();
                list1.addAll(list);
                new BmobObject().deleteBatch(mContext, list1, new DeleteListener() {
                    @Override
                    public void onSuccess() {
                        if(sheet.getPicture()!= null) sheet.getPicture().delete(mContext, new DeleteListener() {
                            @Override
                            public void onSuccess() {
                                sheet.delete(mContext, new DeleteListener() {
                                    @Override
                                    public void onSuccess() {
                                        deleteListener.onSuccess();
                                        EventBus.getDefault().post(new Event(Event.EVENT_DELETE_SHEET));
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        deleteListener.onFailure(i,s);
                                    }
                                });
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                deleteListener.onFailure(i,s);
                            }
                        });

                        else
                            sheet.delete(mContext, new DeleteListener() {
                                @Override
                                public void onSuccess() {
                                    deleteListener.onSuccess();
                                }

                                @Override
                                public void onFailure(int i, String s) {
                                    deleteListener.onFailure(i,s);
                                }
                            });
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        deleteListener.onFailure(i,s);
                    }
                });
            }

            @Override
            public void onError(int i, String s) {
                deleteListener.onFailure(i,s);
            }
        });
    }

    /**
     * 获取关注用户的图片
     */
    public void getFollowSheet(FindListener<Sheet> findListener){
        if(!checkLogin())return;
        BmobQuery<Sheet> query = new BmobQuery<>();
        query.addWhereContainedIn("author",user.getFollow());
        query.include("author");
        query.order("-updatedAt");
        query.findObjects(mContext, findListener);
    }

    /**
     * 获取喜欢的图片
     */
    public void getLikeSheet(FindListener<Sheet> findListener){
        if(!checkLogin()) return;
        BmobQuery<Sheet> query = new BmobQuery<>();
        query.addWhereContainedIn("objectId",user.getLikeSheet());
        query.include("objectId");
        query.order("-updatedAt");
        query.findObjects(mContext, findListener);
    }

    /**
     * 获取我发布的图片
     * @param findListener
     */
    public void getMySheet(FindListener<Sheet> findListener){
        if(!checkLogin()) return;
        BmobQuery<Sheet> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("author", user.getObjectId());
        bmobQuery.order("-updatedAt");
        bmobQuery.include("author");// 希望在查询帖子信息的同时也把发布人的信息查询出来
        bmobQuery.findObjects(mContext,findListener);
    }


}
