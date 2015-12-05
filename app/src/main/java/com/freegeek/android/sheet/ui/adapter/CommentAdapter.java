package com.freegeek.android.sheet.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder> {
    private List<Comment> mComments;
    public CommentAdapter(List<Comment> comments){
        mComments = comments;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comment, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder viewHolder = new ViewHolder(view,parent.getContext());
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Logger.i("LIST COMMENT :" + mComments.get(position).getContent());
        User user = mComments.get(position).getUser();
        if(user == null){
            return;
        }
        if(user.getAvatar() != null) holder.setAvatar(user.getAvatar().getFileUrl(holder.getContext()));
        holder.setDate(mComments.get(position).getUpdatedAt());
        holder.setContent(mComments.get(position).getContent());
        holder.setNick(user.getNick());
        if(position == (mComments.size() -1)){
            holder.setDividerVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mComments.size();
    }




    // Provide a reference to the type of views that you are using
    // (custom viewholder)
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public CircularImageView mAvatar;
        public TextView mDate;
        public TextView mNick;
        public TextView mContent;
        public Context mContext;
        public View mDivider;
        public ViewHolder(View v,Context context) {
            super(v);
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
