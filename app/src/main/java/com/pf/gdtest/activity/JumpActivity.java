package com.pf.gdtest.activity;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.pf.gdtest.MapConfig;
import com.pf.gdtest.beans.LatLonPointBean;
import com.pf.gdtest.beans.TipBean;
import com.pf.gdtest.util.MapUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JumpActivity extends ListActivity {

    private List<String> items = new ArrayList<String>(Arrays.asList(
            "跳转到百度地图",
            "跳转到百度地图网页版",
            "跳转到高德地图",
            "跳转到高德地图网页版",
            "跳转到腾讯地图",
            "跳转到腾讯地图网页版",
            "跳转到谷歌地图",
            "跳转到手机上有的"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        TipBean start = new TipBean("天津站", new LatLonPointBean(39.087233, 117.212155));
        TipBean end = new TipBean("北京西站", new LatLonPointBean(39.909536, 116.399166));
        Intent intent = null;
        switch (position) {
            // 百度地图 
            case 0:
                if (MapUtil.checkApkExist(this, MapConfig.BAIDU_MAP_PACKAGE_NAME)) {
                    MapUtil.jumpToBaiduMap(this, start, end);
                } else {
                    Toast.makeText(this, "当前手机没有安装百度地图", Toast.LENGTH_SHORT).show();
                    Uri uri = Uri.parse("market://details?id=com.baidu.BaiduMap");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                }
                break;
            // 百度地图网页
            case 1:
                MapUtil.jumpToBaiduMapWeb(this, start, end);
                break;
            // 高德地图 
            case 2:
                if (MapUtil.checkApkExist(this, MapConfig.GAODE_MAP_PACKAGE_NAME)) {
                    MapUtil.jumpToGaodeMap(this, start, end);
                } else {
                    Toast.makeText(this, "当前手机没有安装高德地图", Toast.LENGTH_SHORT).show();
                    Uri uri = Uri.parse("market://details?id=com.autonavi.minimap");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                }
                break;
            // 高德地图网页
            case 3:
                MapUtil.jumpToGaodeMapWeb(this, start, end);
                break;
            // 腾讯地图
            case 4:
                if (MapUtil.checkApkExist(this, MapConfig.TENGXUN_MAP_PACKAGE_NAME)) {
                    MapUtil.jumpToTengxunMap(this, start, end);
                } else {
                    Toast.makeText(this, "当前手机没有安装腾讯地图", Toast.LENGTH_SHORT).show();
                    Uri uri = Uri.parse("market://details?id=com.tencent.map");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                }
                break;
            // 腾讯地图网页
            case 5:
                MapUtil.jumpToTengxunMapWeb(this, start, end);
                break;
            // 谷歌地图
            case 6:
                if (MapUtil.checkApkExist(JumpActivity.this, "com.google.android.apps.maps")) {
                    Uri gmmIntentUri = Uri.parse("google.navigation:q=" + 39.06058 + "," + 117.578823 + ", + BeiJing + China");
                    intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    intent.setPackage("com.google.android.apps.maps");
                } else {
                    Toast.makeText(this, "当前手机没有安装谷歌地图", Toast.LENGTH_SHORT).show();
                    Uri uri = Uri.parse("market://details?id=com.google.android.apps.maps");
                    intent = new Intent(Intent.ACTION_VIEW, uri);
                }
                break;
            // 跳转到手机上有的
            case 7:
                StringBuffer sb = new StringBuffer();
                sb.append("geo:").append(39.06058).append(",").append(117.578823)
                        .append("?").append("z=").append(10).append("?").append("q=")
                        .append("地址");
                Uri mUri = Uri.parse(sb.toString());
                intent = new Intent(Intent.ACTION_VIEW, mUri);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}