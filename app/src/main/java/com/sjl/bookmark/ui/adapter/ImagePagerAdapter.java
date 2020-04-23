package com.sjl.bookmark.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sjl.core.util.log.LogUtils;
import com.zhy.adapter.viewpager.PagerListAdapter;

import java.lang.ref.WeakReference;
import java.util.List;

/**
 * 用于轮播图的适配器，刚开始还不支持右无限循环
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ImagePagerAdapter.java
 * @time 2019/2/18 17:11
 * @copyright(C) 2019 song
 */
public class ImagePagerAdapter extends PagerListAdapter<String> {
    private ViewPager mViewPager;
    private Handler mHandler;
    private static final int BANNER_NEXT = 1;
    private static final int BANNER_PAUSE = 2;

    public ImagePagerAdapter(Context context, List<String> data, ViewPager viewPager) {
        super(context, data);
        this.mViewPager = viewPager;

    }

    @Override
    public int getCount() {
        return Integer.MAX_VALUE;
    }

    @Override
    public View newView(int position) {
        int newPosition = position % mData.size();
        ImageView imageView = new ImageView(mContext);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(mContext.getApplicationContext())
                .load(getItem(newPosition))
                .into(imageView);
        return imageView;
    }


    private static class MyHandler extends Handler {
        /**
         * 使用弱引用，避免handler造成内存泄露
         */
        private WeakReference<ViewPager> weakReference;
        /**
         * 轮播间隔时间
         */
        private int intervalTime;

        public MyHandler(ViewPager viewPager, int intervalTime) {
            this.weakReference = new WeakReference<ViewPager>(viewPager);
            this.intervalTime = intervalTime;
        }

        @Override
        public void handleMessage(Message msg) {
            ViewPager viewPager = weakReference.get();
            //Activity不存在了，就不需要再处理了
            if (viewPager == null) {
                return;
            }
            //如果已经有消息了，先移除消息
            if (hasMessages(BANNER_NEXT)) {
                removeMessages(BANNER_NEXT);
            }
            switch (msg.what) {
                case BANNER_NEXT:
                    //跳到下一页，
                    int currentItem = viewPager.getCurrentItem();
                    viewPager.setCurrentItem(++currentItem);
                    //3秒后继续轮播
                    sendEmptyMessageDelayed(BANNER_NEXT, intervalTime);
                    break;
                case BANNER_PAUSE:
                    //暂停,不需要做任务操作
                    LogUtils.i("轮播暂停");
                    break;
            }
        }
    }


    /**
     * 开始轮播
     *
     * @param time 秒
     */
    public void startLoop(int time) {
        if (mHandler != null) {
            stopLoop();
        }
        if (time < 0) {
            time = 3 * 1000;
        } else {
            time = time * 1000;
        }
        final int intervalTime = time;
        mHandler = new MyHandler(mViewPager, intervalTime);
        mViewPager.setOffscreenPageLimit(2);//预加载2个

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager.SCROLL_STATE_DRAGGING://滑动开始
                        //用户正在滑动，暂停轮播
                        mHandler.sendEmptyMessage(BANNER_PAUSE);
                        break;
                    case ViewPager.SCROLL_STATE_IDLE://滑动终止
                        //滑动结束，继续轮播
                        mHandler.sendEmptyMessageDelayed(BANNER_NEXT, intervalTime);
                        break;
                    case ViewPager.SCROLL_STATE_SETTLING://滑动结束
                        break;
                }

            }
        });

        setOnTouchActionListener(new OnTouchActionListener() {
            @Override
            public void onDown() {
                //用户正在按下，暂停轮播,按下时间必须监听view的事件
                mHandler.sendEmptyMessage(BANNER_PAUSE);
            }

            @Override
            public void onUp() {
                mHandler.sendEmptyMessageDelayed(BANNER_NEXT, intervalTime);
            }
        });
        //开始轮播
        mHandler.sendEmptyMessageDelayed(BANNER_NEXT, intervalTime);
    }

    /**
     * 停止轮播
     */
    public void stopLoop() {
        if (mHandler != null) {
            mHandler.removeMessages(BANNER_NEXT);
            mHandler.removeMessages(BANNER_PAUSE);
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

}
