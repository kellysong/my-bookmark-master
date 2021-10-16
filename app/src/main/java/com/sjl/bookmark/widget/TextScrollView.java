package com.sjl.bookmark.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.WindowManager;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename TextScrollView
 * @time 2021/9/24 15:19
 * @copyright(C) 2021 song
 */
public class TextScrollView extends androidx.appcompat.widget.AppCompatTextView {
    /**
     * 文字长度
     */
    private float textLength = 0f;
    /**
     * 滚动条长度
     */
    private float viewWidth = 0f;
    /**
     * 文本x轴 的坐标
     */
    private float step = 0f;
    /**
     * 文本Y轴的坐标
     */
    private float y = 0f;
    /**
     * 文本当前长度
     */
    private float temp_tx1 = 0.0f;
    /**
     * 文本当前变换的长度
     */
    private float temp_tx2 = 0x0f;
    /**
     * 文本滚动开关
     */
    private boolean isStarting = false;
    /**
     * 画笔对象
     */
    private Paint paint = null;
    /**
     * 显示的文字
     */
    private String text = "";
    /**
     * 文本滚动速度
     **/
    private float speed;

    public TextScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TextScrollView(Context context) {
        super(context);
    }

    /**
     * 初始化自动滚动条,每次改变文字内容时，都需要重新初始化一次
     *
     * @param windowManager 获取屏幕
     * @param text          显示的内容
     * @param speed         文字滚动的类型 滚动速度，越大滚动越快
     */
    public void initScrollTextView(WindowManager windowManager, String text,
                                   float speed) {
        // 得到画笔,获取父类的textPaint
        paint = this.getPaint();
        paint.setColor(Color.BLACK);
        paint.setTextSize(getTextSize());
        // 得到文字
        this.text = text;
        this.speed = speed;
        textLength = paint.measureText(text);// 获得当前文本字符串长度
        viewWidth = this.getWidth();// 获取宽度return mRight - mLeft;
        if (viewWidth == 0) {
            // 获取当前屏幕的属性
            Display display = windowManager.getDefaultDisplay();
            viewWidth = display.getWidth();// 获取屏幕宽度  viewWidth 是滚动的开始位置，需要修改的
            // 可再此入手
        }
        step = textLength;
        temp_tx1 = viewWidth + textLength;
        temp_tx2 = viewWidth + textLength * 2;// 自己定义，文字变化多少
        // // 文字的大小+距顶部的距离
        y = this.getTextSize() + this.getPaddingTop();
    }

    /**
     * 开始滚动
     */
    public void starScroll() {
        isStarting = true;
        this.invalidate();
    }

    /**
     * 停止滚动
     */
    public void stopScroll() {
        isStarting = false;
        this.invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                stopScroll();
                break;
            case MotionEvent.ACTION_UP:
                starScroll();
                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (isStarting) {
            if (!TextUtils.isEmpty(text)){
                canvas.drawText(text, temp_tx1 - step, y, paint);
            }
            step += speed;
            // 当文字滚动到屏幕的最左边
            if (step > temp_tx2) {
                // 把文字设置到最右边开始
                step = textLength;
            }
            invalidate();// 刷新屏幕
        } else {
            if (!TextUtils.isEmpty(text)){
                canvas.drawText(text, temp_tx1 - step, y, paint);
            }

        }
    }

}
