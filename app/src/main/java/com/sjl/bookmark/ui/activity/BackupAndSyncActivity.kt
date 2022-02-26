package com.sjl.bookmark.ui.activity

import com.sjl.bookmark.R
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.base.extend.BaseSwipeBackActivity
import com.sjl.bookmark.ui.fragment.BackupAndSyncFragment
import com.sjl.core.net.RxLifecycleUtils
import kotlinx.android.synthetic.main.toolbar_scroll.*

/**
 * 收藏同步与恢复
 */
class BackupAndSyncActivity : BaseSwipeBackActivity() {

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
            .replace(R.id.container, BackupAndSyncFragment())
            .commit()
    }

    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.title_backup_sync))
    }

    override fun initData() {}
    override fun onResume() {
        super.onResume()
        RxLifecycleUtils.setLifecycleOwner(this)
    }
}