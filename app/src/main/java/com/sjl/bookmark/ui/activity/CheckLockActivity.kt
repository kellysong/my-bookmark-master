package com.sjl.bookmark.ui.activity

import android.view.animation.AnimationUtils
import com.sjl.bookmark.R
import com.sjl.bookmark.ui.contract.CheckLockContract
import com.sjl.bookmark.ui.presenter.CheckLockPresenter
import com.sjl.bookmark.widget.LockPatternView
import com.sjl.bookmark.widget.LockPatternView.OnPatternListener
import com.sjl.core.mvp.BaseActivity
import com.sjl.core.util.log.LogUtils
import kotlinx.android.synthetic.main.activity_check_lock.*

/**
 * 手势密码验证Activity
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CheckLockActivity.java
 * @time 2018/3/5 10:05
 * @copyright(C) 2018 song
 */
class CheckLockActivity : BaseActivity<CheckLockPresenter>(), CheckLockContract.View,
    OnPatternListener {

    override fun getLayoutId(): Int {
        setStatusBar(-0x505051)
        return R.layout.activity_check_lock
    }

    override fun initView() {}
    override fun initListener() {
        lockPatternView.setOnPatternListener(this)
    }

    override fun initData() {}
    override fun lockDisplayError() {
        lockPatternView.setDisplayMode(LockPatternView.DisplayMode.Wrong)
        show_text.text = getString(R.string.check_error)
        show_text.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake_x))
    }

    override fun kill() {
        finish()
    }

    override fun onPatternStart() {
        LogUtils.i("onPatternStart")
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
}