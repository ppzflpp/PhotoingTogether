package com.freegeek.android.sheet.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.freegeek.android.sheet.R;
import com.freegeek.android.sheet.bean.Sheet;

/**
 * Created by rtugeek on 15-12-2.
 */
public class SheetCardProvider extends CardProvider<SheetCardProvider> {
    private String mRightTxt="";
    private String mLeftTxt="";
    private int mRightPic;
    private boolean like = false;
    private Sheet sheet;
    private Card mCard;
    @Nullable
    private OnActionClickListener mLikeListener;
    @Nullable
    private OnActionClickListener mItemClickListener;
    @Override
    protected void onCreated() {
        super.onCreated();
    }

    @Override
    public int getLayout() {
        return R.layout.item_sheet_card;
    }

    @Override
    public void render(@NonNull View view, @NonNull Card card) {
        super.render(view, card);
        mCard =card;
        TextView textView = (TextView)view.findViewById(R.id.txt_right_content);
        TextView leftTextView = (TextView)view.findViewById(R.id.txt_left_content);
        final ImageView imgLike = (ImageView)view.findViewById(R.id.img_like);
        ImageView imgRight = (ImageView)view.findViewById(R.id.img_right_img);

        imgRight.setImageResource(mRightPic);
        leftTextView.setText(mLeftTxt);
        textView.setText(mRightTxt);
        if(like){
            imgLike.setImageResource(R.drawable.ic_favorite_black_24dp);
        }else{
            imgLike.setImageResource(R.drawable.ic_favorite_outline_black_24dp);
        }

        OnItemClickListener onItemClickListener =new OnItemClickListener();
        view.findViewById(R.id.image).setOnClickListener(onItemClickListener);

        imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mLikeListener != null) {
                    mLikeListener.onActionClicked(v, mCard);
                }
            }
        });


    }

    private class OnItemClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) {
            if (mItemClickListener != null) {
                mItemClickListener.onActionClicked(v, mCard);
            }
        }
    }

    public boolean isLike() {
        return like;
    }

    public SheetCardProvider setLike(boolean like) {
        this.like = like;
        notifyDataSetChanged();
        return this;
    }

    public Sheet getSheet() {
        return sheet;
    }

    public SheetCardProvider setSheet(Sheet sheet) {
        this.sheet = sheet;
        notifyDataSetChanged();
        return this;
    }

    @Nullable
    public OnActionClickListener getLikeListener() {
        return mLikeListener;
    }

    public SheetCardProvider setLikeListener(@Nullable OnActionClickListener mListener) {
        this.mLikeListener = mListener;
        notifyDataSetChanged();
        return this;
    }

    @Nullable
    public OnActionClickListener getItemClickListener() {
        return mItemClickListener;
    }

    public SheetCardProvider setItemClickListener(@Nullable OnActionClickListener mItemClickListener) {
        this.mItemClickListener = mItemClickListener;
        notifyDataSetChanged();
        return this;
    }

    public SheetCardProvider setRightText(String txt){
        mRightTxt =txt;
        notifyDataSetChanged();
        return this;
    }

    public SheetCardProvider setRightPic(int resId){
        mRightPic = resId;
        notifyDataSetChanged();
        return this;
    }

    public SheetCardProvider setLeftText(String text){
        mLeftTxt = text;
        notifyDataSetChanged();
        return this;
    }

}
