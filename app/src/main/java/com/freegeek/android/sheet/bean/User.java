package com.freegeek.android.sheet.bean;

import com.freegeek.android.sheet.util.APP;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by rtugeek@gmail.com on 2015/11/6.
 */
public class User extends BmobUser implements Serializable{
    private String nick;
    /**
     * true 男 false 女
     */
    private Boolean sex = true;
    private BmobFile avatar;
    private BmobRelation fans;
    private List<String> likeSheet = new ArrayList<>();
    private List<String> follow = new ArrayList<>();
    private BmobGeoPoint location;
    private String  locationName;

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public BmobGeoPoint getLocation() {
        return location;
    }

    public void setLocation(BmobGeoPoint location) {
        this.location = location;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public BmobFile getAvatar() {
        return avatar;
    }

    public void setAvatar(BmobFile avatar) {
        this.avatar = avatar;
    }

    public BmobRelation getFans() {
        return fans;
    }

    public void setFans(BmobRelation fans) {
        this.fans = fans;
    }

    public List<String>  getFollow() {
        return follow;
    }

    public void setFollow(List<String>  follow) {
        this.follow = follow;
    }

    public List<String> getLikeSheet() {
        return likeSheet;
    }

    public void setLikeSheet(List<String> likeSheet) {
        this.likeSheet = likeSheet;
    }

    @Override
    public boolean equals(Object o) {
        try {
            User user = (User) o;
            if(user.getObjectId().equals(getObjectId())){
                return true;
            }
            return false;
        }catch (Exception e){
            return false;
        }
    }

    public boolean addFollow(String userId){
        if(!follow.contains(userId)){
            follow.add(userId);
            return true;
        }
        return false;
    }

    public boolean removeFollow(String userId){
        if(follow.contains(userId)){
            follow.remove(userId);
            return true;
        }
        return false;
    }

    public boolean addLikeSheet(String sheetId){
        if(!likeSheet.contains(sheetId)){
            likeSheet.add(sheetId);
            return true;
        }
        return false;
    }

    public boolean removeLikeSheet(String sheetId){
        if(likeSheet.contains(sheetId)){
            likeSheet.remove(sheetId);
            return true;
        }
        return false;
    }
}
