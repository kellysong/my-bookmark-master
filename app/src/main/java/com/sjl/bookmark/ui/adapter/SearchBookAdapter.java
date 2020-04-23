package com.sjl.bookmark.ui.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sjl.bookmark.R;
import com.sjl.bookmark.entity.zhuishu.SearchBookDto;
import com.sjl.bookmark.net.HttpConstant;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * 搜索书适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename SearchBookAdapter.java
 * @time 2018/12/2 20:10
 * @copyright(C) 2018 song
 */
public class SearchBookAdapter extends CommonAdapter<SearchBookDto.BooksBean> {

    public SearchBookAdapter(Context context, int layoutId, List<SearchBookDto.BooksBean> datas) {
        super(context, layoutId, datas);
    }

    @Override
    protected void convert(ViewHolder holder, SearchBookDto.BooksBean booksBean, int position) {
        //显示图片
        ImageView mIvCover = holder.getView(R.id.search_book_iv_cover);

        Glide.with(mContext)
                .load(HttpConstant.ZHUISHU_IMG_BASE_URL + booksBean.getCover())
                .placeholder(R.drawable.ic_book_loading)
                .error(R.mipmap.ic_load_error)
                .into(mIvCover);
        holder.setText(R.id.search_book_tv_name, booksBean.getTitle());

        holder.setText(R.id.search_book_tv_brief, booksBean.getTitle());

        holder.setText(R.id.search_book_tv_brief, mContext.getString(R.string.nb_search_book_brief,
                booksBean.getLatelyFollower(), booksBean.getRetentionRatio(), booksBean.getAuthor()));
    }
}
