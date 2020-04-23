package com.sjl.bookmark.widget;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 处理recycleview与ViewPager的滑动冲突
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BannerViewPage.java
 * @time 2019/2/20 13:57
 * @copyright(C) 2019 song
 */
public class BannerViewPage extends ViewPager {
    private float x, y;

    public BannerViewPage(Context context) {
        super(context);
    }

    public BannerViewPage(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                y = ev.getY();
                x = ev.getX();
                getParent().requestDisallowInterceptTouchEvent(true);
                break;
            case MotionEvent.ACTION_MOVE:
                if (Math.abs(ev.getX() - x) > Math.abs(ev.getY() - y))
                    getParent().requestDisallowInterceptTouchEvent(true);//请求父元素不要拦截
                else
                    getParent().requestDisallowInterceptTouchEvent(false);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().requestDisallowInterceptTouchEvent(false);
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }


}