package com.pf.gdtest.fragment;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;
import com.pf.gdtest.BaseConfig;
import com.pf.gdtest.R;
import com.pf.gdtest.beans.LatLonPointBean;
import com.pf.gdtest.activity.DrivingRouteOverLay;
import com.pf.gdtest.util.MapUtil;
import com.pf.gdtest.widget.MapContainer;

public class MapFragment
        extends Fragment
        implements RouteSearch.OnRouteSearchListener {

    private View root;
    private MapView mapView;
    private MapContainer mapContainer;
    private AMap aMap;
    private RouteSearch mRouteSearch;
    /**
     * 起点和终点坐标
     */
    private LatLonPoint mStartPoint;
    private LatLonPoint mEndPoint;
    /**
     * 地图的UI
     */
    private UiSettings mUiSettings;
    /**
     * 司机端查看路线页面嵌套地图滑动冲突问题
     */
    private ScrollView scrollView;

    public MapFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_map, container, false);
        mapView = root.findViewById(R.id.mapview);
        mapContainer = root.findViewById(R.id.map_container);
        if (this.scrollView != null) {
            mapContainer.setScrollView(scrollView);
        }
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
            setUpMap();
            init();
        }
        return root;
    }

    protected void init() {
        // 司机端查看路线页面嵌套地图滑动冲突问题
        if (scrollView != null) {
            mapContainer.setScrollView(this.scrollView);
        }
        mRouteSearch = new RouteSearch(getActivity());
        mRouteSearch.setRouteSearchListener(this);
        Bundle argument = getArguments();
        LatLonPointBean startBean = (LatLonPointBean) argument.getSerializable(BaseConfig.START_LATLON);
        LatLonPointBean endBean = (LatLonPointBean) argument.getSerializable(BaseConfig.END_LATLON);
        boolean showLine = argument.getBoolean(BaseConfig.SHOW_LINE);
        mStartPoint = new LatLonPoint(startBean.getLatitude(), startBean.getLongitude());
        mEndPoint = new LatLonPoint(endBean.getLatitude(), endBean.getLongitude());
        // 路径规划
        if (showLine) {
            searchRouteResult();
        } else { // 只绘制起点和终点
            LatLng startLatlng = new LatLng(mStartPoint.getLatitude(), mStartPoint.getLongitude());
            LatLng endLatlng = new LatLng(mEndPoint.getLatitude(), mEndPoint.getLongitude());
            // 绘制起点
            addMarkersToMap(startLatlng, R.mipmap.ic_launcher);
            // 绘制终点
            addMarkersToMap(endLatlng, R.mipmap.ic_launcher);
            // 设置屏幕中心点和缩放
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MapUtil.getCenterLatlng(startLatlng, endLatlng),
                    MapUtil.getScaleLevel(startLatlng, endLatlng)));
        }
    }

    public void setScrollView(ScrollView scrollView) {
        this.scrollView = scrollView;
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
        mUiSettings.setScaleControlsEnabled(false);
        // 设置显示指南针
        mUiSettings.setCompassEnabled(false);
        // 设置了地图是否允许显示缩放按钮
        mUiSettings.setZoomControlsEnabled(false);
        // 设置地图是否允许通过手势来缩放
        mUiSettings.setZoomGesturesEnabled(true);
    }

    /**
     * 在地图上添加标志物
     *
     * @param latLng
     * @param iconResId
     */
    private void addMarkersToMap(LatLng latLng, int iconResId) {
        MarkerOptions markerOption = new MarkerOptions();
        BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(this.getResources(), iconResId));
        markerOption.icon(icon);
        markerOption.position(latLng);
        markerOption.anchor(0.5f, 0.5f);
        markerOption.draggable(false);
        // markerOption.title("经度:" + latLng.longitude)
        // markerOption.snippet("纬度:" + latLng.latitude);
        Marker marker = aMap.addMarker(markerOption);
        marker.showInfoWindow();
    }

    /**
     * 路径规划
     */
    private void searchRouteResult() {
        // showProgress();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(mStartPoint, mEndPoint);
        // 第一个参数表示路径规划的起点和终点
        // 第二个参数表示驾车模式,t = 0（驾车）= 1（公交）= 2（步行）= 3（骑行）= 4（火车）= 5（长途客车）（骑行仅在V788以上版本支持）
        // 第三个参数表示途经点
        // 第四个参数表示避让区域
        // 第五个参数表示避让道路
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo,
                0,
                null,
                null,
                "");
        mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    /**
     * 驾车路线规划
     *
     * @param result
     * @param errorCode
     */
    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        // hideProgress();
        aMap.clear();// 清理地图上的所有覆盖物
        boolean isError = false;
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    final DrivePath drivePath = result.getPaths().get(0);
                    DrivingRouteOverLay drivingRouteOverlay = new DrivingRouteOverLay(getActivity(),
                            aMap,
                            drivePath,
                            result.getStartPos(),
                            result.getTargetPos(),
                            null);
                    // //设置节点marker是否显示
                    drivingRouteOverlay.setNodeIconVisibility(false);
                    // 是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.setIsColorfulline(false);
                    // 移除之前的内容
                    drivingRouteOverlay.removeFromMap();
                    // 在地图上添加路径
                    drivingRouteOverlay.addToMap();
                    // 设置屏幕中心和缩放级别
                    drivingRouteOverlay.zoomToSpan();
                } else if (result != null && result.getPaths() == null) {
                    isError = true;
                }
            } else {
                isError = true;
            }
        } else {
            isError = true;
        }
        if (isError) {
            Toast.makeText(getActivity(), "对不起，没有搜索到相关数据", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }
}