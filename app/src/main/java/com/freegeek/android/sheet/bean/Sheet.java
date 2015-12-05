package com.freegeek.android.sheet.bean;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by rtugeek@gmail.com on 2015/11/6.
 */
public class Sheet extends BmobObject implements Serializable{
    private BmobFile picture;
    private String content="";
    private User author;
    /**
     * 喜欢此照片用户的ID
     */
    private List<String> liker = new ArrayList<>();
    private BmobRelation comment;
    private BmobGeoPoint location;
    private String locationName;
    private Integer report;
    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public BmobFile getPicture() {
        return picture;
    }

    public void setPicture(BmobFile picture) {
        this.picture = picture;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public BmobGeoPoint getLocation() {
        return location;
    }

    public void setLocation(BmobGeoPoint location) {
        this.location = location;
    }

    public int getReport() {
        return report;
    }

    public void setReport(int report) {
        this.report = report;
    }

    public List<String> getLiker() {
        return liker;
    }

    public void setLiker(List<String> liker) {
        this.liker = liker;
    }

    public boolean addLiker(String id){
        if(!liker.contains(id)){
            liker.add(id);
            return true;
        }
        return false;
    }

    public boolean removeLiker(String id){
        if(liker.contains(id)){
            liker.remove(id);
            return true;
        }
        return false;
    }

}
