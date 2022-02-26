package com.sjl.bookmark.ui.presenter

import com.sjl.bookmark.R
import com.sjl.bookmark.api.WanAndroidApiService
import com.sjl.bookmark.entity.Article
import com.sjl.bookmark.entity.DataResponse
import com.sjl.bookmark.entity.HotKey
import com.sjl.bookmark.ui.contract.ArticleSearchContract
import com.sjl.core.net.RetrofitHelper
import com.sjl.core.net.RxSchedulers
import com.sjl.core.util.log.LogUtils
import com.uber.autodispose.ObservableSubscribeProxy
import io.reactivex.functions.Consumer

/**
 * TODO
 *
 * @author Kelly
 * @version 1.0.0
 * @filename ArticleSearchPresenter.java
 * @time 2018/4/1 12:03
 * @copyright(C) 2018 song
 */
class ArticleSearchPresenter : ArticleSearchContract.Presenter() {
    private var mCurrentPage: Int = 0

    /**
     * 获取热门搜索关键字
     */
    override fun getHotKeyData() {
        val apiService: WanAndroidApiService = RetrofitHelper.getInstance().getApiService(
            WanAndroidApiService::class.java
        )
        apiService.hotKeys
            .compose(RxSchedulers.applySchedulers())
            .`as`<ObservableSubscribeProxy<DataResponse<List<HotKey>>>>(
                bindLifecycle<DataResponse<List<HotKey>>>()
            )
            .subscribe(object : Consumer<DataResponse<List<HotKey>>> {
                @Throws(Exception::class)
                override fun accept(dataResponse: DataResponse<List<HotKey>>) {
                    mView.getHotKeySuccess(dataResponse.data)
                }
            }, object : Consumer<Throwable> {
                @Throws(Exception::class)
                override fun accept(throwable: Throwable) {
                    mView.showFailMsg("热门搜索：" + throwable.message)
                }
            })
    }

    /**
     * 根据关键字搜索
     * @param key
     */
    override fun searchData(key: String) {
        mCurrentPage = 0
        val apiService: WanAndroidApiService = RetrofitHelper.getInstance().getApiService(
            WanAndroidApiService::class.java
        )
        apiService.getSearchArticles(mCurrentPage, key)
            .compose(RxSchedulers.applySchedulers())
            .`as`<ObservableSubscribeProxy<DataResponse<Article>>>(
                bindLifecycle<DataResponse<Article>>()
            )
            .subscribe(object : Consumer<DataResponse<Article>> {
                @Throws(Exception::class)
                override fun accept(dataResponse: DataResponse<Article>) {
                    mView.searchDataSuccess(dataResponse.data.datas)
                }
            }, object : Consumer<Throwable> {
                @Throws(Exception::class)
                override fun accept(throwable: Throwable) {
                    LogUtils.e(throwable)
                    mView.showFailMsg("搜索：" + throwable.message)
                }
            })
    }

    /**
     * 上拉加载更多数据
     * @param keyWord
     */
    override fun getMoreData(keyWord: String) {
        mCurrentPage += 1
        val apiService: WanAndroidApiService = RetrofitHelper.getInstance().getApiService(
            WanAndroidApiService::class.java
        )
        apiService.getSearchArticles(mCurrentPage, keyWord)
            .compose(RxSchedulers.applySchedulers())
            .`as`<ObservableSubscribeProxy<DataResponse<Article>>>(
                bindLifecycle<DataResponse<Article>>()
            )
            .subscribe(object : Consumer<DataResponse<Article>> {
                @Throws(Exception::class)
                override fun accept(dataResponse: DataResponse<Article>) {
                    mView.loadMoreDataSuccess(dataResponse.data.datas)
                }
            }, object : Consumer<Throwable> {
                @Throws(Exception::class)
                override fun accept(throwable: Throwable) {
                    mView.showFailMsg(mContext.getString(R.string.paging_load) + throwable.message)
                }
            })
    }
}