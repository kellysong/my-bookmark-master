package com.sjl.bookmark.ui.contract;

import android.content.Intent;

import com.sjl.bookmark.widget.LockPatternView;
import com.sjl.core.mvp.BasePresenter;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CreateLockContract.java
 * @time 2018/11/26 11:38
 * @copyright(C) 2018 song
 */
public interface CreateLockContract {

    /**
     * 图案锁创建view，复用CheckLockContract.View接口
     */
    interface View extends CheckLockContract.View {
        void setTitle(String title);
        void setResults(int isSuccess);

        void clearPattern();

        void showLockMsg(String msg);
    }

    abstract class Presenter extends BasePresenter<View> {

        public abstract void init(Intent intent);


        public abstract void fingerPress();


        public abstract void check(List<LockPatternView.Cell> pattern);

        public abstract void onBack();

    }
}
