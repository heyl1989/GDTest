package com.pf.gdtest;

import com.amap.api.maps2d.model.LatLng;
import com.amap.api.trace.TraceLocation;
import com.pf.gdtest.beans.CoordinateBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaopf
 * @version 1.0
 * @QQ 1308108803
 * @date 2017/11/6
 */

public class InitData {

    public static List<CoordinateBean> getCoordinate1() {
        List<CoordinateBean> list = new ArrayList<>();
        list.add(new CoordinateBean(117.709608, 39.024215));
        list.add(new CoordinateBean(117.70728, 39.020531));
        list.add(new CoordinateBean(117.70287, 39.022181));
        list.add(new CoordinateBean(117.700639, 39.018572));
        list.add(new CoordinateBean(117.65753, 39.030766));
        list.add(new CoordinateBean(117.658142, 39.032412));
        return list;
    }

    public static List<CoordinateBean> getCoordinate2() {
        List<CoordinateBean> list = new ArrayList<>();
        list.add(new CoordinateBean(117.709683, 39.024232));
        list.add(new CoordinateBean(117.711839, 39.027782));
        list.add(new CoordinateBean(117.698203, 39.032891));
        list.add(new CoordinateBean(117.699566, 39.035066));
        list.add(new CoordinateBean(117.696588, 39.036271));
        list.add(new CoordinateBean(117.696889, 39.036758));
        list.add(new CoordinateBean(117.697286, 39.036587));
        return list;
    }

    public static List<CoordinateBean> getCoordinate3() {
        List<CoordinateBean> list = new ArrayList<>();
        list.add(new CoordinateBean(117.57064, 39.04022));
        list.add(new CoordinateBean(117.194358, 39.089269));
        list.add(new CoordinateBean(116.686241, 39.505863));
        list.add(new CoordinateBean(116.433555, 39.913654));
        list.add(new CoordinateBean(118.152915, 39.656159));
        list.add(new CoordinateBean(118.707725, 39.325501));
        list.add(new CoordinateBean(117.99636, 39.274491));
        list.add(new CoordinateBean(117.554161, 38.971922));
        return list;
    }

    public static LatLng[] coordinateToLatlng(List<CoordinateBean> coordinateBeanList) {
        LatLng[] latLngs = new LatLng[coordinateBeanList.size()];
        for (int i = 0; i < coordinateBeanList.size(); i++) {
            latLngs[i] = new LatLng(coordinateBeanList.get(i).getLat(), coordinateBeanList.get(i).getLng());
        }
        return latLngs;
    }

    public static List<TraceLocation> coordinateToTraceLocation(List<CoordinateBean> coordinateBeanList) {
        List<TraceLocation> traceLocations = new ArrayList<>(coordinateBeanList.size());
        for (int i = 0; i < coordinateBeanList.size(); i++) {
            TraceLocation traceLocation = new TraceLocation();
            traceLocation.setLatitude(coordinateBeanList.get(i).getLat());
            traceLocation.setLongitude(coordinateBeanList.get(i).getLng());
            traceLocation.setBearing(coordinateBeanList.get(i).getBearing());
            traceLocation.setSpeed(coordinateBeanList.get(i).getSpeed());
            traceLocation.setTime(coordinateBeanList.get(i).getTime());
            traceLocations.add(traceLocation);
        }
        return traceLocations;
    }

    public static LatLng[] traceLocationToLatLng(List<TraceLocation> traceLocations) {
        LatLng[] latLngs = new LatLng[traceLocations.size()];
        for (int i = 0; i < traceLocations.size(); i++) {
            latLngs[i] = new LatLng(traceLocations.get(i).getLatitude(), traceLocations.get(i).getLongitude());
        }
        return latLngs;
    }

    public static LatLng[] latLngListToLatLng(List<com.amap.api.maps.model.LatLng> latLngList) {
        LatLng[] latLngs = new LatLng[latLngList.size()];
        for (int i = 0; i < latLngList.size(); i++) {
            latLngs[i] = new LatLng(latLngList.get(i).latitude, latLngList.get(i).longitude);
        }
        return latLngs;
    }
}