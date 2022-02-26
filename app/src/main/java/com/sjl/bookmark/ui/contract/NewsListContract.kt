package com.sjl.bookmark.ui.contract;

import com.sjl.bookmark.entity.zhihu.NewsList;
import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsListContract.java
 * @time 2018/12/18 17:05
 * @copyright(C) 2018 song
 */
public interface NewsListContract {
    interface View extends BaseContract.IBaseView {
        /**
         * 刷新新闻列表
         * @param newsLists
         */
        void refreshNewsList(List<NewsList> newsLists);

        /**
         * 显示更多日报数据
         * @param newsLists
         */
        void showMoreNewsList(List<NewsList> newsLists);

    }

    abstract class Presenter extends BasePresenter<View> {
        /**
         * 加载首页数据
         */
        public abstract void loadNews();

        /**
         * 上拉加载更多
         */
        public abstract void loadMore();


        /**
         * 下拉刷新
         */
        public abstract void refresh();
    }
}
