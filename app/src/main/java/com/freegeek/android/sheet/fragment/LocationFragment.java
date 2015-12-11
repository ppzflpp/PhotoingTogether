package com.freegeek.android.sheet.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.freegeek.android.sheet.R;
import com.freegeek.android.sheet.activity.SheetShotActivity;
import com.freegeek.android.sheet.bean.Event;
import com.freegeek.android.sheet.bean.Sheet;
import com.freegeek.android.sheet.service.LocationService;
import com.freegeek.android.sheet.service.UserService;
import com.freegeek.android.sheet.ui.MyMaterialList;
import com.freegeek.android.sheet.ui.SheetCardProvider;
import com.freegeek.android.sheet.util.EventLog;
import com.orhanobut.logger.Logger;
import com.squareup.picasso.RequestCreator;

import java.util.List;
import java.util.Map;

import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.UpdateListener;
import de.greenrobot.event.EventBus;


public class LocationFragment extends BaseFragment {


    private MyMaterialList mSheetMyMaterialList;
    private LinearLayout mTipLinearLayout;
    public LocationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void onEvent(Event event){
        switch (event.getEventCode()){
            case Event.EVENT_GET_LOCATION:
                if(isShowing())refreshList();
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_location, container, false);
        setView(view);
        mSheetMyMaterialList = (MyMaterialList) view.findViewById(R.id.list_sheet);
        mTipLinearLayout = (LinearLayout) view.findViewById(R.id.linear_tip_location);
        refreshList();
        return view;
    }


    private void refreshList(){
        UserService.getInstance().getNearbySheet(new FindListener<Sheet>() {
            @Override
            public void onSuccess(List<Sheet> sheets) {
                if(sheets.size() > 0) mTipLinearLayout.setVisibility(View.GONE);
                for (Sheet sheet : sheets) {
                    if(LocationService.location == null || sheet.getLocation() == null) continue;
                    LatLng myLocation = new LatLng(LocationService.location.getLatitude(),LocationService.location.getLongitude());
                    LatLng toLocation = new LatLng(sheet.getLocation().getLatitude(),sheet.getLocation().getLongitude());
                    double dis = DistanceUtil.getDistance(myLocation, toLocation);
                    String disTxt;
                    if(dis < 1000){
                        disTxt = (int)dis +"m";
                    }else{
                        disTxt = (int)dis / 1000 +"Km";
                    }
                    Card card = new Card.Builder(getActivity())
                            .setTag("SHEET_LOCATION_CARD")
                            .withProvider(new SheetCardProvider())
                            .setTitle(sheet.getContent())
                            .setLike(getCurrentUser() == null ? false : sheet.getLiker().contains(getCurrentUser().getObjectId()))
                            .setRightText(disTxt)
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
                                                mSheetMyMaterialList.getAdapter().notifyDataSetChanged();
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
                                                mSheetMyMaterialList.getAdapter().notifyDataSetChanged();
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
                    mSheetMyMaterialList.getAdapter().add(card);
                }
            }

            @Override
            public void onError(int i, String s) {
                EventLog.BmobToastError(i,s, getActivity());
            }
        });
    }




}
