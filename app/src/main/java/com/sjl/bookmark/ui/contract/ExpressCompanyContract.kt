package com.sjl.bookmark.ui.contract;

import com.sjl.bookmark.entity.ExpressCompany;
import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ExpressCompanyContract.java
 * @time 2018/11/26 11:14
 * @copyright(C) 2018 song
 */
public interface ExpressCompanyContract {
    interface View extends BaseContract.IBaseView {

    }

    abstract class Presenter extends BasePresenter<View> {
        /**
         * 不建议接口有返回值，尽量逻辑在Presenter层处理
         * @return
         */
        public abstract List<ExpressCompany> initCompany();
    }
}
