package com.sjl.bookmark.ui.adapter

import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sjl.bookmark.R
import com.sjl.bookmark.entity.Category
import android.R.color
import androidx.core.graphics.drawable.DrawableCompat
import cn.feng.skin.manager.loader.SkinManager
import com.sjl.bookmark.kotlin.darkmode.DarkModeUtils


/**
 * Android知识分类左侧
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CategoryLeftAdapter
 * @time 2022/12/7 17:41
 * @copyright(C) 2022 song
 */
class CategoryLeftAdapter(data: List<Category>?) : BaseQuickAdapter<Category, BaseViewHolder>(R.layout.category_knowledge_left_recycle_item, data) {
    var selectPosition = - 1
    var selectDrawable: Drawable?=null
    override fun convert(helper: BaseViewHolder, item: Category) {
        val titleTv = helper.getView<TextView>(R.id.typeItemFirst);
        titleTv.text = item.name
        //动态设置指示器颜色
        if (helper.layoutPosition == selectPosition){
            selectDrawable?.let {
                val wrappedDrawable: Drawable = DrawableCompat.wrap(it)
                if (DarkModeUtils.isNightMode(mContext)){
                    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(mContext,R.color.cl_line_title))
                }else{
                    DrawableCompat.setTint(wrappedDrawable, SkinManager.getInstance().getColor(R.color.cl_line_title))
                }

            }
            titleTv.setCompoundDrawables(null, null, selectDrawable, null)
        }else{
            titleTv.setCompoundDrawables(null, null, null, null);
        }
    }

    fun setChoose(position: Int) {
        this.selectPosition = position
        val resources = mContext.resources
        selectDrawable = ContextCompat.getDrawable(mContext,R.drawable.line_title)

        val right =  resources.getDimensionPixelSize(R.dimen.dp_3)
        val bottom =  resources.getDimensionPixelSize(R.dimen.dp_15)
        selectDrawable?.run {
            setBounds(0, 0, right, bottom)
        }
        notifyDataSetChanged()
    }

    fun refresh() {
        if (selectPosition != - 1){
            notifyDataSetChanged()
        }
    }
}