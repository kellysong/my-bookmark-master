package com.sjl.bookmark.ui.adapter

import android.graphics.Color
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.sjl.bookmark.R
import com.sjl.bookmark.app.AppConstant
import com.sjl.bookmark.entity.ThemeSkin
import com.sjl.core.util.PreferencesHelper

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ThemeSkinAdapter.java
 * @time 2018/12/29 22:13
 * @copyright(C) 2018 song
 */
class ThemeSkinAdapter(layoutResId: Int, data: List<ThemeSkin>?) : BaseQuickAdapter<ThemeSkin, BaseViewHolder>(layoutResId, data) {
    private val instance: PreferencesHelper
    override fun convert(helper: BaseViewHolder, item: ThemeSkin) {
        val selectSkin = instance.getInteger(AppConstant.SETTING.CURRENT_SELECT_SKIN, 0)
        if (selectSkin == item.skinIndex) {
            helper.setVisible(R.id.cb_select_color, true)
            helper.setChecked(R.id.cb_select_color, true)
        } else {
            helper.setVisible(R.id.cb_select_color, false)
            helper.setChecked(R.id.cb_select_color, false)
        }
        val textView = helper.getView<TextView>(R.id.tv_color_title)
        textView.isSelected = true
        helper.setText(R.id.tv_color_title, item.skinTitle)
        helper.setBackgroundColor(R.id.iv_color, Color.parseColor(item.skinColor))
    }

    init {
        instance = PreferencesHelper.getInstance(mContext)
    }
}