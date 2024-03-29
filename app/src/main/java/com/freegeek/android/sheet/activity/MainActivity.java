package com.freegeek.android.sheet.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.baidu.location.BDLocation;
import com.freegeek.android.sheet.R;
import com.freegeek.android.sheet.bean.Event;
import com.freegeek.android.sheet.bean.User;
import com.freegeek.android.sheet.fragment.BaseFragment;
import com.freegeek.android.sheet.fragment.FollowFragment;
import com.freegeek.android.sheet.fragment.LikeSheetFragment;
import com.freegeek.android.sheet.fragment.LocationFragment;
import com.freegeek.android.sheet.fragment.ProfileFragment;
import com.freegeek.android.sheet.fragment.RankingFragment;
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
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.orhanobut.logger.Logger;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;

import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import de.greenrobot.event.EventBus;

public class MainActivity extends BaseActivity {
    private Drawer mDrawer;
    private Toolbar mToolbar;
    private AccountHeader mHeaderResult;
    private ProfileFragment mProfileFragment;
    private SheetFragment mSheetFragment;
    private LocationFragment mLocationFragment;
    private RankingFragment mRankingFragment;
    private FollowFragment mFollowFragment;
    private LikeSheetFragment mLikeSheetFragment;
    private PostSheetDialog mPostSheetDialog;
    private IProfile mIProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        setSupportActionBar(mToolbar);

        initFragments();
        initDrawerNavigation();
        refreshUser();
        if(getCurrentUser() == null){
            replaceFragment(mRankingFragment);
        }else{
            replaceFragment(mSheetFragment);
        }

        LocationService.getInstance(this).getLocation();

    }

    private void initFragments(){
        mProfileFragment = new ProfileFragment();
        mSheetFragment = new SheetFragment();
        mLocationFragment = new LocationFragment();
        mRankingFragment = new RankingFragment();
        mFollowFragment = new FollowFragment();
        mLikeSheetFragment = new LikeSheetFragment();
    }

    /**
     * 初始化抽屉导航
     */
    private void initDrawerNavigation() {
        mIProfile = new ProfileDrawerItem().withName(getString(R.string.not_logined_in))
                .withEmail(getString(R.string.tab_here_get_more_fun))
                .withIcon(getResources().getDrawable(R.drawable.avatar));
        mHeaderResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header_bg)
                .addProfiles(mIProfile)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean currentProfile) {
                        if(getCurrentUser() == null) login();
                        else {
                            mProfileFragment.setUser(getCurrentUser());
                            replaceFragment(mProfileFragment);
                        }
                        return false;
                    }
                })
                .withSelectionListEnabledForSingleProfile(false)
                .withOnAccountHeaderSelectionViewClickListener(new AccountHeader.OnAccountHeaderSelectionViewClickListener() {
                    @Override
                    public boolean onClick(View view, IProfile profile) {
                        if(getCurrentUser() == null) login();
                        else{
                            mProfileFragment.setUser(getCurrentUser());
                            replaceFragment(mProfileFragment);
                        }
                        mDrawer.closeDrawer();
                        return false;
                    }
                })
                .build();

        //if you want to update the items at a later time it is recommended to keep it in a variable
        PrimaryDrawerItem itemMsg = new PrimaryDrawerItem().withIcon(R.drawable.ic_location_on_grey_24dp).withName(R.string.nearby);
        PrimaryDrawerItem itemRanking = new PrimaryDrawerItem().withIcon(R.drawable.ic_assessment_grey_24dp).withName(R.string.ranking_list);
        PrimaryDrawerItem itemFollow = new PrimaryDrawerItem().withIcon(R.drawable.ic_grade_grey_24dp).withName(R.string.my_foucs);
        PrimaryDrawerItem itemLike = new PrimaryDrawerItem().withIcon(R.drawable.ic_favorite_grey_24dp).withName(R.string.my_like);

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
                        new DividerDrawerItem()
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        // do something with the clicked item :D
                        if (getCurrentUser() == null) {
                            switch (position) {
                                case 1:
                                    replaceFragment(mLocationFragment);
                                    break;
                                case 2:
                                    replaceFragment(mRankingFragment);
                                    break;
                                case 3:
                                    BaseActivity.showLoginTip(MainActivity.this);
                                    break;
                                case 4:
                                    BaseActivity.showLoginTip(MainActivity.this);
                                    break;
                            }
                        } else {
                            switch (position) {
                                case 1:
                                    replaceFragment(mLocationFragment);
                                    break;
                                case 2:
                                    replaceFragment(mRankingFragment);
                                    break;
                                case 3:
                                    replaceFragment(mFollowFragment);
                                    break;
                                case 4:
                                    replaceFragment(mLikeSheetFragment);
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
        if(getCurrentUser() !=null){
            mIProfile.withName(getCurrentUser().getNick());
            mIProfile.withEmail(getCurrentUser().getEmail());
            mIProfile.withIcon(getResources().getDrawable(R.drawable.avatar));
            if(getCurrentUser().getAvatar() != null ){
                Picasso.with(this).load(getCurrentUser().getAvatar().getFileUrl(this)).error(R.drawable.avatar).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        mIProfile.withIcon(bitmap);
                        mHeaderResult.updateProfile(mIProfile);
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });
            }
        }else{
            mIProfile.withName(getString(R.string.not_logined_in));
            mIProfile.withEmail(getString(R.string.tab_here_get_more_fun));
            mIProfile.withIcon(getResources().getDrawable(R.drawable.avatar));
        }
        mHeaderResult.updateProfile(mIProfile);
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
                refreshUser();
                break;
            case Event.EVENT_USER_SIGN_OUT:
                refreshUser();
                replaceFragment(mRankingFragment);
                break;
            case Event.EVENT_GET_CAMERA_SHEET_PHOTO:
                mPhotoName = event.getTag().toString();
                break;
            case Event.EVENT_GET_LOCATION:
                UserService.getInstance().updateUserLocation();
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
            if(getCurrentUser() == null){
                replaceFragment(mRankingFragment);
            }else {
                replaceFragment(mSheetFragment);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        if(intent.getAction().equals(APP.ACTION.MAIN_ACTIVITY_FRAGMENT_PROFILE)){
            mProfileFragment.setUser((User)intent.getExtras().getSerializable("user"));
            replaceFragment(mProfileFragment);
        }
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
        Logger.i("requestCode:" +requestCode+"--resultCode:"+resultCode);

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
                        if(getCurrentUser().getAvatar()!=null) {getCurrentUser().getAvatar().delete(getActivity());}

                        getCurrentUser().setAvatar(bmobFile);
                        getCurrentUser().update(getActivity(), getCurrentUser().getObjectId(), new UpdateListener() {
                            @Override
                            public void onSuccess() {
                                EventBus.getDefault().post(new Event(Event.EVENT_USER_PROFILE_UPDATE));
                                dismissLoading();
                            }

                            @Override
                            public void onFailure(int i, String s) {
                                EventLog.BmobToastError(i, s,getActivity());
                                dismissLoading();
                            }
                        });
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        EventLog.BmobToastError(i,s,getActivity());
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
