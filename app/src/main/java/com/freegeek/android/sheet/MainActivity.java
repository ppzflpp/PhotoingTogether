package com.freegeek.android.sheet;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.freegeek.android.sheet.activity.BaseActivity;
import com.freegeek.android.sheet.bean.Event;
import com.freegeek.android.sheet.bean.User;
import com.freegeek.android.sheet.fragment.BaseFragment;
import com.freegeek.android.sheet.fragment.LocationFragment;
import com.freegeek.android.sheet.fragment.ProfileFragment;
import com.freegeek.android.sheet.fragment.SheetFragment;
import com.freegeek.android.sheet.ui.dialog.LoginDialog;
import com.freegeek.android.sheet.ui.dialog.SheetDialog;
import com.freegeek.android.sheet.util.APP;
import com.freegeek.android.sheet.util.EventLog;
import com.freegeek.android.sheet.util.FileUtil;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.orhanobut.logger.Logger;
import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.net.URI;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.DeleteListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity {
    private Drawer mDrawer;
    private Toolbar mToolbar;
    private User mUser;
    private AccountHeader mHeaderResult;
    private IProfile iProfile;
    private ProfileFragment mProfileFragment;
    private SheetFragment mSheetFragment;
    private LocationFragment mLocationFragment;
    public LocationClient mLocationClient = null;
    public BDLocationListener myListener = new MyLocationListener();

    private void initLocation(){
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span=1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
        mLocationClient.setLocOption(option);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        setSupportActionBar(mToolbar);

        mUser = BmobUser.getCurrentUser(this, User.class);
        initFragments();
        initDrawerNavigation();
        refreshUser();
        replaceFragment(mSheetFragment);
        mLocationClient = new LocationClient(getApplicationContext());     //声明LocationClient类
        mLocationClient.registerLocationListener(myListener);    //注册监听函数
        mLocationClient.start();
    }

    private void initFragments(){
        mProfileFragment = new ProfileFragment();
        mSheetFragment = new SheetFragment();
        mLocationFragment = new LocationFragment();
    }


    /**
     * 初始化抽屉导航
     */
    private void initDrawerNavigation() {
        iProfile = new ProfileDrawerItem().withName(getString(R.string.not_logined_in))
                .withEmail(getString(R.string.tab_here_get_more_fun))
                .withIcon(getResources().getDrawable(R.drawable.avatar));
        mHeaderResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header_bg)
                .addProfiles(iProfile)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        if(mUser == null) login();
                        else  replaceFragment(mProfileFragment);
                        return false;
                    }
                })
                .withSelectionListEnabledForSingleProfile(false)
                .withOnAccountHeaderSelectionViewClickListener(new AccountHeader.OnAccountHeaderSelectionViewClickListener() {
                    @Override
                    public boolean onClick(View view, IProfile profile) {
                        if(mUser == null) login();
                        else   replaceFragment(mProfileFragment);
                        mDrawer.closeDrawer();
                        return false;
                    }
                })
                .build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem itemMsg = new PrimaryDrawerItem().withIcon(R.drawable.ic_location_on_grey_24dp).withName("附近的人");
        PrimaryDrawerItem itemRanking = new PrimaryDrawerItem().withIcon(R.drawable.ic_assessment_grey_24dp).withName(R.string.ranking_list);
        PrimaryDrawerItem itemFollow = new PrimaryDrawerItem().withIcon(R.drawable.ic_grade_grey_24dp).withName(R.string.my_foucs);
        PrimaryDrawerItem itemLike = new PrimaryDrawerItem().withIcon(R.drawable.ic_favorite_grey_24dp).withName(R.string.my_like);
        SecondaryDrawerItem itemTheme = new SecondaryDrawerItem().withName(R.string.theme);

        // Create the AccountHeader

        //create the drawer and remember the `Drawer` result object
        mDrawer = new DrawerBuilder()
                .withAccountHeader(mHeaderResult)
                .withActivity(this)
                .withToolbar(mToolbar)
                .addDrawerItems(
                        itemMsg,
                        itemRanking,
                        itemFollow,
                        itemLike,
                        new DividerDrawerItem(),
                        itemTheme
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        if (mUser == null) {
                            switch (position) {
                                case 1:
                                    replaceFragment(mLocationFragment);
                                    break;
                                case 3:
                                    break;
                                case 4:
                                    showToast(R.string.not_logined_in);
                                    break;
                            }
                        } else {
                            switch (position) {
                                case 1:
                                    replaceFragment(mLocationFragment);
                                    break;
                                case 3:
                                    break;
                                case 4:
                                    showToast(R.string.not_logined_in);
                                    break;
                            }
                        }

                        return false;
                    }
                })
                .build();
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawer.openDrawer();
            }
        });
    }

    private void refreshUser(){
        mUser = BmobUser.getCurrentUser(this,User.class);
        if(mUser !=null){
            iProfile.withName(mUser.getNick());
            iProfile.withEmail(mUser.getEmail());
            iProfile.withIcon(getResources().getDrawable(R.drawable.avatar));
            if(mUser.getAvatar() != null ){
                ImageLoader.getInstance().loadImage(mUser.getAvatar().getFileUrl(this), new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                        iProfile.withIcon(loadedImage);
                        mHeaderResult.updateProfile(iProfile);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });
            }else{
                mHeaderResult.updateProfile(iProfile);
            }
        }else{
            iProfile.withName(getString(R.string.not_logined_in));
            iProfile.withEmail(getString(R.string.tab_here_get_more_fun));
            iProfile.withIcon(getResources().getDrawable(R.drawable.avatar));
            mHeaderResult.updateProfile(iProfile);
        }
    }

    private void login(){
        final LoginDialog loginDialog = new LoginDialog(MainActivity.this);
        loginDialog.show();
    }

    private String mPhotoName;
    public void onEvent(Event event){
        switch (event.getEventCode()){
            case Event.EVENT_USER_SIGN_IN:
            case Event.EVENT_USER_PROFILE_UPDATE:
                refreshUser();
                break;
            case Event.EVENT_USER_SIGN_OUT:
                refreshUser();
                break;
            case Event.EVENT_GET_CAMERA_SHEET_PHOTO:
                mPhotoName = event.getTag().toString();
                break;
            case Event.EVENT_GET_LOCATION:
                MyApplication.updateUserLocation();
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_homepage) {
            replaceFragment(mSheetFragment);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void replaceFragment(Fragment fragment){
        FragmentTransaction transaction = getFragmentTransaction();
        if(fragment.isAdded()){
            transaction.hide(BaseFragment.getShowingFragement(getSupportFragmentManager()));
            transaction.show(fragment);
        }else{
            transaction.replace(R.id.fragment_content, fragment);
            transaction.addToBackStack(null);
        }
        transaction.commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if(resultCode != RESULT_OK) return;
        switch (requestCode) {
            case Crop.REQUEST_PICK:
                if(result.getData() != null){
                    File file = FileUtil.getTempFile("avatar.png");
                    Uri uri = Uri.fromFile(file);
                    Crop.of(result.getData(), uri).withAspect(1,1).withMaxSize(200,200).asSquare().start(this);
                }
                break;
            case Crop.REQUEST_CROP:
                File file = FileUtil.getTempFile("avatar.png");
                final BmobFile bmobFile = new BmobFile(file);
                showLoading();
                //上传新头像
                bmobFile.uploadblock(this, new UploadFileListener() {
                    @Override
                    public void onSuccess() {
                        //删除旧头像
                        if(mUser.getAvatar()!=null) {mUser.getAvatar().delete(getActivity());}

                        mUser.setAvatar(bmobFile);
                        mUser.update(getActivity(), mUser.getObjectId(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                EventBus.getDefault().post(new Event(Event.EVENT_USER_PROFILE_UPDATE));
                                dismissLoading();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                EventLog.BmobToastError(i,getActivity());
                                dismissLoading();
                            }
                        });
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        EventLog.BmobToastError(i,getActivity());
                        dismissLoading();
                    }
                });
                break;
            case APP.REQUEST.CODE_CAMERA:
                    File file1= FileUtil.getImageFile(mPhotoName);
                    SheetDialog sheetDialog =new SheetDialog(this,file1);
                    sheetDialog.show();
                    sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file1)));
                break;
            case APP.REQUEST.CODE_PICK_PICTURE:
                if(result!=null){
                    File file2 = FileUtil.getFileByUri(this, result.getData());
                    if(file2!=null){
                        SheetDialog sheetDialog2 =new SheetDialog(this,file2);
                        sheetDialog2.show();
                    }
                }
                break;
        }
    }




}
