package com.sjl.bookmark.ui.activity

import com.sjl.bookmark.R
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.base.extend.BaseSwipeBackActivity
import com.sjl.bookmark.ui.fragment.SettingFragment
import com.sjl.core.net.RxLifecycleUtils
import kotlinx.android.synthetic.main.toolbar_scroll.*

/**
 * 设置Activity
 *
 * @author Kelly
 * @version 1.0.0
 * @filename SettingActivity.java
 * @time 2018/3/6 14:28
 * @copyright(C) 2018 song
 */
class SettingActivity : BaseSwipeBackActivity() {

    override fun getLayoutId(): Int {
        return R.layout.activity_setting
    }

    override fun changeStatusBarColor() {
        setColorForSwipeBack()
    }

    override fun initView() {
        setFragment()
    }

    private fun setFragment() {
        fragmentManager
            .beginTransaction()
            .replace(R.id.container, SettingFragment())
            .commit()
    }

    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.title_setting))
    }

    override fun initData() {}
    override fun onResume() {
        super.onResume()
        RxLifecycleUtils.setLifecycleOwner(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        RxLifecycleUtils.clear()
    }
}