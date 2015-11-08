package com.freegeek.android.sheet.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.freegeek.android.sheet.R;
import com.freegeek.android.sheet.bean.Event;
import com.freegeek.android.sheet.bean.User;
import com.freegeek.android.sheet.util.EventLog;
import com.github.siyamed.shapeimageview.CircularImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.rey.material.app.Dialog;
import com.soundcloud.android.crop.Crop;

import org.w3c.dom.Text;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.UpdateListener;
import de.greenrobot.event.EventBus;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends BaseFragment {
    private CircularImageView mAcatar;
    private TextView mNick;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
        mAcatar = (CircularImageView)view.findViewById(R.id.profile_img_avatar);
        mNick = (TextView)view.findViewById(R.id.profile_txt_nick);
        mAcatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.pickImage(getActivity());
            }
        });

        mNick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final User user = BmobUser.getCurrentUser(getActivity(), User.class);
                if(user== null) return;
                LayoutInflater layoutInflater1 = getActivity().getLayoutInflater();
                View editNickView = layoutInflater1.inflate(R.layout.dialog_edit_nick, null);
                final TextInputLayout nickInput = (TextInputLayout)editNickView.findViewById(R.id.dialog_nick_etx_nick);
                final EditText editNick =nickInput.getEditText();

                nickInput.setCounterEnabled(true);
                nickInput.setCounterMaxLength(15);
                editNick.setText(user.getNick());

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
                                    user.setNick(nick);
                                    user.update(getActivity(), new UpdateListener() {
                                        @Override
                                        public void onSuccess() {
                                            EventBus.getDefault().post(new Event(Event.EVENT_USER_PROFILE_UPDATE));
                                            mNick.setText(user.getNick());
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
        view.findViewById(R.id.profile_btn_sign_out).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = BmobUser.getCurrentUser(getActivity(), User.class);
                if (user != null){
                    BmobUser.logOut(getActivity());
                    EventBus.getDefault().post(new Event(Event.EVENT_USER_SIGN_OUT));
                }
            }
        });

        refreshData();
    }

    private void refreshData(){
         User user = BmobUser.getCurrentUser(getActivity(), User.class);
        if(user != null){
            if(user.getAvatar() != null)  ImageLoader.getInstance().displayImage(user.getAvatar().getFileUrl(getActivity()),mAcatar);
            mNick.setText(user.getNick());
        }
    }

    public void onEvent(Event event){
        switch (event.getEventCode()){
            case Event.EVENT_USER_PROFILE_UPDATE:
                refreshData();
                break;
        }
    }


}
