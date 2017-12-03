package com.pf.gdtest.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.view.animation.Interpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.Projection;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;
import com.pf.gdtest.R;
import com.pf.gdtest.util.SensorEventHelper;

import java.util.ArrayList;
import java.util.List;

public class LinesActivity extends AppCompatActivity implements LocationSource,
        AMapLocationListener, AMap.OnMapClickListener, View.OnClickListener, AMap.OnMarkerClickListener {

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
    /**
     * 存放地图上点的集合
     */
    private List<Marker> markerList;
    /**
     * 存放地图上线的集合
     */
    private List<Polyline> lineList;

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
        markerList = new ArrayList<>();
        lineList = new ArrayList<>();
        mSensorHelper = new SensorEventHelper(this);
        if (mSensorHelper != null) {
            mSensorHelper.registerSensorListener();
        }
        location_text = (TextView) findViewById(R.id.location_text);
        location_clearmarker_btn = (Button) findViewById(R.id.location_clearmarker_btn);
        mLocationErrText = (TextView) findViewById(R.id.location_errInfo_text);
        mLocationErrText.setVisibility(View.GONE);

        setUpView();
    }

    private void setUpView() {
        location_clearmarker_btn.setOnClickListener(this);
        location_text.setOnClickListener(this);
    }

    /**
     * 设置一些amap的属性
     */
    private void setUpMap() {
        // 设置定位监听
        aMap.setLocationSource(this);
        // 设置默认定位按钮是否显示
        mUiSettings.setMyLocationButtonEnabled(true);
        // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        aMap.setMyLocationEnabled(true);
        // 设置显示比例尺
        mUiSettings.setScaleControlsEnabled(true);
        // 设置显示指南针
        mUiSettings.setCompassEnabled(true);
        // 对amap添加单击地图事件监听器
        aMap.setOnMapClickListener(this);
        // 地图标志物点击事件
        aMap.setOnMarkerClickListener(this);
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
        Bitmap bMap = BitmapFactory.decodeResource(this.getResources(), R.mipmap.navi_map_gps_locked);
        BitmapDescriptor des = BitmapDescriptorFactory.fromBitmap(bMap);

//		BitmapDescriptor des = BitmapDescriptorFactory.fromResource(R.drawable.navi_map_gps_locked);
        MarkerOptions options = new MarkerOptions();
        options.icon(des);
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = aMap.addMarker(options);
        mLocMarker.setTitle(LOCATION_MARKER_FLAG);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        double offset = 0.001;
        // 将已经在地图上显示的点排除
        for (int i = 0; i < markerList.size(); i++) {
            LatLng location = markerList.get(i).getPosition();
            if (Math.abs(latLng.latitude - location.latitude) <= offset
                    || Math.abs(latLng.longitude - location.longitude) <= offset) {
                Toast.makeText(this, "距离太近，请重新选择位置", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        Log.e(TAG, "point=" + latLng);
        addMarkersToMap(latLng);
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap(LatLng latLng) {
        MarkerOptions markerOption = new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                .position(latLng)
                .draggable(false)
                .title("经度:" + latLng.longitude)
                .snippet("纬度:" + latLng.latitude);
        Marker marker = aMap.addMarker(markerOption);
        marker.showInfoWindow();
        markerList.add(marker);
        addLineToMap(latLng);
    }

    /**
     * 在地图上添加线
     *
     * @param latLng
     */
    private void addLineToMap(LatLng latLng) {
        if (markerList.size() >= 2) {
            if (lineList.size() < markerList.size() - 1) {
                Polyline polyline = aMap.addPolyline((new PolylineOptions())
                        .add(latLng, markerList.get(markerList.size() - 2).getPosition())
                        .width(10)
                        .color(Color.RED));
                lineList.add(polyline);
            }
        }
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

                    markerList.clear();
                    lineList.clear();
                }
                break;
            // 重新定位
            case R.id.location_text:
                if (aMap != null) {
                    mFirstFix = false;
                }
                break;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (!marker.isInfoWindowShown()) {
            marker.showInfoWindow();
        } else {
            marker.hideInfoWindow();
        }
        if (marker.getPosition().latitude != locationLatLng.latitude
                && marker.getPosition().longitude != locationLatLng.longitude) {
            jumpPoint(marker);
        }
        return true;
    }

    /**
     * marker点击时跳动一下
     */
    public void jumpPoint(final Marker marker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = aMap.getProjection();
        final LatLng markerLatlng = marker.getPosition();
        Point markerPoint = proj.toScreenLocation(markerLatlng);
        markerPoint.offset(0, -100);
        final LatLng startLatLng = proj.fromScreenLocation(markerPoint);
        final long duration = 1500;

        final Interpolator interpolator = new BounceInterpolator();
        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * markerLatlng.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * markerLatlng.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));
                if (t < 1.0) {
                    handler.postDelayed(this, 16);
                }
            }
        });
    }
}