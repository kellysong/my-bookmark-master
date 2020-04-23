package com.sjl.bookmark.ui.contract;

import com.sjl.bookmark.entity.WifiInfo;
import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename WifiQueryContract.java
 * @time 2018/11/26 10:41
 * @copyright(C) 2018 song
 */
public interface WifiQueryContract {
    interface View extends BaseContract.IBaseView {
        /**
         * 显示wifi信息
         * @param wifiInfos
         */
        void showWifiInfo(List<WifiInfo> wifiInfos);
    }

    abstract class Presenter extends BasePresenter<View> {

        /**
         * 初始化wifi信息
         */
        public abstract void initWifiInfo();

        /**
         * 复制wifi密码到粘贴板
         * @param password
         */
        public abstract void copyWifiPassword(String password) ;

        /**
         * 连接wifi信息
         * @param wifiInfo
         */
        public abstract void connectWifi(WifiInfo wifiInfo);

    }
}
