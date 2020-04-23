package com.sjl.bookmark.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.sjl.bookmark.R;

/**
 * 自定义PopWindow
 *
 * @author Kelly
 * @version 1.0.0
 * @filename PopWindow.java
 * @time 2018/4/2 16:34
 * @copyright(C) 2018 song
 */
public class PopWindow extends PopupWindow {

    private View conentView;
    private TextView top;

    public PopWindow(final Activity context, final PopWindowOnClickListener itemsOnClick){
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.popup_window, null,false);
        TextView share = (TextView) conentView.findViewById(R.id.tv_share);
        TextView delete = (TextView) conentView.findViewById(R.id.tv_delete);
        TextView more = (TextView) conentView.findViewById(R.id.tv_more);
        top = (TextView) conentView.findViewById(R.id.tv_top);
        share.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                itemsOnClick.onClick(v);
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                itemsOnClick.onClick(v);
            }
        });
        more.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                itemsOnClick.onClick(v);
            }
        });
        top.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                itemsOnClick.onClick(v);
            }
        });

        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);

        //设置PopupWindow的宽和高,必须设置,否则不显示内容
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        //设置PopupWindow的背景为一个空的Drawable对象，如果不设置这个，那么PopupWindow弹出后就无法退出了
        //实例化一个ColorDrawable颜色为半透明  
        ColorDrawable dw = new ColorDrawable(0x00000000);
        //设置SelectPicPopupWindow弹出窗体的背景  
        this.setBackgroundDrawable(dw);
        backgroundAlpha(context, 0.5f);//0.0-1.0  
        this.setOnDismissListener(new OnDismissListener() {

            @Override
            public void onDismiss() {
                backgroundAlpha(context, 1f);
            }
        });
    }

    /**
     * 设置添加屏幕的背景透明度
     * @param context
     * @param v
     */
    public void backgroundAlpha(Activity context, float v) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = v;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        context.getWindow().setAttributes(lp);
    }

    /**
     * 显示popupWindow
     *
     * @param parent
     */
    public void showPopupWindow(View parent) {
        if (!this.isShowing()) {
            // 以下拉方式显示popupwindow
            this.showAtLocation(parent,Gravity.CENTER ,0,0 );
        } else {
            this.dismiss();
        }
    }

    /**
     * 设置置顶状态
     * @param topStates
     */
    public void setTopStates(int topStates) {
        //判断是否已经置顶
        if (topStates == 1){
            top.setText("取消置顶");
            this.executeTopFlag = false;
        }else {
            top.setText("置顶");
            this.executeTopFlag = true;
        }
    }

    private boolean executeTopFlag;

    public boolean isExecuteTopFlag() {
        return executeTopFlag;
    }

    /**
     * PopWindow条目点击事件
     */
    public interface PopWindowOnClickListener{
        void onClick(View v);
    }
}
