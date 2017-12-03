package com.pf.gdtest;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.pf.gdtest.activity.CoordConverActivity;
import com.pf.gdtest.activity.DrivingTripActivity;
import com.pf.gdtest.activity.InputtipsActivity;
import com.pf.gdtest.activity.JumpActivity;
import com.pf.gdtest.activity.LocationActivity;
import com.pf.gdtest.activity.LinesActivity;
import com.pf.gdtest.activity.PoiKeywordSearchActivity;
import com.pf.gdtest.activity.PoiKeyworkSearchTestActivity;
import com.pf.gdtest.activity.ScrollViewMapActivity;
import com.pf.gdtest.activity.TestLines2Activity;
import com.pf.gdtest.activity.TestLinesActivity;
import com.pf.gdtest.activity.TraceDemoActivity;
import com.pf.gdtest.activity.TrajectoryRectificationActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends ListActivity {

    private List<String> items = new ArrayList<String>(Arrays.asList("location",
            "lines", "jump", "testLines", "testLine2", "输入提示",
            "poi关键字搜索", "poi关键字搜索测试", "坐标系转换", "轨迹纠偏", "轨迹纠偏测试",
            "驾车出行路线规划", "scrollview嵌套地图(fragment使用地图)"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        setListAdapter(adapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent intent = null;
        switch (position) {
            case 0:
                intent = new Intent(MainActivity.this, LocationActivity.class);
                break;
            case 1:
                intent = new Intent(MainActivity.this, LinesActivity.class);
                break;
            case 2:
                intent = new Intent(MainActivity.this, JumpActivity.class);
                break;
            case 3:
                intent = new Intent(MainActivity.this, TestLinesActivity.class);
                break;
            case 4:
                intent = new Intent(MainActivity.this, TestLines2Activity.class);
                break;
            case 5:
                intent = new Intent(MainActivity.this, InputtipsActivity.class);
                break;
            case 6:
                intent = new Intent(MainActivity.this, PoiKeywordSearchActivity.class);
                break;
            case 7:
                intent = new Intent(MainActivity.this, PoiKeyworkSearchTestActivity.class);
                break;
            case 8:
                intent = new Intent(MainActivity.this, CoordConverActivity.class);
                break;
            case 9:
                intent = new Intent(MainActivity.this, TraceDemoActivity.class);
                break;
            case 10:
                intent = new Intent(MainActivity.this, TrajectoryRectificationActivity.class);
                break;
            case 11:
                intent = new Intent(MainActivity.this, DrivingTripActivity.class);
                break;
            case 12:
                intent = new Intent(MainActivity.this, ScrollViewMapActivity.class);
                break;
        }
        if (intent != null) {
            startActivity(intent);
        }
    }
}