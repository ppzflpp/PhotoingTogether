package com.freegeek.android.sheet.ui.dialog;

import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.freegeek.android.sheet.R;
import com.freegeek.android.sheet.activity.BaseActivity;
import com.freegeek.android.sheet.bean.Event;
import com.freegeek.android.sheet.bean.User;
import com.freegeek.android.sheet.util.APP;
import com.freegeek.android.sheet.util.EventLog;
import com.freegeek.android.sheet.util.StringUtil;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.SaveListener;
import de.greenrobot.event.EventBus;

/**
 * Created by Administrator on 2015/2/24.
 */
public class SignUpDialog extends BaseDialog {
    private EditText etxUsername,etxMail,etxPassword1,etxPassword2;

    private TextInputLayout mUsernameInput,mEmailInput,mPasswordInput1,mPasswordInput2;
    public SignUpDialog(BaseActivity activity) {
        super(activity);
        LayoutInflater layoutInflater = getLayoutInflater();
        View registerView = layoutInflater.inflate(R.layout.dialog_sign_up,null);

        mUsernameInput = (TextInputLayout)registerView.findViewById(R.id.dialog_sign_up_input_username);
        mEmailInput = (TextInputLayout)registerView.findViewById(R.id.dialog_sign_up_input_email);
        mPasswordInput1 = (TextInputLayout)registerView.findViewById(R.id.dialog_sign_up_input_password_1);
        mPasswordInput2 = (TextInputLayout)registerView.findViewById(R.id.dialog_sign_up_input_password_2);

        etxUsername = mUsernameInput.getEditText();
        etxMail = mEmailInput.getEditText();
        etxPassword1 = mPasswordInput1.getEditText();
        etxPassword2 = mPasswordInput2.getEditText();

        mUsernameInput.setHint(getString(R.string.username));
        mUsernameInput.setCounterEnabled(true);
        mUsernameInput.setCounterMaxLength(15);

        mEmailInput.setHint(getString(R.string.email));
        mPasswordInput1.setHint(getString(R.string.password));
        mPasswordInput1.setCounterEnabled(true);
        mPasswordInput1.setCounterMaxLength(15);

        mPasswordInput2.setHint(getString(R.string.confirm_password));
        mPasswordInput2.setCounterEnabled(true);
        mPasswordInput2.setCounterMaxLength(15);

        contentView(registerView);
        title(activity.getString(R.string.register));
        positiveAction(activity.getString(R.string.confirm));
        negativeAction(activity.getString(R.string.cancel));
        layoutParams(-1, -2);

        etxUsername.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    mUsernameInput.setError(getString(R.string.error_null_username));
                } else {
                    mUsernameInput.setError("");
                }
            }
        });

        etxPassword1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (TextUtils.isEmpty(s)) {
                    mPasswordInput1.setError(getString(R.string.error_null_password));
                } else {
                    if (s.toString().length() < 6) {
                        mPasswordInput1.setError(getString(R.string.error_password_length));
                    } else {
                        mPasswordInput1.setError("");
                    }
                }
            }
        });

        etxPassword2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals(etxPassword1.getText().toString())) {
                    mPasswordInput2.setError("");
                } else {
                    mPasswordInput2.setError(getString(R.string.eror_diff_password));
                }
            }
        });

        positiveActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(etxUsername.getText())) {
                    mUsernameInput.setError(getString(R.string.error_null_username));
                    return;
                }

                if (!TextUtils.isEmpty(etxMail.getText())) {
                    if (!StringUtil.isEmail(etxMail.getText().toString())) {
                        mEmailInput.setError(getString(R.string.error_mail_format));
                    }
                }

                if (TextUtils.isEmpty(etxPassword1.getText())) {
                    mPasswordInput1.setError(getString(R.string.error_null_password));
                    return;
                } else {
                    if (etxPassword1.getText().toString().length() < 6) {
                        mPasswordInput1.setError(getString(R.string.error_password_length));
                    } else {
                        mPasswordInput1.setError("");
                    }
                }

                if (!etxPassword1.getText().toString().equals(etxPassword2.getText().toString())) {
                    mPasswordInput2.setError(getString(R.string.eror_diff_password));
                    return;
                }

                showLoading();
                final User user = new User();
                user.setNick(etxUsername.getText().toString().toLowerCase());
                user.setEmail(etxMail.getText().toString().toLowerCase());
                user.setUsername(etxUsername.getText().toString().toLowerCase());
                user.setPassword(StringUtil.MD5(etxPassword1.getText().toString()));
                user.signUp(getContext(), new SaveListener() {
                    @Override
                    public void onSuccess() {
                        if (!TextUtils.isEmpty(etxMail.getText())) {
                            showToast(getString(R.string.send_email_1) + user.getEmail() + getString(R.string.send_email_2));
                        }
                        dismiss();
                        BmobUser.logOut(getContext());
                        spe.putString(APP.KEY.USERNAME, user.getUsername()).commit();
                        EventBus.getDefault().post(new Event(Event.EVENT_USER_SIGN_UP));
                        dismissLoading();
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        switch (i) {
                            case 202:
                                mUsernameInput.setError(getString(R.string.error_username_used));
                                dismissLoading();
                                break;
                            default:
                                EventLog.BmobToastError(i, getActivity());
                                dismissLoading();
                                break;
                        }

                    }
                });
            }
        });

        negativeActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }



}
