package com.sjl.bookmark.ui.presenter

import android.content.Intent
import com.sjl.bookmark.ui.activity.MainActivity
import com.sjl.bookmark.ui.contract.CheckLockContract
import com.sjl.bookmark.widget.LockPatternUtils
import com.sjl.bookmark.widget.LockPatternView

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CheckLockPresenter.java
 * @time 2018/3/5 11:05
 * @copyright(C) 2018 song
 */
class CheckLockPresenter : CheckLockContract.Presenter() {
    override fun check(pattern: List<LockPatternView.Cell>) {
        if (pattern == null) return
        val instances: LockPatternUtils = LockPatternUtils.getInstances(mContext)
        if (instances.checkPattern(pattern)) {
            val intent: Intent = Intent(mContext, MainActivity::class.java)
            mContext.startActivity(intent)
            mView.kill()
        } else {
            mView.lockDisplayError()
        }
    }
}