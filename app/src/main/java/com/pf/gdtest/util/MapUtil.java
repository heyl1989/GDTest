package com.pf.gdtest.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.amap.api.maps2d.AMapUtils;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.pf.gdtest.MapConfig;
import com.pf.gdtest.beans.LatLonPointBean;
import com.pf.gdtest.beans.TipBean;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhaopf
 * @version 1.0
 * @QQ 1308108803
 * @date 2017/12/3
 * 地图工具类
 */
public class MapUtil {

    /**
     * 把LatLonPoint对象转化为LatLon对象
     */
    public static LatLng convertToLatLng(LatLonPoint latLonPoint) {
        return new LatLng(latLonPoint.getLatitude(), latLonPoint.getLongitude());
    }

    /**
     * 将E-39.135884,N-117.210061格式的数据转为经纬度实体
     *
     * @param point
     * @return
     */
    public static LatLonPointBean convertToLatLonPointBean(String point) {
        String[] position = point.split(",");
        String[] lats = position[0].split("-");
        String[] lons = position[1].split("-");
        double lat = Double.valueOf(lats[1]);
        double lon = Double.valueOf(lons[1]);
        return new LatLonPointBean(lat, lon);
    }

    /**
     * 检查手机上是否安装了腾讯、高德、百度地图
     * 返回已安装的地图
     *
     * @param context
     * @return
     */
    public static List<String> getLocalInstallMap(Context context) {
        List<String> result = new ArrayList<>(4);
        // 百度地图
        if (checkApkExist(context, MapConfig.BAIDU_MAP_PACKAGE_NAME)) {
            result.add("百度地图");
        }
        // 高德地图
        if (checkApkExist(context, MapConfig.GAODE_MAP_PACKAGE_NAME)) {
            result.add("高德地图");
        }
        // 腾讯地图
        if (checkApkExist(context, MapConfig.TENGXUN_MAP_PACKAGE_NAME)) {
            result.add("腾讯地图");
        }
        return result;
    }

