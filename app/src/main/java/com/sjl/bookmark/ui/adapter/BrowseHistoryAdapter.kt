package com.sjl.bookmark.ui.adapter

import android.content.Context
import android.text.Html
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.table.BrowseTrack
import com.sjl.core.util.datetime.TimeUtils

/**
 * 浏览历史适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BrowseHistoryAdapter.java
 * @time 2022/12/2 18:14
 * @copyright(C) 2022 song
 */
class BrowseHistoryAdapter(context: Context, datas: List<BrowseTrack>?) : BaseQuickAdapter<BrowseTrack, BaseViewHolder>(R.layout.browse_history_recyle_item,datas) {
    override fun convert(helper: BaseViewHolder, item: BrowseTrack) {
        helper.setText(R.id.tv_title, Html.fromHtml(item.text)) //转义特殊字符,如&--&amp
        helper.setText(R.id.tv_collect_date, TimeUtils.getRangeByDate(item.createTime))
        helper.setText(R.id.tv_collect_type, item.category)
    }

}