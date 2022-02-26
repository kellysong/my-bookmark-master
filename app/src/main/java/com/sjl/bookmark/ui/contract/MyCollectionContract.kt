package com.sjl.bookmark.ui.contract

import com.sjl.bookmark.entity.table.Collection
import com.sjl.core.mvp.BaseContract.IBaseView
import com.sjl.core.mvp.BasePresenter

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename MyCollectionContract.java
 * @time 2018/11/26 10:50
 * @copyright(C) 2018 song
 */
interface MyCollectionContract {
    interface View : IBaseView {
        fun setMyCollection(collections: List<Collection>, loadType: Int)
    }

    abstract class Presenter : BasePresenter<View>() {
        abstract fun loadMyCollection()
        abstract fun loadMore()

        /**
         * 删除收藏
         *
         * @param collection
         */
        abstract fun deleteCollection(collection: Collection)

        /**
         * 数据为空，询问是否从服务器端恢复收藏的书签
         */
        abstract fun recoverCollectionDataFromServer()
    }
}