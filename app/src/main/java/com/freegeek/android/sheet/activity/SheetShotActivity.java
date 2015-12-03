package com.freegeek.android.sheet.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.freegeek.android.sheet.R;
import com.freegeek.android.sheet.bean.Comment;
import com.freegeek.android.sheet.bean.Sheet;
import com.freegeek.android.sheet.service.UserService;
import com.freegeek.android.sheet.ui.adapter.CommentAdapter;
import com.freegeek.android.sheet.util.EventLog;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by rtugeek on 2015-12-02
 * 需要穿值 Sheet对象
 * Intent intent= new Intent(getActivity(), SheetShotActivity.class);
 * Bundle bundle = new Bundle();
 * bundle.putSerializable(SheetShotActivity.KEY_SHEET,((SheetCardProvider)card.getProvider()).getSheet());
 * intent.putExtras(bundle);
 */
public class SheetShotActivity extends BaseActivity {
    public static String KEY_SHEET = "sheet";
    private ImageView mImgSheet;
    private Sheet mSheet;
    private ImageView mSheetImageView;
    private Toolbar mToolbarToolbar;
    private ImageView mLikeImageView;
    private TextView mLikeNumberTextView;
    private TextView mCommentNumberTextView;
    private RecyclerView mListCommentRecyclerView;
    private CollapsingToolbarLayout mToolbarCollapsingToolbarLayout;
    private LinearLayout mLikeLinearLayout;
    private LinearLayout mCommentLinearLayout;
    private LinearLayout mShareLinearLayout;
    private TextView mContentTextView;
    private CircularImageView mAvatarCircularImageView;
    private TextInputLayout mCommentTextInputLayout;
    private ImageButton mPostCommentImageButton;
    private EditText mCommentEditText;

