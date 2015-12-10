package com.freegeek.android.sheet.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.listeners.RecyclerItemClickListener;
import com.freegeek.android.sheet.R;
import com.freegeek.android.sheet.activity.BaseActivity;
import com.freegeek.android.sheet.activity.SheetShotActivity;
import com.freegeek.android.sheet.bean.Event;
import com.freegeek.android.sheet.bean.Sheet;
import com.freegeek.android.sheet.service.UserService;
import com.freegeek.android.sheet.ui.MyFab;
import com.freegeek.android.sheet.ui.MyMaterialList;
import com.freegeek.android.sheet.ui.SheetCardProvider;
import com.freegeek.android.sheet.util.APP;
import com.freegeek.android.sheet.util.EventLog;
import com.freegeek.android.sheet.util.FileUtil;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import de.greenrobot.event.EventBus;


public class SheetFragment extends BaseFragment {


    public SheetFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    private MaterialSheetFab materialSheetFab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_sheet, container, false);
        setView(view);
        initView(view);
        return view;
    }

    public void onEvent(Event event){
        switch (event.getEventCode()){
            case Event.EVENT_DELETE_SHEET:
            case Event.EVENT_PUSH_SHEET:
                refreshData();
                break;
        }
    }

    private MyMaterialList mListView;
    private void initView(View view){
        MyFab fab = (MyFab) view.findViewById(R.id.fab);
        View sheetView = view.findViewById(R.id.fab_sheet);
        View overlay = view.findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(android.R.color.white);
        int fabColor = getResources().getColor(R.color.material_blue);
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
                sheetColor, fabColor);

        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onShowSheet() {
                super.onShowSheet();
                if (getCurrentUser() == null) {
                    materialSheetFab.hideSheet();
                    BaseActivity.showLoginTip((BaseActivity) getActivity());
                    return;
                }
            }
        });


        mListView = (MyMaterialList)view. findViewById(R.id.material_listview);

        sheetView.findViewById(R.id.fab_item_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();              // 指定开启系统相机的Action
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);              // 根据文件地址创建文件

                String name = System.currentTimeMillis() + "";
                File file1 = FileUtil.getImageFile(name);             // 把文件地址转换成Uri格式
                Uri uri = Uri.fromFile(file1);             // 设置系统相机拍摄照片完成后图片文件的存放地址

                intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
//                intent.putExtra(APP.EXTRA.FILE_NAME, name);
                getActivity().startActivityForResult(intent, APP.REQUEST.CODE_CAMERA);

                EventBus.getDefault().post(new Event(name, Event.EVENT_GET_CAMERA_SHEET_PHOTO));
                materialSheetFab.hideSheet();
            }
        });

        sheetView.findViewById(R.id.fab_item_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Crop.pickImage(getActivity(), APP.REQUEST.CODE_PICK_PICTURE);
                materialSheetFab.hideSheet();
            }
        });

        refreshData();


    }

    public void refreshData(){
        mListView.getAdapter().clearAll();
        UserService.getInstance().getMySheet(new FindListener<Sheet>() {
            @Override
            public void onSuccess(List<Sheet> sheets) {
                if (sheets.size() == 0) {
                    findViewById(R.id.linear_tip).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.linear_tip).setVisibility(View.GONE);
                    for (Sheet sheet : sheets) {
                        Card card = new Card.Builder(getActivity())
                                .setTag("SHEET_CARD")
                                .withProvider(new SheetCardProvider())
                                .setTitle(sheet.getContent())
                                .setLike(getCurrentUser() == null ? false : getCurrentUser().getLikeSheet().contains(sheet.getObjectId()))
                                .setSheet(sheet)
                                .setItemClickListener(new OnActionClickListener() {
                                    @Override
                                    public void onActionClicked(View view, Card card) {
                                        Intent intent = new Intent(getActivity(), SheetShotActivity.class);
                                        Bundle bundle = new Bundle();
                                        bundle.putSerializable(SheetShotActivity.KEY_SHEET, ((SheetCardProvider) card.getProvider()).getSheet());
                                        intent.putExtras(bundle);
                                        startActivity(intent);
                                    }
                                })
                                .setLikeListener(new OnActionClickListener() {
                                    @Override
                                    public void onActionClicked(View view, Card card) {

                                        final SheetCardProvider sheetCardProvider = (SheetCardProvider) card.getProvider();
                                        if (sheetCardProvider.isLike()) {
                                            UserService.getInstance().removeLikeSheet(sheetCardProvider.getSheet(), new UpdateListener() {
                                                @Override
                                                public void onSuccess() {
                                                    sheetCardProvider.setLike(false);
                                                }

                                                @Override
                                                public void onFailure(int i, String s) {
                                                    EventLog.BmobToastError(i, s,getActivity());
                                                }
                                            });

                                        } else {
                                            UserService.getInstance().addLikeSheet(sheetCardProvider.getSheet(), new UpdateListener() {
                                                @Override
                                                public void onSuccess() {
                                                    sheetCardProvider.setLike(true);
                                                }

                                                @Override
                                                public void onFailure(int i, String s) {
                                                    EventLog.BmobToastError(i,s, getActivity());
                                                }
                                            });
                                        }

                                    }
                                })
                                .setDrawable(sheet.getPicture().getFileUrl(getActivity()))
                                .endConfig()
                                .build();
                        mListView.getAdapter().add(card);
                    }

                }
            }

            @Override
            public void onError(int i, String s) {
                EventLog.BmobToastError(i, s,getActivity());
            }
        });
    }

}
