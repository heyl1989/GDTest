package com.pf.gdtest.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.Polyline;
import com.amap.api.maps2d.model.PolylineOptions;
import com.pf.gdtest.R;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaopf
 * @version 1.0
 * @QQ 1308108803
 * @date 2017/12/1
 */
public class RouteOverlay {

    protected Context mContext;
    protected AMap mAMap;
    /**
     * 起始点坐标
     */
    protected LatLng startPoint;
    /**
     * 终止点坐标
     */
    protected LatLng endPoint;
    /**
     * 是否显示节点图标
     */
    protected boolean nodeIconVisible = true;
    /**
     * 标志物集合
     */
    protected List<Marker> stationMarkers = new ArrayList<Marker>();
    /**
     * 起点标志物
     */
    protected Marker startMarker;
    /**
     * 终点标志物
     */
    protected Marker endMarker;
    /**
     * 所有线的集合
     */
    protected List<Polyline> allPolyLines = new ArrayList<Polyline>();
    /**
     * bitmap对象
     */
    private Bitmap startBit, endBit, driveBit;

    public RouteOverlay(Context context) {
        mContext = context;
    }

    /**
     * 去掉BusRouteOverlay上所有的Marker。
     *
     * @since V2.1.0
     */
    public void removeFromMap() {
        if (startMarker != null) {
            startMarker.remove();

        }
        if (endMarker != null) {
            endMarker.remove();
        }
        for (Marker marker : stationMarkers) {
            marker.remove();
        }
        for (Polyline line : allPolyLines) {
            line.remove();
        }
        destroyBit();
    }

    private void destroyBit() {
        if (startBit != null) {
            startBit.recycle();
            startBit = null;
        }
        if (endBit != null) {
            endBit.recycle();
            endBit = null;
        }
        if (driveBit != null) {
            driveBit.recycle();
            driveBit = null;
        }
    }

    /**
     * 给起点Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     *
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    protected BitmapDescriptor getStartBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
    }

    /**
     * 给终点Marker设置图标，并返回更换图标的图片。如不用默认图片，需要重写此方法。
     *
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    protected BitmapDescriptor getEndBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
    }

    protected BitmapDescriptor getDriveBitmapDescriptor() {
        return BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher);
    }

    protected void addStartAndEndMarker() {
        startMarker = mAMap.addMarker((new MarkerOptions())
                .position(startPoint)
                .icon(getStartBitmapDescriptor())
                .title("\u8D77\u70B9"));
        // startMarker.showInfoWindow();

        endMarker = mAMap.addMarker((new MarkerOptions())
                .position(endPoint)
                .icon(getEndBitmapDescriptor())
                .title("\u7EC8\u70B9"));
        // mAMap.moveCamera(CameraUpdateFactory.newLatLngZoom(startPoint,
        // getShowRouteZoom()));
    }

    /**
     * 移动镜头到当前的视角。
     *
     * @since V2.1.0
     */
    public void zoomToSpan() {
        if (startPoint != null) {
            if (mAMap == null) {
                return;
            }
            try {
                LatLngBounds bounds = getLatLngBounds();
                mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }

    protected LatLngBounds getLatLngBounds() {
        LatLngBounds.Builder b = LatLngBounds.builder();
        b.include(new LatLng(startPoint.latitude, startPoint.longitude));
        b.include(new LatLng(endPoint.latitude, endPoint.longitude));
        return b.build();
    }

    /**
     * 路段节点图标控制显示接口。
     *
     * @param visible true为显示节点图标，false为不显示。
     * @since V2.3.1
     */
    public void setNodeIconVisibility(boolean visible) {
        try {
            nodeIconVisible = visible;
            if (this.stationMarkers != null && this.stationMarkers.size() > 0) {
                for (int i = 0; i < this.stationMarkers.size(); i++) {
                    this.stationMarkers.get(i).setVisible(visible);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    protected void addStationMarker(MarkerOptions options) {
        if (options == null) {
            return;
        }
        Marker marker = mAMap.addMarker(options);
        if (marker != null) {
            stationMarkers.add(marker);
        }

    }

    protected void addPolyLine(PolylineOptions options) {
        if (options == null) {
            return;
        }
        Polyline polyline = mAMap.addPolyline(options);
        if (polyline != null) {
            allPolyLines.add(polyline);
        }
    }

    protected float getRouteWidth() {
        return 18f;
    }

    protected int getWalkColor() {
        return Color.parseColor("#6db74d");
    }

    /**
     * 自定义路线颜色。
     * return 自定义路线颜色。
     *
     * @since V2.2.1
     */
    protected int getBusColor() {
        return Color.parseColor("#537edc");
    }

    protected int getDriveColor() {
        return Color.parseColor("#5E97FF");
    }

    // protected int getShowRouteZoom() {
    // return 15;
    // }
}