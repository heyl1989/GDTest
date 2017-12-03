package com.pf.gdtest.activity;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.pf.gdtest.InitData;
import com.pf.gdtest.R;
import com.pf.gdtest.beans.CoordinateBean;

import java.util.List;

public class TestLines2Activity extends AppCompatActivity implements View.OnClickListener {

    private AMap aMap;
    private MapView mapView;
    /**
     * 清除地图上标志的按钮
     */
    private Button location_clearmarker_btn;
    /**
     * 设置数据的按钮
     */
    private Button initdata_btn;

    private UiSettings mUiSettings;
    int index = 1;

    private static final String TAG = "pf";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_lines2);
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
            setUpMap();
        }
        location_clearmarker_btn = (Button) findViewById(R.id.location_clearmarker_btn);
        initdata_btn = (Button) findViewById(R.id.initdata_btn);
        setUpView();
    }

    private void setUpView() {
        location_clearmarker_btn.setOnClickListener(this);
        initdata_btn.setOnClickListener(this);
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 设置默认定位按钮是否显示
        mUiSettings.setMyLocationButtonEnabled(false);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(false);
        // 设置显示比例尺
        mUiSettings.setScaleControlsEnabled(true);
        // 设置显示指南针
        mUiSettings.setCompassEnabled(true);

        LatLng locationLatLng = new LatLng(39.567386, 116.867459);
        Log.e(TAG, "====locationLatLng:" + locationLatLng);
        aMap.moveCamera(CameraUpdateFactory.newLatLng(locationLatLng));
        aMap.moveCamera(CameraUpdateFactory.zoomTo(9));
        Log.e(TAG, "====getMyLocation:" + (aMap.getMyLocation() == null ? "null" : aMap.getMyLocation().toString()));
        Log.e(TAG, "====getCameraPosition:" + (aMap.getCameraPosition() == null ? "null" : aMap.getCameraPosition().toString()));
        Log.e(TAG, "====getMaxZoomLevel:" + aMap.getMaxZoomLevel());
        Log.e(TAG, "====getMinZoomLevel:" + aMap.getMinZoomLevel());
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap(LatLng latLng) {
        MarkerOptions markerOption = new MarkerOptions()
//                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                .icon(BitmapDescriptorFactory.defaultMarker())
                .position(latLng)
                .draggable(false)
                .title("经度:" + latLng.longitude)
                .snippet("纬度:" + latLng.latitude);
        Marker marker = aMap.addMarker(markerOption);
        marker.showInfoWindow();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // 清除标志按钮
            case R.id.location_clearmarker_btn:
                if (aMap != null) {
                    aMap.clear();
                }
                break;
            // 设置数据
            case R.id.initdata_btn:
                if (aMap != null) {
                    aMap.clear();
                }
                List<CoordinateBean> lists = null;
                if (index == 1) {
                    lists = InitData.getCoordinate1();
                    index = 2;
                } else if (index == 2) {
                    lists = InitData.getCoordinate2();
                    index = 3;
                } else if (index == 3) {
                    lists = InitData.getCoordinate3();
                    index = 1;
                }
                LatLng[] latLngs = InitData.coordinateToLatlng(lists);
                aMap.addPolyline((new PolylineOptions())
                        .add(latLngs)
                        .width(10)
                        .color(Color.RED));
                addMarkersToMap(latLngs[0]);
                addMarkersToMap(latLngs[latLngs.length - 1]);

                LatLng maxLng = latLngs[0];
                LatLng minLng = latLngs[0];
                LatLng maxLat = latLngs[0];
                LatLng minLat = latLngs[0];
                for (int i = 1; i < latLngs.length; i++) {
                    if (latLngs[i].longitude > maxLng.longitude) {
                        maxLng = latLngs[i];
                    }
                    if (latLngs[i].longitude < minLng.longitude) {
                        minLng = latLngs[i];
                    }
                    if (latLngs[i].latitude > maxLat.latitude) {
                        maxLat = latLngs[i];
                    }
                    if (latLngs[i].latitude < minLat.latitude) {
                        minLat = latLngs[i];
                    }
                }
                // 中心点的坐标
                LatLng center = new LatLng((maxLat.latitude + minLat.latitude) / 2,
                        (maxLng.longitude + minLng.longitude) / 2);
                // 最大纬度差
                float diffLng = AMapUtils.calculateLineDistance(
                        new LatLng(maxLat.latitude, maxLng.longitude),
                        new LatLng(maxLat.latitude, minLng.longitude));
                // 最大经度差
                float diffLat = AMapUtils.calculateLineDistance(
                        new LatLng(maxLat.latitude, maxLng.longitude),
                        new LatLng(minLat.latitude, maxLng.longitude));
                // 路径最大值
                float distance = diffLng > diffLat ? diffLng : diffLat;
                // 缩放级别
                float level = getLevelWithDistance(distance);
                Log.e(TAG, "center:" + center.toString()
                        + "aMap.getScalePerPixel:" + aMap.getScalePerPixel() + "\n"
                        + "distance:" + distance + "\n"
                        + "level:" + level);
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, level));
                break;
        }
    }

    /**
     * @param maxDistance 最大距离
     * @return
     */
    private float getLevelWithDistance(float maxDistance) {
        float level = 18.0f;
        // 从距离占屏幕的4/5
        float x = maxDistance * 5 / 4;
        float distance = x / 11;
        if (distance <= 10) {
            level = 19.0f;
        } else if (distance <= 25) {
            level = 18.0f;
        } else if (distance <= 50) {
            level = 17.0f;
        } else if (distance <= 100) {
            level = 16.0f;
        } else if (distance <= 200) {
            level = 15.0f;
        } else if (distance <= 500) {
            level = 14.0f;
        } else if (distance <= 1000) {
            level = 13.0f;
        } else if (distance <= 2000) {
            level = 12.0f;
        } else if (distance <= 5000) {
            level = 11.0f;
        } else if (distance <= 10000) {
            level = 10.0f;
        } else if (distance <= 20000) {
            level = 9.0f;
        } else if (distance <= 30000) {
            level = 8.0f;
        } else if (distance <= 50000) {
            level = 7.0f;
        } else if (distance <= 100000) {
            level = 6.0f;
        } else if (distance <= 200000) {
            level = 5.0f;
        } else if (distance <= 500000) {
            level = 4.0f;
        } else if (distance <= 1000000) {
            level = 3.0f;
        }
        return level;
    }
}