package com.sjl.bookmark.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

/**
 * 解决SwipeRefreshLayout嵌套GridView冲突问题
 * 还不知道怎么处理
 *
 * @author Kelly
 * @version 1.0.0
 * @filename VerticalSwipeRefreshLayout.java
 * @time 2018/12/3 10:12
 * @copyright(C) 2018 song
 */
public class GridViewSwipeRefreshLayout extends SwipeRefreshLayout {



    public GridViewSwipeRefreshLayout(Context context) {
        super(context);
    }

    public GridViewSwipeRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }



}
