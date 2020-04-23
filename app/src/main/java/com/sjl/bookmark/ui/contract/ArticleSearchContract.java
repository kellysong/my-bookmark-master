package com.sjl.bookmark.ui.contract;

import com.sjl.bookmark.entity.Article;
import com.sjl.bookmark.entity.HotKey;
import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ArticleSearchContract.java
 * @time 2018/11/26 15:16
 * @copyright(C) 2018 song
 */
public interface ArticleSearchContract {
    interface View extends BaseContract.IBaseView {
        void getHotKeySuccess(List<HotKey> data);

        void searchDataSuccess(List<Article.DatasBean> data);

        void loadMoreDataSuccess(List<Article.DatasBean> data);

        void showFailMsg(String message);
    }

    abstract class Presenter extends BasePresenter<View> {
        /**
         * 获取热门搜索关键字
         */
        public abstract void getHotKeyData();

        /**
         * 根据关键字搜索
         * @param key
         */
        public abstract void searchData(String key);

        /**
         * 上拉加载更多数据
         * @param keyWord
         */
        public abstract void getMoreData(String keyWord);
    }
}