    /**
     * 检查手机上是否安装了指定的软件
     *
     * @param context
     * @param packageName ：应用包名
     * @return
     */
    public static boolean checkApkExist(Context context, String packageName) {
        // 获取packagemanager
        final PackageManager packageManager = context.getPackageManager();
        // 获取所有已安装程序的包信息
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);
        // 用于存储所有已安装程序的包名
        List<String> packageNames = new ArrayList<String>();
        // 从pinfo中将包名字逐一取出，压入pName list中
        if (packageInfos != null) {
            for (int i = 0; i < packageInfos.size(); i++) {
                String packName = packageInfos.get(i).packageName;
                packageNames.add(packName);
            }
        }
        // 判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE
        return packageNames.contains(packageName);
    }

    /**
     * 跳转到百度地图APP导航
     *
     * @param context
     * @param startTipBean 起点名称，坐标
     * @param endTipBean   终点名称，坐标
     */
    public static void jumpToBaiduMap(Context context, TipBean startTipBean, TipBean endTipBean) {
        // 百度地图 参考 http://lbsyun.baidu.com/index.php?title=uri/api/android
        Uri uri = Uri.parse("baidumap://map/direction?"
                // 起点所在城市或县
                + "origin_region=" + startTipBean.getName()
                // 起点
                + "&origin=name:" + startTipBean.getName() + "|latlng:" + startTipBean.getPoint().getLatitude() + "," + startTipBean.getPoint().getLongitude()
                // 终点所在城市或县
                + "&destination_region=" + endTipBean.getName()
                // 终点
                + "&destination=name:" + endTipBean.getName() + "|latlng:" + endTipBean.getPoint().getLatitude() + "," + endTipBean.getPoint().getLongitude()
                // 导航模式:transit(公交),driving(驾车),walking(步行)和riding(骑行).默认:driving
                + "&mode=driving");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    /**
     * 跳转到百度地图网页导航
     *
     * @param context
     * @param startTipBean 起点名称，坐标
     * @param endTipBean   终点名称，坐标
     */
    public static void jumpToBaiduMapWeb(Context context, TipBean startTipBean, TipBean endTipBean) {
        // 百度地图 参考 http://lbsyun.baidu.com/index.php?title=uri/api/web
        Uri uri = Uri.parse("http://api.map.baidu.com/direction?"
                // 输出类型
                + "output=html"
                // 应用名
                + "&src=" + GlobalUtils.getVersionName(context)
                // 起点所在城市或县
                + "&origin_region=" + startTipBean.getName()
                // 起点
                + "&origin=name:" + startTipBean.getName() + "|latlng:" + startTipBean.getPoint().getLatitude() + "," + startTipBean.getPoint().getLongitude()
                // 终点所在城市或县
                + "&destination_region=" + endTipBean.getName()
                // 终点
                + "&destination=name:" + endTipBean.getName() + "|latlng:" + endTipBean.getPoint().getLatitude() + "," + endTipBean.getPoint().getLongitude()
                // 导航模式:transit(公交),driving(驾车),walking(步行)和riding(骑行).默认:driving
                + "&mode=driving");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    /**
     * 跳转到高德地图APP导航
     *
     * @param context
     * @param startTipBean 起点名称，坐标
     * @param endTipBean   终点名称，坐标
     */
    public static void jumpToGaodeMap(Context context, TipBean startTipBean, TipBean endTipBean) {
        // 高德地图 参考 http://lbs.amap.com/api/amap-mobile/guide/android/route
        Uri uri = Uri.parse("amapuri://route/plan/?"
                // 第三方调用应用名称。如 amap
                + "sourceApplication=" + GlobalUtils.getVersionName(context)
                // 起点名称
                + "&sname=" + startTipBean.getName()
                // 起点纬度
                + "&slat=" + startTipBean.getPoint().getLatitude()
                // 起点经度
                + "&slon=" + startTipBean.getPoint().getLongitude()
                // 终点名称
                + "&dname=" + endTipBean.getName()
                // 终点纬度
                + "&dlat=" + endTipBean.getPoint().getLatitude()
                // 终点经度
                + "&dlon=" + endTipBean.getPoint().getLongitude()
                // 起终点是否偏移(0:lat 和 lon 是已经加密后的,不需要国测加密; 1:需要国测加密)
                + "&dev=0"
                // t = 0（驾车）= 1（公交）= 2（步行）= 3（骑行）= 4（火车）= 5（长途客车）（骑行仅在V788以上版本支持）
                + "&t=0");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    /**
     * 跳转到高德地图web导航
     *
     * @param context
     * @param startTipBean 起点名称，坐标
     * @param endTipBean   终点名称，坐标
     */
    public static void jumpToGaodeMapWeb(Context context, TipBean startTipBean, TipBean endTipBean) {
        // 高德地图 参考 http://lbs.amap.com/api/uri-api/guide/travel/route
        Uri uri1 = Uri.parse("http://uri.amap.com/navigation?"
                // 起点经纬度坐标，格式为: from=lon,lat[,name]
                + "from=" + startTipBean.getPoint().getLongitude() + "," + startTipBean.getPoint().getLatitude() + ",[" + startTipBean.getName() + "]"
                // 终点经纬度坐标，格式为: from=lon,lat[,name]
                + "&to=" + endTipBean.getPoint().getLongitude() + "," + endTipBean.getPoint().getLatitude() + ",[" + endTipBean.getName() + "]"
                // 出行方式：驾车：mode=car,公交:：mode=bus；步行：mode=walk；骑行：mode=ride；
                + "&mode=car"
                // 0:推荐策略,1:避免拥堵,2:避免收费,3:不走高速（仅限移动端）
                + "&policy=1"
                // 是否尝试调起高德地图APP并在APP中查看，0表示不调起，1表示调起, 默认值为0
                + "&callnative=0"
                // 使用方来源信息
                + "&src=" + GlobalUtils.getVersionName(context));
        Intent intent = new Intent(Intent.ACTION_VIEW, uri1);
        context.startActivity(intent);
    }

    /**
     * 跳转到腾讯地图APP导航
     *
     * @param context
     * @param startTipBean 起点名称，坐标
     * @param endTipBean   终点名称，坐标
     */
    public static void jumpToTengxunMap(Context context, TipBean startTipBean, TipBean endTipBean) {
        // 这个Uri可以跳转，但是没找到官方的文档
        Uri uri = Uri.parse("qqmap://map/routeplan?"
                // 应用名
                + "referer=" + GlobalUtils.getVersionName(context)
                // 路线规划方式参数：公交：bus 驾车：drive 步行：walk（仅适用移动端）
                + "&type=drive"
                // 起点名称
                + "&from=" + startTipBean.getName()
                // 起点坐标
                + "&fromcoord=" + startTipBean.getPoint().getLatitude() + "," + startTipBean.getPoint().getLongitude()
                // 终点名称
                + "&to=" + endTipBean.getName()
                // 终点坐标
                + "&tocoord=" + endTipBean.getPoint().getLatitude() + "," + endTipBean.getPoint().getLongitude()
                // 0：较快捷  1：无高速  2：距离  默认为0
                + "&policy=0");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    /**
     * 跳转到腾讯地图网页导航
     *
     * @param context
     * @param startTipBean 起点名称，坐标
     * @param endTipBean   终点名称，坐标
     */
    public static void jumpToTengxunMapWeb(Context context, TipBean startTipBean, TipBean endTipBean) {
        // 腾讯地图 参考http://lbs.qq.com/uri_v1/guide-route.html
        Uri uri = Uri.parse("http://apis.map.qq.com/uri/v1/routeplan?"
                // 应用名
                + "referer=" + GlobalUtils.getVersionName(context)
                // 路线规划方式参数：公交：bus 驾车：drive 步行：walk（仅适用移动端）
                + "&type=drive"
                // 起点名称
                + "&from=" + startTipBean.getName()
                // 起点坐标
                + "&fromcoord=" + startTipBean.getPoint().getLatitude() + "," + startTipBean.getPoint().getLongitude()
                // 终点名称
                + "&to=" + endTipBean.getName()
                // 终点坐标
                + "&tocoord=" + endTipBean.getPoint().getLatitude() + "," + endTipBean.getPoint().getLongitude()
                // 0：较快捷  1：无高速  2：距离  默认为0
                + "&policy=0");
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        context.startActivity(intent);
    }

    /**
     * 获取两个点的中心坐标
     *
     * @param start
     * @param end
     * @return
     */
    public static LatLng getCenterLatlng(LatLng start, LatLng end) {
        LatLng maxLng = start;
        LatLng minLng = start;
        LatLng maxLat = start;
        LatLng minLat = start;
        if (end.longitude > maxLng.longitude) {
            maxLng = end;
        }
        if (end.longitude < minLng.longitude) {
            minLng = end;
        }
        if (end.latitude > maxLat.latitude) {
            maxLat = end;
        }
        if (end.latitude < minLat.latitude) {
            minLat = end;
        }
        // 中心点的坐标
        return new LatLng((maxLat.latitude + minLat.latitude) / 2,
                (maxLng.longitude + minLng.longitude) / 2);
    }

    /**
     * 获取一组路径的中心点坐标
     *
     * @param latLngList
     * @return
     */
    public static LatLng getCenterLatlng(List<LatLng> latLngList) {
        LatLng maxLng = latLngList.get(0);
        LatLng minLng = latLngList.get(0);
        LatLng maxLat = latLngList.get(0);
        LatLng minLat = latLngList.get(0);
        for (int i = 1; i < latLngList.size(); i++) {
            if (latLngList.get(i).longitude > maxLng.longitude) {
                maxLng = latLngList.get(i);
            }
            if (latLngList.get(i).longitude < minLng.longitude) {
                minLng = latLngList.get(i);
            }
            if (latLngList.get(i).latitude > maxLat.latitude) {
                maxLat = latLngList.get(i);
            }
            if (latLngList.get(i).latitude < minLat.latitude) {
                minLat = latLngList.get(i);
            }
        }
        // 中心点的坐标
        return new LatLng((maxLat.latitude + minLat.latitude) / 2,
                (maxLng.longitude + minLng.longitude) / 2);
    }

    /**
     * 获取缩放级别
     *
     * @param start
     * @param end
     * @return
     */
    public static float getScaleLevel(LatLng start, LatLng end) {
        LatLng maxLng = start;
        LatLng minLng = start;
        LatLng maxLat = start;
        LatLng minLat = start;
        if (end.longitude > maxLng.longitude) {
            maxLng = end;
        }
        if (end.longitude < minLng.longitude) {
            minLng = end;
        }
        if (end.latitude > maxLat.latitude) {
            maxLat = end;
        }
        if (end.latitude < minLat.latitude) {
            minLat = end;
        }
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
        return getLevelWithDistance(distance);
    }

    /**
     * 获取缩放级别
     *
     * @param latLngList
     * @return
     */
    public static float getScaleLevel(List<LatLng> latLngList) {
        LatLng maxLng = latLngList.get(0);
        LatLng minLng = latLngList.get(0);
        LatLng maxLat = latLngList.get(0);
        LatLng minLat = latLngList.get(0);
        for (int i = 1; i < latLngList.size(); i++) {
            if (latLngList.get(i).longitude > maxLng.longitude) {
                maxLng = latLngList.get(i);
            }
            if (latLngList.get(i).longitude < minLng.longitude) {
                minLng = latLngList.get(i);
            }
            if (latLngList.get(i).latitude > maxLat.latitude) {
                maxLat = latLngList.get(i);
            }
            if (latLngList.get(i).latitude < minLat.latitude) {
                minLat = latLngList.get(i);
            }
        }
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
        return getLevelWithDistance(distance);
    }

    /**
     * @param maxDistance 最大距离
     * @return
     */
    private static float getLevelWithDistance(float maxDistance) {
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
        return level - 1f;
    }
}