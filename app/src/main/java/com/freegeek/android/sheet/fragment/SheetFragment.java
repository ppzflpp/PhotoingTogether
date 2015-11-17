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
import com.dexafree.materialList.view.MaterialListView;
import com.freegeek.android.sheet.R;
import com.freegeek.android.sheet.bean.Event;
import com.freegeek.android.sheet.bean.Sheet;
import com.freegeek.android.sheet.ui.MyFab;
import com.freegeek.android.sheet.util.APP;
import com.freegeek.android.sheet.util.EventLog;
import com.freegeek.android.sheet.util.FileUtil;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.orhanobut.logger.Logger;
import com.soundcloud.android.crop.Crop;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import de.greenrobot.event.EventBus;


public class SheetFragment extends BaseFragment {


    public SheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }



    private MaterialSheetFab materialSheetFab;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_sheet, container, false);
        initView(view);
        return view;
    }


    private MaterialListView mListView;
    private void initView(View view){
        MyFab fab = (MyFab) view.findViewById(R.id.fab);
        View sheetView = view.findViewById(R.id.fab_sheet);
        View overlay = view.findViewById(R.id.overlay);
        int sheetColor = getResources().getColor(android.R.color.white);
        int fabColor = getResources().getColor(R.color.material_blue);
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay,
                sheetColor, fabColor);

        mListView = (MaterialListView)view. findViewById(R.id.material_listview);

        sheetView.findViewById(R.id.fab_item_camera).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();              // 指定开启系统相机的Action
                intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);              // 根据文件地址创建文件

                String name = System.currentTimeMillis()+"";
                File file1= FileUtil.getImageFile(name);             // 把文件地址转换成Uri格式
                Uri uri=Uri.fromFile(file1);             // 设置系统相机拍摄照片完成后图片文件的存放地址

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

        BmobQuery<Sheet> bmobQuery = new BmobQuery<>();
        bmobQuery.addWhereEqualTo("author", getCurrentUser());
        bmobQuery.order("-updatedAt");
        bmobQuery.include("author");// 希望在查询帖子信息的同时也把发布人的信息查询出来
        bmobQuery.findObjects(getActivity(), new FindListener<Sheet>() {
            @Override
            public void onSuccess(List<Sheet> object) {
                for (Sheet sheet: object) {
                    Card card = new Card.Builder(getActivity())
                            .setTag("BIG_IMAGE_CARD")
                            .withProvider(new CardProvider())
                            .setLayout(R.layout.material_big_image_card_layout)
                            .setTitle(sheet.getContent())
                            .setSubtitle(sheet.getContent())
                            .setDrawable(sheet.getPicture().getFileUrl(getActivity()))
                            .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                                @Override
                                public void onImageConfigure(@NonNull final RequestCreator requestCreator) {
//                                    requestCreator.rotate(position * 45.0f)
//                                            .resize(200, 200)
//                                            .centerCrop();
                                }
                            })
                            .endConfig()
                            .build();
                    mListView.getAdapter().add(card);
                }

            }

            @Override
            public void onError(int code, String msg) {
                EventLog.BmobToastError(code,getActivity());
            }
        });
    }


}
