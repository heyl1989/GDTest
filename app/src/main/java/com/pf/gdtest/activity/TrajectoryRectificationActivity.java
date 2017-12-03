package com.pf.gdtest.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.trace.LBSTraceClient;
import com.amap.api.trace.TraceListener;
import com.amap.api.trace.TraceLocation;
import com.amap.api.trace.TraceOverlay;
import com.pf.gdtest.R;
import com.pf.gdtest.TraceData;
import com.pf.gdtest.util.TraceAsset;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TrajectoryRectificationActivity extends AppCompatActivity implements TraceListener,
        View.OnClickListener {

    String TAG = "TraceActivity";
    private Button mGraspButton, mCleanFinishOverlay;
    private TextView mResultShow, mLowSpeedShow;
    private int mCoordinateType = LBSTraceClient.TYPE_AMAP;
    private MapView mMapView;
    private AMap mAMap;
    private LBSTraceClient mTraceClient;

    private List<TraceLocation> mTraceList;

    private static final String DISTANCE_UNIT_DES = " KM";
    private static final String TIME_UNIT_DES = " 分钟";

    private ConcurrentMap<Integer, TraceOverlay> mOverlayList = new ConcurrentHashMap<Integer, TraceOverlay>();
    private ConcurrentMap<Integer, List<LatLng>> mSegments = new ConcurrentHashMap<Integer, List<LatLng>>();
    private int mSequenceLineID = 1000;
    private int index = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trajectory_rectification);
        mGraspButton = (Button) findViewById(R.id.grasp_button);
        mCleanFinishOverlay = (Button) findViewById(R.id.clean_finish_overlay_button);
        mCleanFinishOverlay.setOnClickListener(this);
        mGraspButton.setOnClickListener(this);
        mMapView = (MapView) findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);// 此方法必须重写
        mResultShow = (TextView) findViewById(R.id.show_all_dis);
        mLowSpeedShow = (TextView) findViewById(R.id.show_low_speed);
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        if (mAMap == null) {
            mAMap = mMapView.getMap();
            mAMap.getUiSettings().setRotateGesturesEnabled(false);
            mAMap.getUiSettings().setZoomControlsEnabled(false);
            mAMap.getUiSettings().setScaleControlsEnabled(true);

            initLocationCenter();

            mTraceList = TraceAsset.parseLocationsData(this.getAssets(), "traceRecord" + File.separator + "GPSTrace.txt");
        }
    }

    private void initLocationCenter() {
        LatLng locationLatLng = new LatLng(39.567386, 116.867459);
        mAMap.moveCamera(CameraUpdateFactory.newLatLng(locationLatLng));
        mAMap.moveCamera(CameraUpdateFactory.zoomTo(9));
        mAMap.clear();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    /**
     * 轨迹纠偏过程回调
     */
    @Override
    public void onTraceProcessing(int lineID, int index, List<LatLng> segments) {
        Log.d(TAG, "onTraceProcessing");
        if (segments == null) {
            return;
        }
        Log.e(TAG, "index:" + index + "--" + "lineID:" + lineID);
    }

    /**
     * 轨迹纠偏结束回调
     */
    @Override
    public void onFinished(int lineID, List<LatLng> linepoints, int distance, int watingtime) {
        Log.d(TAG, "onFinished");
        Toast.makeText(this.getApplicationContext(), "onFinished", Toast.LENGTH_SHORT).show();
        if (mOverlayList.containsKey(lineID)) {
            TraceOverlay overlay = mOverlayList.get(lineID);
            overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_FINISH);
            overlay.setDistance(distance);
            overlay.setWaitTime(watingtime);
            overlay.add(linepoints);
            setDistanceWaitInfo(overlay);

            overlay.zoopToSpan();

            mSegments.put(lineID, linepoints);

            changeCenterLevel(lineID);
        }
    }

    private void changeCenterLevel(int lineID) {
        if (mSegments.containsKey(lineID)) {
            List<LatLng> latLngs = mSegments.get(lineID);
            addMarkersToMap(latLngs.get(0));
            addMarkersToMap(latLngs.get(latLngs.size() - 1));

            LatLng center = getCenterLatLng(latLngs);
            float level = getLevelWithLatlngs(latLngs);
            mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, level));
        }
    }

    /**
     * 轨迹纠偏失败回调
     */
    @Override
    public void onRequestFailed(int lineID, String errorInfo) {
        Log.d(TAG, "index:" + index + ",onRequestFailed" + errorInfo);
        Toast.makeText(this.getApplicationContext(), "index:" + index + ",onRequestFailed" + errorInfo, Toast.LENGTH_SHORT).show();
        if (mOverlayList.containsKey(lineID)) {
            TraceOverlay overlay = mOverlayList.get(lineID);
            overlay.setTraceStatus(TraceOverlay.TRACE_STATUS_FAILURE);
            setDistanceWaitInfo(overlay);
        }
        initLocationCenter();
    }

    /**
     * 在地图上添加marker
     */
    private void addMarkersToMap(LatLng latLng) {
        Marker marker = mAMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                // .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher))
                .position(latLng)
                .title("经度:" + latLng.longitude)
                .snippet("纬度:" + latLng.latitude)
                .draggable(true));
