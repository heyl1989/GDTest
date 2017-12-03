package com.pf.gdtest.beans;

import java.io.Serializable;

/**
 * @author zhaopf
 * @version 1.0
 * @QQ 1308108803
 * @date 2017/12/3
 * 经纬度实体
 */
public class LatLonPointBean implements Serializable {
    /**
     * 纬度
     */
    private double latitude;
    /**
     * 经度
     */
    private double longitude;

    public LatLonPointBean(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return "LatLonPointBean{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}