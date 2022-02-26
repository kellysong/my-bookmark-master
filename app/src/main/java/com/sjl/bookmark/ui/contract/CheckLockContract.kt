package com.sjl.bookmark.ui.contract

import com.sjl.bookmark.widget.LockPatternView
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CheckLockContract.java
 * @time 2018/11/26 11:33
 * @copyright(C) 2018 song
 */
interface CheckLockContract {
    /**
     * 图案锁验证view
     */
    interface View : IBaseView {
        fun lockDisplayError()
        fun kill()
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun check(pattern: List<LockPatternView.Cell>)
    }
}