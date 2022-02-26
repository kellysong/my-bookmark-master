package com.sjl.bookmark.ui.contract;


import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BackupAndSyncContract.java
 * @time 2018/11/26 16:13
 * @copyright(C) 2018 song
 */
public interface BackupAndSyncContract {
    /**
     * 备份与同步view
     *
     *
     */
    interface View extends BaseContract.IBaseView {
        /**
         * 显示加载框
         * @param msg 提示信息
         */
        void showLoading(String msg);

        /**
         * 隐藏加载框
         * @param errorMsg
         */
        void hideLoading(String errorMsg);
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void setClickPreferenceKey(String key);

        public abstract void init();
    }
}
