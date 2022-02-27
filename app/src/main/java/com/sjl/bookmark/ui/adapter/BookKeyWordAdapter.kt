package com.sjl.bookmark.ui.adapter

import android.content.Context
import com.sjl.bookmark.R
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder

/**
 * 书籍搜索关键字适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookKeyWordAdapter.java
 * @time 2018/12/2 20:03
 * @copyright(C) 2018 song
 */
class BookKeyWordAdapter(context: Context, layoutId: Int, datas: List<String>?) : CommonAdapter<String?>(context, layoutId, datas) {
    override fun convert(holder: ViewHolder, s: String?, position: Int) {
        holder.setText(R.id.keyword_tv_name, s)
    }
}