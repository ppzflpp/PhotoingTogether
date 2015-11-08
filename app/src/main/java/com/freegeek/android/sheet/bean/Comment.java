package com.freegeek.android.sheet.bean;

import cn.bmob.v3.BmobObject;

/**
 * Created by rtugeek@gmail.com on 2015/11/6.
 */
public class Comment extends BmobObject {
    public User user;
    public Sheet sheet;
    public String content;
    public Comment toComment;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Comment getToComment() {
        return toComment;
    }

    public void setToComment(Comment toComment) {
        this.toComment = toComment;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }
}
