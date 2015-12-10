package com.freegeek.android.sheet.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.freegeek.android.sheet.R;
import com.freegeek.android.sheet.bean.Comment;
import com.freegeek.android.sheet.bean.User;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by rtugeek on 15-12-3.
 */
public class CommentAdapter extends BaseAdapter{
    private List<Comment> mComments;
    private Context mContext;
    public CommentAdapter(List<Comment> comments,Context context){
        mComments = comments;
        mContext = context;
    }


    public int getItemCount() {
        return mComments.size();
    }

    @Override
    public int getCount() {
        return mComments.size();
    }

    @Override
    public Comment getItem(int position) {
        return mComments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if(convertView == null){
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_comment, parent, false);
            // set the view's size, margins, paddings and layout parameters
            viewHolder = new ViewHolder(convertView,parent.getContext());
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
        Comment comment = mComments.get(position);
        viewHolder.setNick(comment.getUser().getNick());
        viewHolder.setAvatar(comment.getUser().getAvatar().getFileUrl(parent.getContext()));
        viewHolder.setContent(comment.getContent());
        viewHolder.setDate(comment.getUpdatedAt());
        if(position == mComments.size() -1) viewHolder.setDividerVisibility(View.GONE);
        else viewHolder.setDividerVisibility(View.VISIBLE);
        return convertView;
    }


    // Provide a reference to the type of views that you are using
    // (custom viewholder)
    public class ViewHolder{
        public CircularImageView mAvatar;
        public TextView mDate;
        public TextView mNick;
        public TextView mContent;
        public Context mContext;
        public View mDivider;
        public ViewHolder(View v,Context context) {
            mAvatar = (CircularImageView) v.findViewById(R.id.img_avatar);
            mDate = (TextView)v.findViewById(R.id.txt_date);
            mNick = (TextView)v.findViewById(R.id.txt_nick);
            mContent = (TextView)v.findViewById(R.id.txt_content);
            mDivider = (View)v.findViewById(R.id.divider);
            mContext = context;
        }

        public void setAvatar(String url){
            Picasso.with(mContext).load(url).error(R.drawable.avatar).into(mAvatar);
        }

        public void setDate(String date){
            if(TextUtils.isEmpty(date)){
                DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                date = format2.format(new Date(System.currentTimeMillis()));
            }
            mDate.setText(date);
        }

        public void setNick(String nick){
            mNick.setText(nick);
        }

        public void setDividerVisibility(int visibilit){
            mDivider.setVisibility(visibilit);
        }

        public void setContent(String content){
            mContent.setText(content);
        }

        public void getAvatar(){

        }

        public Context getContext(){
            return mContext;
        }

    }


}
