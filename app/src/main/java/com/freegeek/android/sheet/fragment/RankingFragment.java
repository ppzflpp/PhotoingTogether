package com.freegeek.android.sheet.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.freegeek.android.sheet.R;
import com.freegeek.android.sheet.activity.SheetShotActivity;
import com.freegeek.android.sheet.bean.Sheet;
import com.freegeek.android.sheet.service.UserService;
import com.freegeek.android.sheet.ui.MyMaterialList;
import com.freegeek.android.sheet.ui.SheetCardProvider;
import com.freegeek.android.sheet.util.EventLog;
import com.squareup.picasso.RequestCreator;

import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by rtugeek on 2015-12-04
 */
public class RankingFragment extends BaseFragment {


    private MyMaterialList mSheetMyMaterialList;

    public RankingFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ranking, container, false);
        init(view);
        return view;
    }

    public void init(View view){
        mSheetMyMaterialList = (MyMaterialList) view.findViewById(R.id.list_sheet);
        BmobQuery<Sheet> bmobQuery = new BmobQuery<>();
        bmobQuery.order("-likerNumber");
        bmobQuery.include("author");
        bmobQuery.findObjects(getActivity(), new FindListener<Sheet>() {
            @Override
            public void onSuccess(final List<Sheet> sheets) {
                for (Sheet sheet : sheets) {
                    Card card = new Card.Builder(getActivity())
                            .setTag("SHEET_RANKING_CARD")
                            .withProvider(new SheetCardProvider())
                            .setTitle(sheet.getContent())
                            .setLike( sheet.getLiker().contains(getCurrentUser().getObjectId()))
                            .setLeftText(String.valueOf(sheet.getLiker().size()))
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
                                        UserService.getInstance().removeLikeSheet(sheetCardProvider.getSheet(), new UpdateListener() {
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
                                        UserService.getInstance().addLikeSheet(sheetCardProvider.getSheet(), new UpdateListener() {
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
                    mSheetMyMaterialList.getAdapter().add(card);
                }

            }

            @Override
            public void onError(int code, String msg) {
                EventLog.BmobToastError(code, getActivity());
            }
        });
    }
}
