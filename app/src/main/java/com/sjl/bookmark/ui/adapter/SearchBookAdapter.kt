package com.sjl.bookmark.ui.adapter

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.zhuishu.SearchBookDto.BooksBean
import com.sjl.bookmark.net.HttpConstant
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder

/**
 * 搜索书适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename SearchBookAdapter.java
 * @time 2018/12/2 20:10
 * @copyright(C) 2018 song
 */
class SearchBookAdapter(context: Context, layoutId: Int, datas: List<BooksBean>?) : CommonAdapter<BooksBean>(context, layoutId, datas) {
    override fun convert(holder: ViewHolder, booksBean: BooksBean, position: Int) {
        //显示图片
        val mIvCover = holder.getView<ImageView>(R.id.search_book_iv_cover)
        Glide.with(mContext)
                .load(HttpConstant.ZHUISHU_IMG_BASE_URL + booksBean.cover)
                .placeholder(R.drawable.ic_book_loading)
                .error(R.mipmap.ic_load_error)
                .into(mIvCover)
        holder.setText(R.id.search_book_tv_name, booksBean.title)
        holder.setText(R.id.search_book_tv_brief, booksBean.title)
        holder.setText(R.id.search_book_tv_brief, mContext.getString(R.string.nb_search_book_brief,
                booksBean.latelyFollower, booksBean.retentionRatio, booksBean.author))
    }
}