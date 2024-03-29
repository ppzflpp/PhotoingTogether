package com.freegeek.android.sheet.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.freegeek.android.sheet.R;
import com.freegeek.android.sheet.bean.Comment;
import com.freegeek.android.sheet.bean.Sheet;
import com.freegeek.android.sheet.service.UserService;
import com.freegeek.android.sheet.ui.adapter.CommentAdapter;
import com.freegeek.android.sheet.util.APP;
import com.freegeek.android.sheet.util.EventLog;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.rey.material.app.Dialog;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.FindListener;
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
    public static String KEY_POSITION = "position";
    private Sheet mSheet;


    private List<Comment> mComments = new ArrayList<>();
    private CommentAdapter mCommentAdapter;
    private ImageView mLikeImageView;
    private TextView mLikeNumberTextView;
    private CircularImageView mAvatarCircularImageView;
    private TextView mContentTextView;
    private TextInputLayout mCommentTextInputLayout;
    private ImageButton mPostCommentImageButton;
    private ListView mListView;
    private EditText mCommentEditText;
    private TextView mCommentNumberTextView;
    private ImageView mPictureImageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sheet_shot);
        mLikeImageView = (ImageView) findViewById(R.id.img_like);
        mLikeNumberTextView = (TextView) findViewById(R.id.txt_like_number);
        mAvatarCircularImageView = (CircularImageView) findViewById(R.id.img_avatar);
        mContentTextView = (TextView) findViewById(R.id.txt_content);
        mCommentTextInputLayout = (TextInputLayout) findViewById(R.id.input_comment);
        mPostCommentImageButton = (ImageButton) findViewById(R.id.btn_post_comment);
        mListView = (ListView)findViewById(R.id.list_comment);
        mCommentEditText = mCommentTextInputLayout.getEditText();
        mCommentNumberTextView = (TextView) findViewById(R.id.txt_comment_number);
        mPictureImageView = (ImageView) findViewById(R.id.img_sheet);
//
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
        Picasso.with(this).load(mSheet.getPicture().getFileUrl(this)).error(R.drawable.header_bg).into(mPictureImageView);
        if( mSheet.getAuthor().getAvatar() !=null) Picasso.with(this)
                .load(mSheet.getAuthor().getAvatar().getFileUrl(this))
                .error(R.drawable.avatar)
                .into(mAvatarCircularImageView);
        mContentTextView.setText(mSheet.getContent());
        refreshLike();
        refreshComment();

        mCommentTextInputLayout.setCounterEnabled(true);
        mCommentTextInputLayout.setCounterMaxLength(140);

        mCommentAdapter = new CommentAdapter(mComments,getActivity());
        mListView.setAdapter(mCommentAdapter);

        //隐藏提示
        if(sp.getBoolean(APP.KEY.TIP_DRAG,false)){
           findViewById(R.id.txt_tip_drag).setVisibility(View.GONE);
        }

        findViewById(R.id.linear_like).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentUser() == null) BaseActivity.showLoginTip(SheetShotActivity.this);
                if (mSheet.getLiker().contains(getCurrentUser().getObjectId())) {
                    UserService.getInstance().removeLikeSheet(mSheet, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            refreshLike();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            EventLog.BmobToastError(i, s, getActivity());
                        }
                    });

                } else {
                    UserService.getInstance().addLikeSheet(mSheet, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            refreshLike();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            EventLog.BmobToastError(i, s, getActivity());
                        }
                    });
                }
            }
        });

        mPostCommentImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(mCommentEditText.getText())) {
                    mCommentTextInputLayout.setError(getString(R.string.alert_null_text));
                } else {
                    mCommentTextInputLayout.setError("");
                    mCommentTextInputLayout.setHint(getString(R.string.comment));
                    Comment comment = new Comment();
                    comment.setSheet(mSheet);
                    comment.setContent(mCommentEditText.getText().toString());
                    showLoading();
                    UserService.getInstance().postComment(comment, new UserService.MySaveListener() {
                        @Override
                        public void onSuccess(Object o) {
                            mComments.add(0, (Comment) o);
                            mCommentNumberTextView.setText(String.valueOf(mComments.size()));
                            mCommentAdapter.notifyDataSetChanged();
                            dismissLoading();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            EventLog.BmobToastError(i, s, getActivity());
                            dismissLoading();
                        }
                    });
                }
            }
        });

        mAvatarCircularImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("user", mSheet.getAuthor());
                intent.putExtras(bundle);
                intent.setAction(APP.ACTION.MAIN_ACTIVITY_FRAGMENT_PROFILE);
                startActivity(intent);
            }
        });

        findViewById(R.id.linear_share).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share(mSheet.getContent(), mSheet.getPicture().getFileUrl(getActivity()));
            }
        });

        if(!sp.getBoolean(APP.KEY.TIP_DRAG,false))spe.putBoolean(APP.KEY.TIP_DRAG,true).commit();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:
                final Dialog dialog = new Dialog(this);
                dialog.setTitle(R.string.delete_this_picture);
                dialog.negativeAction(R.string.cancel);
                dialog.positiveAction(R.string.confirm);
                dialog.negativeActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                dialog.positiveActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        showLoading();
                        UserService.getInstance().deleteSheet(mSheet, new DeleteListener() {
                            @Override
                            public void onSuccess() {
                                dismissLoading();
                                finish();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                   EventLog.BmobToastError(i,s, getActivity());
                                dismissLoading();
                            }
                        });
                    }
                });
                dialog.show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(getCurrentUser()!=null &&mSheet.getAuthor().getObjectId().equals(getCurrentUser().getObjectId())){
            getMenuInflater().inflate(R.menu.menu_sheet_shot, menu);
        }
        return true;
    }

    private void refreshLike(){
        mLikeNumberTextView.setText(mSheet.getLiker().size()+ "");
        if (getCurrentUser() !=null &&mSheet.getLiker().contains(getCurrentUser().getObjectId())) {
            mLikeImageView.setImageResource(R.drawable.ic_favorite_black_24dp);
        } else {
            mLikeImageView.setImageResource(R.drawable.ic_favorite_outline_black_24dp);
        }
    }

    private void refreshComment(){
        UserService.getInstance().getSheetComments(mSheet, new FindListener<Comment>() {
            @Override
            public void onSuccess(List<Comment> list) {
                mComments.clear();
                mComments.addAll(list);
                mCommentNumberTextView.setText(String.valueOf(list.size()));
                mCommentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int i, String s) {
                EventLog.BmobToastError(i,s,getActivity());
            }
        });
    }

}
