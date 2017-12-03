package com.pf.gdtest.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CameraPosition;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.PolylineOptions;
import com.pf.gdtest.InitData;
import com.pf.gdtest.R;
import com.pf.gdtest.util.SensorEventHelper;

public class TestLinesActivity extends AppCompatActivity implements LocationSource,
        AMapLocationListener, View.OnClickListener {

    private AMap aMap;
    private MapView mapView;
    /**
     * 定位失败显示错误原因
     */
    private TextView mLocationErrText;
    /**
     * 清除地图上标志的按钮
     */
    private Button location_clearmarker_btn;
    /**
     * 设置数据的按钮
     */
    private Button initdata_btn;
    /**
     * 右上角定位的位置，自己盖在上面的
     */
    private TextView location_text;

    private SensorEventHelper mSensorHelper;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private UiSettings mUiSettings;

    private boolean mFirstFix = false;
    private Marker mLocMarker;
    private Circle mCircle;
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR = Color.argb(10, 0, 0, 180);
    public static final String LOCATION_MARKER_FLAG = "mylocation";
    private static final String TAG = "pf";
    /**
     * 当前定位的地址
     */
    private LatLng locationLatLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lines);
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
        mSensorHelper = new SensorEventHelper(this);
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
        location_text = (TextView) findViewById(R.id.location_text);
        location_clearmarker_btn = (Button) findViewById(R.id.location_clearmarker_btn);
        initdata_btn = (Button) findViewById(R.id.initdata_btn);
        mLocationErrText = (TextView) findViewById(R.id.location_errInfo_text);
        mLocationErrText.setVisibility(View.GONE);

        setUpView();
    }

    private void setUpView() {
        location_clearmarker_btn.setOnClickListener(this);
        initdata_btn.setOnClickListener(this);
        location_text.setOnClickListener(this);
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 设置定位监听
        aMap.setLocationSource(this);
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                Log.e(TAG, "onCameraChange:" + cameraPosition.zoom);
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
                Log.e(TAG, "onCameraChangeFinish:" + cameraPosition.zoom);
            }
        });
        // 设置默认定位按钮是否显示
        mUiSettings.setMyLocationButtonEnabled(true);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        // 设置显示比例尺
        mUiSettings.setScaleControlsEnabled(true);
        // 设置显示指南针
        mUiSettings.setCompassEnabled(true);
    }

    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if (aMapLocation != null && aMapLocation.getErrorCode() == 0) {
                Log.e(TAG, aMapLocation.getLatitude() + "--" + aMapLocation.getLongitude());
                mLocationErrText.setVisibility(View.GONE);
                if (!mFirstFix) {
                    locationLatLng = new LatLng(aMapLocation.getLatitude(), aMapLocation.getLongitude());
                    mFirstFix = true;
                    addCircle(locationLatLng, aMapLocation.getAccuracy());//添加定位精度圆
                    addMarker(locationLatLng);//添加定位图标
                    mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng, 18));
                } else {
                    mCircle.setCenter(locationLatLng);
                    mCircle.setRadius(aMapLocation.getAccuracy());
                    mLocMarker.setPosition(locationLatLng);
                }
            } else {
                String errText = "定位失败," + aMapLocation.getErrorCode() + ": " + aMapLocation.getErrorInfo();
                Log.e(TAG, errText);
                mLocationErrText.setVisibility(View.VISIBLE);
                mLocationErrText.setText(errText);
            }
        }
    }

    private void addCircle(LatLng latlng, double radius) {
        if (mCircle != null) {
            return;
        }
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = aMap.addCircle(options);
    }

    private void addMarker(LatLng latlng) {
        if (mLocMarker != null) {
            return;
        }
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(),
                R.mipmap.navi_map_gps_locked);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);

