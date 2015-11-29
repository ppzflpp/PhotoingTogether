package com.freegeek.android.sheet.activity;

import android.content.Intent;
import android.media.midi.MidiDeviceInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.Poi;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiNearbySearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.utils.poi.BaiduMapPoiSearch;
import com.freegeek.android.sheet.MyApplication;
import com.freegeek.android.sheet.R;
import com.freegeek.android.sheet.bean.Event;
import com.freegeek.android.sheet.service.LocationService;
import com.freegeek.android.sheet.util.APP;
import com.orhanobut.logger.Logger;
import com.rey.material.widget.SnackBar;

import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * create by rtugeek on 2015-11-29
 *
 * result return intent bundle:
 * name 地址名称
 * latitude 纬度
 * longitude 经度
 */
public class PickLocationActivity extends BaseActivity implements OnGetGeoCoderResultListener {
    MapView mMapView = null;
    BaiduMap mBaiduMap = null;
    TextView mLocation;
    LatLng mLatLng = new LatLng(0,0);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_location);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        mMapView = (MapView) findViewById(R.id.baiduMap);
        mLocation= (TextView)findViewById(R.id.pick_location_txt_location);

        //初始化toobar
        toolbar.setTitle(R.string.Location);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_white_24dp);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mMapView.showZoomControls(false);
        mBaiduMap= mMapView.getMap();
        mBaiduMap.setOnMarkerDragListener(new BaiduMap.OnMarkerDragListener() {
            public void onMarkerDrag(Marker marker) {
                //拖拽中
            }

            public void onMarkerDragEnd(Marker marker) {
//                拖拽结束 通过地理编码检索位置
                GeoCoder mSearch = GeoCoder.newInstance();
                mSearch.setOnGetGeoCodeResultListener(PickLocationActivity.this);
                ReverseGeoCodeOption reverseGeoCodeOption = new ReverseGeoCodeOption();
                reverseGeoCodeOption.location(marker.getPosition());
                mSearch.reverseGeoCode(reverseGeoCodeOption);
                mLatLng = marker.getPosition();
            }

            public void onMarkerDragStart(Marker marker) {
                //开始拖拽
            }
        });


        refreshLocation();
        LocationService.getInstance(this).getLocation();

        if(!sp.getBoolean(APP.KEY.TIP_LOCATION,false)){
            SnackBar mSnackBar = SnackBar.make(this).text(R.string.tip_location).duration(5000).applyStyle(R.style.SnackBarMultiLine);
            mSnackBar.show(this);
            spe.putBoolean(APP.KEY.TIP_LOCATION,true).commit();
        }
    }

    private void refreshLocation(){
        if(MyApplication.location != null){
            mBaiduMap.clear();
            mBaiduMap.setMyLocationEnabled(true);

            mLatLng = new LatLng(MyApplication.location.getLatitude(),MyApplication.location.getLongitude());

            //添加标注
            BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                    .fromResource(R.drawable.ic_geo
                    );
            Bundle bundle = new Bundle();
            bundle.putInt("id", 0);
            OverlayOptions oo = new MarkerOptions().draggable(true).icon(mCurrentMarker).position(mLatLng).extraInfo(bundle);
            mBaiduMap.addOverlay(oo);
            //地图移动到当前地点
            MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(mLatLng);
            mBaiduMap.animateMapStatus(u);

            // 当不需要定位图层时关闭定位图层
            mBaiduMap.setMyLocationEnabled(false);

            List<Poi> list = MyApplication.location.getPoiList();// POI数据
            if (list != null) {
                mLocation.setText(list.get(0).getName());
                Logger.i(list.get(0).getName());
            }

        }

    }

    public void onEvent(Event event){
        switch (event.getEventCode()){
            case Event.EVENT_GET_LOCATION:
                refreshLocation();
                break;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Override
    protected void onResume() {
        super.onResume();
        startService(LocationService.class);
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_pick_location, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refresh:
                LocationService.getInstance(this).getLocation();
                break;
            case R.id.action_ok:
                Bundle bundle = new Bundle();
                bundle.putString("name", mLocation.getText().toString());
                bundle.putDouble("latitude", mLatLng.latitude);
                bundle.putDouble("longitude", mLatLng.longitude);

                Intent intent = new Intent();
                intent.putExtras(bundle);

                setResult(RESULT_OK, intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {

    }

    @Override
    public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {
        List<PoiInfo> list = reverseGeoCodeResult.getPoiList();
        if(list != null){
            if(list.size() > -1) mLocation.setText(list.get(0).name);
        }
    }
}
