package com.freegeek.android.sheet.ui.dialog;

import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.freegeek.android.sheet.R;
import com.freegeek.android.sheet.activity.BaseActivity;
import com.freegeek.android.sheet.bean.Event;
import com.freegeek.android.sheet.bean.User;
import com.freegeek.android.sheet.util.APP;
import com.freegeek.android.sheet.util.EventLog;
import com.freegeek.android.sheet.util.StringUtil;
import com.orhanobut.logger.Logger;
import com.rey.material.app.Dialog;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONObject;

import cn.bmob.v3.listener.SaveListener;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/2/24.
 */
public class LoginDialog extends Dialog {
    private EditText username,password;
    private TextInputLayout usernameInput;
    private TextInputLayout passwordInput;

    private BaseActivity baseActivity;
    public LoginDialog(final BaseActivity context) {
        super(context, R.style.Material_App_Dialog_Light);
        baseActivity = context;
        LayoutInflater layoutInflater = getLayoutInflater();
        View loginView = layoutInflater.inflate(R.layout.dialog_login, null);
        SharedPreferences sharedPreferences = context.getSharedPreferences(APP.STRING.SHARED_NAME,0);

        usernameInput = (TextInputLayout)loginView.findViewById(R.id.dialog_login_input_username);
        passwordInput = (TextInputLayout)loginView.findViewById(R.id.dialog_login_input_password);

        usernameInput.setHint(context.getString(R.string.username));
        usernameInput.setCounterEnabled(true);
        usernameInput.setCounterMaxLength(15);

        passwordInput.setHint(context.getString(R.string.password));
        passwordInput.setCounterEnabled(true);
        passwordInput.setCounterMaxLength(15);

        username = usernameInput.getEditText();
        password = passwordInput.getEditText();

        username.setText(sharedPreferences.getString(APP.STRING.SHARED_NAME,""));
        positiveAction(context.getString(R.string.confirm));
        title(context.getString(R.string.login));
        negativeAction(context.getString(R.string.cancel));
        cancelable(true);

        loginView.findViewById(R.id.login_txt_forget_password).setOnClickListener(new View.OnClickListener() {//找回密码
            @Override
            public void onClick(View v) {
                dismiss();
                FindPasswordDialog findPasswordDialog = new FindPasswordDialog(baseActivity);
                findPasswordDialog.show();
            }
        });
        contentView(loginView);

        loginView.findViewById(R.id.login_txt_register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                SignUpDialog registerDialog = new SignUpDialog(baseActivity);
                registerDialog.show();
            }
        });

        username.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    usernameInput.setError(context.getString(R.string.error_null_username));
                }else{
                    usernameInput.setError(null);
                }
            }
        });

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(TextUtils.isEmpty(s)){
                    passwordInput.setError(baseActivity.getString(R.string.error_null_password));
                }else {
                    passwordInput.setError(null);
                }
            }
        });

        negativeActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        positiveActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(username.getText())) {
                    usernameInput.setError(context.getString(R.string.error_null_username));
                    return;
                }
                if (TextUtils.isEmpty(password.getText())) {
                    passwordInput.setError(context.getString(R.string.error_null_password));
                    return;
                }
                baseActivity.showLoading();
                final User user = getUser();
                user.login(baseActivity, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        EventBus.getDefault().post(new Event(Event.EVENT_USER_SIGN_IN));
                        MobclickAgent.onEvent(baseActivity, "UserLogin");
                        dismiss();
                        baseActivity.dismissLoading();
                        Logger.i("user login succeed!");
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        switch (i){
                            case 101:
                                passwordInput.setError(baseActivity.getString(R.string.error_username_password_error));
                                break;
                            default:
                                Logger.i(s);
                                EventLog.BmobToastError(i, baseActivity);
                                break;
                        }
                        baseActivity.dismissLoading();
                    }
                });
            }
        });
        layoutParams(-1,-2);

    }

    /**
     * 获取用户
     * @return
     */
    public User getUser(){
        User user = new User();
        user.setUsername(username.getText().toString().trim().toLowerCase());
        user.setPassword(StringUtil.MD5(password.getText().toString()));
        return user;
    }



}