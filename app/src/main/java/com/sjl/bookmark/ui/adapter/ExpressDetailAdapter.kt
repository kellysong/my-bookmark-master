package com.sjl.bookmark.ui.adapter

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.ExpressDetail.DataBean
import com.sjl.core.util.ViewUtils
import com.zhy.adapter.recyclerview.CommonAdapter
import com.zhy.adapter.recyclerview.base.ViewHolder

/**
 * 快递明细适配器
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressDetailAdapter.java
 * @time 2018/5/2 14:13
 * @copyright(C) 2018 song
 */
class ExpressDetailAdapter(context: Context, layoutId: Int, datas: List<DataBean>?) : CommonAdapter<DataBean>(context, layoutId, datas) {
    override fun convert(holder: ViewHolder, dataBean: DataBean, position: Int) {
        val time = holder.getView<TextView>(R.id.tv_time)
        val detail = holder.getView<TextView>(R.id.tv_detail)
        val ivLogistics = holder.getView<ImageView>(R.id.iv_logistics)
        time.text = dataBean.time
        detail.text = dataBean.context
        val first = position == 0
        val line = holder.getView<View>(R.id.line)
        line.setPadding(0, ViewUtils.dp2px(context, if (first) 12 else 0), 0, 0)
        ivLogistics.isSelected = first
        time.isSelected = first
        detail.isSelected = first
    }
}