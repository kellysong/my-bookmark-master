package com.sjl.bookmark.ui.contract;

import android.content.Intent;

import com.sjl.bookmark.entity.table.Bookmark;
import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookmarkContract.java
 * @time 2018/11/26 10:59
 * @copyright(C) 2018 song
 */
public interface BookmarkContract {
    interface View extends BaseContract.IBaseView {
        void showBookmarkData(List<Bookmark> bookmarks, int loadingState);

        void setItemMenuVisible(boolean visible);
    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void init(Intent intent);

        /**
         * 初始化数据
         */
        public abstract List<Bookmark> initBookmarkList();

        /**
         * 上拉加载
         */
        public abstract void pullRefreshUp();
        /**
         * 下拉刷新
         */
        public abstract void pullRefreshDown();
    }
}
