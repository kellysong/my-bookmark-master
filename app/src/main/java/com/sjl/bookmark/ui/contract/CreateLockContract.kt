package com.sjl.bookmark.ui.contract

import android.content.Intent
import com.sjl.bookmark.widget.LockPatternView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CreateLockContract.java
 * @time 2018/11/26 11:38
 * @copyright(C) 2018 song
 */
interface CreateLockContract {
    /**
     * 图案锁创建view，复用CheckLockContract.View接口
     */
    interface View : CheckLockContract.View {
        fun setTitle(title: String)
        fun setResults(isSuccess: Int)
        fun clearPattern()
        fun showLockMsg(msg: String)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun init(intent: Intent)
        abstract fun fingerPress()
        abstract fun check(pattern: List<LockPatternView.Cell>)
        abstract fun onBack()
    }
}