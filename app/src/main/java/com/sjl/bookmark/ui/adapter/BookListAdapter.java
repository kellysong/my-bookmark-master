package com.sjl.bookmark.ui.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.zhuishu.table.RecommendBook;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * 书籍推荐列表适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookListAdapter.java
 * @time 2018/12/7 11:01
 * @copyright(C) 2018 song
 */
public class BookListAdapter extends CommonAdapter<RecommendBook>{

    public BookListAdapter(Context context, int layoutId, List<RecommendBook> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, RecommendBook recommendBook, int position) {
        ImageView mIvPortrait = holder.getView(R.id.book_brief_iv_portrait);
        //头像
        Glide.with(getContext())
                .load(recommendBook.getCover())
                .placeholder(R.mipmap.ic_default_portrait)
                .error(R.mipmap.ic_load_error)
                .fitCenter()
                .into(mIvPortrait);
        //书名
        holder.setText(R.id.book_brief_tv_title,recommendBook.getTitle());
        //作者
        holder.setText(R.id.book_brief_tv_author,recommendBook.getAuthor());

    }
}
