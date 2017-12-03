package com.pf.gdtest.beans;

import java.io.Serializable;

/**
 * @author zhaopf
 * @version 1.0
 * @QQ 1308108803
 * @date 2017/12/3
 * 坐标点实体
 */
public class TipBean implements Serializable {

    private String name;
    private LatLonPointBean point;

    public TipBean(String name, LatLonPointBean point) {
        this.name = name;
        this.point = point;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LatLonPointBean getPoint() {
        return point;
    }

    public void setPoint(LatLonPointBean point) {
        this.point = point;
    }

    @Override
    public String toString() {
        return "TipBean{" +
                "name='" + name + '\'' +
                ", point=" + point +
                '}';
    }
}