package com.sjl.bookmark.ui.adapter

import android.content.Context
import android.text.Html
import android.text.TextUtils
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
        if (!TextUtils.isEmpty(item.text)){
            helper.setText(R.id.tv_title, Html.fromHtml(item.text)) //转义特殊字符,如&--&amp
        }else{
            helper.setText(R.id.tv_title, item.articleId) //转义特殊字符,如&--&amp
        }

        if (item.createTime != null){
            helper.setGone(R.id.tv_collect_date,true).setText(R.id.tv_collect_date, TimeUtils.getTimeFormatText(item.createTime))
        }else{
            helper.setGone(R.id.tv_collect_date,false)
        }

        if (!TextUtils.isEmpty(item.category)){
            helper.setGone(R.id.tv_collect_type,true).setText(R.id.tv_collect_type, item.category)
        }else{
            helper.setGone(R.id.tv_collect_type,false)
        }

    }

}