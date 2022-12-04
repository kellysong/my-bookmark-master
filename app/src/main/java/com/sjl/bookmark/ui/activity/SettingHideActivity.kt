package com.sjl.bookmark.ui.activity

import com.sjl.bookmark.R
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.mvp.NoPresenter
import kotlinx.android.synthetic.main.setting_hide_activity.*
import kotlinx.android.synthetic.main.toolbar_default.*

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename SettingHideActivity
 * @time 2022/12/3 14:01
 * @copyright(C) 2022 song
 */ 
class SettingHideActivity : BaseActivity<NoPresenter>(){
    override fun getLayoutId(): Int {
      return R.layout.setting_hide_activity
    }

    override fun initView() {
      
    }

    override fun initListener() {
        bindingToolbar(common_toolbar, "隐藏设置")
        btn_reset_bookmark_flag.setOnClickListener {
            val sharedPreferences = getSharedPreferences("bookmark", MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putBoolean("readFlag", false)
            editor.commit()
            openActivity(MainActivity::class.java)
            finish()
        }
    }

    override fun initData() {
      
    }

}