package com.sjl.bookmark.ui.contract;

import com.sjl.bookmark.entity.Category;
import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename CategoryContract.java
 * @time 2018/11/26 15:42
 * @copyright(C) 2018 song
 */
public interface CategoryContract {
    interface View extends BaseContract.IBaseView {
        void setCategory(List<Category> categories);

        /**
         * 显示下拉加载进度
         */
        void showLoading();

        void showFail(String message);

    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void loadCategoryData();

        /**
         * 下拉刷新
         */
        public abstract void refresh();
    }
}
