package com.sjl.bookmark.ui.adapter

import android.text.Html
import android.view.View
import android.widget.CheckBox
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.table.Collection
import com.sjl.core.util.datetime.TimeUtils

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MyCollectionAdapter.java
 * @time 2018/3/26 15:10
 * @copyright(C) 2018 song
 */
class MyCollectionAdapter(layoutResId: Int, data: List<Collection>?) : BaseQuickAdapter<Collection, BaseViewHolder>(layoutResId, data) {
    var mEditMode = false
    override fun convert(holder: BaseViewHolder, item: Collection) {
        val checkBox = holder.getView<CheckBox>(R.id.cb_item)
        if (mEditMode) {
            if (item.isSelectItem) {
                item.isSelectItem = true
                checkBox.isChecked = true
            } else {
                item.isSelectItem = false
                checkBox.isChecked = false
            }
            checkBox.visibility = View.VISIBLE
        } else {
            item.isSelectItem = false
            checkBox.isChecked = false
            checkBox.visibility = View.INVISIBLE
        }
        holder.setText(R.id.tv_title, Html.fromHtml(item.title)) //转义特殊字符,如&--&amp
        holder.setText(R.id.tv_collect_date, TimeUtils.getRangeByDate(item.date))
        holder.setText(R.id.tv_content, item.href)
        if (item.type == 0) {
            holder.setText(R.id.tv_collect_type, "网页")
            holder.setVisible(R.id.tv_content, false)
        } else if (item.type == 1) {
            holder.setText(R.id.tv_collect_type, "笔记")
            holder.setVisible(R.id.tv_content, true)
        } else {
            holder.setText(R.id.tv_collect_type, "其它")
            holder.setVisible(R.id.tv_content, false)
        }
        if (item.getTop() == 1) {
            holder.itemView.setBackgroundResource(R.drawable.list_top_oval_item_selector)
        } else {
            holder.itemView.setBackgroundResource(R.drawable.list_oval_item_selector)
        }
    }

    fun setEditMode(editMode: Boolean) {
        mEditMode = editMode
        notifyDataSetChanged()
    }
}