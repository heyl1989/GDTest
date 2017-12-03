package com.pf.gdtest.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

/**
 * @author zhaopf
 * @version 1.0
 * @QQ 1308108803
 * @date 2017/12/2
 * 解决scrollview嵌套地图滑动冲突问题
 */
public class MapContainer extends RelativeLayout {

    private ScrollView scrollView;

    public MapContainer(Context context) {
        super(context);
    }

    public MapContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollView(ScrollView scrollView) {
        this.scrollView = scrollView;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            if (scrollView != null) {
                scrollView.requestDisallowInterceptTouchEvent(false);
            }
        } else {
            if (scrollView != null) {
                scrollView.requestDisallowInterceptTouchEvent(true);
            }
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}