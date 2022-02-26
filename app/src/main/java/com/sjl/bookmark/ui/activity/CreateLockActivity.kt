package com.sjl.bookmark.ui.activity

import android.view.View
import com.sjl.bookmark.R
import com.sjl.bookmark.kotlin.language.I18nUtils
import com.sjl.bookmark.ui.contract.CreateLockContract
import com.sjl.bookmark.ui.presenter.CreateLockPresenter
import com.sjl.bookmark.widget.LockPatternView
import com.sjl.bookmark.widget.LockPatternView.OnPatternListener
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.util.log.LogUtils
import kotlinx.android.synthetic.main.activity_create_lock.*
import kotlinx.android.synthetic.main.toolbar_default.*

/**
 * 设置手势密码Activity
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CreateLockActivity.java
 * @time 2018/3/5 10:00
 * @copyright(C) 2018 song
 */
class CreateLockActivity : BaseActivity<CreateLockPresenter>(),
    CreateLockContract.View, OnPatternListener {

    override fun getLayoutId(): Int {
        return R.layout.activity_create_lock
    }

    override fun initView() {}
    override fun initListener() {
        bindingToolbar(common_toolbar, I18nUtils.getString(R.string.title_setting_gesture_pwd))
        common_toolbar.setNavigationOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View) {
                mPresenter.onBack()
                finish()
            }
        })
        lockPatternView.setOnPatternListener(this)
    }

    override fun initData() {
        mPresenter.init(intent)
    }

    override fun lockDisplayError() {
        lockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong)
    }

    override fun setTitle(title: String) {
        common_toolbar.title = title
    }

    override fun setResults(isSuccess: Int) {
        setResult(isSuccess)
    }

    override fun clearPattern() {}
    override fun kill() {
        finish()
    }

    override fun showLockMsg(msg: String) {
        iv_warn_msg.text = msg
    }

    override fun onPatternStart() {
        LogUtils.i("onPatternStart")
        mPresenter.fingerPress()
    }

    override fun onPatternCleared() {
        LogUtils.i("onPatternCleared")
    }

    override fun onPatternCellAdded(pattern: List<LockPatternView.Cell>) {
        LogUtils.i("onPatternCellAdded")
    }

    override fun onPatternDetected(pattern: List<LockPatternView.Cell>) {
        LogUtils.i("onPatternDetected")
        mPresenter.check(pattern)
    }

    override fun onBackPressed() {
        mPresenter.onBack()
        super.onBackPressed()
    }
}