    private List<Comment> mComments = new ArrayList<>();
    private CommentAdapter mCommentAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_shot);
        mSheetImageView = (ImageView) findViewById(R.id.img_sheet);
        mAvatarCircularImageView = (CircularImageView) findViewById(R.id.img_avatar);
        mContentTextView = (TextView) findViewById(R.id.txt_content);
        mLikeImageView = (ImageView) findViewById(R.id.img_like);
        mLikeNumberTextView = (TextView) findViewById(R.id.txt_like_number);
        mLikeLinearLayout = (LinearLayout) findViewById(R.id.linear_like);
        mCommentNumberTextView = (TextView) findViewById(R.id.txt_comment_number);
        mCommentLinearLayout = (LinearLayout) findViewById(R.id.linear_comment);
        mShareLinearLayout = (LinearLayout) findViewById(R.id.linear_share);
        mToolbarToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mCommentTextInputLayout = (TextInputLayout) findViewById(R.id.input_comment);
        mPostCommentImageButton = (ImageButton) findViewById(R.id.btn_post_comment);
        mListCommentRecyclerView = (RecyclerView) findViewById(R.id.list_comment);

        mSheetImageView = (ImageView) findViewById(R.id.img_sheet);
        mContentTextView = (TextView) findViewById(R.id.txt_content);
        mAvatarCircularImageView = (CircularImageView) findViewById(R.id.img_avatar);
        mLikeImageView = (ImageView) findViewById(R.id.img_like);
        mLikeNumberTextView = (TextView) findViewById(R.id.txt_like_number);
        mLikeLinearLayout = (LinearLayout) findViewById(R.id.linear_like);
        mCommentNumberTextView = (TextView) findViewById(R.id.txt_comment_number);
        mCommentLinearLayout = (LinearLayout) findViewById(R.id.linear_comment);
        mShareLinearLayout = (LinearLayout) findViewById(R.id.linear_share);
        mToolbarToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mListCommentRecyclerView = (RecyclerView) findViewById(R.id.list_comment);


        mSheetImageView = (ImageView) findViewById(R.id.img_sheet);
        mToolbarToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbarCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        mLikeImageView = (ImageView) findViewById(R.id.img_like);
        mLikeNumberTextView = (TextView) findViewById(R.id.txt_like_number);
        mLikeLinearLayout = (LinearLayout) findViewById(R.id.linear_like);
        mCommentNumberTextView = (TextView) findViewById(R.id.txt_comment_number);
        mCommentLinearLayout = (LinearLayout) findViewById(R.id.linear_comment);
        mShareLinearLayout = (LinearLayout) findViewById(R.id.linear_share);
        mListCommentRecyclerView = (RecyclerView) findViewById(R.id.list_comment);
        mSheetImageView = (ImageView) findViewById(R.id.img_sheet);
        mToolbarToolbar = (Toolbar) findViewById(R.id.toolbar);
        mLikeImageView = (ImageView) findViewById(R.id.img_like);
        mLikeNumberTextView = (TextView) findViewById(R.id.txt_like_number);
        mCommentNumberTextView = (TextView) findViewById(R.id.txt_comment_number);
        mListCommentRecyclerView = (RecyclerView) findViewById(R.id.list_comment);
        mImgSheet = (ImageView)findViewById(R.id.img_sheet);
        mCommentEditText = mCommentTextInputLayout.getEditText();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mSheet =(Sheet) getIntent().getExtras().getSerializable(KEY_SHEET);

        //初始化信息
        Picasso.with(this).load(mSheet.getPicture().getFileUrl(this)).error(R.drawable.header_bg).into(mImgSheet);
        Picasso.with(this).load(getCurrentUser().getAvatar().getFileUrl(this)).error(R.drawable.avatar).into(mAvatarCircularImageView);
        mContentTextView.setText(mSheet.getContent());
        refreshLike();
        refreshComment();

        mCommentTextInputLayout.setCounterEnabled(true);
        mCommentTextInputLayout.setCounterMaxLength(140);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mListCommentRecyclerView.setLayoutManager(layoutManager);
        mCommentAdapter = new CommentAdapter(mComments);
        mListCommentRecyclerView.setAdapter(mCommentAdapter);
        //设置Item增加、移除动画
        mListCommentRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mLikeLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSheet.getLiker().contains(getCurrentUser().getObjectId())) {
                    UserService.getInstance(getActivity()).removeLikeSheet(mSheet, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            refreshLike();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            EventLog.BmobToastError(i, getActivity());
                        }
                    });

                } else {
                    UserService.getInstance(getActivity()).addLikeSheet(mSheet, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            refreshLike();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            EventLog.BmobToastError(i, getActivity());
                        }
                    });
                }
            }
        });

        mPostCommentImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(TextUtils.isEmpty(mCommentEditText.getText())){
                    mCommentTextInputLayout.setError(getString(R.string.alert_null_text));
                }else {
                    mCommentTextInputLayout.setError("");
                    mCommentTextInputLayout.setHint(getString(R.string.comment));
                    Comment comment = new Comment();
                    comment.setSheet(mSheet);
                    comment.setContent(mCommentEditText.getText().toString());
                    showLoading();
                    UserService.getInstance(getActivity()).postComment(comment, new UserService.MySaveListener() {
                        @Override
                        public void onSuccess(Object o) {
                            mComments.add(0,(Comment)o);
                            mCommentNumberTextView.setText(String.valueOf(mComments.size()));
                            mCommentAdapter.notifyItemInserted(0);
                            dismissLoading();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            EventLog.BmobToastError(i,getActivity());
                            dismissLoading();
                        }
                    });
                }
            }
        });
    }

    private void refreshLike(){
        mLikeNumberTextView.setText(mSheet.getLiker().size()+ "");
        if (mSheet.getLiker().contains(getCurrentUser().getObjectId())) {
            mLikeImageView.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            mLikeImageView.setImageResource(R.drawable.ic_favorite_outline_black_24dp);
        }
    }

    private void refreshComment(){
        UserService.getInstance(this).getSheetComments(mSheet, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {
                mComments.clear();
                mComments.addAll(list);
                mCommentNumberTextView.setText(String.valueOf(list.size()));
                mCommentAdapter.notifyDataSetChanged();

            }

            @Override
            public void onError(int i, String s) {
                EventLog.BmobToastError(i,getActivity());
            }
        });
    }

}
