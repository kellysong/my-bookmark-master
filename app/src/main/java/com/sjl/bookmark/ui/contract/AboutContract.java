package com.sjl.bookmark.ui.contract;


import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

/**
 * 改良mvp模式
 *
 * @author Kelly
 * @version 1.0.0
 * @filename AboutContract.java
 * @time 2018/11/26 9:50
 * @copyright(C) 2018 song
 */
public interface AboutContract {
    interface View extends BaseContract.IBaseView {
        /**
         * 显示当前app版本
         *
         * @param version app版本
         */
        void showCurrentVersion(String version);
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void getCurrentVersion();
    }
}
