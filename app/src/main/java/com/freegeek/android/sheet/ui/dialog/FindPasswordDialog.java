package com.freegeek.android.sheet.ui.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


import com.freegeek.android.sheet.R;
import com.freegeek.android.sheet.activity.BaseActivity;
import com.freegeek.android.sheet.util.APP;
import com.freegeek.android.sheet.util.EventLog;
import com.freegeek.android.sheet.util.StringUtil;
import com.orhanobut.logger.Logger;
import com.rey.material.app.Dialog;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.listener.ResetPasswordByEmailListener;

/**
 * Created by Administrator on 2015/2/24.
 */
public class FindPasswordDialog extends BaseDialog {
    private EditText etxMail;
    private TextInputLayout mEmailInput;

    public FindPasswordDialog(BaseActivity context) {
        super(context);
        LayoutInflater layoutInflater = getLayoutInflater();
        View findPasswordView = layoutInflater.inflate(R.layout.dialog_find_password, null);

        mEmailInput=(TextInputLayout)findPasswordView.findViewById(R.id.dialog_find_password_input_mail);
        mEmailInput.setHint(getString(R.string.input_your_email));

        etxMail = mEmailInput.getEditText();
        contentView(findPasswordView);

        title(context.getString(R.string.find_password));
        positiveAction(context.getString(R.string.confirm));
        negativeAction(context.getString(R.string.cancel));

        positiveActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtil.isEmail(etxMail.getText().toString())) {
                    showLoading();
                    BmobUser.resetPasswordByEmail(getActivity(), etxMail.getText().toString(), new ResetPasswordByEmailListener() {
                        @Override
                        public void onSuccess() {
                            dismiss();
                            showToast(getString(R.string.reset_password_1) + etxMail.getText().toString() + getString(R.string.reset_password_2));
                            dismissLoading();
                        }

                        @Override
                        public void onFailure(int code, String e) {
                            switch (code) {
                                case 205:
                                    mEmailInput.setError(getString(R.string.this_mail_not_be_used));
                                    break;
                                default:
                                    EventLog.BmobToastError(code, getActivity());
                                    break;
                            }
                            dismissLoading();
                        }
                    });
                } else {
                    mEmailInput.setError(getString(R.string.error_mail_format));
                }
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
