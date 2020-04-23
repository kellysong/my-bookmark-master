package com.sjl.bookmark.ui.adapter;

import android.content.Context;

import com.sjl.bookmark.R;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * 书籍搜索关键字适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookKeyWordAdapter.java
 * @time 2018/12/2 20:03
 * @copyright(C) 2018 song
 */
public class BookKeyWordAdapter extends CommonAdapter<String> {
    public BookKeyWordAdapter(Context context, int layoutId, List<String> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, String s, int position) {
        holder.setText(R.id.keyword_tv_name,s);
    }
}
