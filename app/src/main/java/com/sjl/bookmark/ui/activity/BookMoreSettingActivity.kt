package com.sjl.bookmark.ui.activity

import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import android.widget.TextView
import com.sjl.bookmark.R
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.widget.reader.ReadSettingManager
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.mvp.NoPresenter
import kotlinx.android.synthetic.main.book_more_setting_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*

/**
 * 阅读器更多设置
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookMoreSettingActivity.java
 * @time 2018/12/5 11:24
 * @copyright(C) 2018 song
 */
class BookMoreSettingActivity : BaseActivity<NoPresenter>() {

    private lateinit var mSettingManager: ReadSettingManager
    private var isVolumeTurnPage: Boolean = false
    private var isFullScreen: Boolean = false
    override fun getLayoutId(): Int {
        return R.layout.book_more_setting_activity
    }

    override fun initView() {
        more_setting_tv_convert_type.isSelected = true
    }

    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.title_read_setting))
        more_setting_rl_volume.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                isVolumeTurnPage = !isVolumeTurnPage
                more_setting_sc_volume!!.isChecked = isVolumeTurnPage
                mSettingManager.isVolumeTurnPage = isVolumeTurnPage
            }
        })
        more_setting_rl_full_screen!!.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                isFullScreen = !isFullScreen
                more_setting_sc_full_screen.isChecked = isFullScreen
                mSettingManager.isFullScreen = isFullScreen
            }
        })
    }

    override fun initData() {
        mSettingManager = ReadSettingManager.getInstance()
        isVolumeTurnPage = mSettingManager.isVolumeTurnPage
        isFullScreen = mSettingManager.isFullScreen
        val convertType: Int = mSettingManager.convertType
        more_setting_sc_volume.isChecked = isVolumeTurnPage
        more_setting_sc_full_screen.isChecked = isFullScreen
        val adapter: ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(
            this,
            R.array.conversion_type_array, android.R.layout.simple_spinner_item
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        more_setting_sc_convert_type.adapter = adapter
        more_setting_sc_convert_type.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                val tv: TextView = view as TextView
                tv.gravity = Gravity.RIGHT //设置居中
                mSettingManager.convertType = position //设置语言转换类型
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        more_setting_sc_convert_type.setSelection(convertType)
    }
}