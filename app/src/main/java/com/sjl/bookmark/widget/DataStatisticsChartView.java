package com.sjl.bookmark.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.util.AttributeSet;
import android.view.View;

import com.sjl.core.util.log.LogUtils;

import java.util.List;

/**
 * 文章浏览数据统计图
 */
public class DataStatisticsChartView extends View {

    Paint linePaint;
    Paint textPaint;
    Paint chartLinePaint;
    float gridX, gridY, xSpace = 0, ySpace = 0, spaceYT = 0, yStart = 0;
    String[] xAxis = null;
    int[] yAxis = null;

    List<int[]> data = null;

    public DataStatisticsChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DataStatisticsChartView(Context context) {
        this(context, null);
    }

    public DataStatisticsChartView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    PathEffect effect;
    Path path;

    private void init() {
        linePaint = new Paint();
        textPaint = new Paint();
        chartLinePaint = new Paint();

        //设置绘制模式为-虚线作为背景线。
        effect = new DashPathEffect(new float[]{10, 10, 10, 10, 10}, 2);
        //背景虚线路径.
        path = new Path();
        //只是绘制的XY轴
        linePaint.setStyle(Paint.Style.STROKE);
//        linePaint.setStrokeWidth((float) 0.7);
        linePaint.setStrokeWidth((float) 1.0);             //设置线宽

        linePaint.setColor(Color.GRAY);
        linePaint.setAntiAlias(true);// 锯齿不显示

        //XY刻度上的字
        textPaint.setStyle(Paint.Style.FILL);// 设置非填充
        textPaint.setStrokeWidth(2);// 笔宽5像素
        textPaint.setColor(Color.GRAY);// 设置为蓝笔
        textPaint.setAntiAlias(true);// 锯齿不显示
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(30);


        //绘制的折线
        chartLinePaint.setStyle(Paint.Style.FILL);
        chartLinePaint.setStrokeWidth(5);
        chartLinePaint.setColor(Color.parseColor("#03A9F4"));
        chartLinePaint.setAntiAlias(true);

    }



    public void setData(List<int[]> data) {
        this.data = data;
        invalidate();
    }

    public void setXAxis(String[] xAxis) {
        this.xAxis = xAxis;
    }

    public void setYAxis(int[] yAxis) {
        this.yAxis = yAxis;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //基准点。
        gridX = 40;
        gridY = getHeight() - 50;
        //XY间隔。
        if (xAxis != null && xAxis.length > 0) {
            xSpace = (getWidth() - gridX) / xAxis.length;
        }

        if (yAxis != null && yAxis.length > 0) {
            ySpace = (gridY - 70) / yAxis.length;
            yStart = yAxis[0];
            if (yAxis.length > 2) {
                spaceYT = Math.abs(yAxis[1] - yAxis[0]);
            }
        }


        //画Y轴(带箭头)。
        canvas.drawLine(gridX, gridY - 20 - 10, gridX, 30 + 10, linePaint);
        canvas.drawLine(gridX, 30 + 10, gridX - 6, 30 + 14 + 10, linePaint);//Y轴箭头。
        canvas.drawLine(gridX, 30 + 10, gridX + 6, 30 + 14 + 10, linePaint);


        float y = 0;
        //画X轴。
        y = gridY - 20;
        canvas.drawLine(gridX, y - 10, getWidth(), y - 10, linePaint);//X轴.
        canvas.drawLine(getWidth(), y - 10, getWidth() - 14, y - 6 - 10, linePaint);//X轴箭头。
        canvas.drawLine(getWidth(), y - 10, getWidth() - 14, y + 6 - 10, linePaint);

        //绘制X刻度坐标。
        float x = 0;
        int xOffset = 50;
        if (xAxis != null) {
            for (int n = 0; n < xAxis.length; n++) {
                //取X刻度坐标.
                x = gridX + (n) * xSpace;//在原点(0,0)处也画刻度（不画的话就是n+1）,向右移动一个跨度。
                //画X轴具体刻度值。
                if (xAxis[n] != null) {
                    //canvas.drawLine(x, gridY - 30, x, gridY - 18, linePaint);//短X刻度。
                    canvas.drawText(xAxis[n], x + xOffset, gridY + 5, textPaint);//X具体刻度值。
                }
            }
        }

        float my = 0;

        if (yAxis != null) {

            for (int n = 0; n < yAxis.length; n++) {
                //取Y刻度坐标.
                my = gridY - 30 - (n) * ySpace;

                //画y轴具体刻度值。
                canvas.drawText(String.valueOf(yAxis[n]), gridX - 20, my, textPaint);

                if (my != gridY - 30) {
                    linePaint.setPathEffect(effect);//设法虚线间隔样式。
                    //画除X轴之外的------背景虚线一条-------
                    path.moveTo(gridX, my);//背景【虚线起点】。
                    path.lineTo(getWidth(), my);//背景【虚线终点】。
                    canvas.drawPath(path, linePaint);
                }

            }
        }

        if (data != null && data.size() > 0) {
            float lastPointX = 0; //前一个点
            float lastPointY = 0;
            float currentPointX = 0;//当前点
            float currentPointY = 0;
            for (int n = 0; n < data.size(); n++) {
                int da[] = data.get(n);
                for (int m = 0; m < da.length; m++) {
                    int point = da[m];
                    LogUtils.i("point value:" + point);
                    currentPointX = m * xSpace + gridX + xOffset;
                    currentPointY = gridY - 30 - ((point - yStart) * (ySpace / spaceYT));
                    if (m > 0) {
                        canvas.drawLine(lastPointX, lastPointY, currentPointX, currentPointY, chartLinePaint);
                    }
                    lastPointX = currentPointX;
                    lastPointY = currentPointY;
                }

            }
        }
    }
}