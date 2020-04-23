package com.sjl.bookmark.ui.contract;

import com.sjl.bookmark.entity.Article;
import com.sjl.bookmark.entity.TopBanner;
import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename HomeContract.java
 * @time 2018/11/26 15:24
 * @copyright(C) 2018 song
 */
public interface HomeContract {
    interface View extends BaseContract.IBaseView {
        /**
         * 显示下拉加载进度
         */
        void showLoading();

        /**
         * 隐藏下拉加载进度
         */
        void hideLoading();

        /**
         * 设置头部轮播图
         *
         * @param banners
         */
        void setHomeBanners(List<TopBanner> banners);

        /**
         * 设置文章列表
         *
         * @param article
         * @param loadType
         */
        void setHomeArticles(Article article, int loadType);


        void collectArticleSuccess(int position, Article.DatasBean bean);

        void showFaild(String message);

    }

    abstract class Presenter extends BasePresenter<View> {

        /**
         *初始化首页数据
         */
        public abstract void loadHomeData();

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
