package com.sjl.bookmark.ui.contract;

import com.sjl.bookmark.entity.zhuishu.table.CollectBook;
import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

import java.util.List;

/**
 * 书架Contract
 *
 * @author Kelly
 * @version 1.0.0
 * @filename BookShelfContract.java
 * @time 2018/11/30 14:39
 * @copyright(C) 2018 song
 */
public interface BookShelfContract {
    interface View extends BaseContract.IBaseView {
        void showErrorMsg(String msg);

        void showRecommendBook(List<CollectBook> collBookBeans);

        /**
         * 删除刷新图书
         */
        void refreshBook();

    }

    abstract class Presenter extends BasePresenter<View> {
        /**
         * 联网获取最新图书，并集合本地收藏图书进行刷新
         */
        public abstract void refreshCollectBooks();

        /**
         * 从本地获取收藏的图书放进书架
         */
        public abstract void getRecommendBook();

        public abstract void deleteBook(CollectBook collectBook);

        /**
         * 删除所有书籍
         */
        public abstract void deleteAllBook();
    }
}
