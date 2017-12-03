package com.pf.gdtest.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ScrollView;

import com.pf.gdtest.BaseConfig;
import com.pf.gdtest.R;
import com.pf.gdtest.fragment.MapFragment;
import com.pf.gdtest.util.MapUtil;

public class ScrollViewMapActivity extends AppCompatActivity {

    /**
     * ScrollView
     */
    private ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scroll_view_map);
        initView();
        initMap();
    }

    private void initMap() {
        // 地图
        MapFragment mapFragment = new MapFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(BaseConfig.START_LATLON, MapUtil.convertToLatLonPointBean("E-39.087233,N-117.212155"));
        bundle.putSerializable(BaseConfig.END_LATLON, MapUtil.convertToLatLonPointBean("E-39.909536,N-116.399166"));
        bundle.putBoolean(BaseConfig.SHOW_LINE, true);
        mapFragment.setArguments(bundle);
        mapFragment.setScrollView(scrollView);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.ll_map, mapFragment)
                .show(mapFragment)
                .commit();
    }

    private void initView() {
        scrollView = findViewById(R.id.sv_task_view_route);
    }
}