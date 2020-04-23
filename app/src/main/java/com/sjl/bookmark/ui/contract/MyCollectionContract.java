package com.sjl.bookmark.ui.contract;

import com.sjl.bookmark.entity.table.Collection;
import com.sjl.core.mvp.BaseContract;
import com.sjl.core.mvp.BasePresenter;

import java.util.List;

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MyCollectionContract.java
 * @time 2018/11/26 10:50
 * @copyright(C) 2018 song
 */
public interface MyCollectionContract {
    interface View extends BaseContract.IBaseView {
        void setMyCollection(List<Collection> collections, int loadType);

    }

    abstract class Presenter extends BasePresenter<View> {
        public abstract void loadMyCollection();
        public abstract void loadMore();
        /**
         * 删除收藏
         *
         * @param collection
         */
        public abstract void deleteCollection(Collection collection);

        /**
         * 数据为空，询问是否从服务器端恢复收藏的书签
         */
        public abstract void recoverCollectionDataFromServer();
    }
}
