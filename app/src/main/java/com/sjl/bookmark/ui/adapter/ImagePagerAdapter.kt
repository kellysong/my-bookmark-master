package com.sjl.bookmark.ui.adapter

import android.content.Context
import android.os.Handler
import android.os.Message
import android.view.View
import android.widget.ImageView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.sjl.core.util.log.LogUtils
import com.zhy.adapter.viewpager.PagerListAdapter
import java.lang.ref.WeakReference

/**
 * 用于轮播图的适配器，刚开始还不支持右无限循环
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ImagePagerAdapter.java
 * @time 2019/2/18 17:11
 * @copyright(C) 2019 song
 */
class ImagePagerAdapter(context: Context, data: List<String>?, private val mViewPager: ViewPager) : PagerListAdapter<String>(context, data) {
    private var mHandler: Handler? = null
    override fun getCount(): Int {
        return Int.MAX_VALUE
    }

    override fun newView(position: Int): View {
        val newPosition = position % mData.size
        val imageView = ImageView(mContext)
        imageView.scaleType = ImageView.ScaleType.FIT_XY
        Glide.with(mContext.applicationContext)
                .load(getItem(newPosition))
                .into(imageView)
        return imageView
    }

    private class MyHandler(viewPager: ViewPager, intervalTime: Int) : Handler() {
        /**
         * 使用弱引用，避免handler造成内存泄露
         */
        private val weakReference: WeakReference<ViewPager>

        /**
         * 轮播间隔时间
         */
        private val intervalTime: Int
        override fun handleMessage(msg: Message) {
            val viewPager = weakReference.get() ?: return
            //Activity不存在了，就不需要再处理了
            //如果已经有消息了，先移除消息
            if (hasMessages(BANNER_NEXT)) {
                removeMessages(BANNER_NEXT)
            }
            when (msg.what) {
                BANNER_NEXT -> {
                    //跳到下一页，
                    var currentItem = viewPager.currentItem
                    viewPager.currentItem = ++currentItem
                    //3秒后继续轮播
                    sendEmptyMessageDelayed(BANNER_NEXT, intervalTime.toLong())
                }
                BANNER_PAUSE ->                     //暂停,不需要做任务操作
                    LogUtils.i("轮播暂停")
            }
        }

        init {
            weakReference = WeakReference(viewPager)
            this.intervalTime = intervalTime
        }
    }

    /**
     * 开始轮播
     *
     * @param time 秒
     */
    fun startLoop(time: Int) {
        var time = time
        if (mHandler != null) {
            stopLoop()
        }
        time = if (time < 0) {
            3 * 1000
        } else {
            time * 1000
        }
        val intervalTime = time
        mHandler = MyHandler(mViewPager, intervalTime)
        mHandler?.apply {
            mViewPager.offscreenPageLimit = 2 //预加载2个
            mViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
                override fun onPageSelected(position: Int) {}
                override fun onPageScrollStateChanged(state: Int) {
                    when (state) {
                        ViewPager.SCROLL_STATE_DRAGGING ->                         //用户正在滑动，暂停轮播
                            sendEmptyMessage(BANNER_PAUSE)
                        ViewPager.SCROLL_STATE_IDLE ->                         //滑动结束，继续轮播
                            sendEmptyMessageDelayed(BANNER_NEXT, intervalTime.toLong())
                        ViewPager.SCROLL_STATE_SETTLING -> {
                        }
                    }
                }
            })
            setOnTouchActionListener(object : OnTouchActionListener {
                override fun onDown() {
                    //用户正在按下，暂停轮播,按下时间必须监听view的事件
                    sendEmptyMessage(BANNER_PAUSE)
                }

                override fun onUp() {
                    sendEmptyMessageDelayed(BANNER_NEXT, intervalTime.toLong())
                }
            })
            //开始轮播
            sendEmptyMessageDelayed(BANNER_NEXT, intervalTime.toLong())
        }

    }

    /**
     * 停止轮播
     */
    fun stopLoop() {
        mHandler?.apply {
            removeMessages(BANNER_NEXT)
            removeMessages(BANNER_PAUSE)
            removeCallbacksAndMessages(null)
            null
        }

    }

    companion object {
        private const val BANNER_NEXT = 1
        private const val BANNER_PAUSE = 2
    }
}