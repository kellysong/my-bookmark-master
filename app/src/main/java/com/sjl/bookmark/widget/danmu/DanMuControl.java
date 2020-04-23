package com.sjl.bookmark.widget.danmu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.sjl.core.manager.CachedThreadManager;
import com.sjl.core.util.log.LogUtils;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.model.android.Danmakus;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import master.flame.danmaku.danmaku.util.IOUtils;

/**
 * 弹幕控制器
 * 参考：https://blog.csdn.net/zxq614/article/details/52622792
 *
 * @author Kelly
 * @version 1.0.0
 * @filename DanMuControl.java
 * @time 2019/3/27 17:08
 * @copyright(C) 2019 song
 */
public class DanMuControl {
    private Context mContext;
    private IDanmakuView mDanmakuView;
    private DanmakuContext mDanmakuContext;

    public DanMuControl(Context context, IDanmakuView danmakuView) {
        this.mContext = context;
        this.mDanmakuView = danmakuView;
        initDanmuConfig();
    }

    /**
     * 初始化配置
     */
    private void initDanmuConfig() {
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<Integer, Integer>();
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5); // 滚动弹幕最大显示5行
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<Integer, Boolean>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);

        mDanmakuContext = DanmakuContext.create();
        mDanmakuContext
                .setDanmakuStyle(IDisplayer.DANMAKU_STYLE_NONE)
                .setDuplicateMergingEnabled(false)//设置不合并相同内容弹幕
                .setScrollSpeedFactor(2.0f)//越大速度越慢
                .setScaleTextSize(1.2f)//设置字体缩放比例
                .setCacheStuffer(new MyCacheStuffer(mContext), mCacheStufferAdapter)// 图文混排使用SpannedCacheStuffer
                .setMaximumLines(maxLinesPair)//设置最大行数策略
                .preventOverlapping(overlappingEnablePair);//设置禁止重叠策略

        if (mDanmakuView != null) {
            mDanmakuView.setCallback(new DrawHandler.Callback() {
                @Override
                public void prepared() {
                    mDanmakuView.start();
                }

                @Override
                public void updateTimer(DanmakuTimer timer) {
                }

                @Override
                public void danmakuShown(BaseDanmaku danmaku) {
                }

                @Override
                public void drawingFinished() {
                }
            });
        }

        mDanmakuView.prepare(new BaseDanmakuParser() {

            @Override
            protected Danmakus parse() {
                return new Danmakus();
            }
        }, mDanmakuContext);
        mDanmakuView.enableDanmakuDrawingCache(true);
    }

    private BaseCacheStuffer.Proxy mCacheStufferAdapter = new BaseCacheStuffer.Proxy() {

        @Override
        public void prepareDrawing(final BaseDanmaku danmaku, boolean fromWorkerThread) {
        }

        @Override
        public void releaseResource(BaseDanmaku danmaku) {
            // tag包含bitmap，一定要清空
            danmaku.tag = null;
        }
    };


    /**
     * 批量发送弹幕
     *
     * @param danMuInfos
     */
    public void addDanmu(final List<DanMuInfo> danMuInfos) {
        if (danMuInfos == null || mDanmakuView == null) {
            return;
        }
        CachedThreadManager.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                int num = 0;
                int appendTime = 1200;
                int size = danMuInfos.size();
                for (int i = 0; i < size; i++) {
                    final BaseDanmaku danmaku = mDanmakuContext.mDanmakuFactory.createDanmaku(BaseDanmaku.TYPE_SCROLL_RL);
                    if (mDanmakuView == null || danmaku == null) {
                        LogUtils.w("创建弹幕失败：" + i);
                        break;
                    }
                    DanMuInfo danMuInfo = danMuInfos.get(i);
                    InputStream inputStream = null;
                    try {
                        // 从网络获取图片并且保存到一个bitmap里
                        URLConnection urlConnection = new URL(danMuInfo.avatarUrl).openConnection();
                        inputStream = urlConnection.getInputStream();
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        bitmap = makeRoundCorner(bitmap);

                        // 组装需要传递给danmaku的数据
                        Map<String, Object> map = new HashMap<String, Object>();
                        map.put("name", danMuInfo.name);
                        map.put("content", danMuInfo.content);
                        map.put("bitmap", bitmap);
                        danmaku.tag = map;
                    } catch (Exception e) {
//                        LogUtils.e(e);
                        continue;
                    } finally {
                        IOUtils.closeQuietly(inputStream);
                    }
                    danmaku.text = "";
                    danmaku.padding = 5;
                    /**
                     * 设置最大行数，priority=0的时候可以，但是会丢失一部分。1的时候有时会超出行数。
                     设置重叠，priority = 0的时候可以，1的时候还会重叠，0的时候还是会丢失一部分。
                     */
                    danmaku.priority = 0;
                    danmaku.isLive = true;
                    danmaku.setTime(mDanmakuView.getCurrentTime() + appendTime);
                    danmaku.textSize = 0;
                    mDanmakuView.addDanmaku(danmaku);
                    if (i % 5 == 0) {//每隔appendTime毫秒发送5条,防止同一时间发送太多丢失数据
                        try {
                            Thread.sleep(appendTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        appendTime += 1000;
                        LogUtils.i("addDanmu批次:" + (++num));
                    }
                }
            }
        });
    }

    /**
     * 将图片变成圆形
     *
     * @param bitmap
     * @return
     */
    private static Bitmap makeRoundCorner(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int left = 0, top = 0, right = width, bottom = height;
        float roundPx = height / 2;
        if (width > height) {
            left = (width - height) / 2;
            top = 0;
            right = left + height;
            bottom = height;
        } else if (height > width) {
            left = 0;
            top = (height - width) / 2;
            right = width;
            bottom = top + width;
            roundPx = width / 2;
        }
        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        int color = 0xff424242;
        Paint paint = new Paint();
        Rect rect = new Rect(left, top, right, bottom);
        RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

}