//		BitmapDescriptor des = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
        MarkerOptions options = new MarkerOptions();
        options.icon(des);
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = aMap.addMarker(options);
        mLocMarker.setTitle(LOCATION_MARKER_FLAG);
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap(LatLng latLng) {
        MarkerOptions markerOption = new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
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
                    mCircle = null;
                    mLocMarker = null;
                    mFirstFix = false;
                }
                break;
            // 重新定位
            case R.id.location_text:
                if (aMap != null) {
                    mFirstFix = false;
                }
                break;
            // 设置数据
            case R.id.initdata_btn:
                LatLng[] latLngs = InitData.coordinateToLatlng(InitData.getCoordinate2());
                aMap.addPolyline((new PolylineOptions())
                        .add(latLngs)
                        .width(10)
                        .color(Color.RED));
                addMarkersToMap(latLngs[0]);
                addMarkersToMap(latLngs[latLngs.length - 1]);

                LatLng maxLng = latLngs[0];
                LatLng maxLat = latLngs[0];
                LatLng minLng = latLngs[0];
                LatLng minLat = latLngs[0];
                for (int i = 1; i < latLngs.length; i++) {
                    if (latLngs[i].longitude > maxLng.longitude) {
                        maxLng = latLngs[i];
                    }
                    if (latLngs[i].latitude > maxLat.latitude) {
                        maxLat = latLngs[i];
                    }
                    if (latLngs[i].longitude < minLng.longitude) {
                        minLng = latLngs[i];
                    }
                    if (latLngs[i].latitude < minLat.latitude) {
                        minLat = latLngs[i];
                    }
                }

                LatLng center = new LatLng((latLngs[latLngs.length - 1].latitude + latLngs[0].latitude) / 2,
                        (latLngs[latLngs.length - 1].longitude + latLngs[0].longitude) / 2);
                Log.e(TAG, "minLng.longitude:" + minLng.longitude + "\n"
                        + "maxLng.longitude" + maxLng.longitude + "\n"
                        + "maxLng.latitude" + maxLng.latitude + "\n"
                        + "minLng.latitude" + minLng.latitude + "\n");
                aMap.animateCamera(CameraUpdateFactory.newCameraPosition(CameraPosition.builder().target(center).build()));
                Log.e(TAG, "============" + aMap.getScalePerPixel());
//                checkLength(latLngs);
                break;
        }
    }

//    private void checkLength(LatLng[] latLngs) {
//        LatLng maxLng = latLngs[0];
//        LatLng maxLat = latLngs[0];
//        LatLng minLng = latLngs[0];
//        LatLng minLat = latLngs[0];
//        for (int i = 1; i < latLngs.length; i++) {
//            if (latLngs[i].longitude > maxLng.longitude) {
//                maxLng = latLngs[i];
//            }
//            if (latLngs[i].latitude > maxLat.latitude) {
//                maxLat = latLngs[i];
//            }
//            if (latLngs[i].longitude < minLng.longitude) {
//                minLng = latLngs[i];
//            }
//            if (latLngs[i].latitude < minLat.latitude) {
//                minLat = latLngs[i];
//            }
//        }
//
//        float diffLng = AMapUtils.calculateLineDistance(
//                new LatLng(Math.abs(maxLng.longitude), Math.abs(maxLng.latitude)),
//                new LatLng(Math.abs(minLng.longitude), Math.abs(minLng.latitude)));
//        float diffLat = AMapUtils.calculateLineDistance(
//                new LatLng(Math.abs(maxLat.longitude), Math.abs(maxLat.latitude)),
//                new LatLng(Math.abs(minLat.longitude), Math.abs(minLat.latitude)));
//        float diff1 = AMapUtils.calculateLineDistance(
//                new LatLng(Math.abs(maxLng.longitude), Math.abs(maxLat.latitude)),
//                new LatLng(Math.abs(maxLng.longitude), Math.abs(minLat.latitude)));
//        float diff2 = AMapUtils.calculateLineDistance(
//                new LatLng(Math.abs(maxLng.longitude), Math.abs(maxLat.latitude)),
//                new LatLng(Math.abs(minLng.longitude), Math.abs(maxLat.latitude)));
//        float distance = diffLng > diffLat ? diffLng : diffLat;
//
//        float level = distance * (1 / 200);
//
//        Log.e(TAG, "maxLng:" + maxLng + "\n"
//                + "maxLat:" + maxLat + "\n"
//                + "minLng:" + minLng + "\n"
//                + "minLat:" + minLat + "\n"
//                + "diffLng:" + diffLng + "\n"
//                + "diffLat:" + diffLat + "\n"
//                + "diff1:" + diff1 + "\n"
//                + "diff2:" + diff2 + "\n"
//                + "distance:" + distance + "\n"
//                + "level:" + level);
//    }
}