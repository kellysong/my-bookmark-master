package com.sjl.bookmark.widget;

import android.support.design.widget.AppBarLayout;

/**
 * CollapsingToolbarLayout的展开与折叠
 * 使用官方提供的 AppBarLayout.OnOffsetChangedListener就能实现了，不过要封装一下才好用。
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AppBarStateChangeListener.java
 * @time 2018/12/21 14:45
 * @copyright(C) 2018 song
 */
public abstract class AppBarStateChangeListener implements AppBarLayout.OnOffsetChangedListener {

    public enum State {
        EXPANDED,
        COLLAPSED,
        IDLE
    }

    private State mCurrentState = State.IDLE;

    @Override
    public final void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        onShadowChanged(appBarLayout, verticalOffset);
        if (verticalOffset == 0) {
            if (mCurrentState != State.EXPANDED) {
                onStateChanged(appBarLayout, State.EXPANDED);
            }
            mCurrentState = State.EXPANDED;
        } else if (Math.abs(verticalOffset) >= appBarLayout.getTotalScrollRange()) {
            if (mCurrentState != State.COLLAPSED) {
                onStateChanged(appBarLayout, State.COLLAPSED);
            }
            mCurrentState = State.COLLAPSED;
        } else {
            if (mCurrentState != State.IDLE) {
                onStateChanged(appBarLayout, State.IDLE);
            }
            mCurrentState = State.IDLE;
        }
    }

    /**
     * 渐变改变
     * onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset)的第二个参数是AppBarLayout的Y轴偏移量，
     * 当AppBarLayout处于扩展状态时，verticalOffset=0;
     * 当AppBarLayout处于折叠状态时，verticalOffset=-appBarLayout.getTotalScrollRange()，注意是负的;
     * 当处于滚动状态时，verticalOffset 从0 到 负的appBarLayout.getTotalScrollRange()之间变化，所以这里使用Math.abs来取绝对值。
     * 当向上滚动时，Math.abs(verticalOffset*1.0f)越来越大，向下滚动时，Math.abs(verticalOffset*1.0f)越来越小，
     * 通过Math.abs(verticalOffset*1.0f)/appBarLayout.getTotalScrollRange()计算偏移量的百分比来改变Toolbar背景透明度
     *
     * @param appBarLayout
     * @param verticalOffset
     */
    public abstract void onShadowChanged(AppBarLayout appBarLayout, int verticalOffset);

    /**
     * 状态监听
     *
     * @param appBarLayout
     * @param state
     */
    public abstract void onStateChanged(AppBarLayout appBarLayout, State state);
}
