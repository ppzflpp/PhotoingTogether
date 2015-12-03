package com.freegeek.android.sheet.ui.dialog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.TextInputLayout;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.freegeek.android.sheet.MyApplication;
import com.freegeek.android.sheet.R;
import com.freegeek.android.sheet.activity.BaseActivity;
import com.freegeek.android.sheet.activity.PickLocationActivity;
import com.freegeek.android.sheet.bean.Event;
import com.freegeek.android.sheet.bean.Sheet;
import com.freegeek.android.sheet.util.APP;
import com.freegeek.android.sheet.util.BitmapUtil;
import com.freegeek.android.sheet.util.EventLog;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.orhanobut.logger.Logger;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.greenrobot.event.EventBus;


/**
 * Created by rtugeek@gmail.com on 2015/11/11.
 */
public class PostSheetDialog extends BaseDialog {
    private TextInputLayout mThoughtInput;
    private EditText mEditText;
    private ImageView mImageView;
    private TextView mTxtLocation;
    private BDLocation mLocation;
    public PostSheetDialog(BaseActivity context, final File localImage) {
        super(context);
        View view = getActivity().getLayoutInflater().inflate(R.layout.dialog_post_sheet,null);
        mThoughtInput = (TextInputLayout)view.findViewById(R.id.dialog_input_thought);
        mTxtLocation = (TextView)view.findViewById(R.id.dialog_txt_location);

        mThoughtInput.setHint(getString(R.string.Thought));
        mThoughtInput.setCounterEnabled(true);
        mThoughtInput.setCounterMaxLength(140);

        mEditText = mThoughtInput.getEditText();

        Logger.i(localImage.getPath());

        mImageView= (ImageView)view.findViewById(R.id.dialog_img_sheet_img);
        ImageLoader.getInstance().loadImage("file://" + localImage.getAbsolutePath(), BitmapUtil.getImageDisplayOption(), new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mImageView.setImageBitmap(loadedImage);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });

        setContentView(view);

        negativeAction(getString(R.string.cancel));
        positiveAction(getString(R.string.confirm));

        negativeActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        positiveActionClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getCurrentUser() == null){
                    dismiss();
                    BaseActivity.showLoginTip(getActivity());
                    return;
                }
                File file = BitmapUtil.getZipImageFile(localImage.getAbsolutePath(), System.currentTimeMillis() + "");
                final BmobFile bmobFile = new BmobFile(file);
                showLoading();
                bmobFile.uploadblock(getActivity(), new UploadFileListener() {
                    @Override
                    public void onSuccess() {
                        Sheet sheet = new Sheet();
                        sheet.setAuthor(getCurrentUser());
                        sheet.setContent(mEditText.getText().toString());
                        sheet.setPicture(bmobFile);
                        sheet.setLocationName(mTxtLocation.getText().toString());
                        //添加地理位置
                        BmobGeoPoint bmobGeoPoint =new BmobGeoPoint(mLocation.getLongitude(),mLocation.getLatitude());
                        sheet.setLocation(bmobGeoPoint);

                        sheet.save(getActivity(), new SaveListener() {
                            @Override
                            public void onSuccess() {
                                dismissLoading();
                                dismiss();
                                EventBus.getDefault().post(new Event(Event.EVENT_PUSH_SHEET));
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                Logger.i(s);
                                EventLog.BmobToastError(i, getActivity());
                                dismissLoading();
                            }
                        });
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        dismissLoading();
                    }
                });
            }
        });

        view.findViewById(R.id.dialog_linear_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PickLocationActivity.class);
                getActivity().startActivityForResult(intent, APP.REQUEST.CODE_PICK_LOCATION);
            }
        });

        setLocation(MyApplication.location);
    }

    /**
     * 设置定位
     * @param bdLocation
     */
    public void setLocation(BDLocation bdLocation){
        mLocation = bdLocation;
        if(mLocation != null){
            mTxtLocation.setText(mLocation.getLocationDescribe());
        }
    }


}
