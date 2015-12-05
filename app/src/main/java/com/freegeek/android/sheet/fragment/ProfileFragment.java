package com.freegeek.android.sheet.fragment;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.freegeek.android.sheet.R;
import com.freegeek.android.sheet.activity.BaseActivity;
import com.freegeek.android.sheet.bean.Event;
import com.freegeek.android.sheet.bean.User;
import com.freegeek.android.sheet.service.UserService;
import com.freegeek.android.sheet.util.EventLog;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.rey.material.app.Dialog;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import de.greenrobot.event.EventBus;

/**
 *
 */
public class ProfileFragment extends BaseFragment {
    private CircularImageView mAvatar;
    private TextView mNick;
    private Button mButton;
    private User mUser;
    private boolean inited = false;
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);
        initView(view);
        return view;
    }

    private void initView(View view){
        mAvatar = (CircularImageView)view.findViewById(R.id.profile_img_avatar);
        mNick = (TextView)view.findViewById(R.id.profile_txt_nick);
        mButton = (Button) view.findViewById(R.id.profile_btn_bottom);

        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isMe()) return;
                Crop.pickImage(getActivity());
            }
        });

        mNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isMe()) return;
                LayoutInflater layoutInflater1 = getActivity().getLayoutInflater();
                View editNickView = layoutInflater1.inflate(R.layout.dialog_edit_nick, null);
                final TextInputLayout nickInput = (TextInputLayout)editNickView.findViewById(R.id.dialog_nick_etx_nick);
                final EditText editNick =nickInput.getEditText();

                nickInput.setCounterEnabled(true);
                nickInput.setCounterMaxLength(15);
                editNick.setText(mUser.getNick());

                editNick.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        if(TextUtils.isEmpty(s)){
                            nickInput.setError(getString(R.string.error_nick_null));
                            nickInput.setErrorEnabled(true);
                        }else{
                            nickInput.setErrorEnabled(false);
                        }
                    }
                });
                final Dialog editNickDialog = new Dialog(getActivity());
                editNickDialog.title(getString(R.string.nick))
                        .contentView(editNickView)
                        .positiveAction(getString(R.string.confirm))
                        .negativeAction(getString(R.string.cancel))
                        .positiveActionClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String nick = editNick.getText().toString();
                                if (nick.length() == 0) {
                                    nickInput.setError(getString(R.string.error_nick_null));
                                } else {
                                    showLoading();
                                    mUser.setNick(nick);
                                    mUser.update(getActivity(), new UpdateListener() {
                                        @Override
                                        public void onSuccess() {
                                            EventBus.getDefault().post(new Event(Event.EVENT_USER_PROFILE_UPDATE));
                                            mNick.setText(mUser.getNick());
                                            dismissLoading();
                                            editNickDialog.dismiss();
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            EventLog.BmobToastError(i, getActivity());
                                            dismissLoading();
                                        }
                                    });
                                }
                            }
                        }).negativeActionClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editNickDialog.dismiss();
                    }
                }).show();
            }
        });
        inited = true;
        refreshData();
    }


    /**
     * 判断是不是自己
     * @return
     */
    private boolean isMe(){
        if(getCurrentUser()!=null && mUser != null && mUser.getObjectId().equals(getCurrentUser().getObjectId())) return true;
        return false;
    }

    /**
     *刷新数据
     */
    private void refreshData(){
        if(mUser != null){
            if(mUser.getAvatar() != null)
                Picasso.with(getContext())
                        .load(mUser.getAvatar()
                        .getFileUrl(getActivity()))//TODO BUG
                        .into(mAvatar);
            mNick.setText(mUser.getNick());
            if(isMe()){
                mButton.setText(R.string.logout);
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        User user = BmobUser.getCurrentUser(getActivity(), User.class);
                        if (user != null) {
                            UserService.getInstance().logout();
                        }
                    }
                });
            }else{
                if(getCurrentUser() !=null && mUser!= null && getCurrentUser().getFollow().contains(mUser.getObjectId())){
                    mButton.setText(R.string.cancel_follow);
                }else {
                    mButton.setText(R.string.follow);
                }
                mButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(getCurrentUser() !=null && getCurrentUser().getFollow().contains(mUser.getObjectId())){
                            if(getCurrentUser().removeFollow(mUser.getObjectId())){
                                getCurrentUser().update(getActivity(), new UpdateListener() {
                                    @Override
                                    public void onSuccess() {
                                        mButton.setText(R.string.follow);
                                    }

                                    @Override
                                    public void onFailure(int i, String s) {
                                        EventLog.BmobToastError(i, getActivity());
                                    }
                                });
                            }
                        }else{
                            if(getCurrentUser() == null){
                                BaseActivity.showLoginTip((BaseActivity) getActivity());
                            }else{
                                if(getCurrentUser().addFollow(mUser.getObjectId())){
                                    getCurrentUser().update(getActivity(), new UpdateListener() {
                                        @Override
                                        public void onSuccess() {
                                            mButton.setText(R.string.cancel_follow);
                                        }

                                        @Override
                                        public void onFailure(int i, String s) {
                                            EventLog.BmobToastError(i, s, getActivity());
                                        }
                                    });
                                }
                            }
                            }

                    }
                });

            }
        }
    }

    public void onEvent(Event event){
        switch (event.getEventCode()){
            case Event.EVENT_USER_PROFILE_UPDATE:
                refreshData();
                break;
        }
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User mUser) {
        this.mUser = mUser;
        if(inited) refreshData();
    }
}
