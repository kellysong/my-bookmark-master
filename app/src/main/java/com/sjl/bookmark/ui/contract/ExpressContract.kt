package com.sjl.bookmark.ui.contract;

import com.sjl.bookmark.entity.table.HistoryExpress;
import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressContract.java
 * @time 2018/11/26 11:06
 * @copyright(C) 2018 song
 */
public interface ExpressContract {
    interface View extends BaseContract.IBaseView {
        void setHistoryExpress(List<HistoryExpress> historyExpresses);
    }

    abstract class Presenter extends BasePresenter<View> {
        /**
         * 获取未验收的快递
         */
        public abstract void getUnCheckList();

        /**
         * 获取所有历史快递，包括已验收和未验收
         */
        public abstract void getHistoryExpresses();
    }
}
