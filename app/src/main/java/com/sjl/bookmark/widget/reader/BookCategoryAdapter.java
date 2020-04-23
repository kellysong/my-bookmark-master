package com.sjl.bookmark.widget.reader;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.widget.TextView;

import com.sjl.bookmark.R;
import com.sjl.bookmark.widget.reader.bean.TxtChapter;
import com.zhy.adapter.abslistview.CommonAdapter;
import com.zhy.adapter.abslistview.ViewHolder;

import java.util.List;

/**
 * 书籍章节目录适配器
 * 前后两章显示红点，当前章显示标签
 */
public class BookCategoryAdapter extends CommonAdapter<TxtChapter> {
    private int currentSelected = 0;


    public BookCategoryAdapter(Context context, int layoutId, List<TxtChapter> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, TxtChapter txtChapter, int position) {
        TextView mTvChapter = holder.getView(R.id.category_tv_chapter);
        //首先判断是否该章已下载
        Drawable drawable = null;

        //TODO:目录显示设计的有点不好，需要靠成员变量是否为null来判断。
        //如果没有链接地址表示是本地文件
        if (txtChapter.link == null) {
            drawable = ContextCompat.getDrawable(getContext(), R.drawable.selector_category_load);
        } else {
            if (txtChapter.bookId != null
                    && BookManager.isChapterCached(txtChapter.bookId, txtChapter.title)) {
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.selector_category_load);
            } else {
                drawable = ContextCompat.getDrawable(getContext(), R.drawable.selector_category_unload);
            }
        }

        mTvChapter.setSelected(false);
        mTvChapter.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        mTvChapter.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null);//设置textView的drawable
        mTvChapter.setText(txtChapter.title);

        if (position == currentSelected) {
            mTvChapter.setTextColor(ContextCompat.getColor(getContext(), R.color.light_red));
            mTvChapter.setSelected(true);
        }
    }

    public void setChapter(int pos) {
        currentSelected = pos;
        notifyDataSetChanged();
    }
}
