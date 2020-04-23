package com.sjl.bookmark.ui.contract;

import com.sjl.bookmark.entity.zhihu.NewsCommentDto;
import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename NewsCommentContract.java
 * @time 2018/12/24 15:44
 * @copyright(C) 2018 song
 */
public interface NewsCommentContract {
    interface View extends BaseContract.IBaseView {
        /**
         * 显示新闻评论
         * @param newsCommentDto
         */
        void showNewsComment(NewsCommentDto newsCommentDto);


        /**
         * 显示加载错误信息
         * @param errorMsg
         */
        void showError(String errorMsg);

    }

    abstract class Presenter extends BasePresenter<View> {


        /**
         * 加载长评论
         * @param id
         */
        public abstract void loadLongComment(String id);

        /**
         * 加载短评论
         * @param id
         */
        public abstract void loadShortComment(String id);


    }
}
