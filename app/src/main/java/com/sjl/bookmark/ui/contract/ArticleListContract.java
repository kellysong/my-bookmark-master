package com.sjl.bookmark.ui.contract;

import com.sjl.bookmark.entity.Article;
import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ArticleListContract.java
 * @time 2018/11/26 15:32
 * @copyright(C) 2018 song
 */
public interface ArticleListContract {
    interface View extends BaseContract.IBaseView {
        void setCategoryArticles(Article article, int loadType);
    }

    abstract class Presenter extends BasePresenter<View> {

        public abstract void loadMore();

        public abstract void refresh();
    }
}
