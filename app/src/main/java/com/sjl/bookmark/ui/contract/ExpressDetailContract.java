package com.sjl.bookmark.ui.contract;

import android.content.Intent;

import com.sjl.bookmark.entity.ExpressDetail;
import com.sjl.bookmark.entity.ExpressSearchInfo;
import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressDetailContract.java
 * @time 2018/11/26 11:19
 * @copyright(C) 2018 song
 */
public interface ExpressDetailContract {
    interface View extends BaseContract.IBaseView {
        /**
         * 显示快递来源
         * @param expressSearchInfo
         */
        void showExpressSource(ExpressSearchInfo expressSearchInfo);

        /**
         * 显示物流详情
         * @param expressDetail
         */
        void showExpressDetail(ExpressDetail expressDetail);

        /**
         * 显示错误信息
         */
        void showErrorInfo();
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void init(Intent intent);

        /**
         * 获取快递备注信息
         *
         * @param postId
         * @return
         */
        public abstract String getExpressRemark(String postId);

        /**
         * 查询快递明细
         *
         * @param searchInfo
         */
        public abstract void queryExpressDetail(ExpressSearchInfo searchInfo);

        /**
         * 更新本地快递信息
         * @param searchInfo
         * @param expressDetail
         */
        public abstract void updateExpressDetail(ExpressSearchInfo searchInfo, ExpressDetail expressDetail);


        /**
         * 判断本地是否缓存有快递信息
         * @param postId
         * @return
         */
        public abstract boolean checkExistExpress(String postId);

        /**
         * 更新快递单备注信息
         * @param postId
         * @param remark
         */
        public abstract void updateExpressRemark(String postId, String remark);
    }
}
