package com.sjl.bookmark.ui.contract;

import com.sjl.bookmark.widget.LockPatternView;
import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CheckLockContract.java
 * @time 2018/11/26 11:33
 * @copyright(C) 2018 song
 */
public interface CheckLockContract {
    /**
     * 图案锁验证view
     */
    interface View extends BaseContract.IBaseView {

        void lockDisplayError();

        void kill();
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void check(List<LockPatternView.Cell> pattern);
    }
}
