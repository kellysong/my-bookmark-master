package com.sjl.bookmark.widget.reader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.sjl.bookmark.R;
import com.sjl.bookmark.widget.reader.bean.PageStyle;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * 页面颜色风格适配器
 */

public class PageStyleAdapter extends CommonAdapter<Drawable> {
    private int currentChecked;

    public PageStyleAdapter(Context context, int layoutId, List<Drawable> datas) {
        super(context, layoutId, datas);
    }


    @Override
    protected void convert(ViewHolder holder, Drawable drawable, final int position) {
        holder.setDrawable(R.id.read_bg_view, drawable);
        ImageView view = holder.getView(R.id.read_bg_iv_checked);
        view.setVisibility(View.GONE);
        if (currentChecked == position) {
            view.setVisibility(View.VISIBLE);
        }
    }

    public void setPageStyleChecked(PageStyle pageStyle) {
        currentChecked = pageStyle.ordinal();//获取枚举所在顺序
    }
}