//        marker.setRotateAngle(90);// 设置marker旋转90度
//        marker.setPositionByPixels(400, 400);
        marker.setClickable(true);
        marker.showInfoWindow();// 设置默认显示一个infowinfow
    }

    /**
     * 调起一次轨迹纠偏
     */
    private void traceGrasp() {
        if (mOverlayList.containsKey(mSequenceLineID)) {
            TraceOverlay overlay = mOverlayList.get(mSequenceLineID);
            overlay.zoopToSpan();
            int status = overlay.getTraceStatus();
            String tipString = "";
            if (status == TraceOverlay.TRACE_STATUS_PROCESSING) {
                tipString = "该线路轨迹纠偏进行中...";
                setDistanceWaitInfo(overlay);
            } else if (status == TraceOverlay.TRACE_STATUS_FINISH) {
                setDistanceWaitInfo(overlay);
                tipString = "该线路轨迹已完成";
            } else if (status == TraceOverlay.TRACE_STATUS_FAILURE) {
                tipString = "该线路轨迹失败";
            } else if (status == TraceOverlay.TRACE_STATUS_PREPARE) {
                tipString = "该线路轨迹纠偏已经开始";
            }
            Toast.makeText(this.getApplicationContext(), tipString, Toast.LENGTH_SHORT).show();
            changeCenterLevel(mSequenceLineID);
            return;
        }

        useMyData();

        TraceOverlay mTraceOverlay = new TraceOverlay(mAMap);
        mOverlayList.put(mSequenceLineID, mTraceOverlay);
        List<LatLng> mapList = traceLocationToMap(mTraceList);
        mTraceOverlay.setProperCamera(mapList);
        mTraceClient = new LBSTraceClient(this.getApplicationContext());
        mTraceClient.queryProcessedTrace(mSequenceLineID, mTraceList, mCoordinateType, this);
    }

    private void useMyData() {
        mTraceList = TraceData.coordinateToTraceLocations(TraceData.getCoordinate1());
        changeType();
//        if (index == 1) {
//            mTraceList = TraceData.coordinateToTraceLocations(TraceData.getCoordinate1());
//        } else if (index == 2) {
//            mTraceList = TraceData.coordinateToTraceLocations(TraceData.getCoordinate2());
//        } else if (index == 3) {
//            mTraceList = TraceData.coordinateToTraceLocations(TraceData.getCoordinate3());
//        }
//        mCoordinateType = LBSTraceClient.TYPE_AMAP;
        index++;
        if (index == 4) {
            index = 1;
        }
    }

    private void useDemoData() {
        if (index == 1) {
            mTraceList = TraceAsset.parseLocationsData(this.getAssets(), "traceRecord" + File.separator + "AMapTrace.txt");
        } else if (index == 2) {
            mTraceList = TraceAsset.parseLocationsData(this.getAssets(), "traceRecord" + File.separator + "BaiduTrace.txt");
        } else if (index == 3) {
            mTraceList = TraceAsset.parseLocationsData(this.getAssets(), "traceRecord" + File.separator + "GPSTrace.txt");
        }
        changeType();
        index++;
        if (index == 4) {
            index = 1;
        }
    }

    private void changeType() {
        if (index == 1) {
            mCoordinateType = LBSTraceClient.TYPE_AMAP;
        } else if (index == 2) {
            mCoordinateType = LBSTraceClient.TYPE_BAIDU;
        } else if (index == 3) {
            mCoordinateType = LBSTraceClient.TYPE_GPS;
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.grasp_button:
                traceGrasp();
                break;
            case R.id.clean_finish_overlay_button:
                cleanFinishTrace();
                break;
        }
    }

    /**
     * 清除地图已完成或出错的轨迹
     */
    private void cleanFinishTrace() {
        Iterator iter = mOverlayList.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Integer key = (Integer) entry.getKey();
            TraceOverlay overlay = (TraceOverlay) entry.getValue();
            if (overlay.getTraceStatus() == TraceOverlay.TRACE_STATUS_FINISH
                    || overlay.getTraceStatus() == TraceOverlay.TRACE_STATUS_FAILURE) {
                overlay.remove();
                mOverlayList.remove(key);
                mSegments.remove(key);
            }
        }
        mAMap.clear();
    }

    /**
     * 设置显示总里程和等待时间
     *
     * @param overlay
     */
    private void setDistanceWaitInfo(TraceOverlay overlay) {
        int distance = -1;
        int waittime = -1;
        if (overlay != null) {
            distance = overlay.getDistance();
            waittime = overlay.getWaitTime();
        }
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        mResultShow.setText(mResultShow.getText().toString().substring(0, 4)
                + decimalFormat.format(distance / 1000d) + DISTANCE_UNIT_DES);
        mLowSpeedShow.setText(mLowSpeedShow.getText().toString().substring(0, 3)
                + decimalFormat.format(waittime / 60d) + TIME_UNIT_DES);
    }

    /**
     * 轨迹纠偏点转换为地图LatLng
     *
     * @param traceLocationList
     * @return
     */
    public List<LatLng> traceLocationToMap(List<TraceLocation> traceLocationList) {
        List<LatLng> mapList = new ArrayList<LatLng>();
        for (TraceLocation location : traceLocationList) {
            LatLng latlng = new LatLng(location.getLatitude(), location.getLongitude());
            mapList.add(latlng);
        }
        return mapList;
    }

    /**
     * 获取路径规划后的地图中心点坐标
     *
     * @param latLngs
     * @return
     */
    private LatLng getCenterLatLng(List<LatLng> latLngs) {
        LatLng maxLng = latLngs.get(0); // 最大经度
        LatLng minLng = latLngs.get(0); // 最小经度
        LatLng maxLat = latLngs.get(0); // 最大纬度
        LatLng minLat = latLngs.get(0); // 最小纬度
        for (int i = 1; i < latLngs.size(); i++) {
            if (latLngs.get(i).longitude > maxLng.longitude) {
                maxLng = latLngs.get(i);
            }
            if (latLngs.get(i).longitude < minLng.longitude) {
                minLng = latLngs.get(i);
            }
            if (latLngs.get(i).latitude > maxLat.latitude) {
                maxLat = latLngs.get(i);
            }
            if (latLngs.get(i).latitude < minLat.latitude) {
                minLat = latLngs.get(i);
            }
        }
        // 中心点的坐标
        LatLng center = new LatLng((Math.abs(maxLat.latitude) + Math.abs(minLat.latitude)) / 2,   // 纬度
                (Math.abs(maxLng.longitude) + Math.abs(minLng.longitude)) / 2);  // 经度
        return center;
    }

    /**
     * 获取路径规划后的地图缩放级别
     *
     * @param latLngs
     * @return
     */
    private float getLevelWithLatlngs(List<LatLng> latLngs) {
        LatLng maxLng = latLngs.get(0); // 最大经度
        LatLng minLng = latLngs.get(0); // 最小经度
        LatLng maxLat = latLngs.get(0); // 最大纬度
        LatLng minLat = latLngs.get(0); // 最小纬度
        for (int i = 1; i < latLngs.size(); i++) {
            if (latLngs.get(i).longitude > maxLng.longitude) {
                maxLng = latLngs.get(i);
            }
            if (latLngs.get(i).longitude < minLng.longitude) {
                minLng = latLngs.get(i);
            }
            if (latLngs.get(i).latitude > maxLat.latitude) {
                maxLat = latLngs.get(i);
            }
            if (latLngs.get(i).latitude < minLat.latitude) {
                minLat = latLngs.get(i);
            }
        }
        // 中心点的坐标
        LatLng center = new LatLng((maxLat.latitude + minLat.latitude) / 2,
                (maxLng.longitude + minLng.longitude) / 2);
        // 最大经度差
        float diffLngDistance = AMapUtils.calculateLineDistance(
                new LatLng(maxLat.latitude, maxLng.longitude),
                new LatLng(maxLat.latitude, minLng.longitude));
        // 最大纬度差
        float diffLatDistance = AMapUtils.calculateLineDistance(
                new LatLng(maxLat.latitude, maxLng.longitude),
                new LatLng(minLat.latitude, maxLng.longitude));
        // 路径最大值
        float maxDistance = diffLngDistance > diffLatDistance ? diffLngDistance : diffLatDistance;
        StringBuffer sb = new StringBuffer();
        for (LatLng l : latLngs) {
            sb.append(l.latitude + "," + l.longitude + "\n");
        }
        Log.e(TAG, "===中心点坐标:" + center.latitude + "," + center.longitude + "\n"
                + "最大经度:" + maxLng.latitude + "," + maxLng.longitude + "\n"
                + "最小经度:" + minLng.latitude + "," + minLng.longitude + "\n"
                + "最大纬度:" + maxLat.latitude + "," + maxLat.longitude + "\n"
                + "最小纬度:" + minLat.latitude + "," + minLat.longitude + "\n"
                + "最大经度差:" + diffLngDistance + "\n"
                + "最大纬度差:" + diffLatDistance + "\n"
                + "路径最大值:" + maxDistance + "\n"
                + sb.toString());
        // 缩放级别
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
