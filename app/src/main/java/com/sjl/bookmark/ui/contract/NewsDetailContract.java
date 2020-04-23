package com.sjl.bookmark.ui.contract;

import com.sjl.bookmark.entity.zhihu.NewsDetailDto;
import com.sjl.bookmark.entity.zhihu.NewsExtraDto;
import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsDetailContract.java
 * @time 2018/12/21 11:28
 * @copyright(C) 2018 song
 */
public interface NewsDetailContract {
    interface View extends BaseContract.IBaseView {
        /**
         * 新闻额外信息
         * @param newsExtra
         */
        void showNewsExtra(NewsExtraDto newsExtra);

        /**
         * 显示详情
         * @param newsDetail
         */
        void showNewsDetail(NewsDetailDto newsDetail);

        /**
         * 显示加载错误信息
         * @param errorMsg
         */
        void showError(String errorMsg);

    }

    abstract class Presenter extends BasePresenter<View> {

        /**
         * 分享新闻
         * @param content
         * @param imgUrl
         */
        public abstract void shareNews(String content, String imgUrl);

        /**
         * 加载新闻额外信息
         * @param id
         */
        public abstract void loadNewsExtra(String id);

        /**
         * 加载新闻详情
         * @param id
         */
        public abstract void loadNewsDetail(String id);


    }
}
