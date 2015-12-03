package com.freegeek.android.sheet.service;

import android.content.Context;

import com.freegeek.android.sheet.bean.Comment;
import com.freegeek.android.sheet.bean.Sheet;
import com.freegeek.android.sheet.bean.User;
import com.orhanobut.logger.Logger;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by rtugeek on 15-12-1.
 * 用户管理服务
 */
public class UserService {
    private User mUser;
    private static UserService mInstance;
    private Context mContext;
    private List<Sheet> mLikeSheets = new ArrayList<>();
    public UserService(Context context){
        this.mContext = context;
        mUser = BmobUser.getCurrentUser(context,User.class);
    }

    public static synchronized UserService getInstance(Context context) {
        if(mInstance == null){
            synchronized (UserService.class) {
                if(mInstance == null)
                mInstance = new UserService(context);
            }
        }
        return mInstance;
    }

    public void refreshUser(){
        mUser = BmobUser.getCurrentUser(mContext,User.class);
    }

    public void updateUser(){

    }

    /**
     * 添加喜欢的图片
     * @param sheet
     */
    public void addLikeSheet(Sheet sheet,UpdateListener updateListener){
        if(!checkLogin()) return;
        if(sheet.addLiker(mUser.getObjectId()))
            sheet.update(mContext, updateListener);
    }

    /**
     * 删除喜欢的图片
     * @param sheet
     */
    public void removeLikeSheet(Sheet sheet,UpdateListener updateListener){
        if(!checkLogin()) return;
        if(sheet.removeLiker(mUser.getObjectId()))
            sheet.update(mContext, updateListener);
    }

    public boolean checkLogin(){
        if(mUser == null){
            Logger.i("USER DOES NOT SIGN IN!");
            return false;
        }
        return true;
    }

    /**
     * 注销
     */
    public void logout(){
        if(checkLogin()){
            Logger.i("USER HAD ALREADY LOGOUT!");
            return;
        }
        BmobUser.logOut(mContext);
        mUser = null;
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

    public List<Sheet> getLikeSheet(){
        return mLikeSheets;
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
        comment.setUser(mUser);
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
}
