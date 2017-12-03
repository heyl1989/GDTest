package com.pf.gdtest.beans;

/**
 * @author zhaopf
 * @version 1.0
 * @QQ 1308108803
 * @date 2017/11/6
 */

public class CoordinateBean {
    /**
     * 经度
     */
    private Double lng;
    /**
     * 纬度
     */
    private Double lat;
    private float speed;
    private float bearing;
    private Long time = 0L;

    public CoordinateBean(Double lng, Double lat) {
        this.lng = lng;
        this.lat = lat;
    }

    public CoordinateBean(Double lng, Double lat, float speed, float bearing, Long time) {
        this.lng = lng;
        this.lat = lat;
        this.speed = speed;
        this.bearing = bearing;
        this.time = time;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public Long getTime() {
        return time;
    }

    public void setTime(Long time) {
        this.time = time;
    }
}