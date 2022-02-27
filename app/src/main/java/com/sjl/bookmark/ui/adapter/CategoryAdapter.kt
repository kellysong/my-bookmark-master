package com.sjl.bookmark.ui.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.Category

/**
 * Android知识分类
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CategoryAdapter.java
 * @time 2018/3/22 16:02
 * @copyright(C) 2018 song
 */
class CategoryAdapter(layoutResId: Int, data: List<Category>?) : BaseQuickAdapter<Category, BaseViewHolder>(layoutResId, data) {
    override fun convert(helper: BaseViewHolder, item: Category) {
        helper.setText(R.id.typeItemFirst, item.name)
        val sb = StringBuffer()
        for (childrenBean in item.children) {
            sb.append(childrenBean.name + "     ")
        }
        helper.setText(R.id.typeItemSecond, sb.toString())
    }
}