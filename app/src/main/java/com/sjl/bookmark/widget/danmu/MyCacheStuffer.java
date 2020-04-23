package com.sjl.bookmark.widget.danmu;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextPaint;

import com.sjl.core.util.ViewUtils;

import java.util.Map;

import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.android.AndroidDisplayer;
import master.flame.danmaku.danmaku.model.android.BaseCacheStuffer;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MyCacheStuffer.java
 * @time 2019/3/27 17:11
 * @copyright(C) 2019 song
 */
public class MyCacheStuffer extends BaseCacheStuffer {
    /**
     * 头像直径
     */
    private int AVATAR_DIAMETER;
    /**
     * 头像边框宽度
     */
    private int AVATAR_PADDING;
    /**
     * 文字和头像间距
     */
    private int TEXT_LEFT_PADDING;
    /**
     * 文字和右边线距离
     */
    private int TEXT_RIGHT_PADDING;
    /**
     * 文字大小
     */
    private int TEXT_SIZE;

    /**
     * 昵称 红色
     */
    private int NICK_COLOR = 0xffeeeeee;
    /**
     * 文字内容  白色
     */
    private int TEXT_COLOR = 0xffffffff;
    /**
     * 文字灰色背景色值
     */
    private int TEXT_BG_COLOR = 0x66000000;
    /**
     * 文字灰色背景圆角
     */
    private int TEXT_BG_RADIUS;

    public MyCacheStuffer(Context context) {
        // 初始化固定参数，这些参数可以根据自己需求自行设定
        AVATAR_DIAMETER = ViewUtils.dp2px(context, 33);
        AVATAR_PADDING = ViewUtils.dp2px(context, 1);
        TEXT_LEFT_PADDING = ViewUtils.dp2px(context, 2);
        TEXT_RIGHT_PADDING = ViewUtils.dp2px(context, 10);
        TEXT_SIZE = ViewUtils.dp2px(context, 14);
        TEXT_BG_RADIUS = ViewUtils.dp2px(context, 30);
    }


    @Override
    public void measure(BaseDanmaku danmaku, TextPaint paint, boolean fromWorkerThread) {
        // 初始化数据
        Map<String, Object> map = (Map<String, Object>) danmaku.tag;
        if (map == null || map.isEmpty()) {
            return;
        }
        String name = (String) map.get("name");
        String content = (String) map.get("content");

        // 设置画笔
        paint.setTextSize(TEXT_SIZE);

        // 计算名字和内容的长度，取最大值
        float nameWidth = paint.measureText(name);
        float contentWidth = paint.measureText(content);
//        float maxWidth = Math.max(nameWidth, contentWidth);
        float maxWidth = nameWidth + contentWidth;
        /**
         * danmaku.paintWidth = 头像的直径 + 头像边圈的宽度*2 + 文字的长度 + 文字左边距 + 文字右边距。
         * danmaku.paintHeight = 头像的直径 + 头像边圈的宽度*2；
         */
        // 设置弹幕区域的宽高
        danmaku.paintWidth = maxWidth + AVATAR_DIAMETER + AVATAR_PADDING * 2 + TEXT_LEFT_PADDING + TEXT_RIGHT_PADDING; // 设置弹幕区域的宽度
        danmaku.paintHeight = AVATAR_DIAMETER + AVATAR_PADDING * 2; // 设置弹幕区域的高度
    }

    @Override
    public void clearCaches() {

    }


    @Override
    public void drawDanmaku(BaseDanmaku danmaku, Canvas canvas, float left, float top, boolean fromWorkerThread, AndroidDisplayer.DisplayerConfig displayerConfig) {
        // 初始化数据
        Map<String, Object> map = (Map<String, Object>) danmaku.tag;
        if (map == null || map.isEmpty()) {
            return;
        }
        String name = (String) map.get("name");
        String content = (String) map.get("content");
        Bitmap bitmap = (Bitmap) map.get("bitmap");

        // 设置画笔
        Paint paint = new Paint();
        paint.setTextSize(TEXT_SIZE);

        // 绘制文字灰色背景
        Rect rect = new Rect();
        paint.getTextBounds(name + content, 0, name.length() + content.length(), rect);
        paint.setColor(TEXT_BG_COLOR);
        paint.setAntiAlias(true);
        float bgLeft = left + AVATAR_DIAMETER / 2 + AVATAR_PADDING;
        float bgTop = top;
        float bgRight = left + AVATAR_DIAMETER + AVATAR_PADDING * 2 + TEXT_LEFT_PADDING + rect.width() + TEXT_RIGHT_PADDING;
        float bgBottom = top + AVATAR_DIAMETER + AVATAR_PADDING;
        /**
         * 绘制圆角矩形
         * 参数一：矩形对象

         参数二：圆角的x半径(椭圆的长轴的一半)

         参数三：圆角的y半径(椭圆的短轴的一半)

         参数四：画笔对象
         */
        canvas.drawRoundRect(new RectF(bgLeft, bgTop, bgRight, bgBottom), TEXT_BG_RADIUS, TEXT_BG_RADIUS, paint);

        // 绘制头像背景
        paint.setColor(Color.WHITE);
        float centerX = left + AVATAR_DIAMETER / 2 + AVATAR_PADDING;
        float centerY = left + AVATAR_DIAMETER / 2 + AVATAR_PADDING;
        float radius = AVATAR_DIAMETER / 2 + AVATAR_PADDING; // 半径
        canvas.drawCircle(centerX, centerY, radius, paint);

        // 绘制头像
        float avatorLeft = left + AVATAR_PADDING;
        float avatorTop = top + AVATAR_PADDING;
        float avatorRight = left + AVATAR_PADDING + AVATAR_DIAMETER;
        float avatorBottom = top + AVATAR_PADDING + AVATAR_DIAMETER;
        canvas.drawBitmap(bitmap, null, new RectF(avatorLeft, avatorTop, avatorRight, avatorBottom), paint);

        // 绘制名字
        paint.setColor(NICK_COLOR);
        float nameLeft = left + AVATAR_DIAMETER + AVATAR_PADDING * 2 + TEXT_LEFT_PADDING;
//        float nameBottom = top + rect.height() + AVATAR_PADDING + (AVATAR_DIAMETER / 2 - rect.height()) / 2;
//        canvas.drawText(name, nameLeft, nameBottom, paint);
//
//        // 绘制弹幕内容
//        paint.setColor(TEXT_COLOR);
//        float nameLeft = nameLeft;
//        float contentBottom = top + AVATAR_PADDING + AVATAR_DIAMETER / 2 + rect.height() + (AVATAR_DIAMETER / 2 - rect.height()) / 2;
//        canvas.drawText(content, nameLeft, contentBottom, paint);


        //计算文字的相应偏移量
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        //为基线到字体上边框的距离,即上图中的top
        float textTop = fontMetrics.top;
        //为基线到字体下边框的距离,即上图中的bottom
        float textBottom = fontMetrics.bottom;

        float contentBottom = top + AVATAR_DIAMETER / 2;
        //基线中间点的y轴计算公式
        int baseLineY = (int) (contentBottom - textTop / 2 - textBottom / 2);
        //绘制文字
        canvas.drawText(name, nameLeft, baseLineY, paint);

        // 绘制弹幕内容
        paint.setColor(TEXT_COLOR);
        float contentLeft = nameLeft + paint.measureText(name);

        canvas.drawText(content, contentLeft, baseLineY, paint);
    }

}