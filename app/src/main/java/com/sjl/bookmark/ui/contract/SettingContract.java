package com.sjl.bookmark.ui.contract;

import android.content.Intent;
import android.preference.Preference;

import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;


/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename SettingContract.java
 * @time 2018/11/26 16:29
 * @copyright(C) 2018 song
 */
public interface SettingContract {
    interface View extends BaseContract.IBaseView {
        void openChangeThemeActivity();


        void readyGo(Class clazz, Intent intent);

        /**
         * 显示加载框
         *
         * @param type 0进度条对话框，1圆形加载对话框
         * @param msg  提示信息
         */
        void showLoading(int type, String msg);

        /**
         * 隐藏加载框
         *
         * @param ret
         * @param errorMsg
         */
        void hideLoading(boolean ret, String errorMsg);

        void update(int percent);
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void init();

        public abstract void setClickPreferenceKey(Preference preference, String key);


        public abstract void resetGestureFlag();
    }
}
