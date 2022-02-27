package com.sjl.bookmark.ui.adapter

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.zhuishu.table.RecommendBook
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder

/**
 * 书籍推荐列表适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookListAdapter.java
 * @time 2018/12/7 11:01
 * @copyright(C) 2018 song
 */
class BookListAdapter(context: Context, layoutId: Int, datas: List<RecommendBook>?) : CommonAdapter<RecommendBook>(context, layoutId, datas) {
    override fun convert(holder: ViewHolder, recommendBook: RecommendBook, position: Int) {
        val mIvPortrait = holder.getView<ImageView>(R.id.book_brief_iv_portrait)
        //头像
        Glide.with(context)
                .load(recommendBook.cover)
                .placeholder(R.mipmap.ic_default_portrait)
                .error(R.mipmap.ic_load_error)
                .fitCenter()
                .into(mIvPortrait)
        //书名
        holder.setText(R.id.book_brief_tv_title, recommendBook.title)
        //作者
        holder.setText(R.id.book_brief_tv_author, recommendBook.author)
    }
}