package com.sjl.bookmark.ui.presenter

import com.sjl.bookmark.api.WanAndroidApiService
import com.sjl.bookmark.entity.Article
import com.sjl.bookmark.entity.DataResponse
import com.sjl.bookmark.net.HttpConstant
import com.sjl.bookmark.ui.contract.ArticleListContract
import com.sjl.core.net.RetrofitHelper
import com.sjl.core.net.RxSchedulers
import com.uber.autodispose.ObservableSubscribeProxy
import io.reactivex.functions.Consumer

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ArticleListPresenter.java
 * @time 2018/3/23 16:28
 * @copyright(C) 2018 song
 */
class ArticleListPresenter : ArticleListContract.Presenter() {
    private var mIsRefresh: Boolean = true
    private var mPage: Int = 0
    private var mCid: Int = 0
    fun loadCategoryArticles(cid: Int) {
        mCid = cid
        val apiService: WanAndroidApiService = RetrofitHelper.getInstance().getApiService(
            WanAndroidApiService::class.java
        )
        apiService.getKnowledgeCategoryArticles(mPage, mCid)
            .compose(RxSchedulers.applySchedulers())
            .`as`<ObservableSubscribeProxy<DataResponse<Article>>>(
                bindLifecycle<DataResponse<Article>>()
            )
            .subscribe(object : Consumer<DataResponse<Article>> {
                @Throws(Exception::class)
                override fun accept(dataResponse: DataResponse<Article>) {
                    val loadType: Int =
                        if (mIsRefresh) HttpConstant.LoadType.TYPE_REFRESH_SUCCESS else HttpConstant.LoadType.TYPE_LOAD_MORE_SUCCESS
                    mView.setCategoryArticles(dataResponse.data, loadType)
                }
            }, object : Consumer<Throwable> {
                @Throws(Exception::class)
                override fun accept(throwable: Throwable) {
                    val loadType: Int =
                        if (mIsRefresh) HttpConstant.LoadType.TYPE_REFRESH_ERROR else HttpConstant.LoadType.TYPE_LOAD_MORE_ERROR
                    mView.setCategoryArticles(Article(), loadType)
                }
            })
    }

    override fun loadMore() {
        mPage++
        mIsRefresh = false
        loadCategoryArticles(mCid)
    }

    override fun refresh() {
        mPage = 0
        mIsRefresh = true
        loadCategoryArticles(mCid)
    }
}