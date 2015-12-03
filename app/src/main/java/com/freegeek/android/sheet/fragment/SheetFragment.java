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
                if(getCurrentUser() == null){
                    materialSheetFab.hideSheet();
                    BaseActivity.showLoginTip((BaseActivity)getActivity());
                    return;
                }
            }
        });


        mListView = (MyMaterialList)view. findViewById(R.id.material_listview);

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
        bmobQuery.include("author,liker");// 希望在查询帖子信息的同时也把发布人的信息查询出来
        bmobQuery.findObjects(getActivity(), new FindListener<Sheet>() {
            @Override
            public void onSuccess(final List<Sheet> sheets) {
                for (Sheet sheet : sheets) {
                    Card card = new Card.Builder(getActivity())
                            .setTag("SHEET_CARD")
                            .withProvider(new SheetCardProvider())
                            .setTitle(sheet.getContent())
                            .setLike(sheet.getLiker().contains(getCurrentUser().getObjectId()))
                            .setCommentNumber(5)
                            .setSheet(sheet)
                            .setItemClickListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, Card card) {
                                    Intent intent= new Intent(getActivity(), SheetShotActivity.class);
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
                                        UserService.getInstance(getContext()).removeLikeSheet(sheetCardProvider.getSheet(), new UpdateListener() {
                                            @Override
                                            public void onSuccess() {
                                                sheetCardProvider.setLike(false);
                                            }

                                            @Override
                                            public void onFailure(int i, String s) {
                                                EventLog.BmobToastError(i, getActivity());
                                            }
                                        });

                                    } else {
                                        UserService.getInstance(getContext()).addLikeSheet(sheetCardProvider.getSheet(), new UpdateListener() {
                                            @Override
                                            public void onSuccess() {
                                                sheetCardProvider.setLike(true);
                                            }

                                            @Override
                                            public void onFailure(int i, String s) {
                                                EventLog.BmobToastError(i, getActivity());
                                            }
                                        });
                                    }

                                }
                            })
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
                EventLog.BmobToastError(code, getActivity());
            }
        });

    }


}
