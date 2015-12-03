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

import com.baidu.location.BDLocation;
import com.freegeek.android.sheet.activity.BaseActivity;
import com.freegeek.android.sheet.activity.PickLocationActivity;
import com.freegeek.android.sheet.bean.Event;
import com.freegeek.android.sheet.bean.User;
import com.freegeek.android.sheet.fragment.BaseFragment;
import com.freegeek.android.sheet.fragment.LocationFragment;
import com.freegeek.android.sheet.fragment.ProfileFragment;
import com.freegeek.android.sheet.fragment.SheetFragment;
import com.freegeek.android.sheet.service.LocationService;
import com.freegeek.android.sheet.service.UserService;
import com.freegeek.android.sheet.ui.dialog.LoginDialog;
import com.freegeek.android.sheet.ui.dialog.PostSheetDialog;
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
import com.soundcloud.android.crop.Crop;

import java.io.File;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity {
    private Drawer mDrawer;
    private Toolbar mToolbar;
    private User mUser;
    private AccountHeader mHeaderResult;
    private ProfileFragment mProfileFragment;
    private SheetFragment mSheetFragment;
    private LocationFragment mLocationFragment;
    private PostSheetDialog mPostSheetDialog;

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

        LocationService.getInstance(this).getLocation();
        UserService.getInstance(this).refreshLikeSheet();

        startActivity(PickLocationActivity.class);
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
        IProfile iProfile = new ProfileDrawerItem().withName(getString(R.string.not_logined_in))
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
        mHeaderResult.removeProfile(0);
        if(mUser !=null){
            IProfile iProfile = new ProfileDrawerItem().withName(mUser.getNick())
                    .withEmail(mUser.getEmail())
                    .withIcon(getResources().getDrawable(R.drawable.avatar));
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
                        mHeaderResult.removeProfile(0);
                        IProfile iProfile = new ProfileDrawerItem().withName(mUser.getNick())
                                .withEmail(mUser.getEmail())
                                .withIcon(loadedImage);
                        mHeaderResult.addProfile(iProfile,0);
                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                });

            }
            mHeaderResult.addProfile(iProfile,0);
        }else{
            IProfile iProfile = new ProfileDrawerItem().withName(getString(R.string.not_logined_in))
                    .withEmail(getString(R.string.tab_here_get_more_fun))
                    .withIcon(getResources().getDrawable(R.drawable.avatar));
            mHeaderResult.addProfile(iProfile,0);
        }
    }

    private void login(){
        LoginDialog loginDialog = new LoginDialog(MainActivity.this);
        loginDialog.show();
    }

    private String mPhotoName;
    public void onEvent(Event event){
        switch (event.getEventCode()){
            case Event.EVENT_USER_SIGN_IN:
            case Event.EVENT_USER_PROFILE_UPDATE:
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
                mPostSheetDialog =new PostSheetDialog(this,file1);
                mPostSheetDialog.show();

                //刷新手机媒体库
                sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file1)));
                break;
            case APP.REQUEST.CODE_PICK_PICTURE:
                if(result!=null){
                    File file2 = FileUtil.getFileByUri(this, result.getData());
                    if(file2!=null){
                        mPostSheetDialog =new PostSheetDialog(this,file2);
                        mPostSheetDialog.show();
                    }
                }
                break;
            case APP.REQUEST.CODE_PICK_LOCATION:
                if(mPostSheetDialog != null){
                    if(mPostSheetDialog.isShowing()){
                        BDLocation bdLocation = result.getExtras().getParcelable(PickLocationActivity.KEY_LOCATION);
                        mPostSheetDialog.setLocation(bdLocation);
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if(mSheetFragment.isShowing()){
            finish();
            return;
        }
        super.onBackPressed();
    }
}
