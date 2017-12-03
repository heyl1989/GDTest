package com.pf.gdtest.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.pf.gdtest.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LocationActivity extends AppCompatActivity implements AMapLocationListener {

    private static final String TAG = "pf---";

    // 声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient = null;
    private AMapLocationClientOption mLocationClientOption = null;
    private TextView tv_show;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        tv_show = (TextView) findViewById(R.id.tv_show);
        initGd();
    }

    private void initGd() {
        // 初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());
        // 初始化定位属性
        mLocationClientOption = new AMapLocationClientOption();
        // 设置定位精度
        mLocationClientOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        // 是否返回地址信息
        mLocationClientOption.setNeedAddress(true);
        // 是否只定位一次
        mLocationClientOption.setOnceLocation(false);
        // 设置是否强制刷新WIFI，默认为强制刷新
        mLocationClientOption.setWifiActiveScan(true);
        // 是否允许模拟位置
        mLocationClientOption.setMockEnable(false);
        // 定位时间间隔
        mLocationClientOption.setInterval(3000);
        // 给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationClientOption);
        // 设置定位回调监听
        mLocationClient.setLocationListener(this);
        // 启动定位
        mLocationClient.startLocation();
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        Log.e(TAG, "进来了");
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                // 解析定位结果

                //获取定位时间
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());

                String location = "定位成功:" + "\n"
                        // 获取定位时间
                        + df.format(date)
                        // 获取当前定位结果来源，如网络定位结果，详见定位类型表
                        + amapLocation.getLocationType() + "\n"
                        // 获取纬度
                        + amapLocation.getLatitude() + "\n"
                        // 获取经度
                        + amapLocation.getLongitude() + "\n"
                        // 获取精度信息
                        + amapLocation.getAccuracy() + "\n"
                        // 地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                        + amapLocation.getAddress() + "\n"
                        // 国家信息
                        + amapLocation.getCountry() + "\n"
                        // 省信息
                        + amapLocation.getProvince() + "\n"
                        // 城市信息
                        + amapLocation.getCity() + "\n"
                        // 城区信息
                        + amapLocation.getDistrict() + "\n"
                        // 街道信息
                        + amapLocation.getStreet() + "\n"
                        // 街道门牌号信息
                        + amapLocation.getStreetNum() + "\n"
                        // 城市编码
                        + amapLocation.getCityCode() + "\n"
                        // 地区编码
                        + amapLocation.getAdCode() + "\n"
                        // 获取当前定位点的AOI信息
                        + amapLocation.getAoiName() + "\n"
                        // 获取当前室内定位的建筑物Id
                        + amapLocation.getBuildingId() + "\n"
                        // 获取当前室内定位的楼层
                        + amapLocation.getFloor() + "\n"
                        // 获取GPS的当前状态
                        + amapLocation.getGpsAccuracyStatus();

                tv_show.setText(location);
                Log.e(TAG, location);
                Log.e(TAG, "定位成功---" + amapLocation.toString());
            } else {
                // 定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                Log.e(TAG, "AmapError location Error,"
                        + " ErrCode:" + amapLocation.getErrorCode()
                        + ", errInfo:" + amapLocation.getErrorInfo());
            }
        }
    }
}